package client.gui.view;

import client.gui.i18n.LocalizationManager;
import client.gui.model.CommandHistoryTableModel;
import network.CommandHistoryEntry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.util.List;

public class CommandHistoryDialog extends JDialog implements LocalizationManager.LocaleChangeListener {
    private final LocalizationManager localization;
    private final CommandHistoryTableModel tableModel;

    public CommandHistoryDialog(JFrame owner, LocalizationManager localization) {
        super(owner, false);
        this.localization = localization;
        this.tableModel = new CommandHistoryTableModel(localization);
        localization.addListener(this);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        localeChanged();
        setSize(650, 400);
        setLocationRelativeTo(owner);
    }

    public void setHistory(List<CommandHistoryEntry> history) {
        tableModel.setHistory(history);
    }

    @Override
    public void localeChanged() {
        setTitle(localization.text("main.history"));
    }
}
