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
    private final Credentials credentials;

    public CommandRequest(String commandName, String[] args, Organization organization) {
        this(commandName, args, organization, Map.of(), Credentials.empty());
    }

    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts) {
        this(commandName, args, organization, scripts, Credentials.empty());
    }
  
    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts, String username, String password) {
        this(commandName, args, organization, scripts, Credentials.of(username, password));
    }

    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts, Credentials credentials) {
        this.commandName = commandName;
        this.args = args == null ? new String[0] : Arrays.copyOf(args, args.length);
        this.organization = organization;
        this.scripts = scripts == null ? Map.of() : new HashMap<>(scripts);
        this.credentials = credentials == null ? Credentials.empty() : credentials;
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return Arrays.copyOf(args, args.length); }
    public Organization getOrganization() { return organization; }
    public Map<String, List<String>> getScripts() { return new HashMap<>(scripts); }
    public Credentials getCredentials() { return credentials; }
    public String getUsername() { return credentials.username(); }
    public String getPassword() { return credentials.password(); }
}
