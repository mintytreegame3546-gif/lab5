package network;

import java.io.Serializable;

/**
 * Immutable username/password pair sent with every client request for user identification.
 * Empty strings represent missing credentials and avoid passing null values through the network protocol.
 *
 * @param username user login name
 * @param password user password in plain text before server-side hashing
 */
public record Credentials(String username, String password) implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Credentials EMPTY = new Credentials("", "");

    /**
     * Creates a credential pair and normalizes null values to empty strings.
     *
     * @param username user login name
     * @param password user password
     */
    public Credentials {
        username = username == null ? "" : username;
        password = password == null ? "" : password;
    }

    /**
     * Creates credentials from possibly nullable values.
     *
     * @param username user login name
     * @param password user password
     * @return normalized credentials
     */
    public static Credentials of(String username, String password) {
        return new Credentials(username, password);
    }

    /**
     * Returns reusable empty credentials.
     *
     * @return empty credential pair
     */
    public static Credentials empty() {
        return EMPTY;
    }

    /**
     * Checks whether both fields contain non-blank values.
     *
     * @return true when username and password are present
     */
    public boolean isComplete() {
        return !username.isBlank() && !password.isBlank();
    }
}
