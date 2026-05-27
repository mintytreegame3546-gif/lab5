package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class InfoServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public InfoServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "info"; }
    public String getDescription() { return "Display information about the collection"; }

    public CommandResponse execute(CommandRequest request) {
        return new CommandResponse(true, collectionManager.getInfo());
    }
}
