package network;

import data.Organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommandResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final List<Organization> organizations;
    private final List<CommandHistoryEntry> history;
    private final boolean push;

    public CommandResponse(boolean success, String message) {
        this(success, message, List.of(), false);
    }

    public CommandResponse(boolean success, String message, List<Organization> organizations) {
        this(success, message, organizations, List.of(), false);
    }

    public CommandResponse(boolean success, String message, List<Organization> organizations, boolean push) {
        this(success, message, organizations, List.of(), push);
    }

    public CommandResponse(boolean success, String message, List<Organization> organizations,
                           List<CommandHistoryEntry> history) {
        this(success, message, organizations, history, false);
    }

    public CommandResponse(boolean success, String message, List<Organization> organizations,
                           List<CommandHistoryEntry> history, boolean push) {
        this.success = success;
        this.message = message;
        this.organizations = new ArrayList<>(organizations);
        this.history = new ArrayList<>(history);
        this.push = push;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Organization> getOrganizations() { return new ArrayList<>(organizations); }
    public List<CommandHistoryEntry> getHistory() { return new ArrayList<>(history); }
    public boolean isPush() { return push; }
}
