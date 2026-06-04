package server.commands;

import data.Organization;
import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class AddIfMinServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public AddIfMinServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "add_if_min"; }
    public String getDescription() { return "Add a new organization if its annual turnover is less than the minimum in collection"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        Organization source = request.getOrganization();
        boolean shouldAdd = collectionManager.getCollection().stream().min(Organization::compareTo)
                .map(min -> source.compareTo(min) < 0)
                .orElse(true);
        if (!shouldAdd) return new CommandResponse(false, "Organization was not lower than the minimum element");
        Organization org = organizationRepository.add(source, request.getUsername());
        collectionManager.add(org);
        return new CommandResponse(true, "Organization added with ID " + org.getId());
    }
}
