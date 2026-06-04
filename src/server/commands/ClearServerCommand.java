package server.commands;

import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class ClearServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public ClearServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "clear"; }
    public String getDescription() { return "Remove your organizations from the collection"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        int removed = organizationRepository.clearOwned(request.getUsername());
        collectionManager.removeIf(o -> request.getUsername().equals(o.getOwner()));
        return new CommandResponse(true, "Removed " + removed + " owned organizations");
    }
}
