package commands;

import data.Organization;
import managers.CollectionManager;
import managers.InputManager;

public class RemoveLowerCommand implements Command {
    private final CollectionManager cm;
    private final InputManager im;

    public RemoveLowerCommand(CollectionManager cm, InputManager im) {
        this.cm = cm;
        this.im = im;
    }

    public void execute(String[] a) {
        Organization org = im.readOrganization(0);
        cm.getCollection().removeIf(o -> o.compareTo(org) < 0);
    }

    public String getName() {
        return "remove_lower";
    }

    public String getDescription() {
        return "Remove all organizations whose annual turnover is lower than the given organization";
    }
}
