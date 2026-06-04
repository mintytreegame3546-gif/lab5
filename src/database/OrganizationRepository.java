package database;

import data.Address;
import data.Coordinates;
import data.Organization;
import data.OrganizationType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class OrganizationRepository {
    private final DatabaseManager databaseManager;

    public OrganizationRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Organization> loadAll() throws SQLException {
        LinkedList<Organization> organizations = new LinkedList<>();
        String sql = "SELECT id, name, coordinate_x, coordinate_y, creation_date, annual_turnover, type, street, zip_code, owner_username FROM organizations";
        try (Connection connection = databaseManager.getConnection(); Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) organizations.add(readOrganization(rs));
        }
        return organizations;
    }

    public Organization add(Organization source, String owner) throws SQLException {
        String sql = "INSERT INTO organizations(name, coordinate_x, coordinate_y, creation_date, annual_turnover, type, street, zip_code, owner_username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        LocalDateTime creationDate = LocalDateTime.now();
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, source, creationDate, owner);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return copyWithDbFields(source, rs.getLong(1), creationDate, owner);
            }
        }
    }

    public Organization update(long id, Organization source, String owner) throws SQLException {
        String sql = "UPDATE organizations SET name = ?, coordinate_x = ?, coordinate_y = ?, creation_date = ?, annual_turnover = ?, type = ?, street = ?, zip_code = ? " +
                "WHERE id = ? AND owner_username = ?";
        LocalDateTime creationDate = LocalDateTime.now();
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, source, creationDate, owner);
            statement.setLong(9, id);
            statement.setString(10, owner);
            if (statement.executeUpdate() != 1) return null;
            return copyWithDbFields(source, id, creationDate, owner);
        }
    }

    public boolean removeById(long id, String owner) throws SQLException {
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM organizations WHERE id = ? AND owner_username = ?")) {
            statement.setLong(1, id);
            statement.setString(2, owner);
            return statement.executeUpdate() == 1;
        }
    }

    public int clearOwned(String owner) throws SQLException {
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM organizations WHERE owner_username = ?")) {
            statement.setString(1, owner);
            return statement.executeUpdate();
        }
    }

    public int removeOwnedIds(List<Long> ids, String owner) throws SQLException {
        if (ids.isEmpty()) return 0;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        String sql = "DELETE FROM organizations WHERE owner_username = ? AND id IN (" + placeholders + ")";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, owner);
            for (int i = 0; i < ids.size(); i++) statement.setLong(i + 2, ids.get(i));
            return statement.executeUpdate();
        }
    }

    private void fillStatement(PreparedStatement statement, Organization source, LocalDateTime creationDate, String owner) throws SQLException {
        statement.setString(1, source.getName());
        statement.setLong(2, source.getCoordinates().getX());
        statement.setDouble(3, source.getCoordinates().getY());
        statement.setTimestamp(4, Timestamp.valueOf(creationDate));
        statement.setFloat(5, source.getAnnualTurnover());
        statement.setString(6, source.getType() == null ? null : source.getType().name());
        statement.setString(7, source.getOfficialAddress().getStreet());
        statement.setString(8, source.getOfficialAddress().getZipCode());
        statement.setString(9, owner);
    }

    private Organization readOrganization(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        return new Organization(rs.getLong("id"), rs.getString("name"),
                new Coordinates(rs.getLong("coordinate_x"), rs.getDouble("coordinate_y")),
                rs.getTimestamp("creation_date").toLocalDateTime(), rs.getFloat("annual_turnover"),
                type == null ? null : OrganizationType.valueOf(type),
                new Address(rs.getString("street"), rs.getString("zip_code")), rs.getString("owner_username"));
    }

    private Organization copyWithDbFields(Organization source, long id, LocalDateTime creationDate, String owner) {
        return new Organization(id, source.getName(),
                new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()),
                creationDate, source.getAnnualTurnover(), source.getType(),
                new Address(source.getOfficialAddress().getStreet(), source.getOfficialAddress().getZipCode()), owner);
    }
}
