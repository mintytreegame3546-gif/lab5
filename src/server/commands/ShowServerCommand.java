package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShowServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public ShowServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "show"; }
    public String getDescription() { return "Display all organizations in the collection"; }

    public CommandResponse execute(CommandRequest request) {
        List<Organization> sorted = collectionManager.getCollection().stream()
                .sorted(Comparator.comparing(Organization::getName))
                .collect(Collectors.toList());
        if (sorted.isEmpty()) return new CommandResponse(true, "Collection is empty", sorted);
        String message = sorted.stream().map(Organization::toString).collect(Collectors.joining("\n"));
        return new CommandResponse(true, message, sorted);
    }
}
