package managers;

import data.Organization;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class CollectionManager {
    private final LinkedList<Organization> collection = new LinkedList<>();
    private final LocalDateTime initializationDate = LocalDateTime.now();

    public synchronized void add(Organization org) {
        collection.add(org);
        collection.sort(Comparator.naturalOrder());
    }

    public synchronized void addAll(List<Organization> organizations) {
        collection.clear();
        collection.addAll(organizations);
        collection.sort(Comparator.naturalOrder());
    }

    public synchronized LinkedList<Organization> getCollection() { return new LinkedList<>(collection); }
    public synchronized boolean isEmpty() { return collection.isEmpty(); }
    public synchronized void clear() { collection.clear(); }
    public synchronized long size() { return collection.size(); }
    public synchronized boolean removeIf(Predicate<Organization> predicate) { return collection.removeIf(predicate); }
    public synchronized Organization removeFirst() { return collection.removeFirst(); }

    public synchronized String getInfo() {
        return "Type: LinkedList | Date Created: " + initializationDate + " | Size: " + collection.size();
    }

    public void info() {
        System.out.println(getInfo());
    }
}
