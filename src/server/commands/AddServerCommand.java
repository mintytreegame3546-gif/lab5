package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class AddServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public AddServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "add"; }
    public String getDescription() { return "Add a new organization"; }

    public CommandResponse execute(CommandRequest request) {
        String validation = ServerCommandSupport.validateOrganization(request.getOrganization());
        if (validation != null) return new CommandResponse(false, validation);
        Organization org = ServerCommandSupport.withServerFields(request.getOrganization(), collectionManager.generateId());
        collectionManager.add(org);
        return new CommandResponse(true, "Organization added with ID " + org.getId());
    }
}
