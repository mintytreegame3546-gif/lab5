package server;

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

    public ServerCommandProcessor(CollectionManager collectionManager) {
        register(new InfoServerCommand(collectionManager));
        register(new ShowServerCommand(collectionManager));
        register(new AddServerCommand(collectionManager));
        register(new UpdateServerCommand(collectionManager));
        register(new RemoveByIdServerCommand(collectionManager));
        register(new ClearServerCommand(collectionManager));
        register(new RemoveFirstServerCommand(collectionManager));
        register(new AddIfMinServerCommand(collectionManager));
        register(new RemoveLowerServerCommand(collectionManager));
        register(new SumTurnoverServerCommand(collectionManager));
        register(new FilterNameServerCommand(collectionManager));
        register(new PrintAddressAscServerCommand(collectionManager));
        register(new HelpServerCommand(commands));
    }

    public CommandResponse process(CommandRequest request) {
        ServerCommand command = commands.get(request.getCommandName());
        if (command == null) return new CommandResponse(false, "Error: Unknown command. Enter 'help' for available commands");
        try {
            return command.execute(request);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid number");
        } catch (Exception e) {
            return new CommandResponse(false, "Error executing command: " + e.getMessage());
        }
    }

    private void register(ServerCommand command) {
        commands.put(command.getName(), command);
    }
}
