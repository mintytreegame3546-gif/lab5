package commands;
import managers.CommandManager;

import java.io.File;
import java.util.Scanner;

public class ExecuteScriptCommand implements Command {
    private final CommandManager cm; public ExecuteScriptCommand(CommandManager cm) { this.cm = cm; }
    public void execute(String[] a) {
        if (a.length == 0) { System.out.println("Error: Please enter file name!"); return; }
        String name = a[0];
        int depth = cm.getScriptStack().getOrDefault(name, 0);
        if (depth >= 5) { System.out.println("Recursion Error: Maximum script depth (5) reached." + name); return; }

        cm.getScriptStack().put(name, depth + 1);
        File f = new File(name);

        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) cm.execute(s.nextLine());
        } catch (Exception e) { System.out.println("Script Error: " + e.getMessage()); }
        finally { cm.getScriptStack().put(name, cm.getScriptStack().get(name) - 1); }
    }
    public String getName() { return "execute_script"; }
    public String getDescription() { return "Execute commands from a script file"; }
}
