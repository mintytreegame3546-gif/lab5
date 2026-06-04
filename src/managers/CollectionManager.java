package managers;

import data.Organization;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Thread-safe in-memory collection storage used by server read commands.
 */
public class CollectionManager {
    private final List<Organization> collection = new LinkedList<>();
    private final LocalDateTime initializationDate = LocalDateTime.now();

    /**
     * Adds an organization and keeps natural ordering.
     *
     * @param organization organization to add
     */
    public synchronized void add(Organization organization) {
        collection.add(organization);
        collection.sort(Comparator.naturalOrder());
    }

    /**
     * Replaces the current in-memory state with database-loaded organizations.
     *
     * @param organizations organizations loaded from persistent storage
     */
    public synchronized void addAll(List<Organization> organizations) {
        collection.clear();
        collection.addAll(organizations);
        collection.sort(Comparator.naturalOrder());
    }

    /**
     * Returns a snapshot copy of the collection.
     *
     * @return collection snapshot
     */
    public synchronized List<Organization> getCollection() {
        return new LinkedList<>(collection);
    }

    /**
     * Finds an organization by id.
     *
     * @param id organization id
     * @return matching organization if it exists
     */
    public synchronized Optional<Organization> findById(long id) {
        return collection.stream().filter(organization -> organization.getId() == id).findFirst();
    }

    /**
     * Removes an organization by id.
     *
     * @param id organization id
     * @return true when an organization was removed
     */
    public synchronized boolean removeById(long id) {
        return collection.removeIf(organization -> organization.getId() == id);
    }

    /**
     * Removes organizations matching a predicate.
     *
     * @param predicate removal predicate
     * @return number of removed organizations
     */
    public synchronized int removeIf(Predicate<Organization> predicate) {
        int before = collection.size();
        collection.removeIf(predicate);
        return before - collection.size();
    }

    /**
     * Replaces one organization while preserving sort order.
     *
     * @param id organization id to replace
     * @param organization replacement organization
     */
    public synchronized void replace(long id, Organization organization) {
        collection.removeIf(existing -> existing.getId() == id);
        collection.add(organization);
        collection.sort(Comparator.naturalOrder());
    }

    /**
     * Builds a human-readable collection summary.
     *
     * @return collection summary
     */
    public synchronized String getInfo() {
        return "Type: LinkedList | Date Created: " + initializationDate + " | Size: " + collection.size();
    }

    /**
     * Prints collection summary to standard output.
     */
    public void info() {
        System.out.println(getInfo());
    }
}
