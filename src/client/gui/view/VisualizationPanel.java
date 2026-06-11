package client.gui.view;

import client.gui.i18n.LocalizationManager;
import data.Organization;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class VisualizationPanel extends JPanel {
    private final Map<Long, Float> animation = new HashMap<>();
    private List<Organization> organizations = List.of();
    private Consumer<Organization> openDetails = organization -> { };
    private Consumer<Organization> edit = organization -> { };

    public VisualizationPanel(LocalizationManager localization) {
        setBackground(UIManager.getColor("Panel.background"));
        Timer timer = new Timer(40, event -> tick());
        timer.start();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                findAt(event.getPoint()).ifPresent(organization -> {
                    if (event.getClickCount() >= 2 || event.getButton() == MouseEvent.BUTTON3) edit.accept(organization);
                    else openDetails.accept(organization);
                });
            }
        });
    }

    public void setOpenDetails(Consumer<Organization> openDetails) {
        this.openDetails = openDetails;
    }

    public void setEdit(Consumer<Organization> edit) {
        this.edit = edit;
    }

    public void setOrganizations(List<Organization> organizations, List<Organization> added) {
        this.organizations = List.copyOf(organizations);
        for (Organization organization : added) animation.put(organization.getId(), 0.1f);
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Panel.background"));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Organization organization : organizations) draw(g, organization);
        g.dispose();
    }

    private void draw(Graphics2D g, Organization organization) {
        Shape shape = shapeFor(organization);
        float scale = animation.getOrDefault(organization.getId(), 1.0f);
        var bounds = shape.getBounds2D();
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        var old = g.getTransform();
        g.translate(cx, cy);
        g.scale(scale, scale);
        g.translate(-cx, -cy);
        g.setColor(colorFor(organization.getOwnerUsername()));
        g.fill(shape);
        g.setColor(Color.DARK_GRAY);
        g.draw(shape);
        g.setTransform(old);
    }

    private Optional<Organization> findAt(Point point) {
        return organizations.stream().filter(organization -> shapeFor(organization).contains(point)).findFirst();
    }

    private Shape shapeFor(Organization organization) {
        int x = Math.floorMod(organization.getCoordinates().getX().intValue(), Math.max(1, getWidth() - 60)) + 10;
        int y = Math.floorMod(organization.getCoordinates().getY().intValue(), Math.max(1, getHeight() - 60)) + 10;
        int size = Math.max(20, Math.min(80, Math.round(organization.getAnnualTurnover() / 1000f)));
        return new Ellipse2D.Double(x, y, size, size);
    }

    private Color colorFor(String owner) {
        int hash = owner == null ? 0 : owner.hashCode();
        return Color.getHSBColor((hash & 0xffff) / 65535f, 0.55f, 0.90f);
    }

    private void tick() {
        if (animation.isEmpty()) return;
        animation.replaceAll((id, scale) -> Math.min(1.0f, scale + 0.08f));
        animation.entrySet().removeIf(entry -> entry.getValue() >= 1.0f);
        repaint();
    }
}
