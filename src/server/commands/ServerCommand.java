package server.commands;
import network.CommandRequest;
import network.CommandResponse;

public interface ServerCommand {
    String getName();
    String getDescription();
    CommandResponse execute(CommandRequest request) throws Exception;
}
