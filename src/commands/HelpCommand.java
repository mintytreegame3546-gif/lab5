package commands;

import managers.CommandManager;

public class HelpCommand implements Command {
    private final CommandManager cm;

    public HelpCommand(CommandManager cm) {
        this.cm = cm;
    }

    public void execute(String[] a) {
        System.out.println("=====AVAILABLE COMMANDS====");
        cm.getCommands().values().forEach(c -> System.out.println(c.getName() + ": " + c.getDescription()));
    }

    public String getName() {
        return "help";
    }

    public String getDescription() {
        return "Display all available commands";
    }
}
