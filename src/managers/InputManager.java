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
    public InputManager(Scanner s) { this.scanner = s; }

    public Organization readOrganization(long id) {
        String name;
        while (true) {
            System.out.print("Enter organization name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) break;
            System.out.println("Error: Organization name cannot be empty");
        }

        Long x = readLong();
        Double y = readDouble();
        Float turnover = readFloat();

        OrganizationType type = readType();

        System.out.print("Enter street name: ");
        String street = scanner.nextLine().trim();
        if (street.isEmpty()) street = null;

        System.out.print("Enter ZipCode: ");
        String zip = scanner.nextLine().trim();
        if (zip.isEmpty()) zip = null;

        return new Organization(id, name, new Coordinates(x, y), LocalDateTime.now(), turnover, type, new Address(street, zip));
    }

    private Long readLong() {
        while (true) {
            try {
                System.out.print("Coordinate X (<=90): ");
                long val = Long.parseLong(scanner.nextLine().trim());
                if (val <= 90L) return val;
                System.out.println("Error: Value must be >= " + 90L);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private Double readDouble() {
        while (true) {
            try {
                System.out.print("Coordinate Y (<=117): ");
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val <= 117.0) return val;
                System.out.println("Error: value must be >= " + 117.0);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private Float readFloat() {
        while (true) {
            try {
                System.out.print("Annual turnover (>0): ");
                float val = Float.parseFloat(scanner.nextLine().trim());
                if (val > (float) 0.0) return val;
                System.out.println("Error: value must be >= " + (float) 0.0);
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
}