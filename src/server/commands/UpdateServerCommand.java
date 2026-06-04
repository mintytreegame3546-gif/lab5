package server.commands;

import data.Organization;
import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class UpdateServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public UpdateServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "update"; }
    public String getDescription() { return "Update an organization by ID"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID");
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid ID");
        }
        Organization current = collectionManager.getCollection().stream().filter(o -> o.getId() == id).findFirst().orElse(null);
        if (current == null) return new CommandResponse(false, "Error Organization with ID " + id + " not found!");
        if (!request.getUsername().equals(current.getOwner())) return new CommandResponse(false, "Error: You can modify only your own organizations");
        if (request.getOrganization() == null) return new CommandResponse(true, "ID is valid");
        Organization updated = organizationRepository.update(id, request.getOrganization(), request.getUsername());
        if (updated == null) return new CommandResponse(false, "Error: database update failed");
        collectionManager.removeIf(o -> o.getId() == id);
        collectionManager.add(updated);
        return new CommandResponse(true, "Organization with ID " + id + " updated!");
    }
}
