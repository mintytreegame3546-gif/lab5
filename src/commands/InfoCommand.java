package commands;

import managers.CollectionManager;

public class InfoCommand implements Command {
    private final CollectionManager cm;
    public InfoCommand(CollectionManager cm) { this.cm = cm; }
    public void execute(String[] a) { cm.info(); }
    public String getName() { return "info"; }
    public String getDescription() { return "Display information about the collection"; }
}
