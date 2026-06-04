package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.db.DatabaseManager;

public class RemoveByIdServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public RemoveByIdServerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public String getName() { return "remove_by_id"; }
    public String getDescription() { return "Remove an organization by ID"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String usernameError = ServerCommandSupport.requireUsername(request);
        if (usernameError != null) return new CommandResponse(false, usernameError);
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID.");
        long id = Long.parseLong(args[0]);
        var existing = collectionManager.findById(id);
        if (existing.isEmpty()) return new CommandResponse(false, "Error: Organization with ID " + id + " not found!");
        if (!request.getUsername().equals(existing.get().getOwnerUsername())) {
            return new CommandResponse(false, "Error: you can modify only your own organizations");
        }
        if (databaseManager.deleteOrganization(id, request.getUsername())) {
            collectionManager.removeById(id);
            return new CommandResponse(true, "Organization with ID " + id + " removed");
        }
        return new CommandResponse(false, "Error: you can modify only your own organizations");
    }
}
