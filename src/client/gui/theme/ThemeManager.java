package client.gui.theme;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public enum Theme { LIGHT, DARK }

    public interface ThemeChangeListener {
        void themeChanged(Theme theme);
    }

    private final List<ThemeChangeListener> listeners = new ArrayList<>();
    private Theme theme = Theme.LIGHT;

    public Theme theme() {
        return theme;
    }

    public void addListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void apply(Theme theme, JFrame window) {
        Runnable update = () -> {
            this.theme = theme;
            applyColors(theme);
            SwingUtilities.updateComponentTreeUI(window);
            for (ThemeChangeListener listener : List.copyOf(listeners)) listener.themeChanged(theme);
        };
        if (SwingUtilities.isEventDispatchThread()) update.run();
        else SwingUtilities.invokeLater(update);
    }

    private void applyColors(Theme theme) {
        if (theme == Theme.DARK) applyDarkColors();
        else applyLightColors();
    }

    private void applyLightColors() {
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("Table.gridColor", new Color(220, 220, 220));
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Button.background", new Color(238, 238, 238));
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Menu.background", new Color(238, 238, 238));
        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("MenuItem.background", new Color(238, 238, 238));
        UIManager.put("MenuItem.foreground", Color.BLACK);
    }

    private void applyDarkColors() {
        Color background = new Color(43, 43, 43);
        Color foreground = new Color(230, 230, 230);
        UIManager.put("Panel.background", background);
        UIManager.put("Table.background", new Color(55, 55, 55));
        UIManager.put("Table.foreground", foreground);
        UIManager.put("Table.gridColor", new Color(80, 80, 80));
        UIManager.put("TextField.background", new Color(60, 60, 60));
        UIManager.put("TextField.foreground", foreground);
        UIManager.put("Label.foreground", foreground);
        UIManager.put("Button.background", new Color(70, 70, 70));
        UIManager.put("Button.foreground", foreground);
        UIManager.put("Menu.background", background);
        UIManager.put("Menu.foreground", foreground);
        UIManager.put("MenuItem.background", background);
        UIManager.put("MenuItem.foreground", foreground);
    }
}
