package commands;

public interface Command {
    void execute(String[] args);
    String getName();
    String getDescription();
}