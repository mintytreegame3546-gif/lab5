package network;

import data.Organization;

import java.io.Serializable;
import java.util.Arrays;

public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String[] args;
    private final Organization organization;
    private final String username;
    private final String password;

    public CommandRequest(String commandName, String[] args, Organization organization) {
        this(commandName, args, organization, null, null);
    }

    public CommandRequest(String commandName, String[] args, Organization organization, String username, String password) {
        this.commandName = commandName;
        this.args = args == null ? new String[0] : args;
        this.organization = organization;
        this.username = username;
        this.password = password;
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return Arrays.copyOf(args, args.length); }
    public Organization getOrganization() { return organization; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
