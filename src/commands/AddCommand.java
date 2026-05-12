package commands;

import managers.CollectionManager;
import managers.InputManager;

public class AddCommand implements Command {
    private final CollectionManager cm; private final InputManager im;
    public AddCommand(CollectionManager cm, InputManager im) { this.cm = cm; this.im = im; }
    public void execute(String[] a) { cm.add(im.readOrganization(cm.generateId())); }
    public String getName() { return "add"; }
    public String getDescription() { return "Add a new organization"; }
}
