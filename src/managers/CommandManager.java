package managers;

import commands.Command;

import java.util.*;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, Integer> scriptStack = new HashMap<>();

    public void register(Command c) { commands.put(c.getName(), c); }

    public void execute(String input) {
        if (input.trim().isEmpty()) return;
        String[] tokens = input.trim().split("\\s+");
        Command c = commands.get(tokens[0]);
        if (c != null) {
            try {
                c.execute(Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (NumberFormatException e) {
                System.out.println("Error: PLease enter a valid command");
            } catch (Exception e) {
                System.out.println("Error loading commands: " + e.getMessage());
            }
        } else System.out.println("Error: Unknown command. Enter 'help' for available commands");
    }
    public Map<String, Command> getCommands() { return commands; }
    public Map<String, Integer> getScriptStack() { return scriptStack; }
}

