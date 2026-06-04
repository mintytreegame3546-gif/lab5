package client;

import data.Organization;
import managers.InputManager;
import network.CommandRequest;
import network.CommandResponse;
import network.SerializationUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientMain {
    private static final int DEFAULT_PORT = 5555;
    private static final int RETRIES = 3;
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(2);
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        InetSocketAddress server = new InetSocketAddress(host, port);

        try (DatagramChannel channel = DatagramChannel.open(); Scanner scanner = new Scanner(System.in)) {
            channel.configureBlocking(false);
            InputManager inputManager = new InputManager(scanner);
            String[] credentials = new String[2];
            System.out.println("Client started. Use 'register username password' or 'login username password'. Enter 'help' for commands or 'exit' to quit.");
            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\s+");
                String commandName = tokens[0];
                if ("exit".equals(commandName)) {
                    System.out.println("Goodbye");
                    break;
                }
                if ("save".equals(commandName)) {
                    System.out.println("Error: save is a server-only command");
                    continue;
                }
                sendCommand(channel, server, inputManager, tokens, credentials);
            }
        }
    }

    private static void sendCommand(DatagramChannel channel, InetSocketAddress server, InputManager inputManager, String[] tokens, String[] credentials) throws Exception {
        String name = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        Map<String, List<String>> scripts = "execute_script".equals(name) ? readScriptBundle(args) : Map.of();
        if (scripts == null) return;
        String requestUsername = credentials[0];
        String requestPassword = credentials[1];
        if (("login".equals(name) || "register".equals(name)) && args.length >= 2) {
            requestUsername = args[0];
            requestPassword = args[1];
        }
        CommandResponse response = sendRequest(channel, server, new CommandRequest(name, args, null, scripts, requestUsername, requestPassword));
        if (response == null) return;

        if (!response.isSuccess() && "Error: organization payload is required".equals(response.getMessage())) {
            Organization organization = inputManager.readOrganization(0);
            response = sendRequest(channel, server, new CommandRequest(name, args, organization, scripts, requestUsername, requestPassword));
            if (response == null) return;
        }

        if (response.isSuccess() && ("login".equals(name) || "register".equals(name)) && args.length >= 2) {
            credentials[0] = args[0];
            credentials[1] = args[1];
        }
        System.out.println(response.getMessage());
    }

    private static Map<String, List<String>> readScriptBundle(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: file_name is required");
            return null;
        }
        String root = args[0];
        Map<String, List<String>> scripts = new HashMap<>();
        try {
            loadScript(root, scripts, 1);
            return scripts;
        } catch (Exception e) {
            System.out.println("Error reading script: " + e.getMessage());
            return null;
        }
    }

    private static void loadScript(String fileName, Map<String, List<String>> scripts, int depth) throws Exception {
        if (scripts.containsKey(fileName) || depth > 5) return;
        List<String> lines = Files.readAllLines(Path.of(fileName));
        scripts.put(fileName, lines);
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            if (!"execute_script".equals(tokens[0]) || tokens.length < 2) continue;
            loadScript(tokens[1], scripts, depth + 1);
        }
    }

    private static CommandResponse sendRequest(DatagramChannel channel, InetSocketAddress server, CommandRequest request) throws Exception {
        byte[] requestBytes = SerializationUtils.serialize(request);
        ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
        for (int attempt = 0; attempt < RETRIES; attempt++) {
            requestBuffer.rewind();
            channel.send(requestBuffer, server);
            long deadline = System.currentTimeMillis() + RESPONSE_TIMEOUT.toMillis();
            ByteBuffer responseBuffer = ByteBuffer.allocate(SerializationUtils.BUFFER_SIZE);
            while (System.currentTimeMillis() < deadline) {
                SocketAddress address = channel.receive(responseBuffer);
                if (address != null) {
                    responseBuffer.flip();
                    byte[] responseBytes = new byte[responseBuffer.remaining()];
                    responseBuffer.get(responseBytes);
                    Object object = SerializationUtils.deserialize(responseBytes, responseBytes.length);
                    if (object instanceof CommandResponse response) return response;
                    return new CommandResponse(false, "Error: invalid response object");
                }
                Thread.sleep(50);
            }
            System.out.println("No response from server, retry " + (attempt + 1) + " of " + RETRIES + "...");
        }
        System.out.println("Server is temporarily unavailable. Please try again later.");
        return null;
    }
}
