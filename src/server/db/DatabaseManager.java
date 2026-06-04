package server.db;

import data.Address;
import data.Coordinates;
import data.Organization;
import data.OrganizationType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {
    private final DatabaseConfig config;

    public DatabaseManager(DatabaseConfig config) {
        this.config = config;
    }

    public void initialize() throws Exception {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS organization_id_seq START WITH 1 INCREMENT BY 1");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(64) PRIMARY KEY," +
                    "password_hash VARCHAR(56) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS organizations (" +
                    "id BIGINT PRIMARY KEY DEFAULT nextval('organization_id_seq')," +
                    "name VARCHAR(255) NOT NULL," +
                    "x BIGINT NOT NULL," +
                    "y DOUBLE PRECISION NOT NULL," +
                    "creation_date TIMESTAMP NOT NULL," +
                    "annual_turnover REAL NOT NULL," +
                    "type VARCHAR(64)," +
                    "street VARCHAR(255)," +
                    "zip_code VARCHAR(64)," +
                    "owner_username VARCHAR(64) NOT NULL REFERENCES users(username))");
        }
    }

    public boolean createUser(String username, String passwordHash) throws Exception {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users(username, password_hash) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean authenticate(String username, String passwordHash) throws Exception {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM users WHERE username = ? AND password_hash = ?")) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public List<Organization> loadOrganizations() throws Exception {
        List<Organization> organizations = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM organizations");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) organizations.add(readOrganization(resultSet));
        }
        return organizations;
    }

    public Organization insertOrganization(Organization source, String ownerUsername) throws Exception {
        LocalDateTime creationDate = LocalDateTime.now();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO organizations(name, x, y, creation_date, annual_turnover, type, "
                             + "street, zip_code, owner_username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id")) {
            fillInsertFields(statement, source, creationDate, ownerUsername);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return copyWithServerFields(source, resultSet.getLong(1), creationDate, ownerUsername);
            }
        }
    }

    public Optional<Organization> updateOrganization(long id, Organization source,
                                                     String ownerUsername) throws Exception {
        LocalDateTime creationDate = LocalDateTime.now();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE organizations SET name = ?, x = ?, y = ?, creation_date = ?, annual_turnover = ?, "
                             + "type = ?, street = ?, zip_code = ? WHERE id = ? AND owner_username = ?")) {
            fillUpdateFields(statement, source, creationDate);
            statement.setLong(9, id);
            statement.setString(10, ownerUsername);
            if (statement.executeUpdate() == 0) return Optional.empty();
            return Optional.of(copyWithServerFields(source, id, creationDate, ownerUsername));
        }
    }

    public boolean deleteOrganization(long id, String ownerUsername) throws Exception {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM organizations WHERE id = ? AND owner_username = ?")) {
            statement.setLong(1, id);
            statement.setString(2, ownerUsername);
            return statement.executeUpdate() > 0;
        }
    }

    private void fillInsertFields(PreparedStatement statement, Organization source,
                                  LocalDateTime creationDate, String ownerUsername) throws Exception {
        fillUpdateFields(statement, source, creationDate);
        statement.setString(9, ownerUsername);
    }

    private void fillUpdateFields(PreparedStatement statement, Organization source,
                                  LocalDateTime creationDate) throws Exception {
        statement.setString(1, source.getName());
        statement.setLong(2, source.getCoordinates().getX());
        statement.setDouble(3, source.getCoordinates().getY());
        statement.setTimestamp(4, Timestamp.valueOf(creationDate));
        statement.setFloat(5, source.getAnnualTurnover());
        statement.setString(6, source.getType() == null ? null : source.getType().name());
        statement.setString(7, source.getOfficialAddress().getStreet());
        statement.setString(8, source.getOfficialAddress().getZipCode());
    }

    private Organization readOrganization(ResultSet resultSet) throws Exception {
        String type = resultSet.getString("type");
        return new Organization(resultSet.getLong("id"), resultSet.getString("name"),
                new Coordinates(resultSet.getLong("x"), resultSet.getDouble("y")),
                resultSet.getTimestamp("creation_date").toLocalDateTime(),
                resultSet.getFloat("annual_turnover"), type == null ? null : OrganizationType.valueOf(type),
                new Address(resultSet.getString("street"), resultSet.getString("zip_code")),
                resultSet.getString("owner_username"));
    }

    private Organization copyWithServerFields(Organization source, long id, LocalDateTime creationDate,
                                              String ownerUsername) {
        return new Organization(id, source.getName(),
                new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()),
                creationDate, source.getAnnualTurnover(), source.getType(),
                new Address(source.getOfficialAddress().getStreet(), source.getOfficialAddress().getZipCode()),
                ownerUsername);
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(config.url(), config.username(), config.password());
    }
}
