package server.commands;

import data.Address;
import data.Coordinates;
import data.Organization;

import java.time.LocalDateTime;

final class ServerCommandSupport {
    private ServerCommandSupport() { }

    static Organization withServerFields(Organization source, long id) {
        return new Organization(id, source.getName(),
                new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()),
                LocalDateTime.now(), source.getAnnualTurnover(), source.getType(),
                new Address(source.getOfficialAddress().getStreet(), source.getOfficialAddress().getZipCode()));
    }
}
