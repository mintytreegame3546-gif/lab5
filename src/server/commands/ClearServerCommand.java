package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class ClearServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public ClearServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "clear"; }
    public String getDescription() { return "Clear the collection"; }

    public CommandResponse execute(CommandRequest request) {
        collectionManager.clear();
        return new CommandResponse(true, "Collection cleared");
    }
}
