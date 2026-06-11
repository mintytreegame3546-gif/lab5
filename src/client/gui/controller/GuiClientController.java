package client.gui.controller;

import client.gui.i18n.LocalizationManager;
import client.gui.model.OrganizationRepository;
import client.gui.model.OrganizationTableModel;
import client.gui.net.ClientNetworkService;
import client.gui.theme.ThemeManager;
import client.gui.view.AuthFrame;
import client.gui.view.CommandHistoryDialog;
import client.gui.view.MainFrame;
import client.gui.view.OrganizationFormDialog;
import client.gui.view.VisualizationPanel;
import data.Organization;
import network.CommandHistoryEntry;
import network.CommandResponse;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.List;

public class GuiClientController {
    private final ClientNetworkService network;
    private final LocalizationManager localization = new LocalizationManager();
    private final OrganizationRepository repository = new OrganizationRepository();
    private final ThemeManager themeManager = new ThemeManager();
    private AuthFrame authFrame;
    private MainFrame mainFrame;

    public GuiClientController(ClientNetworkService network) {
        this.network = network;
        network.addCollectionUpdateListener(repository::replaceAll);
        repository.addListener((organizations, added) -> {
            if (mainFrame != null) mainFrame.setOrganizations(organizations, added);
        });
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            authFrame = new AuthFrame(localization);
            authFrame.onLogin((username, password) -> authenticate(true, username, password));
            authFrame.onRegister((username, password) -> authenticate(false, username, password));
            authFrame.setVisible(true);
        });
    }

    private void authenticate(boolean login, String username, String password) {
        run(response -> {
            if (response.isSuccess()) openMainWindow();
            else authFrame.showError(response.getMessage());
        }, () -> login ? network.login(username, password) : network.register(username, password));
    }

    private void openMainWindow() {
        authFrame.dispose();
        OrganizationTableModel tableModel = new OrganizationTableModel(localization);
        VisualizationPanel canvas = new VisualizationPanel(localization);
        mainFrame = new MainFrame(localization, tableModel, canvas, themeManager, network.username());
        mainFrame.onRefresh(this::refresh);
        mainFrame.onAdd(this::add);
        mainFrame.onEdit(() -> mainFrame.selectedOrganization().ifPresentOrElse(this::edit,
                () -> mainFrame.showError(localization.text("dialog.select"))));
        mainFrame.onDelete(this::deleteSelected);
        mainFrame.onExecute(this::executeRaw);
        mainFrame.onHistory(this::showHistory);
        mainFrame.onCanvasDetails(this::showDetails);
        mainFrame.onCanvasEdit(this::edit);
        mainFrame.setVisible(true);
        refresh();
    }

    private void refresh() {
        run(response -> {
            if (response.isSuccess()) repository.replaceAll(response.getOrganizations());
            mainFrame.setStatus(response.getMessage());
        }, network::refresh);
    }

    private void add() {
        new OrganizationFormDialog(mainFrame, localization, null).showDialog().ifPresent(organization ->
                run(response -> {
                    mainFrame.setStatus(response.getMessage());
                    refresh();
                }, () -> network.add(organization)));
    }

    private void edit(Organization organization) {
        if (!network.username().equals(organization.getOwnerUsername())) {
            mainFrame.showError(localization.text("dialog.notOwner"));
            return;
        }
        new OrganizationFormDialog(mainFrame, localization, organization).showDialog().ifPresent(updated ->
                run(response -> {
                    mainFrame.setStatus(response.getMessage());
                    refresh();
                }, () -> network.update(organization.getId(), updated)));
    }

    private void deleteSelected() {
        mainFrame.selectedOrganization().ifPresentOrElse(this::delete,
                () -> mainFrame.showError(localization.text("dialog.select")));
    }

    private void delete(Organization organization) {
        if (!network.username().equals(organization.getOwnerUsername())) {
            mainFrame.showError(localization.text("dialog.notOwner"));
            return;
        }
        run(response -> {
            mainFrame.setStatus(response.getMessage());
            refresh();
        }, () -> network.remove(organization.getId()));
    }

    private void executeRaw(String line) {
        String command = line == null ? "" : line.trim().split("\\s+")[0];
        if ("add_if_min".equals(command) || "remove_lower".equals(command)) {
            new OrganizationFormDialog(mainFrame, localization, null).showDialog().ifPresent(organization ->
                    run(response -> {
                        mainFrame.setStatus(response.getMessage());
                        refresh();
                    }, () -> network.executeRawWithOrganization(line, organization)));
            return;
        }
        run(response -> {
            updateFromResponse(response);
            mainFrame.setStatus(response.getMessage());
        }, () -> network.executeRaw(line));
    }

    private void showHistory() {
        CommandHistoryDialog dialog = new CommandHistoryDialog(mainFrame, localization);
        dialog.setVisible(true);
        new SwingWorker<List<CommandHistoryEntry>, Void>() {
            @Override
            protected List<CommandHistoryEntry> doInBackground() throws Exception {
                return network.history();
            }

            @Override
            protected void done() {
                try {
                    dialog.setHistory(get());
                } catch (Exception e) {
                    mainFrame.showError(e.getMessage());
                }
            }
        }.execute();
    }

    private void showDetails(Organization organization) {
        mainFrame.showMessage(localization.text("dialog.details"), organization.toString());
    }

    private void updateFromResponse(CommandResponse response) {
        List<Organization> organizations = response.getOrganizations();
        if (!organizations.isEmpty() || "Collection is empty".equals(response.getMessage())) {
            repository.replaceAll(organizations);
        }
    }

    private void run(ResponseHandler handler, NetworkCall call) {
        new SwingWorker<CommandResponse, Void>() {
            @Override
            protected CommandResponse doInBackground() throws Exception {
                return call.execute();
            }

            @Override
            protected void done() {
                try {
                    handler.handle(get());
                } catch (Exception e) {
                    JFrame frame = mainFrame == null ? authFrame : mainFrame;
                    if (frame instanceof MainFrame view) view.showError(e.getMessage());
                    else if (frame instanceof AuthFrame view) view.showError(e.getMessage());
                }
            }
        }.execute();
    }

    private interface NetworkCall {
        CommandResponse execute() throws Exception;
    }

    private interface ResponseHandler {
        void handle(CommandResponse response);
    }
}
