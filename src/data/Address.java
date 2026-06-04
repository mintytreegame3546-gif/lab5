package data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable postal address used by an organization.
 */
public class Address implements Comparable<Address>, Serializable {
    private static final long serialVersionUID = 1L;
    private final String street;
    private final String zipCode;

    /**
     * Creates an address.
     *
     * @param street street name
     * @param zipCode postal zip code
     */
    public Address(String street, String zipCode) {
        this.street = street;
        this.zipCode = zipCode;
    }

    /**
     * @return street name
     */
    public String getStreet() { return street; }

    /**
     * @return postal zip code
     */
    public String getZipCode() { return zipCode; }

    @Override
    public int compareTo(Address other) {
        if (this.street == null) return -1;
        if (other.street == null) return 1;
        return this.street.compareTo(other.street);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Address address)) return false;
        return Objects.equals(street, address.street) && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, zipCode);
    }
}
