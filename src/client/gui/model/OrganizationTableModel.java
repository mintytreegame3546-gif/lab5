package client.gui.model;

import client.gui.i18n.LocalizationManager;
import data.Address;
import data.Coordinates;
import data.Organization;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class OrganizationTableModel extends AbstractTableModel implements LocalizationManager.LocaleChangeListener {
    private static final String[] COLUMN_KEYS = {
            "table.id", "table.name", "table.x", "table.y", "table.created", "table.turnover",
            "table.type", "table.street", "table.zip", "table.owner"
    };

    private final LocalizationManager localization;
    private List<Organization> source = List.of();
    private List<Organization> rows = List.of();
    private String filter = "";

    public OrganizationTableModel(LocalizationManager localization) {
        this.localization = localization;
        localization.addListener(this);
    }

    public void setOrganizations(List<Organization> organizations) {
        source = List.copyOf(organizations);
        applyFilter();
    }

    public void setFilter(String filter) {
        this.filter = filter == null ? "" : filter.trim().toLowerCase(localization.getLocale());
        applyFilter();
    }

    public Organization getOrganizationAt(int row) {
        return rows.get(row);
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
        Organization organization = rows.get(rowIndex);
        Coordinates coordinates = organization.getCoordinates();
        Address address = organization.getOfficialAddress();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(localization.getLocale());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, localization.getLocale());
        return switch (columnIndex) {
            case 0 -> organization.getId();
            case 1 -> organization.getName();
            case 2 -> numberFormat.format(coordinates.getX());
            case 3 -> numberFormat.format(coordinates.getY());
            case 4 -> dateFormat.format(Date.from(organization.getCreationDate().atZone(ZoneId.systemDefault()).toInstant()));
            case 5 -> numberFormat.format(organization.getAnnualTurnover());
            case 6 -> organization.getType();
            case 7 -> address.getStreet();
            case 8 -> address.getZipCode();
            case 9 -> organization.getOwnerUsername();
            default -> "";
        };
    }

    @Override
    public void localeChanged() {
        fireTableStructureChanged();
    }

    private void applyFilter() {
        rows = source.stream()
                .filter(organization -> filter.isEmpty()
                        || organization.getName().toLowerCase(localization.getLocale()).contains(filter)
                        || String.valueOf(organization.getOwnerUsername()).toLowerCase(localization.getLocale()).contains(filter))
                .sorted(Comparator.comparing(Organization::getName))
                .toList();
        fireTableDataChanged();
    }
}
