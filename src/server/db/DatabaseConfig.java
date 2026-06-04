package server.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public record DatabaseConfig(String url, String username, String password) {
    private static final String DEFAULT_HOST = "pg";
    private static final String DEFAULT_DATABASE = "studs";

    public DatabaseConfig {
        if (isBlank(url)) throw new IllegalArgumentException("Database URL is required");
        if (isBlank(username)) throw new IllegalArgumentException("Database username is required");
        if (isBlank(password)) throw new IllegalArgumentException("Database password is required");
    }

    public static DatabaseConfig fromFile(String path) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        }
        String host = properties.getProperty("db.host", DEFAULT_HOST);
        String database = properties.getProperty("db.name", DEFAULT_DATABASE);
        String defaultUrl = "jdbc:postgresql://" + host + "/" + database;
        return new DatabaseConfig(properties.getProperty("db.url", defaultUrl),
                properties.getProperty("db.user"), properties.getProperty("db.password"));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
