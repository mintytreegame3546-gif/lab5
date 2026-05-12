package server;

import managers.CollectionManager;
import managers.FileManager;
import network.CommandRequest;
import network.CommandResponse;
import network.SerializationUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());
    private static final int DEFAULT_PORT = 5555;

    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "data.csv";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(file);
        fileManager.load(collectionManager);
        ServerCommandProcessor processor = new ServerCommandProcessor(collectionManager);
        AtomicBoolean saved = new AtomicBoolean(false);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> saveOnce(fileManager, collectionManager, saved)));

        logger.info("Server startup on port " + port + " with data file " + file);
        System.out.println("Server started. Type 'save' to save or 'exit' to save and stop.");

        try (DatagramSocket socket = new DatagramSocket(port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            socket.setSoTimeout(500);
            Set<String> clients = new HashSet<>();
            byte[] buffer = new byte[SerializationUtils.BUFFER_SIZE];
            boolean running = true;
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String client = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                    if (clients.add(client)) logger.info("New connection from " + client);
                    logger.info("Request received from " + client);
                    CommandResponse response = handlePacket(processor, packet);
                    byte[] responseBytes = SerializationUtils.serialize(response);
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                    logger.info("Response sent to " + client);
                } catch (SocketTimeoutException ignored) {
                    if (console.ready()) {
                        String command = console.readLine().trim();
                        if ("save".equals(command)) fileManager.save(collectionManager.getCollection());
                        if ("exit".equals(command)) running = false;
                    }
                }
            }
        } finally {
            saveOnce(fileManager, collectionManager, saved);
            logger.info("Server stopped");
        }
    }

    private static void saveOnce(FileManager fileManager, CollectionManager collectionManager, AtomicBoolean saved) {
        if (saved.compareAndSet(false, true)) fileManager.save(collectionManager.getCollection());
    }

    private static CommandResponse handlePacket(ServerCommandProcessor processor, DatagramPacket packet) {
        try {
            Object object = SerializationUtils.deserialize(packet.getData(), packet.getLength());
            if (!(object instanceof CommandRequest request)) return new CommandResponse(false, "Error: invalid request object");
            return processor.process(request);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to process request", e);
            return new CommandResponse(false, "Error: failed to read request: " + e.getMessage());
        }
    }
}
