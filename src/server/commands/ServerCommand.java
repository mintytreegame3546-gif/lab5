package server.commands;

import network.CommandRequest;
import network.CommandResponse;

/**
 * Common contract for all server-side commands.
 */
public interface ServerCommand {
    /**
     * @return command name entered by the client
     */
    String getName();

    /**
     * @return user-facing command description
     */
    String getDescription();

    /**
     * Executes a command request.
     *
     * @param request client request
     * @return command response
     * @throws Exception when command execution fails
     */
    CommandResponse execute(CommandRequest request) throws Exception;
}
