package managers;

import data.Address;
import data.Coordinates;
import data.Organization;
import data.OrganizationType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Scanner;

public class InputManager {
    private final Scanner scanner;

    public InputManager(Scanner scanner) {
        this.scanner = scanner;
    }
  
    public Organization readOrganization(long id) {
        String name = readName();
        Long x = readLong();
        Double y = readDouble();
        Float turnover = readFloat();
        OrganizationType type = readType();
        Address address = readAddress();
        return new Organization(id, name, new Coordinates(x, y), LocalDateTime.now(), turnover, type, address);
    }

    private String readName() {
        while (true) {
            System.out.print("Enter organization name: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) return name;
            System.out.println("Error: Organization name cannot be empty");
        }
    }

    private Long readLong() {
        while (true) {
            try {
                System.out.print("Coordinate X (<=90): ");
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value <= 90L) return value;
                System.out.println("Error: Value must be <= " + 90L);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private Double readDouble() {
        while (true) {
            try {
                System.out.print("Coordinate Y (<=117): ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value <= 117.0) return value;
                System.out.println("Error: value must be <= " + 117.0);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private Float readFloat() {
        while (true) {
            try {
                System.out.print("Annual turnover (>0): ");
                float value = Float.parseFloat(scanner.nextLine().trim());
                if (value > 0.0f) return value;
                System.out.println("Error: value must be > " + 0.0f);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private OrganizationType readType() {
        while (true) {
            System.out.print("Enter organization type:");
            System.out.println(Arrays.toString(OrganizationType.values()));
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) return null;
            try {
                return OrganizationType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Organization type unknown");
            }
        }
    }

    private Address readAddress() {
        System.out.print("Enter street name: ");
        String street = scanner.nextLine().trim();
        System.out.print("Enter ZipCode: ");
        String zipCode = scanner.nextLine().trim();
        return new Address(street.isEmpty() ? null : street, zipCode.isEmpty() ? null : zipCode);
    }
}
