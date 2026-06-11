package client.gui.view;

import client.gui.i18n.LocalizationManager;
import client.gui.model.OrganizationTableModel;
import client.gui.theme.ThemeManager;
import data.Organization;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class MainFrame extends JFrame implements LocalizationManager.LocaleChangeListener {
    private final LocalizationManager localization;
    private final OrganizationTableModel tableModel;
    private final JTable table;
    private final VisualizationPanel canvas;
    private final ThemeManager themeManager;
    private final String username;
    private final JLabel userLabel = new JLabel();
    private final JLabel status = new JLabel();
    private final JLabel filterLabel = new JLabel();
    private final JLabel commandLabel = new JLabel();
    private final JTextField filter = new JTextField(16);
    private final JTextField command = new JTextField(18);
    private final JButton refresh = new JButton();
    private final JButton add = new JButton();
    private final JButton edit = new JButton();
    private final JButton delete = new JButton();
    private final JButton execute = new JButton();
    private final JButton history = new JButton();
    private final JMenu languageMenu = new JMenu();
    private final JMenu themeMenu = new JMenu();
    private final JRadioButtonMenuItem lightTheme = new JRadioButtonMenuItem();
    private final JRadioButtonMenuItem darkTheme = new JRadioButtonMenuItem();
    private final JComboBox<String> languageBox;
    private final Map<String, Locale> locales = new LinkedHashMap<>();

    public MainFrame(LocalizationManager localization, OrganizationTableModel tableModel, VisualizationPanel canvas,
                     ThemeManager themeManager, String username) {
        this.localization = localization;
        this.tableModel = tableModel;
        this.canvas = canvas;
        this.themeManager = themeManager;
        this.username = username;
        this.table = new JTable(tableModel);
        locales.put("English (Canada)", Locale.CANADA);
        locales.put("Русский", Locale.forLanguageTag("ru"));
        locales.put("Deutsch", Locale.GERMAN);
        locales.put("Magyar", Locale.forLanguageTag("hu"));
        this.languageBox = new JComboBox<>(locales.keySet().toArray(String[]::new));
        localization.addListener(this);
        setJMenuBar(menu());
        add(toolbar(), BorderLayout.NORTH);
        add(split(), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        filter.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tableModel.setFilter(filter.getText()); }
            public void removeUpdate(DocumentEvent e) { tableModel.setFilter(filter.getText()); }
            public void changedUpdate(DocumentEvent e) { tableModel.setFilter(filter.getText()); }
        });
        languageBox.addActionListener(event -> localization.setLocale(locales.get(languageBox.getSelectedItem())));
        lightTheme.addActionListener(event -> themeManager.apply(ThemeManager.Theme.LIGHT, this));
        darkTheme.addActionListener(event -> themeManager.apply(ThemeManager.Theme.DARK, this));
        lightTheme.setSelected(true);
        localeChanged();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    public Optional<Organization> selectedOrganization() {
        int row = table.getSelectedRow();
        if (row < 0) return Optional.empty();
        return Optional.of(tableModel.getOrganizationAt(table.convertRowIndexToModel(row)));
    }

    public void setOrganizations(List<Organization> organizations, List<Organization> added) {
        tableModel.setOrganizations(organizations);
        canvas.setOrganizations(organizations, added);
    }

    public void setStatus(String message) {
        status.setText(message);
    }

    public void onRefresh(Runnable action) { refresh.addActionListener(event -> action.run()); }
    public void onAdd(Runnable action) { add.addActionListener(event -> action.run()); }
    public void onEdit(Runnable action) { edit.addActionListener(event -> action.run()); table.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() >= 2) action.run(); } }); }
    public void onDelete(Runnable action) { delete.addActionListener(event -> action.run()); }
    public void onExecute(Consumer<String> action) { execute.addActionListener(event -> action.accept(command.getText())); }
    public void onHistory(Runnable action) { history.addActionListener(event -> action.run()); }
    public void onCanvasDetails(Consumer<Organization> action) { canvas.setOpenDetails(action); }
    public void onCanvasEdit(Consumer<Organization> action) { canvas.setEdit(action); }

    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, localization.text("dialog.error"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void localeChanged() {
        setTitle(localization.text("app.title"));
        userLabel.setText(localization.format("main.user", username));
        filterLabel.setText(localization.text("main.filter"));
        commandLabel.setText(localization.text("main.command"));
        refresh.setText(localization.text("main.refresh"));
        add.setText(localization.text("main.add"));
        edit.setText(localization.text("main.edit"));
        delete.setText(localization.text("main.delete"));
        execute.setText(localization.text("main.execute"));
        history.setText(localization.text("main.history"));
        languageMenu.setText(localization.text("auth.language"));
        themeMenu.setText(localization.text("theme.menu"));
        lightTheme.setText(localization.text("theme.light"));
        darkTheme.setText(localization.text("theme.dark"));
        if (status.getText().isBlank()) status.setText(localization.text("main.status.ready"));
    }

    private JMenuBar menu() {
        JMenuBar bar = new JMenuBar();
        languageMenu.add(languageBox);
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightTheme);
        themeGroup.add(darkTheme);
        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);
        bar.add(languageMenu);
        bar.add(themeMenu);
        return bar;
    }

    private JPanel toolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(userLabel);
        panel.add(filterLabel);
        panel.add(filter);
        panel.add(refresh);
        panel.add(add);
        panel.add(edit);
        panel.add(delete);
        panel.add(history);
        panel.add(commandLabel);
        panel.add(command);
        panel.add(execute);
        return panel;
    }

    private JSplitPane split() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), canvas);
        splitPane.setResizeWeight(0.55);
        return splitPane;
    }
}
