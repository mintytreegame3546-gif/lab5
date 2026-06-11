package client.gui.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    public interface LocaleChangeListener {
        void localeChanged();
    }

    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    private Locale locale = Locale.CANADA;
    private ResourceBundle bundle = ResourceBundle.getBundle("client.gui.i18n.Messages", locale);

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("client.gui.i18n.Messages", locale);
        for (LocaleChangeListener listener : List.copyOf(listeners)) listener.localeChanged();
    }

    public String text(String key) {
        return bundle.getString(key);
    }

    public String format(String key, Object... args) {
        return new MessageFormat(text(key), locale).format(args);
    }

    public void addListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }
}
