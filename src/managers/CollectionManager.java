package managers;

import data.Organization;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;

public class CollectionManager {
    private final LinkedList<Organization> collection = new LinkedList<>();
    private final LocalDateTime initializationDate = LocalDateTime.now();
    private long nextId = 1;

    public void add(Organization org) { collection.add(org); Collections.sort(collection); }
    public LinkedList<Organization> getCollection() { return collection; }
    public void clear() { collection.clear(); }
    public long generateId() { return nextId++; }
    public void setNextId(long id) { if (id >= nextId) nextId = id + 1; }

    public void info() {
        System.out.println("Type: LinkedList | Date Created: " + initializationDate + " | Size: " + collection.size());
    }
}