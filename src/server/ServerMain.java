package server;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import network.SerializationUtils;
import server.db.DatabaseConfig;
import server.db.DatabaseManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public final class ServerMain {
    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());
    private static final int DEFAULT_PORT = 5555;
    private static final int READER_THREADS = 4;
    private static final int SOCKET_TIMEOUT_MILLIS = 500;

    static {
        configureLoggerToStdout();
    }

    private ServerMain() {
    }

    public static void main(String[] args) throws Exception {
        ServerSettings settings = ServerSettings.fromArgs(args);
        DatabaseManager databaseManager = new DatabaseManager(DatabaseConfig.fromFile(settings.configPath()));
        databaseManager.initialize();

        CollectionManager collectionManager = new CollectionManager();
        collectionManager.addAll(databaseManager.loadOrganizations());
        ServerCommandProcessor processor = new ServerCommandProcessor(collectionManager, databaseManager);

        LOGGER.info("Server startup on port " + settings.port() + " with PostgreSQL config " + settings.configPath());
        System.out.println("Server started. Type 'exit' to stop. Changes are saved in PostgreSQL immediately.");
        runServer(settings.port(), processor);
    }

    private static void runServer(int port, ServerCommandProcessor processor) throws Exception {
        ExecutorService readPool = Executors.newFixedThreadPool(READER_THREADS);
        ForkJoinPool processingPool = new ForkJoinPool();
        ForkJoinPool sendingPool = new ForkJoinPool();
        try (DatagramSocket socket = new DatagramSocket(port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            socket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
            ServerRuntime runtime = new ServerRuntime(processor, new RequestReader(), new ResponseSender(), socket);
            receiveRequests(console, readPool, processingPool, sendingPool, runtime);
        } finally {
            readPool.shutdownNow();
            processingPool.shutdownNow();
            sendingPool.shutdownNow();
            LOGGER.info("Server stopped");
        }
    }

    private static void receiveRequests(BufferedReader console, ExecutorService readPool, ForkJoinPool processingPool,
                                        ForkJoinPool sendingPool, ServerRuntime runtime) throws Exception {
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(LOGGER);
        byte[] buffer = new byte[SerializationUtils.BUFFER_SIZE];
        boolean running = true;
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                runtime.socket().receive(packet);
                DatagramPacket requestPacket = copyPacket(packet);
                String client = connectionReceiver.register(requestPacket);
                LOGGER.info("Request received from " + client);
                RequestTask task = new RequestTask(runtime, processingPool, sendingPool, requestPacket, client);
                readPool.submit(() -> handleRequest(task));
            } catch (SocketTimeoutException ignored) {
                running = !shouldStop(console);
            }
        }
    }

    private static boolean shouldStop(BufferedReader console) throws Exception {
        return console.ready() && "exit".equals(console.readLine().trim());
    }

    private static void handleRequest(RequestTask task) {
        try {
            var request = task.runtime().requestReader().read(task.packet());
            task.processingPool().submit(() -> processRequest(task, request));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read request from " + task.client(), e);
            CommandResponse response = new CommandResponse(false, "Error: failed to read request: " + e.getMessage());
            task.sendingPool().submit(() -> sendResponse(task, response));
        }
    }

    private static void processRequest(RequestTask task, CommandRequest request) {
        CommandResponse response = task.runtime().processor().process(request);
        task.sendingPool().submit(() -> sendResponse(task, response));
    }

    private static void sendResponse(RequestTask task, CommandResponse response) {
        try {
            task.runtime().responseSender().send(task.runtime().socket(), task.packet(), response);
            LOGGER.info("Response sent to " + task.client());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send response to " + task.client(), e);
        }
    }

    private static DatagramPacket copyPacket(DatagramPacket packet) {
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        return new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
    }

    private static void configureLoggerToStdout() {
        LOGGER.setUseParentHandlers(false);
        for (Handler handler : LOGGER.getHandlers()) LOGGER.removeHandler(handler);
        StreamHandler stdoutHandler = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        stdoutHandler.setLevel(Level.ALL);
        LOGGER.addHandler(stdoutHandler);
        LOGGER.setLevel(Level.INFO);
    }

    private record ServerSettings(String configPath, int port) {
        private static ServerSettings fromArgs(String[] args) {
            String configPath = args.length > 0 ? args[0] : "db.properties";
            int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
            return new ServerSettings(configPath, port);
        }
    }

    private record ServerRuntime(ServerCommandProcessor processor, RequestReader requestReader,
                                 ResponseSender responseSender, DatagramSocket socket) {
    }

    private record RequestTask(ServerRuntime runtime, ForkJoinPool processingPool, ForkJoinPool sendingPool,
                               DatagramPacket packet, String client) {
    }
}
