package commands;
import data.Organization;
import managers.CollectionManager;
import managers.InputManager;

import java.util.Collections;

public class AddIfMinCommand implements Command {
    private final CollectionManager cm; private final InputManager im;
    public AddIfMinCommand(CollectionManager cm, InputManager im) { this.cm = cm; this.im = im; }
    public void execute(String[] a) {
        Organization org = im.readOrganization(cm.generateId());
        if (cm.getCollection().isEmpty() || org.compareTo(Collections.min(cm.getCollection())) < 0) cm.add(org);
    }
    public String getName() { return "add_if_min"; }
    public String getDescription() { return "Add a new organization if its annual turnover is less than the minimum in collection"; }
}
