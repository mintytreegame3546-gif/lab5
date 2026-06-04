package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.db.DatabaseManager;

public class UpdateServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public UpdateServerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public String getName() { return "update"; }
    public String getDescription() { return "Update an organization by ID"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String usernameError = ServerCommandSupport.requireUsername(request);
        if (usernameError != null) return new CommandResponse(false, usernameError);
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID");
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid ID");
        }
        var existing = collectionManager.findById(id);
        if (existing.isEmpty()) return new CommandResponse(false, "Error Organization with ID " + id + " not found!");
        if (!request.getUsername().equals(existing.get().getOwnerUsername())) {
            return new CommandResponse(false, "Error: you can modify only your own organizations");
        }
        String validation = ServerCommandSupport.validateOrganization(request.getOrganization());
        if (validation != null) return new CommandResponse(false, validation);
        var updated = databaseManager.updateOrganization(id, request.getOrganization(), request.getUsername());
        if (updated.isEmpty()) return new CommandResponse(false, "Error: you can modify only your own organizations");
        collectionManager.replace(id, updated.get());
        return new CommandResponse(true, "Organization with ID " + id + " updated!");
    }
}
