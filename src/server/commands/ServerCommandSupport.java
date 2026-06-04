package server.commands;

import data.Organization;
import network.CommandRequest;

final class ServerCommandSupport {
    private ServerCommandSupport() { }

    static String validateOrganization(Organization organization) {
        if (organization == null) return "Error: organization payload is required";
        if (organization.getName() == null || organization.getName().trim().isEmpty()) {
            return "Error: organization name cannot be empty";
        }
        if (organization.getCoordinates() == null) return "Error: coordinates are required";
        if (organization.getCoordinates().getX() == null) return "Error: coordinates.x is required";
        if (organization.getCoordinates().getY() == null) return "Error: coordinates.y is required";
        if (organization.getCoordinates().getX() > 90L) return "Error: coordinates.x must be <= 90";
        if (organization.getCoordinates().getY() > 117.0) return "Error: coordinates.y must be <= 117";
        if (organization.getAnnualTurnover() <= 0f) return "Error: annualTurnover must be > 0";
        if (organization.getOfficialAddress() == null) return "Error: officialAddress is required";
        return null;
    }

    static String requireUsername(CommandRequest request) {
        return request.getUsername() == null || request.getUsername().trim().isEmpty()
                ? "Error: authorized username is required"
                : null;
    }
}
