package server.commands;

import network.CommandRequest;
import network.CommandResponse;

import java.util.Map;
import java.util.stream.Collectors;

public class HelpServerCommand implements ServerCommand {
    private final Map<String, ServerCommand> commands;

    public HelpServerCommand(Map<String, ServerCommand> commands) {
        this.commands = commands;
    }

    public String getName() { return "help"; }
    public String getDescription() { return "Display all available commands"; }

    public CommandResponse execute(CommandRequest request) {
        return new CommandResponse(true, "=====AVAILABLE COMMANDS====\n" + commands.values().stream()
                .sorted((left, right) -> left.getName().compareTo(right.getName()))
                .map(command -> command.getName() + ": " + command.getDescription())
                .collect(Collectors.joining("\n")));
    }
}
