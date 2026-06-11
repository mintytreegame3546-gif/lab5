package client.gui.view;

import client.gui.i18n.LocalizationManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public class AuthFrame extends JFrame implements LocalizationManager.LocaleChangeListener {
    private final LocalizationManager localization;
    private final JTextField username = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);
    private final JLabel usernameLabel = new JLabel();
    private final JLabel passwordLabel = new JLabel();
    private final JLabel languageLabel = new JLabel();
    private final JButton login = new JButton();
    private final JButton register = new JButton();
    private final JComboBox<String> languageBox;
    private final Map<String, Locale> locales = new LinkedHashMap<>();

    public AuthFrame(LocalizationManager localization) {
        this.localization = localization;
        locales.put("English (Canada)", Locale.CANADA);
        locales.put("Русский", Locale.forLanguageTag("ru"));
        locales.put("Deutsch", Locale.GERMAN);
        locales.put("Magyar", Locale.forLanguageTag("hu"));
        languageBox = new JComboBox<>(locales.keySet().toArray(String[]::new));
        localization.addListener(this);
        add(form(), BorderLayout.CENTER);
        add(buttons(), BorderLayout.SOUTH);
        languageBox.addActionListener(event -> localization.setLocale(locales.get(languageBox.getSelectedItem())));
        localeChanged();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public void onLogin(BiConsumer<String, String> action) {
        login.addActionListener(event -> action.accept(username.getText(), new String(password.getPassword())));
    }

    public void onRegister(BiConsumer<String, String> action) {
        register.addActionListener(event -> action.accept(username.getText(), new String(password.getPassword())));
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, localization.text("dialog.error"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void localeChanged() {
        setTitle(localization.text("auth.title"));
        usernameLabel.setText(localization.text("auth.username"));
        passwordLabel.setText(localization.text("auth.password"));
        languageLabel.setText(localization.text("auth.language"));
        login.setText(localization.text("auth.login"));
        register.setText(localization.text("auth.register"));
    }

    private JPanel form() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(usernameLabel);
        panel.add(username);
        panel.add(passwordLabel);
        panel.add(password);
        panel.add(languageLabel);
        panel.add(languageBox);
        return panel;
    }

    private JPanel buttons() {
        JPanel panel = new JPanel();
        panel.add(login);
        panel.add(register);
        return panel;
    }
}
