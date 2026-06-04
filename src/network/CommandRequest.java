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
    private final String username;
    private final String password;

    public CommandRequest(String commandName, String[] args, Organization organization) {
        this(commandName, args, organization, Map.of(), null, null);
    }

    public CommandRequest(String commandName, String[] args, Organization organization, Map<String, List<String>> scripts) {
        this(commandName, args, organization, scripts, null, null);
    }

    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts, String username, String password) {
        this.commandName = commandName;
        this.args = args == null ? new String[0] : Arrays.copyOf(args, args.length);
        this.organization = organization;
        this.scripts = scripts == null ? Map.of() : new HashMap<>(scripts);
        this.username = username;
        this.password = password;
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return Arrays.copyOf(args, args.length); }
    public Organization getOrganization() { return organization; }
    public Map<String, List<String>> getScripts() { return new HashMap<>(scripts); }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
