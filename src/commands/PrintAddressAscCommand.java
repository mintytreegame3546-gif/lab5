package commands;

import data.Organization;
import managers.CollectionManager;

public class PrintAddressAscCommand implements Command {
    private final CollectionManager cm;

    public PrintAddressAscCommand(CollectionManager cm) {
        this.cm = cm;
    }

    public void execute(String[] a) {
        cm.getCollection().stream().map(Organization::getOfficialAddress).sorted().forEach(addr ->
                System.out.println(addr.getStreet() + " " + addr.getZipCode()));
    }

    public String getName() {
        return "print_field_ascending_official_address";
    }

    public String getDescription() {
        return "Display unique official addresses in ascending order";
    }
}
