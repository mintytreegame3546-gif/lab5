package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class RemoveByIdServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public RemoveByIdServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "remove_by_id"; }
    public String getDescription() { return "Remove an organization by ID"; }

    public CommandResponse execute(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID.");
        long id = Long.parseLong(args[0]);
        if (collectionManager.getCollection().removeIf(o -> o.getId() == id)) {
            return new CommandResponse(true, "Organization with ID " + id + " removed");
        }
        return new CommandResponse(false, "Error: Organization with ID " + id + " not found!");
    }
}
