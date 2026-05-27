package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class RemoveFirstServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public RemoveFirstServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "remove_first"; }
    public String getDescription() { return "Remove the first element in the collection"; }

    public CommandResponse execute(CommandRequest request) {
        if (collectionManager.getCollection().isEmpty()) return new CommandResponse(false, "Collection is empty");
        collectionManager.getCollection().removeFirst();
        return new CommandResponse(true, "First organization removed");
    }
}
