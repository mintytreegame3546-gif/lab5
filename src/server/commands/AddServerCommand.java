package server.commands;

import data.Organization;
import database.OrganizationRepository;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class AddServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;
    private final OrganizationRepository organizationRepository;

    public AddServerCommand(CollectionManager collectionManager, OrganizationRepository organizationRepository) {
        this.collectionManager = collectionManager;
        this.organizationRepository = organizationRepository;
    }

    public String getName() { return "add"; }
    public String getDescription() { return "Add a new organization"; }

    public CommandResponse execute(CommandRequest request) throws Exception {
        Organization org = organizationRepository.add(request.getOrganization(), request.getUsername());
        collectionManager.add(org);
        return new CommandResponse(true, "Organization added with ID " + org.getId());
    }
}
