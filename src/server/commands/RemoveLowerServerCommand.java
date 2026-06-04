package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.db.DatabaseManager;

public class RemoveLowerServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public RemoveLowerServerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public String getName() { return "remove_lower"; }
    public String getDescription() { return "Remove owned organizations lower than the given organization"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String usernameError = ServerCommandSupport.requireUsername(request);
        if (usernameError != null) return new CommandResponse(false, usernameError);
        String validation = ServerCommandSupport.validateOrganization(request.getOrganization());
        if (validation != null) return new CommandResponse(false, validation);
        Organization org = request.getOrganization();
        int removed = 0;
        for (Organization existing : collectionManager.getCollection()) {
            if (request.getUsername().equals(existing.getOwnerUsername()) && existing.compareTo(org) < 0
                    && databaseManager.deleteOrganization(existing.getId(), request.getUsername())) {
                collectionManager.removeById(existing.getId());
                removed++;
            }
        }
        return new CommandResponse(true, "Removed " + removed + " organizations");
    }
}
