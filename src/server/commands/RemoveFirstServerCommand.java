package server.commands;

import data.Organization;
import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class RemoveFirstServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public RemoveFirstServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "remove_first"; }
    public String getDescription() { return "Remove your first organization in the collection"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        Organization firstOwned = collectionManager.getCollection().stream()
                .filter(o -> request.getUsername().equals(o.getOwner()))
                .findFirst().orElse(null);
        if (firstOwned == null) return new CommandResponse(false, "You have no organizations to remove");
        if (organizationRepository.removeById(firstOwned.getId(), request.getUsername())) {
            collectionManager.removeIf(o -> o.getId() == firstOwned.getId());
            return new CommandResponse(true, "First owned organization removed");
        }
        return new CommandResponse(false, "Error: database remove failed");
    }
}
