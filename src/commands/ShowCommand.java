package commands;

import managers.CollectionManager;

public class ShowCommand implements Command {
    private final CollectionManager cm;
    public ShowCommand(CollectionManager cm) { this.cm = cm; }
    public void execute(String[] a) { cm.getCollection().forEach(System.out::println); }
    public String getName() { return "show"; }
    public String getDescription() { return "Display all organizations in the collection"; }
}
