package client.gui.view;

import client.gui.i18n.LocalizationManager;
import data.Address;
import data.Coordinates;
import data.Organization;
import data.OrganizationType;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.Optional;

public class OrganizationFormDialog extends JDialog {
    private final LocalizationManager localization;
    private final JTextField nameField = new JTextField(20);
    private final JTextField xField = new JTextField(20);
    private final JTextField yField = new JTextField(20);
    private final JTextField turnoverField = new JTextField(20);
    private final JComboBox<OrganizationType> typeBox = new JComboBox<>(OrganizationType.values());
    private final JTextField streetField = new JTextField(20);
    private final JTextField zipField = new JTextField(20);
    private Optional<Organization> result = Optional.empty();

    public OrganizationFormDialog(JFrame owner, LocalizationManager localization, Organization organization) {
        super(owner, true);
        this.localization = localization;
        setTitle(localization.text("dialog.organization"));
        add(form(), BorderLayout.CENTER);
        add(buttons(), BorderLayout.SOUTH);
        if (organization != null) fill(organization);
        pack();
        setLocationRelativeTo(owner);
    }

    public Optional<Organization> showDialog() {
        setVisible(true);
        return result;
    }

    private JPanel form() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        addField(panel, "field.name", nameField);
        addField(panel, "field.x", xField);
        addField(panel, "field.y", yField);
        addField(panel, "field.turnover", turnoverField);
        addField(panel, "field.type", typeBox);
        addField(panel, "field.street", streetField);
        addField(panel, "field.zip", zipField);
        return panel;
    }

    private JPanel buttons() {
        JPanel panel = new JPanel();
        JButton save = new JButton(localization.text("dialog.save"));
        JButton cancel = new JButton(localization.text("dialog.cancel"));
        save.addActionListener(event -> save());
        cancel.addActionListener(event -> dispose());
        panel.add(save);
        panel.add(cancel);
        return panel;
    }

    private void addField(JPanel panel, String key, java.awt.Component component) {
        panel.add(new JLabel(localization.text(key)));
        panel.add(component);
    }

    private void fill(Organization organization) {
        nameField.setText(organization.getName());
        xField.setText(String.valueOf(organization.getCoordinates().getX()));
        yField.setText(String.valueOf(organization.getCoordinates().getY()));
        turnoverField.setText(String.valueOf(organization.getAnnualTurnover()));
        typeBox.setSelectedItem(organization.getType());
        streetField.setText(organization.getOfficialAddress().getStreet());
        zipField.setText(organization.getOfficialAddress().getZipCode());
    }

    private void save() {
        try {
            result = Optional.of(new Organization(0, nameField.getText().trim(),
                    new Coordinates(Long.parseLong(xField.getText().trim()), Double.parseDouble(yField.getText().trim())),
                    LocalDateTime.now(), Float.parseFloat(turnoverField.getText().trim()),
                    (OrganizationType) typeBox.getSelectedItem(),
                    new Address(streetField.getText().trim(), zipField.getText().trim())));
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), localization.text("dialog.error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
