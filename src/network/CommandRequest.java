package network;

import data.Organization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializable request object transported from client to server over UDP.
 */
public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String[] args;
    private final Organization organization;
    private final Map<String, List<String>> scripts;
    private final Credentials credentials;

    /**
     * Creates a request without script content or credentials.
     *
     * @param commandName command name
     * @param args command arguments
     * @param organization optional organization payload
     */
    public CommandRequest(String commandName, String[] args, Organization organization) {
        this(commandName, args, organization, Map.of(), Credentials.empty());
    }

    /**
     * Creates a request with script content and no credentials.
     *
     * @param commandName command name
     * @param args command arguments
     * @param organization optional organization payload
     * @param scripts bundled script contents
     */
    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts) {
        this(commandName, args, organization, scripts, Credentials.empty());
    }

    /**
     * Creates a request with script content and raw credential fields.
     *
     * @param commandName command name
     * @param args command arguments
     * @param organization optional organization payload
     * @param scripts bundled script contents
     * @param username username sent with the request
     * @param password password sent with the request
     */
    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts, String username, String password) {
        this(commandName, args, organization, scripts, Credentials.of(username, password));
    }

    /**
     * Creates a request with script content and credentials.
     *
     * @param commandName command name
     * @param args command arguments
     * @param organization optional organization payload
     * @param scripts bundled script contents
     * @param credentials user credentials
     */
    public CommandRequest(String commandName, String[] args, Organization organization,
                          Map<String, List<String>> scripts, Credentials credentials) {
        this.commandName = commandName;
        this.args = args == null ? new String[0] : Arrays.copyOf(args, args.length);
        this.organization = organization;
        this.scripts = scripts == null ? Map.of() : new HashMap<>(scripts);
        this.credentials = credentials == null ? Credentials.empty() : credentials;
    }

    /**
     * @return command name
     */
    public String getCommandName() { return commandName; }

    /**
     * @return defensive copy of command arguments
     */
    public String[] getArgs() { return Arrays.copyOf(args, args.length); }

    /**
     * @return optional organization payload
     */
    public Organization getOrganization() { return organization; }

    /**
     * @return defensive copy of bundled script contents
     */
    public Map<String, List<String>> getScripts() { return new HashMap<>(scripts); }

    /**
     * @return request credentials
     */
    public Credentials getCredentials() { return credentials; }

    /**
     * @return username sent with request
     */
    public String getUsername() { return credentials.username(); }

    /**
     * @return password sent with request
     */
    public String getPassword() { return credentials.password(); }
}
