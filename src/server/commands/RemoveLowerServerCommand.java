package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class RemoveLowerServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public RemoveLowerServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "remove_lower"; }
    public String getDescription() { return "Remove all organizations whose annual turnover is lower than the given organization"; }

    public CommandResponse execute(CommandRequest request) {
        Organization org = request.getOrganization();
        long before = collectionManager.getCollection().size();
        collectionManager.getCollection().removeIf(o -> o.compareTo(org) < 0);
        return new CommandResponse(true, "Removed " + (before - collectionManager.getCollection().size()) + " organizations");
    }
}
