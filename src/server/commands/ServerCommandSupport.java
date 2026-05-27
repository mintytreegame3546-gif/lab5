package server.commands;

import data.Address;
import data.Coordinates;
import data.Organization;

import java.time.LocalDateTime;

final class ServerCommandSupport {
    private ServerCommandSupport() { }

    static String validateOrganization(Organization organization) {
        if (organization == null) return "Error: organization payload is required";
        if (organization.getName() == null || organization.getName().trim().isEmpty()) return "Error: organization name cannot be empty";
        if (organization.getCoordinates() == null) return "Error: coordinates are required";
        if (organization.getCoordinates().getX() > 90L) return "Error: coordinates.x must be <= 90";
        if (organization.getCoordinates().getY() > 117.0) return "Error: coordinates.y must be <= 117";
        if (organization.getAnnualTurnover() <= 0f) return "Error: annualTurnover must be > 0";
        if (organization.getOfficialAddress() == null) return "Error: officialAddress is required";
        return null;
    }

    static Organization withServerFields(Organization source, long id) {
        return new Organization(id, source.getName(),
                new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()),
                LocalDateTime.now(), source.getAnnualTurnover(), source.getType(),
                new Address(source.getOfficialAddress().getStreet(), source.getOfficialAddress().getZipCode()));
    }
}
