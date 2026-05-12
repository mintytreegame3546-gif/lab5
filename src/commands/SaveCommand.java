package commands;

import managers.CollectionManager;
import managers.FileManager;

public class SaveCommand implements Command {
    private final CollectionManager cm;
    private final FileManager fm;

    public SaveCommand(CollectionManager cm, FileManager fm) {
        this.cm = cm;
        this.fm = fm;
    }

    public void execute(String[] a) {
        fm.save(cm.getCollection());
    }

    public String getName() {
        return "save";
    }

    public String getDescription() {
        return "Save the collection to a file";
    }
}
