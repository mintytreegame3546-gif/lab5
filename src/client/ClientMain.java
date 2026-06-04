package client;

import data.Organization;
import managers.InputManager;
import network.CommandRequest;
import network.CommandResponse;
import network.Credentials;
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
import java.util.Optional;
import java.util.Scanner;

public final class ClientMain {
    private static final int DEFAULT_PORT = 5555;
    private static final int RETRIES = 3;
    private static final int MAX_SCRIPT_DEPTH = 5;
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(2);

    private ClientMain() {
    }
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        InetSocketAddress server = new InetSocketAddress(host, port);

        try (DatagramChannel channel = DatagramChannel.open(); Scanner scanner = new Scanner(System.in)) {
            channel.configureBlocking(false);
            runInteractiveLoop(channel, server, new InputManager(scanner), scanner);
        }
    }

    private static void runInteractiveLoop(DatagramChannel channel, InetSocketAddress server,
                                           InputManager inputManager, Scanner scanner) throws Exception {
        Credentials credentials = Credentials.empty();
        System.out.println("Client started. Use 'register username password' or 'login username password'.");
        System.out.println("Enter 'help' for commands or 'exit' to quit.");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) return;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            if ("exit".equals(tokens[0])) {
                System.out.println("Goodbye");
                return;
            }
            if ("save".equals(tokens[0])) {
                System.out.println("Error: save is a server-only command");
                continue;
            }
            credentials = sendCommand(channel, server, inputManager, tokens, credentials);
        }
    }

    private static Credentials sendCommand(DatagramChannel channel, InetSocketAddress server, InputManager inputManager,
                                           String[] tokens, Credentials currentCredentials) throws Exception {
        String commandName = tokens[0];
        String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);
        Optional<Map<String, List<String>>> scripts = readScriptBundle(commandName, commandArgs);
        if (scripts.isEmpty()) return currentCredentials;

        Credentials requestCredentials = credentialsForRequest(commandName, commandArgs, currentCredentials);
        CommandRequest request = new CommandRequest(commandName, commandArgs, null, scripts.get(), requestCredentials);
        Optional<CommandResponse> response = sendRequest(channel, server, request);
        if (response.isEmpty()) return currentCredentials;

        CommandResponse finalResponse = collectPayloadIfNeeded(
                channel, server, inputManager, request, response.get()).orElse(response.get());
        System.out.println(finalResponse.getMessage());
        if (shouldStoreCredentials(commandName, commandArgs, finalResponse)) return requestCredentials;
        return currentCredentials;
    }

    private static Optional<CommandResponse> collectPayloadIfNeeded(DatagramChannel channel, InetSocketAddress server,
                                                                    InputManager inputManager, CommandRequest request,
                                                                    CommandResponse response) throws Exception {
        if (response.isSuccess() || !"Error: organization payload is required".equals(response.getMessage())) {
            return Optional.of(response);
        }
        Organization organization = inputManager.readOrganization(0);
        CommandRequest requestWithPayload = new CommandRequest(
                request.getCommandName(), request.getArgs(), organization,
                request.getScripts(), request.getCredentials());
        return sendRequest(channel, server, requestWithPayload);
    }

    private static Credentials credentialsForRequest(String commandName, String[] args,
                                                     Credentials currentCredentials) {
        if (isAuthCommand(commandName) && args.length >= 2) return Credentials.of(args[0], args[1]);
        return currentCredentials;
    }

    private static boolean shouldStoreCredentials(String commandName, String[] args, CommandResponse response) {
        return response.isSuccess() && isAuthCommand(commandName) && args.length >= 2;
    }

    private static boolean isAuthCommand(String commandName) {
        return "login".equals(commandName) || "register".equals(commandName);
    }

    private static Optional<Map<String, List<String>>> readScriptBundle(String commandName, String[] args) {
        if (!"execute_script".equals(commandName)) return Optional.of(Map.of());
        if (args.length == 0) {
            System.out.println("Error: file_name is required");
            return Optional.empty();
        }
        Map<String, List<String>> scripts = new HashMap<>();
        try {
            loadScript(args[0], scripts, 1);
            return Optional.of(scripts);
        } catch (Exception e) {
            System.out.println("Error reading script: " + e.getMessage());
            return Optional.empty();
        }
    }

    private static void loadScript(String fileName, Map<String, List<String>> scripts, int depth) throws Exception {
        if (scripts.containsKey(fileName) || depth > MAX_SCRIPT_DEPTH) return;
        List<String> lines = Files.readAllLines(Path.of(fileName));
        scripts.put(fileName, lines);
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            if ("execute_script".equals(tokens[0]) && tokens.length >= 2) loadScript(tokens[1], scripts, depth + 1);
        }
    }

    private static Optional<CommandResponse> sendRequest(DatagramChannel channel, InetSocketAddress server,
                                                         CommandRequest request) throws Exception {
        byte[] requestBytes = SerializationUtils.serialize(request);
        ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
        for (int attempt = 0; attempt < RETRIES; attempt++) {
            requestBuffer.rewind();
            channel.send(requestBuffer, server);
            Optional<CommandResponse> response = waitForResponse(channel);
            if (response.isPresent()) return response;
            System.out.println("No response from server, retry " + (attempt + 1) + " of " + RETRIES + "...");
        }
        System.out.println("Server is temporarily unavailable. Please try again later.");
        return Optional.empty();
    }

    private static Optional<CommandResponse> waitForResponse(DatagramChannel channel) throws Exception {
        long deadline = System.currentTimeMillis() + RESPONSE_TIMEOUT.toMillis();
        ByteBuffer responseBuffer = ByteBuffer.allocate(SerializationUtils.BUFFER_SIZE);
        while (System.currentTimeMillis() < deadline) {
            SocketAddress address = channel.receive(responseBuffer);
            if (address != null) return Optional.of(readResponse(responseBuffer));
            Thread.sleep(50);
        }
        return Optional.empty();
    }

    private static CommandResponse readResponse(ByteBuffer responseBuffer) throws Exception {
        responseBuffer.flip();
        byte[] responseBytes = new byte[responseBuffer.remaining()];
        responseBuffer.get(responseBytes);
        Object object = SerializationUtils.deserialize(responseBytes, responseBytes.length);
        if (object instanceof CommandResponse response) return response;
        return new CommandResponse(false, "Error: invalid response object");
    }
}
