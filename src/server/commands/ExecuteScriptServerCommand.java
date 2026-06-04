package server.commands;

import network.CommandRequest;
import network.CommandResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecuteScriptServerCommand implements ServerCommand {
    private static final int MAX_RECURSION = 5;
    private final Map<String, ServerCommand> commands;

    public ExecuteScriptServerCommand(Map<String, ServerCommand> commands) {
        this.commands = commands;
    }

    public String getName() { return "execute_script"; }
    public String getDescription() { return "Execute commands from script file"; }

    public CommandResponse execute(CommandRequest request) {
        String[] args = request.getArgs();
        if (args.length == 0) return new CommandResponse(false, "Error: file_name is required");
        String root = args[0];
        Map<String, List<String>> scripts = request.getScripts();
        if (!scripts.containsKey(root)) {
            return new CommandResponse(false, "Error: script content for '" + root + "' was not provided");
        }
        StringBuilder output = new StringBuilder();
        executeScript(root, scripts, 1, new HashSet<>(), output, request);
        String message = output.toString().trim();
        return new CommandResponse(true, message.isEmpty() ? "Script executed" : message);
    }

    private boolean executeScript(String fileName, Map<String, List<String>> scripts, int depth,
                                  Set<String> active, StringBuilder output, CommandRequest request) {
        if (depth > MAX_RECURSION) {
            output.append("Recursion limit exceeded (max 5). Execution stopped.\n");
            return true;
        }
        if (active.contains(fileName)) {
            output.append("Recursion detected for script '").append(fileName).append("'.\n");
            return false;
        }
        List<String> lines = scripts.get(fileName);
        if (lines == null) {
            output.append("Error: script content for '").append(fileName).append("' was not provided\n");
            return false;
        }
        active.add(fileName);
        boolean shouldStop = false;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            String commandName = tokens[0];
            String[] commandArgs = java.util.Arrays.copyOfRange(tokens, 1, tokens.length);
            if ("execute_script".equals(commandName)) {
                if (commandArgs.length == 0) {
                    output.append("Error: file_name is required\n");
                    continue;
                }
                shouldStop = executeScript(commandArgs[0], scripts, depth + 1, active, output, request);
                if (shouldStop) break;
                continue;
            }
            CommandResponse response = executeNestedCommand(commandName, commandArgs, scripts, request);
            output.append(response.getMessage()).append("\n");
        }
        active.remove(fileName);
        return shouldStop;
    }

    private CommandResponse executeNestedCommand(String commandName, String[] args,
                                                  Map<String, List<String>> scripts, CommandRequest request) {
        ServerCommand nested = commands.get(commandName);
        if (nested == null) return new CommandResponse(false, "Error: Unknown command '" + commandName + "'");
        try {
            return nested.execute(new CommandRequest(commandName, args, null, scripts, request.getCredentials()));
        } catch (NumberFormatException e) {
            return new CommandResponse(false, "Error: Please enter a valid number");
        } catch (Exception e) {
            return new CommandResponse(false, "Error executing command: " + e.getMessage());
        }
    }
}
