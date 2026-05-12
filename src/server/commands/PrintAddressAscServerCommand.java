package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

import java.util.stream.Collectors;

public class PrintAddressAscServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public PrintAddressAscServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "print_field_ascending_official_address"; }
    public String getDescription() { return "Display official addresses in ascending order"; }

    public CommandResponse execute(CommandRequest request) {
        String result = collectionManager.getCollection().stream()
                .map(Organization::getOfficialAddress)
                .sorted()
                .map(addr -> addr.getStreet() + " " + addr.getZipCode())
                .collect(Collectors.joining("\n"));
        return new CommandResponse(true, result.isEmpty() ? "No addresses" : result);
    }
}
