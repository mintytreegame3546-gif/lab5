package data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable coordinate pair for an organization.
 */
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long x;
    private final Double y;

    /**
     * Creates coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public Coordinates(Long x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x coordinate
     */
    public Long getX() { return x; }

    /**
     * @return y coordinate
     */
    public Double getY() { return y; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Coordinates that)) return false;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
