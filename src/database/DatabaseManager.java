package database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseManager(String propertiesPath) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(propertiesPath)) {
            properties.load(input);
        }
        String host = properties.getProperty("host", "pg");
        String database = properties.getProperty("database", "studs");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.url = properties.getProperty("url", "jdbc:postgresql://" + host + "/" + database);
        if (username == null || password == null) throw new IllegalArgumentException("Database username/password are required");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public void initialize() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS organization_id_sequence START WITH 1 INCREMENT BY 1");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(80) PRIMARY KEY," +
                    "password_hash VARCHAR(80) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS organizations (" +
                    "id BIGINT PRIMARY KEY DEFAULT nextval('organization_id_sequence')," +
                    "name TEXT NOT NULL," +
                    "coordinate_x BIGINT NOT NULL," +
                    "coordinate_y DOUBLE PRECISION NOT NULL," +
                    "creation_date TIMESTAMP NOT NULL," +
                    "annual_turnover REAL NOT NULL," +
                    "type TEXT," +
                    "street TEXT," +
                    "zip_code TEXT," +
                    "owner_username VARCHAR(80) NOT NULL REFERENCES users(username))");
            statement.execute("SELECT setval('organization_id_sequence', COALESCE((SELECT MAX(id) FROM organizations), 0) + 1, false)");
        }
    }
}
