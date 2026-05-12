package commands;

import managers.CollectionManager;
import managers.InputManager;

public class UpdateCommand implements Command {
    private final CollectionManager cm;
    private final InputManager im;
    public UpdateCommand(CollectionManager cm, InputManager im) { this.cm = cm; this.im = im; }
    public void execute(String[] a) {
        if (a.length == 0) {
            System.out.println("Error Please enter a valid ID");
            return;
        }
        long id = Long.parseLong(a[0]);
        boolean exists = cm.getCollection().stream().anyMatch(o -> o.getId() == id);
        if (exists) {
            cm.getCollection().removeIf(o -> o.getId() == id);
            cm.add(im.readOrganization(id));
            System.out.println("Organization with ID " + id + " updated!");
        } else {
            System.out.println("Error Organization with ID " + id + " not found!");
        }
    }

    public String getName() { return "update"; }
    public String getDescription() { return "Update an organization by ID"; }
}
