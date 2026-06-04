package server.commands;

import data.Organization;
import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveLowerServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public RemoveLowerServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "remove_lower"; }
    public String getDescription() { return "Remove your organizations whose annual turnover is lower than the given organization"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        Organization org = request.getOrganization();
        List<Long> ids = collectionManager.getCollection().stream()
                .filter(o -> request.getUsername().equals(o.getOwner()))
                .filter(o -> o.compareTo(org) < 0)
                .map(Organization::getId)
                .collect(Collectors.toList());
        int removed = organizationRepository.removeOwnedIds(ids, request.getUsername());
        collectionManager.removeIf(o -> ids.contains(o.getId()));
        return new CommandResponse(true, "Removed " + removed + " owned organizations");
    }
}
