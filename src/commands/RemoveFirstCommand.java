package commands;

import managers.CollectionManager;

public class RemoveFirstCommand implements Command {
    private final CollectionManager cm;

    public RemoveFirstCommand(CollectionManager cm) {
        this.cm = cm;
    }

    public void execute(String[] a) {
        if (!cm.getCollection().isEmpty()) cm.getCollection().removeFirst();
    }

    public String getName() {
        return "remove_first";
    }

    public String getDescription() {
        return "Remove the first element in the collection";
    }
}
