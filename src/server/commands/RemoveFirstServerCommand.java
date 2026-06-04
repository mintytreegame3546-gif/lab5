package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.db.DatabaseManager;

import java.util.Optional;

public class RemoveFirstServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public RemoveFirstServerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public String getName() { return "remove_first"; }
    public String getDescription() { return "Remove the first owned element in the collection"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String usernameError = ServerCommandSupport.requireUsername(request);
        if (usernameError != null) return new CommandResponse(false, usernameError);
        Optional<Organization> firstOwned = collectionManager.getCollection().stream()
                .filter(o -> request.getUsername().equals(o.getOwnerUsername()))
                .findFirst();
        if (firstOwned.isEmpty()) return new CommandResponse(false, "No owned organizations to remove");
        long id = firstOwned.get().getId();
        if (databaseManager.deleteOrganization(id, request.getUsername())) {
            collectionManager.removeById(id);
            return new CommandResponse(true, "First owned organization removed");
        }
        return new CommandResponse(false, "Error: you can modify only your own organizations");
    }
}
