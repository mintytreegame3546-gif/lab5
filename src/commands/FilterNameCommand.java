package commands;

import managers.CollectionManager;

public class FilterNameCommand implements Command {
    private final CollectionManager cm;
    public FilterNameCommand(CollectionManager cm) { this.cm = cm; }
    public void execute(String[] a) {
        if (a.length == 0) {
            System.out.println("Error: Please enter name for filtering!");
            return;
        }
        String namePart = a[0].toLowerCase();
        long count = cm.getCollection().stream()
                .filter(o -> o.getName().toLowerCase().contains(namePart))
                .peek(System.out::println)
                .count();
        if (count == 0) System.out.println("Name not found");
    }
    public String getName() { return "filter_contains_name"; }
    public String getDescription() { return "Display organizations whose name contains the given substring"; }
}
