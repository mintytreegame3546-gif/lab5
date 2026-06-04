package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.db.DatabaseManager;

public class ClearServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public ClearServerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public String getName() { return "clear"; }
    public String getDescription() { return "Clear your organizations from the collection"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String usernameError = ServerCommandSupport.requireUsername(request);
        if (usernameError != null) return new CommandResponse(false, usernameError);
        int removed = 0;
        for (var organization : collectionManager.getCollection()) {
            if (request.getUsername().equals(organization.getOwnerUsername())
                    && databaseManager.deleteOrganization(organization.getId(), request.getUsername())) {
                collectionManager.removeById(organization.getId());
                removed++;
            }
        }
        return new CommandResponse(true, "Removed " + removed + " owned organizations");
    }
}
