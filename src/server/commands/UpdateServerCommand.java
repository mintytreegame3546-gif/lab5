package server.commands;

import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class UpdateServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public UpdateServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "update"; }
    public String getDescription() { return "Update an organization by ID"; }

    public CommandResponse execute(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID");
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid ID");
        }
        boolean exists = collectionManager.getCollection().stream().anyMatch(o -> o.getId() == id);
        if (!exists) return new CommandResponse(false, "Error Organization with ID " + id + " not found!");
        if (request.getOrganization() == null) return new CommandResponse(true, "ID is valid");
        String validation = ServerCommandSupport.validateOrganization(request.getOrganization());
        if (validation != null) return new CommandResponse(false, validation);
        collectionManager.getCollection().removeIf(o -> o.getId() == id);
        collectionManager.add(ServerCommandSupport.withServerFields(request.getOrganization(), id));
        return new CommandResponse(true, "Organization with ID " + id + " updated!");
    }
}
