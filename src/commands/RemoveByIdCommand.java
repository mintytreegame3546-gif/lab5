package commands;

import managers.CollectionManager;

public class RemoveByIdCommand implements Command {
    private final CollectionManager cm;
    public RemoveByIdCommand(CollectionManager cm) { this.cm = cm; }
    public void execute(String[] a) {
        if (a.length == 0) {
            System.out.println("Error: Please enter a valid ID.");
            return;
        }
        long id = Long.parseLong(a[0]);
        if (cm.getCollection().removeIf(o -> o.getId() == id)) {
            System.out.println("Organization with ID " + id + " removed");
        } else {
            System.out.println("Error: Organization with ID " + id + " not found!");
        }
    }

    public String getName() { return "remove_by_id"; }
    public String getDescription() { return "Remove an organization by ID"; }
}
