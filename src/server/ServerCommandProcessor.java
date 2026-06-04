package server;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import network.Credentials;
import server.auth.PasswordHasher;
import server.commands.AddIfMinServerCommand;
import server.commands.AddServerCommand;
import server.commands.ClearServerCommand;
import server.commands.ExecuteScriptServerCommand;
import server.commands.FilterNameServerCommand;
import server.commands.HelpServerCommand;
import server.commands.InfoServerCommand;
import server.commands.PrintAddressAscServerCommand;
import server.commands.RemoveByIdServerCommand;
import server.commands.RemoveFirstServerCommand;
import server.commands.RemoveLowerServerCommand;
import server.commands.ServerCommand;
import server.commands.ShowServerCommand;
import server.commands.SumTurnoverServerCommand;
import server.commands.UpdateServerCommand;
import server.db.DatabaseManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServerCommandProcessor {
    private static final Logger LOGGER = Logger.getLogger(ServerCommandProcessor.class.getName());

    private final Map<String, ServerCommand> commands = new LinkedHashMap<>();
    private final DatabaseManager databaseManager;
    private final PasswordHasher passwordHasher = new PasswordHasher();
    public ServerCommandProcessor(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        register(new InfoServerCommand(collectionManager));
        register(new ShowServerCommand(collectionManager));
        register(new AddServerCommand(collectionManager, databaseManager));
        register(new UpdateServerCommand(collectionManager, databaseManager));
        register(new RemoveByIdServerCommand(collectionManager, databaseManager));
        register(new ClearServerCommand(collectionManager, databaseManager));
        register(new RemoveFirstServerCommand(collectionManager, databaseManager));
        register(new AddIfMinServerCommand(collectionManager, databaseManager));
        register(new RemoveLowerServerCommand(collectionManager, databaseManager));
        register(new SumTurnoverServerCommand(collectionManager));
        register(new FilterNameServerCommand(collectionManager));
        register(new PrintAddressAscServerCommand(collectionManager));
        register(new ExecuteScriptServerCommand(commands));
        register(new HelpServerCommand(commands));
    }

    public CommandResponse process(CommandRequest request) {
        try {
            if ("register".equals(request.getCommandName())) return registerUser(request);
            if ("login".equals(request.getCommandName())) return loginUser(request);
            if (!isAuthorized(request.getCredentials())) {
                return new CommandResponse(false, "Error: authorization is required. Use login or register first");
            }
            return executeCommand(request);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid number");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to process command " + request.getCommandName(), e);
            return new CommandResponse(false, "Error executing command: " + e.getMessage());
        }
    }

    private CommandResponse executeCommand(CommandRequest request) throws Exception {
        ServerCommand command = commands.get(request.getCommandName());
        if (command == null) {
            return new CommandResponse(false, "Error: Unknown command. Enter 'help' for available commands");
        }
        return command.execute(request);
    }

    private CommandResponse registerUser(CommandRequest request) throws Exception {
        Optional<Credentials> credentials = credentialsFromRequest(request);
        if (credentials.isEmpty()) return new CommandResponse(false, "Error: register requires username and password");
        return createUser(credentials.get(), "User registered and authorized");
    }

    private CommandResponse loginUser(CommandRequest request) throws Exception {
        Credentials credentials = credentialsFromRequest(request).orElse(Credentials.empty());
        if (!credentials.isComplete()) return new CommandResponse(false, "Error: login requires username and password");
        if (isAuthorized(credentials)) return new CommandResponse(true, "User authorized");
        return new CommandResponse(false, "Error: invalid username or password");
    }

    private CommandResponse createUser(Credentials credentials, String successMessage) throws Exception {
        String passwordHash = passwordHasher.hash(credentials.password());
        if (databaseManager.createUser(credentials.username(), passwordHash)) {
            return new CommandResponse(true, successMessage);
        }
        return new CommandResponse(false, "Error: user already exists");
    }

    private boolean isAuthorized(Credentials credentials) throws Exception {
        if (!credentials.isComplete()) return false;
        return databaseManager.authenticate(credentials.username(), passwordHasher.hash(credentials.password()));
    }

    private Optional<Credentials> credentialsFromRequest(CommandRequest request) {
        String[] args = request.getArgs();
        Credentials credentials = args.length >= 2 ? Credentials.of(args[0], args[1]) : request.getCredentials();
        return credentials.isComplete() ? Optional.of(credentials) : Optional.empty();
    }

    private void register(ServerCommand command) {
        commands.put(command.getName(), command);
    }

    private static class Credentials {
        private final String username;
        private final String password;

        private Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
