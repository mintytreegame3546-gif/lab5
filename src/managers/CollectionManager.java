package managers;

import data.Organization;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CollectionManager {
    private final LinkedList<Organization> collection = new LinkedList<>();
    private final LocalDateTime initializationDate = LocalDateTime.now();
    private long nextId = 1;

    public void add(Organization org) {
        collection.add(org);
        collection.sort(Comparator.naturalOrder());
    }
    public LinkedList<Organization> getCollection() { return collection; }
    public void clear() { collection.clear(); }
    public long generateId() { return nextId++; }
    public void setNextId(long id) { if (id >= nextId) nextId = id + 1; }

    public String getInfo() {
        return "Type: LinkedList | Date Created: " + initializationDate + " | Size: " + collection.size();
    }

    public void info() {
        System.out.println(getInfo());
    }

    public LinkedList<Organization> sortedByName() {
        return collection.stream()
                .sorted(Comparator.comparing(Organization::getName))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}