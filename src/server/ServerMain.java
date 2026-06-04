package server;

import managers.CollectionManager;
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

public class ServerMain {
    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());
    private static final int DEFAULT_PORT = 5555;
    private static final int READER_THREADS = 4;

    static {
        configureLoggerToStdout();
    }

    public static void main(String[] args) throws Exception {
        String configPath = args.length > 0 ? args[0] : "db.properties";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        DatabaseManager databaseManager = new DatabaseManager(DatabaseConfig.fromFile(configPath));
        databaseManager.initialize();
        CollectionManager collectionManager = new CollectionManager();
        collectionManager.addAll(databaseManager.loadOrganizations());
        ServerCommandProcessor processor = new ServerCommandProcessor(collectionManager, databaseManager);

        logger.info("Server startup on port " + port + " with PostgreSQL config " + configPath);
        System.out.println("Server started. Type 'exit' to stop. Collection changes are saved in PostgreSQL immediately.");

        ExecutorService readPool = Executors.newFixedThreadPool(READER_THREADS);
        ForkJoinPool processingPool = new ForkJoinPool();
        ForkJoinPool sendingPool = new ForkJoinPool();
        try (DatagramSocket socket = new DatagramSocket(port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            socket.setSoTimeout(500);
            ConnectionReceiver connectionReceiver = new ConnectionReceiver(logger);
            RequestReader requestReader = new RequestReader();
            ResponseSender responseSender = new ResponseSender();
            byte[] buffer = new byte[SerializationUtils.BUFFER_SIZE];
            boolean running = true;
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    DatagramPacket requestPacket = copyPacket(packet);
                    String client = connectionReceiver.register(requestPacket);
                    logger.info("Request received from " + client);
                    readPool.submit(() -> handleRequest(processor, requestReader, responseSender,
                            processingPool, sendingPool, socket, requestPacket, client));
                } catch (SocketTimeoutException ignored) {
                    if (console.ready() && "exit".equals(console.readLine().trim())) running = false;
                }
            }
        } finally {
            readPool.shutdownNow();
            processingPool.shutdownNow();
            sendingPool.shutdownNow();
            logger.info("Server stopped");
        }
    }

    private static void handleRequest(ServerCommandProcessor processor, RequestReader requestReader,
                                      ResponseSender responseSender, ForkJoinPool processingPool,
                                      ForkJoinPool sendingPool, DatagramSocket socket,
                                      DatagramPacket packet, String client) {
        try {
            var request = requestReader.read(packet);
            processingPool.submit(() -> {
                CommandResponse response = processor.process(request);
                sendingPool.submit(() -> sendResponse(responseSender, socket, packet, response, client));
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to read request", e);
            sendingPool.submit(() -> sendResponse(responseSender, socket, packet,
                    new CommandResponse(false, "Error: failed to read request: " + e.getMessage()), client));
        }
    }

    private static void sendResponse(ResponseSender responseSender, DatagramSocket socket,
                                     DatagramPacket packet, CommandResponse response, String client) {
        try {
            responseSender.send(socket, packet, response);
            logger.info("Response sent to " + client);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send response", e);
        }
    }

    private static DatagramPacket copyPacket(DatagramPacket packet) {
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        return new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
    }

    private static void configureLoggerToStdout() {
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) logger.removeHandler(handler);
        StreamHandler stdoutHandler = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        stdoutHandler.setLevel(Level.ALL);
        logger.addHandler(stdoutHandler);
        logger.setLevel(Level.INFO);
    }
}
