package server;

import database.DatabaseManager;
import database.OrganizationRepository;
import database.UserRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import network.SerializationUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());
    private static final int DEFAULT_PORT = 5555;
    private static final int READ_THREADS = 4;

    public static void main(String[] args) throws Exception {
        String dbProperties = args.length > 0 ? args[0] : "db.properties";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        DatabaseManager databaseManager = new DatabaseManager(dbProperties);
        databaseManager.initialize();
        OrganizationRepository organizationRepository = new OrganizationRepository(databaseManager);
        UserRepository userRepository = new UserRepository(databaseManager);
        CollectionManager collectionManager = new CollectionManager();
        collectionManager.addAll(organizationRepository.loadAll());
        ServerCommandProcessor processor = new ServerCommandProcessor(collectionManager, organizationRepository, userRepository);

        logger.info("Server startup on port " + port + " with PostgreSQL storage");
        System.out.println("Server started. Type 'save' to confirm database persistence or 'exit' to stop.");

        ExecutorService readers = Executors.newFixedThreadPool(READ_THREADS);
        ForkJoinPool processors = new ForkJoinPool();
        ForkJoinPool senders = new ForkJoinPool();
        try (DatagramSocket socket = new DatagramSocket(port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            socket.setSoTimeout(500);
            ConnectionReceiver connectionReceiver = new ConnectionReceiver(logger);
            RequestReader requestReader = new RequestReader();
            ResponseSender responseSender = new ResponseSender();
            boolean running = true;
            while (running) {
                try {
                    byte[] buffer = new byte[SerializationUtils.BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String client = connectionReceiver.register(packet);
                    logger.info("Request received from " + client);
                    readers.submit(() -> {
                        CommandRequest request;
                        try {
                            request = requestReader.read(packet);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Failed to read request", e);
                            CommandResponse response = new CommandResponse(false, "Error: failed to read request: " + e.getMessage());
                            senders.submit(() -> sendResponse(socket, responseSender, packet, response, client));
                            return;
                        }
                        processors.submit(() -> {
                            CommandResponse response = processRequest(processor, request);
                            senders.submit(() -> sendResponse(socket, responseSender, packet, response, client));
                        });
                    });
                } catch (SocketTimeoutException ignored) {
                    if (console.ready()) {
                        String command = console.readLine().trim();
                        if ("save".equals(command)) System.out.println("Data is persisted in PostgreSQL immediately.");
                        if ("exit".equals(command)) running = false;
                    }
                }
            }
        } finally {
            readers.shutdown();
            processors.shutdown();
            senders.shutdown();
            logger.info("Server stopped");
        }
    }

    private static CommandResponse processRequest(ServerCommandProcessor processor, CommandRequest request) {
        try {
            return processor.process(request);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to process request", e);
            return new CommandResponse(false, "Error: failed to process request: " + e.getMessage());
        }
    }

    private static void sendResponse(DatagramSocket socket, ResponseSender responseSender, DatagramPacket packet, CommandResponse response, String client) {
        try {
            responseSender.send(socket, packet, response);
            logger.info("Response sent to " + client);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send response", e);
        }
    }
}
