package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class AddIfMinServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public AddIfMinServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "add_if_min"; }
    public String getDescription() { return "Add a new organization if its annual turnover is less than the minimum in collection"; }

    public CommandResponse execute(CommandRequest request) {
        String validation = ServerCommandSupport.validateOrganization(request.getOrganization());
        if (validation != null) return new CommandResponse(false, validation);
        Organization org = ServerCommandSupport.withServerFields(request.getOrganization(), collectionManager.generateId());
        boolean added = collectionManager.getCollection().stream().min(Organization::compareTo)
                .map(min -> org.compareTo(min) < 0)
                .orElse(true);
        if (added) {
            collectionManager.add(org);
            return new CommandResponse(true, "Organization added with ID " + org.getId());
        }
        return new CommandResponse(false, "Organization was not lower than the minimum element");
    }
}
