package data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Organization implements Comparable<Organization>, Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final float annualTurnover;
    private final OrganizationType type;
    private final Address officialAddress;

    public Organization(long id, String name, Coordinates coordinates, LocalDateTime creationDate,
                        float annualTurnover, OrganizationType type, Address officialAddress) {
        this.id = id; this.name = name; this.coordinates = coordinates;
        this.creationDate = creationDate; this.annualTurnover = annualTurnover;
        this.type = type; this.officialAddress = officialAddress;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public float getAnnualTurnover() { return annualTurnover; }
    public Address getOfficialAddress() { return officialAddress; }
    public OrganizationType getType() { return type; }
    public Coordinates getCoordinates() { return coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }

    @Override
    public int compareTo(Organization o) {
        return Float.compare(this.annualTurnover, o.getAnnualTurnover());
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Coordinates: (X:%d, Y:%.2f) | Turnover: %.2f | Type: %s | Address: [Street: %s, Zipcode: %s]",
                id, name, coordinates.getX(), coordinates.getY(), annualTurnover,
                (type == null ? "null" : type),
                (officialAddress.getStreet() == null ? "null" : officialAddress.getStreet()),
                (officialAddress.getZipCode() == null ? "null" : officialAddress.getZipCode()));
    }
}