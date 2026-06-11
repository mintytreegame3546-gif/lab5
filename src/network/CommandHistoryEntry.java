package network;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CommandHistoryEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String arguments;
    private final LocalDateTime executedAt;

    public CommandHistoryEntry(String commandName, String arguments, LocalDateTime executedAt) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.executedAt = executedAt;
    }

    public String getCommandName() { return commandName; }
    public String getArguments() { return arguments; }
    public LocalDateTime getExecutedAt() { return executedAt; }
}
