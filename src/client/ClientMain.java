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
import java.time.Duration;
import java.util.Arrays;
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
            Credentials credentials = authenticate(channel, server, scanner);
            if (credentials == null) return;
            InputManager inputManager = new InputManager(scanner);
            System.out.println("Client started. Enter 'help' for commands or 'exit' to quit.");
            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if ("exit".equals(line)) {
                    System.out.println("Goodbye");
                    break;
                }
                String commandName = line.split("\\s+", 2)[0];
                if ("save".equals(commandName)) {
                    System.out.println("Error: save is a server-only command");
                    continue;
                }
                sendLine(channel, server, inputManager, scanner, line, credentials);
            }
        }
    }

    private static Credentials authenticate(DatagramChannel channel, InetSocketAddress server, Scanner scanner) throws Exception {
        while (true) {
            System.out.print("Type 'login', 'register', or 'exit': ");
            String command = scanner.nextLine().trim().toLowerCase();
            if ("exit".equals(command)) return null;
            if (!"login".equals(command) && !"register".equals(command)) continue;
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            CommandResponse response = sendRequest(channel, server, new CommandRequest(command, new String[0], null, username, password));
            if (response == null) {
                System.out.println("Server is temporarily unavailable. Please try again later.");
                continue;
            }
            System.out.println(response.getMessage());
            if (response.isSuccess()) return new Credentials(username, password);
        }
    }

    private static void sendLine(DatagramChannel channel, InetSocketAddress server, InputManager inputManager, Scanner scanner,
                                 String line, Credentials credentials) throws Exception {
        String[] tokens = line.split("\\s+");
        String name = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        if ("update".equals(name)) {
            String id = args.length > 0 ? args[0] : "";
            while (true) {
                String[] requestArgs = id.isEmpty() ? new String[0] : new String[]{id};
                CommandResponse validation = sendRequest(channel, server, credentials.request("update", requestArgs, null));
                if (validation == null) {
                    System.out.println("Server is temporarily unavailable. Please try again later.");
                    return;
                }
                if (validation.isSuccess()) break;
                System.out.println(validation.getMessage());
                System.out.print("Enter a valid owned ID for update (or empty to cancel): ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) return;
                id = input;
            }
            Organization organization = inputManager.readOrganization(0);
            CommandResponse updateResponse = sendRequest(channel, server, credentials.request("update", new String[]{id}, organization));
            if (updateResponse == null) {
                System.out.println("Server is temporarily unavailable. Please try again later.");
                return;
            }
            System.out.println(updateResponse.getMessage());
            return;
        }

        Organization organization = requiresOrganization(name) ? inputManager.readOrganization(0) : null;
        CommandResponse response = sendRequest(channel, server, credentials.request(name, args, organization));
        if (response == null) {
            System.out.println("Server is temporarily unavailable. Please try again later.");
            return;
        }
        System.out.println(response.getMessage());
    }

    private static boolean requiresOrganization(String commandName) {
        return "add".equals(commandName)
                || "add_if_min".equals(commandName)
                || "remove_lower".equals(commandName);
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
                    if (object instanceof CommandResponse) return (CommandResponse) object;
                    return new CommandResponse(false, "Error: invalid response object");
                }
                Thread.sleep(50);
            }
            System.out.println("No response from server, retry " + (attempt + 1) + " of " + RETRIES + "...");
        }
        return null;
    }

    private static class Credentials {
        private final String username;
        private final String password;

        private Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        CommandRequest request(String command, String[] args, Organization organization) {
            return new CommandRequest(command, args, organization, username, password);
        }
    }
}
