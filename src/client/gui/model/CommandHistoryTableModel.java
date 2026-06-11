package client.gui.model;

import client.gui.i18n.LocalizationManager;
import network.CommandHistoryEntry;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CommandHistoryTableModel extends AbstractTableModel implements LocalizationManager.LocaleChangeListener {
    private static final String[] COLUMN_KEYS = {"history.command", "history.arguments", "history.executedAt"};

    private final LocalizationManager localization;
    private List<CommandHistoryEntry> rows = List.of();

    public CommandHistoryTableModel(LocalizationManager localization) {
        this.localization = localization;
        localization.addListener(this);
    }

    public void setHistory(List<CommandHistoryEntry> history) {
        rows = List.copyOf(history);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_KEYS.length;
    }

    @Override
    public String getColumnName(int column) {
        return localization.text(COLUMN_KEYS[column]);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CommandHistoryEntry entry = rows.get(rowIndex);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
                localization.getLocale());
        return switch (columnIndex) {
            case 0 -> entry.getCommandName();
            case 1 -> entry.getArguments();
            case 2 -> dateFormat.format(Date.from(entry.getExecutedAt().atZone(ZoneId.systemDefault()).toInstant()));
            default -> "";
        };
    }

    @Override
    public void localeChanged() {
        fireTableStructureChanged();
    }
}
