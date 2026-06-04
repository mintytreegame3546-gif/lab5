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

    public CommandResponse(boolean success, String message) {
        this(success, message, List.of());
    }

    public CommandResponse(boolean success, String message, List<Organization> organizations) {
        this.success = success;
        this.message = message;
        this.organizations = new ArrayList<>(organizations);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Organization> getOrganizations() { return new ArrayList<>(organizations); }
}
