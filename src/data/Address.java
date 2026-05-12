package data;

public class Address implements Comparable<Address> {
    private final String street;
    private final String zipCode;

    public Address(String street, String zipCode) { this.street = street; this.zipCode = zipCode; }
    public String getStreet() { return street; }
    public String getZipCode() { return zipCode; }

    @Override
    public int compareTo(Address o) {
        if (this.street == null) return -1;
        if (o.street == null) return 1;
        return this.street.compareTo(o.street);
    }
}
