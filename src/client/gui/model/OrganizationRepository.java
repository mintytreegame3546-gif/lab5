package client.gui.model;

import data.Organization;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OrganizationRepository {
    public interface ChangeListener {
        void organizationsChanged(List<Organization> organizations, List<Organization> added);
    }

    private final List<Organization> organizations = new ArrayList<>();
    private final List<ChangeListener> listeners = new ArrayList<>();

    public synchronized void replaceAll(List<Organization> newOrganizations) {
        List<Long> existingIds = organizations.stream().map(Organization::getId).toList();
        List<Organization> added = newOrganizations.stream()
                .filter(organization -> !existingIds.contains(organization.getId()))
                .toList();
        organizations.clear();
        organizations.addAll(newOrganizations.stream().sorted(Comparator.comparing(Organization::getName)).toList());
        notifyListeners(added);
    }

    public synchronized List<Organization> snapshot() {
        return new ArrayList<>(organizations);
    }

    public synchronized Optional<Organization> findById(long id) {
        return organizations.stream().filter(organization -> organization.getId() == id).findFirst();
    }

    public synchronized void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(List<Organization> added) {
        List<Organization> snapshot = snapshot();
        Runnable notification = () -> {
            for (ChangeListener listener : List.copyOf(listeners)) listener.organizationsChanged(snapshot, added);
        };
        if (SwingUtilities.isEventDispatchThread()) notification.run();
        else SwingUtilities.invokeLater(notification);
    }
}
