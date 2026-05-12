package data;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long x;
    private final Double y;

    public Coordinates(Long x, Double y) { this.x = x; this.y = y; }
    public Long getX() { return x; }
    public Double getY() { return y; }
}
