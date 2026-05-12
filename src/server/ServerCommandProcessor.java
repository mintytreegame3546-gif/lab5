package server;

import data.Address;
import data.Coordinates;
import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServerCommandProcessor {
    private final CollectionManager collectionManager;
    private final Map<String, Function<CommandRequest, CommandResponse>> handlers;

    public ServerCommandProcessor(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        this.handlers = Map.ofEntries(
                Map.entry("help", this::help),
                Map.entry("info", this::info),
                Map.entry("show", this::show),
                Map.entry("add", this::add),
                Map.entry("update", this::update),
                Map.entry("remove_by_id", this::removeById),
                Map.entry("clear", this::clear),
                Map.entry("remove_first", this::removeFirst),
                Map.entry("add_if_min", this::addIfMin),
                Map.entry("remove_lower", this::removeLower),
                Map.entry("sum_of_annual_turnover", this::sumTurnover),
                Map.entry("filter_contains_name", this::filterName),
                Map.entry("print_field_ascending_official_address", this::printAddressAsc)
        );
    }

    public CommandResponse process(CommandRequest request) {
        Function<CommandRequest, CommandResponse> handler = handlers.get(request.getCommandName());
        if (handler == null) return new CommandResponse(false, "Error: Unknown command. Enter 'help' for available commands");
        try {
            return handler.apply(request);
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid number");
        } catch (Exception e) {
            return new CommandResponse(false, "Error executing command: " + e.getMessage());
        }
    }

    private CommandResponse help(CommandRequest request) {
        return new CommandResponse(true, "=====AVAILABLE COMMANDS====\n" + handlers.keySet().stream()
                .sorted()
                .map(name -> name + ": " + description(name))
                .collect(Collectors.joining("\n")));
    }

    private String description(String name) {
        return switch (name) {
            case "add" -> "Add a new organization";
            case "add_if_min" -> "Add a new organization if its annual turnover is less than the minimum in collection";
            case "clear" -> "Clear the collection";
            case "filter_contains_name" -> "Display organizations whose name contains the given substring";
            case "help" -> "Display all available commands";
            case "info" -> "Display information about the collection";
            case "print_field_ascending_official_address" -> "Display official addresses in ascending order";
            case "remove_by_id" -> "Remove an organization by ID";
            case "remove_first" -> "Remove the first element in the collection";
            case "remove_lower" -> "Remove all organizations whose annual turnover is lower than the given organization";
            case "show" -> "Display all organizations in the collection";
            case "sum_of_annual_turnover" -> "Display the sum of annual turnovers of all organizations";
            case "update" -> "Update an organization by ID";
            default -> "";
        };
    }

    private CommandResponse info(CommandRequest request) {
        return new CommandResponse(true, collectionManager.getInfo());
    }

    private CommandResponse show(CommandRequest request) {
        List<Organization> sorted = collectionManager.getCollection().stream()
                .sorted(Comparator.comparing(Organization::getName))
                .collect(Collectors.toList());
        if (sorted.isEmpty()) return new CommandResponse(true, "Collection is empty", sorted);
        return new CommandResponse(true, sorted.stream().map(Organization::toString).collect(Collectors.joining("\n")), sorted);
    }

    private CommandResponse add(CommandRequest request) {
        Organization org = withServerFields(request.getOrganization(), collectionManager.generateId());
        collectionManager.add(org);
        return new CommandResponse(true, "Organization added with ID " + org.getId());
    }

    private CommandResponse update(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error Please enter a valid ID");
        long id = Long.parseLong(args[0]);
        boolean exists = collectionManager.getCollection().stream().anyMatch(o -> o.getId() == id);
        if (!exists) return new CommandResponse(false, "Error Organization with ID " + id + " not found!");
        collectionManager.getCollection().removeIf(o -> o.getId() == id);
        collectionManager.add(withServerFields(request.getOrganization(), id));
        return new CommandResponse(true, "Organization with ID " + id + " updated!");
    }

    private CommandResponse removeById(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter a valid ID.");
        long id = Long.parseLong(args[0]);
        if (collectionManager.getCollection().removeIf(o -> o.getId() == id)) {
            return new CommandResponse(true, "Organization with ID " + id + " removed");
        }
        return new CommandResponse(false, "Error: Organization with ID " + id + " not found!");
    }

    private CommandResponse clear(CommandRequest request) {
        collectionManager.clear();
        return new CommandResponse(true, "Collection cleared");
    }

    private CommandResponse removeFirst(CommandRequest request) {
        if (collectionManager.getCollection().isEmpty()) return new CommandResponse(false, "Collection is empty");
        collectionManager.getCollection().removeFirst();
        return new CommandResponse(true, "First organization removed");
    }

    private CommandResponse addIfMin(CommandRequest request) {
        Organization org = withServerFields(request.getOrganization(), collectionManager.generateId());
        boolean added = collectionManager.getCollection().stream().min(Organization::compareTo)
                .map(min -> org.compareTo(min) < 0)
                .orElse(true);
        if (added) {
            collectionManager.add(org);
            return new CommandResponse(true, "Organization added with ID " + org.getId());
        }
        return new CommandResponse(false, "Organization was not lower than the minimum element");
    }

    private CommandResponse removeLower(CommandRequest request) {
        Organization org = request.getOrganization();
        long before = collectionManager.getCollection().size();
        collectionManager.getCollection().removeIf(o -> o.compareTo(org) < 0);
        return new CommandResponse(true, "Removed " + (before - collectionManager.getCollection().size()) + " organizations");
    }

    private CommandResponse sumTurnover(CommandRequest request) {
        double sum = collectionManager.getCollection().stream().mapToDouble(Organization::getAnnualTurnover).sum();
        return new CommandResponse(true, "total sum: " + sum);
    }

    private CommandResponse filterName(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: Please enter name for filtering!");
        String namePart = args[0].toLowerCase();
        List<Organization> found = collectionManager.getCollection().stream()
                .filter(o -> o.getName().toLowerCase().contains(namePart))
                .sorted(Comparator.comparing(Organization::getName))
                .collect(Collectors.toList());
        if (found.isEmpty()) return new CommandResponse(false, "Name not found", found);
        return new CommandResponse(true, found.stream().map(Organization::toString).collect(Collectors.joining("\n")), found);
    }

    private CommandResponse printAddressAsc(CommandRequest request) {
        String result = collectionManager.getCollection().stream()
                .map(Organization::getOfficialAddress)
                .sorted()
                .map(addr -> addr.getStreet() + " " + addr.getZipCode())
                .collect(Collectors.joining("\n"));
        return new CommandResponse(true, result.isEmpty() ? "No addresses" : result);
    }

    private Organization withServerFields(Organization source, long id) {
        return new Organization(id, source.getName(),
                new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()),
                LocalDateTime.now(), source.getAnnualTurnover(), source.getType(),
                new Address(source.getOfficialAddress().getStreet(), source.getOfficialAddress().getZipCode()));
    }
}
