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
        if (!scripts.containsKey(root)) return new CommandResponse(false, "Error: script content for '" + root + "' was not provided");
        StringBuilder output = new StringBuilder();
        executeScript(root, scripts, 1, new HashSet<>(), output);
        return new CommandResponse(true, output.toString().trim().isEmpty() ? "Script executed" : output.toString().trim());
    }

    private void executeScript(String fileName, Map<String, List<String>> scripts, int depth, Set<String> active, StringBuilder output) {
        if (depth > MAX_RECURSION) {
            output.append("Recursion limit exceeded (max 5). Execution stopped.\n");
            return;
        }
        if (active.contains(fileName)) {
            output.append("Recursion detected for script '").append(fileName).append("'.\n");
            return;
        }
        List<String> lines = scripts.get(fileName);
        if (lines == null) {
            output.append("Error: script content for '").append(fileName).append("' was not provided\n");
            return;
        }
        active.add(fileName);
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
                executeScript(commandArgs[0], scripts, depth + 1, active, output);
                if (output.indexOf("Recursion limit exceeded") >= 0) break;
                continue;
            }
            ServerCommand nested = commands.get(commandName);
            if (nested == null) {
                output.append("Error: Unknown command '").append(commandName).append("'\n");
                continue;
            }
            CommandResponse response = nested.execute(new CommandRequest(commandName, commandArgs, null, scripts));
            output.append(response.getMessage()).append("\n");
        }
        active.remove(fileName);
    }
}
