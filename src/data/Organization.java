package data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable organization model stored in the collection and PostgreSQL.
 */
public class Organization implements Comparable<Organization>, Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final float annualTurnover;
    private final OrganizationType type;
    private final Address officialAddress;
    private final String ownerUsername;

    /**
     * Creates an organization without ownership metadata.
     *
     * @param id organization id
     * @param name organization name
     * @param coordinates organization coordinates
     * @param creationDate creation timestamp
     * @param annualTurnover annual turnover
     * @param type organization type
     * @param officialAddress official address
     */
    public Organization(long id, String name, Coordinates coordinates, LocalDateTime creationDate,
                        float annualTurnover, OrganizationType type, Address officialAddress) {
        this(id, name, coordinates, creationDate, annualTurnover, type, officialAddress, null);
    }

    /**
     * Creates an organization with ownership metadata.
     *
     * @param id organization id
     * @param name organization name
     * @param coordinates organization coordinates
     * @param creationDate creation timestamp
     * @param annualTurnover annual turnover
     * @param type organization type
     * @param officialAddress official address
     * @param ownerUsername username of the creator/owner
     */
    public Organization(long id, String name, Coordinates coordinates, LocalDateTime creationDate,
                        float annualTurnover, OrganizationType type, Address officialAddress, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.officialAddress = officialAddress;
        this.ownerUsername = ownerUsername;
    }

    /**
     * @return organization id
     */
    public long getId() { return id; }

    /**
     * @return organization name
     */
    public String getName() { return name; }

    /**
     * @return annual turnover
     */
    public float getAnnualTurnover() { return annualTurnover; }

    /**
     * @return official address
     */
    public Address getOfficialAddress() { return officialAddress; }

    /**
     * @return organization type
     */
    public OrganizationType getType() { return type; }

    /**
     * @return coordinates
     */
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * @return creation timestamp
     */
    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * @return username of the owner
     */
    public String getOwnerUsername() { return ownerUsername; }

    @Override
    public int compareTo(Organization other) {
        return Float.compare(this.annualTurnover, other.getAnnualTurnover());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Organization that)) return false;
        return id == that.id
                && Float.compare(annualTurnover, that.annualTurnover) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(coordinates, that.coordinates)
                && Objects.equals(creationDate, that.creationDate)
                && type == that.type
                && Objects.equals(officialAddress, that.officialAddress)
                && Objects.equals(ownerUsername, that.ownerUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, annualTurnover, type, officialAddress, ownerUsername);
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Name: %s | Coordinates: (X:%d, Y:%.2f) | Turnover: %.2f | Type: %s "
                        + "| Address: [Street: %s, Zipcode: %s] | Owner: %s",
                id, name, coordinates.getX(), coordinates.getY(), annualTurnover, type == null ? "null" : type,
                officialAddress.getStreet() == null ? "null" : officialAddress.getStreet(),
                officialAddress.getZipCode() == null ? "null" : officialAddress.getZipCode(),
                ownerUsername == null ? "unknown" : ownerUsername);
    }
}
