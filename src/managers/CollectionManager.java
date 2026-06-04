package managers;

import data.Organization;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

    public synchronized Optional<Organization> findById(long id) {
        return collection.stream().filter(o -> o.getId() == id).findFirst();
    }

    public synchronized boolean removeById(long id) {
        return collection.removeIf(o -> o.getId() == id);
    }

    public synchronized int removeIf(Predicate<Organization> predicate) {
        int before = collection.size();
        collection.removeIf(predicate);
        return before - collection.size();
    }

    public synchronized void replace(long id, Organization organization) {
        collection.removeIf(o -> o.getId() == id);
        collection.add(organization);
        collection.sort(Comparator.naturalOrder());
    }

    public synchronized String getInfo() {
        return "Type: LinkedList | Date Created: " + initializationDate + " | Size: " + collection.size();
    }

    public void info() {
        System.out.println(getInfo());
    }
}
