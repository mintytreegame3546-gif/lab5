package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FilterNameServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public FilterNameServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "filter_contains_name"; }
    public String getDescription() { return "Display organizations whose name contains the given substring"; }

    public CommandResponse execute(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter name for filtering!");
        String namePart = args[0].toLowerCase();
        List<Organization> found = collectionManager.getCollection().stream()
                .filter(o -> o.getName().toLowerCase().contains(namePart))
                .sorted(Comparator.comparing(Organization::getName))
                .collect(Collectors.toList());
        if (found.isEmpty()) return new CommandResponse(false, "Name not found", found);
        String message = found.stream().map(Organization::toString).collect(Collectors.joining("\n"));
        return new CommandResponse(true, message, found);
    }
}
