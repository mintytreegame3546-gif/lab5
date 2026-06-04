package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;
import server.db.DatabaseManager;

public class AddIfMinServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public AddIfMinServerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public String getName() { return "add_if_min"; }
    public String getDescription() { return "Add a new organization if it is lower than the minimum"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String usernameError = ServerCommandSupport.requireUsername(request);
        if (usernameError != null) return new CommandResponse(false, usernameError);
        String validation = ServerCommandSupport.validateOrganization(request.getOrganization());
        if (validation != null) return new CommandResponse(false, validation);
        Organization candidate = request.getOrganization();
        boolean shouldAdd = collectionManager.getCollection().stream().min(Organization::compareTo)
                .map(min -> candidate.compareTo(min) < 0)
                .orElse(true);
        if (!shouldAdd) return new CommandResponse(false, "Organization was not lower than the minimum element");
        Organization org = databaseManager.insertOrganization(candidate, request.getUsername());
        collectionManager.add(org);
        return new CommandResponse(true, "Organization added with ID " + org.getId());
    }
}
