package commands;

import managers.CollectionManager;

public class ClearCommand implements Command {
    private final CollectionManager cm;
    public ClearCommand(CollectionManager cm) { this.cm = cm; }
    public void execute(String[] a) { cm.clear(); }
    public String getName() { return "clear"; }
    public String getDescription() { return "Clear the collection"; }
}
