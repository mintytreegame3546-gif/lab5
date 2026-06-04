package server.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DatabaseConfig fromFile(String path) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        }
        String host = properties.getProperty("db.host", "pg");
        String database = properties.getProperty("db.name", "studs");
        String url = properties.getProperty("db.url", "jdbc:postgresql://" + host + "/" + database);
        return new DatabaseConfig(url, properties.getProperty("db.user"), properties.getProperty("db.password"));
    }

    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
