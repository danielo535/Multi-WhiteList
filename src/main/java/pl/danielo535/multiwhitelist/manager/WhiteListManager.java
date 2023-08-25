package pl.danielo535.multiwhitelist.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static pl.danielo535.multiwhitelist.MultiWhiteList.MessageColorize;
/**
 * Manages whitelist-related operations and UUID fetching.
 */
public class WhiteListManager {
    private final MysqlManager mysqlManager;
    public WhiteListManager(MysqlManager mysqlManager) {
        this.mysqlManager = mysqlManager;
    }
    // Methods for managing the whitelist

    /**
     * Adds a player to the whitelist in the database.
     *
     * @param playerName The name of the player to be added.
     * @param playerUUID The UUID of the player to be added.
     * @throws SQLException If a database error occurs during the operation.
     */
    public void addPlayerWhitelist(String playerName, UUID playerUUID) throws SQLException {
        String sql = "INSERT INTO whitelist (player_name, uuid) VALUES (?, ?)";
        try (PreparedStatement statement = mysqlManager.connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            getLogger().warning(MessageColorize("✘ Database connection failed!"));
        }
    }
    /**
     * Removes a player from the whitelist in the database.
     *
     * @param playerUUID The UUID of the player to be removed.
     * @return The number of rows affected.
     * @throws SQLException If a database error occurs during the operation.
     */
    public int removePlayerWhitelist(UUID playerUUID) throws SQLException {
        String sql = "DELETE FROM whitelist WHERE uuid = ?";
        try (PreparedStatement statement = mysqlManager.connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            getLogger().warning(MessageColorize("✘ Database connection failed!"));
            return 0;
        }
    }
    /**
     * Checks if a player is present in the whitelist.
     *
     * @param playerUUID The UUID of the player to check.
     * @return True if the player is whitelisted, false otherwise.
     * @throws SQLException If a database error occurs during the operation.
     */
    public boolean containPlayerWhitelist(UUID playerUUID) throws SQLException {
        String sql = "SELECT * FROM whitelist WHERE uuid = ?";
        try (PreparedStatement statement = mysqlManager.connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            getLogger().warning(MessageColorize("✘ Database connection failed!"));
            return false;
        }
    }
    /**
     * Retrieves the status of the whitelist.
     *
     * @return True if the whitelist is enabled, false if disabled.
     * @throws SQLException If a database error occurs during the operation.
     */
    public boolean getWhitelistStatus() throws SQLException {
        String sql = "SELECT whitelist_status FROM whitelist_settings";
        try (PreparedStatement statement = mysqlManager.connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() && resultSet.getBoolean("whitelist_status");
        } catch (SQLException e) {
            getLogger().warning(MessageColorize("✘ Database connection failed!"));
            return false;
        }
    }
    /**
     * Retrieves a list of whitelisted player names.
     *
     * @return A list containing whitelisted player names.
     * @throws SQLException If a database error occurs during the operation.
     */
    public List<String> getWhitelistedPlayers() throws SQLException {
        List<String> whitelistedPlayers = new ArrayList<>();
        String sql = "SELECT player_name FROM whitelist";

        try (PreparedStatement statement = mysqlManager.connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                whitelistedPlayers.add(resultSet.getString("player_name"));
            }
        }
        return whitelistedPlayers;
    }
    // Rest of your methods with descriptions...

    /**
     * Fetches the UUID of a player using their Minecraft username.
     *
     * @param playerName The Minecraft username of the player.
     * @return The UUID of the player, or null if not found.
     */
    public UUID fetchUUID(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (jsonObject.has("id")) {
                String uuidString = jsonObject.get("id").getAsString();
                return UUID.fromString(uuidString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"
                ));
            }
        } catch (IOException e) {
            //empty
        }
        UUID offlineUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
        if (offlineUUID != null) {
            return offlineUUID;
        } else {
            return null;
        }
    }
}
