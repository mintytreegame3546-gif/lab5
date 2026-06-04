package server.commands;

import data.Organization;
import managers.CollectionManager;
import network.CommandRequest;
import network.CommandResponse;

public class SumTurnoverServerCommand implements ServerCommand {
    private final CollectionManager collectionManager;

    public SumTurnoverServerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String getName() { return "sum_of_annual_turnover"; }
    public String getDescription() { return "Display the sum of annual turnovers of all organizations"; }

    public CommandResponse execute(CommandRequest request) {
        double sum = collectionManager.getCollection().stream().mapToDouble(Organization::getAnnualTurnover).sum();
        return new CommandResponse(true, "total sum: " + sum);
    }
}
