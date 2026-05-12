package commands;

public class ExitCommand implements Command {
    public void execute(String[] a) { System.out.println("Goodbye"); System.exit(0); }
    public String getName() { return "exit"; }
    public String getDescription() { return "Exit the program"; }
}
