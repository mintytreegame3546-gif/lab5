package network;

import data.Organization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String[] args;
    private final Organization organization;
    private final Map<String, List<String>> scripts;

    public CommandRequest(String commandName, String[] args, Organization organization) {
        this(commandName, args, organization, Map.of());
    }

    public CommandRequest(String commandName, String[] args, Organization organization, Map<String, List<String>> scripts) {
        this.commandName = commandName;
        this.args = args == null ? new String[0] : args;
        this.organization = organization;
        this.scripts = scripts == null ? Map.of() : new HashMap<>(scripts);
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return Arrays.copyOf(args, args.length); }
    public Organization getOrganization() { return organization; }
    public Map<String, List<String>> getScripts() { return new HashMap<>(scripts); }
}
