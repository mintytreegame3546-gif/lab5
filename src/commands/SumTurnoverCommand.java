package commands;

import data.Organization;
import managers.CollectionManager;

public class SumTurnoverCommand implements Command {
    private final CollectionManager cm;
    public SumTurnoverCommand(CollectionManager cm) { this.cm = cm; }
    public void execute(String[] a) {
        double sum = cm.getCollection().stream().mapToDouble(Organization::getAnnualTurnover).sum();
        System.out.println("total sum: " + sum);
    }
    public String getName() { return "sum_of_annual_turnover"; }
    public String getDescription() { return "Display the sum of annual turnovers of all organizations"; }
}
