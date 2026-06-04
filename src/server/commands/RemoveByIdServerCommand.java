package server.commands;

import data.Organization;
import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class RemoveByIdServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public RemoveByIdServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "remove_by_id"; }
    public String getDescription() { return "Remove an organization by ID"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID.");
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid ID.");
        }
        Organization current = collectionManager.getCollection().stream().filter(o -> o.getId() == id).findFirst().orElse(null);
        if (current == null) return new CommandResponse(false, "Error: Organization with ID " + id + " not found!");
        if (!request.getUsername().equals(current.getOwner())) return new CommandResponse(false, "Error: You can modify only your own organizations");
        if (organizationRepository.removeById(id, request.getUsername())) {
            collectionManager.removeIf(o -> o.getId() == id);
            return new CommandResponse(true, "Organization with ID " + id + " removed");
        }
        return new CommandResponse(false, "Error: database remove failed");
    }
}
