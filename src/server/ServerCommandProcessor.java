package server;

import database.OrganizationRepository;
import database.UserRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.commands.AddIfMinServerCommand;
import server.commands.AddServerCommand;
import server.commands.ClearServerCommand;
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

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerCommandProcessor {
    private final Map<String, ServerCommand> commands = new LinkedHashMap<>();
    private final UserRepository userRepository;

    public ServerCommandProcessor(CollectionManager collectionManager, OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        register(new InfoServerCommand(collectionManager));
        register(new ShowServerCommand(collectionManager));
        register(new AddServerCommand(collectionManager, organizationRepository));
        register(new UpdateServerCommand(collectionManager, organizationRepository));
        register(new RemoveByIdServerCommand(collectionManager, organizationRepository));
        register(new ClearServerCommand(collectionManager, organizationRepository));
        register(new RemoveFirstServerCommand(collectionManager, organizationRepository));
        register(new AddIfMinServerCommand(collectionManager, organizationRepository));
        register(new RemoveLowerServerCommand(collectionManager, organizationRepository));
        register(new SumTurnoverServerCommand(collectionManager));
        register(new FilterNameServerCommand(collectionManager));
        register(new PrintAddressAscServerCommand(collectionManager));
        register(new HelpServerCommand(commands));
    }

    public CommandResponse process(CommandRequest request) {
        try {
            if ("register".equals(request.getCommandName())) return registerUser(request);
            if ("login".equals(request.getCommandName())) return login(request);
            if (!hasCredentials(request)) return new CommandResponse(false, "Error: authorization required");
            if (!userRepository.authenticate(request.getUsername(), request.getPassword())) {
                return new CommandResponse(false, "Error: invalid username or password");
            }
            ServerCommand command = commands.get(request.getCommandName());
            if (command == null) return new CommandResponse(false, "Error: Unknown command. Enter 'help' for available commands");
            return command.execute(request);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid number");
        } catch (Exception e) {
            return new CommandResponse(false, "Error executing command: " + e.getMessage());
        }
    }

    private CommandResponse registerUser(CommandRequest request) throws Exception {
        if (!hasCredentials(request)) return new CommandResponse(false, "Error: username and password are required");
        if (userRepository.register(request.getUsername(), request.getPassword())) return new CommandResponse(true, "Registration successful");
        return new CommandResponse(false, "Error: user already exists");
    }

    private CommandResponse login(CommandRequest request) throws Exception {
        if (!hasCredentials(request)) return new CommandResponse(false, "Error: username and password are required");
        if (userRepository.authenticate(request.getUsername(), request.getPassword())) return new CommandResponse(true, "Login successful");
        return new CommandResponse(false, "Error: invalid username or password");
    }

    private boolean hasCredentials(CommandRequest request) {
        return request.getUsername() != null && !request.getUsername().isBlank()
                && request.getPassword() != null && !request.getPassword().isBlank();
    }

    private void register(ServerCommand command) {
        commands.put(command.getName(), command);
    }
}
