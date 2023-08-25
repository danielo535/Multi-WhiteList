package pl.danielo535.multiwhitelist.manager;

import pl.danielo535.multiwhitelist.MultiWhiteList;
import pl.danielo535.multiwhitelist.config.ConfigStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;
/**
 * Manages the connection to the MySQL database, creation of tables, and database operations.
 */
public class MysqlManager {
    public Connection connection;
    private String host, database, username, password, url;
    private int port;

    private final MultiWhiteList plugin;
    public MysqlManager(MultiWhiteList plugin) {
        this.plugin = plugin;
    }
    /**
     * Establishes a connection to the MySQL database using configuration parameters.
     */
    public void connect() {
        host = ConfigStorage.DATABASE_HOST;
        port = ConfigStorage.DATABASE_PORT;
        database = ConfigStorage.DATABASE_DATABASE;
        username = ConfigStorage.DATABASE_USERNAME;
        password = ConfigStorage.DATABASE_PASSWORD;

        url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            // empty
        }
    }
    /**
     * Closes the connection to the MySQL database.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Creates necessary tables in the database if they don't exist.
     *
     * @param connection The database connection to execute SQL statements.
     * @throws SQLException If a database error occurs during table creation.
     */
    public void createTables(Connection connection) throws SQLException {
        String[] createTableSQL = {
                "CREATE TABLE IF NOT EXISTS whitelist (" +
                        "uuid VARCHAR(36) NOT NULL UNIQUE," +
                        "player_name VARCHAR(64) NOT NULL" +
                        ")",
                "CREATE TABLE IF NOT EXISTS whitelist_settings (" +
                        "whitelist_status BOOLEAN NOT NULL" +
                        ")",
                "INSERT INTO whitelist_settings (whitelist_status) " +
                        "SELECT FALSE " +
                        "WHERE NOT EXISTS (SELECT 1 FROM whitelist_settings)"
        };
        try (Stream<String> sqlStream = Arrays.stream(createTableSQL)) {
            sqlStream.forEach(sql -> {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
