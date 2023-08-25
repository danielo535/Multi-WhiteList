package pl.danielo535.multiwhitelist.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.danielo535.multiwhitelist.config.ConfigStorage;
import pl.danielo535.multiwhitelist.manager.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static pl.danielo535.multiwhitelist.MultiWhiteList.MessageColorize;

/**
 * Command handling various actions related to player whitelist.
 * Operates on a MySQL database and manages whitelist settings, as well as adding/removing players from the whitelist.
 * Supports subcommands: on, off, reload, status, list, add, remove.
 */
public class WhiteListCommand implements CommandExecutor {
    private final WhiteListManager whiteListManager;
    private final MysqlManager mysqlManager;
    public WhiteListCommand(WhiteListManager whiteListManager, MysqlManager mysqlManager) {
        this.whiteListManager = whiteListManager;
        this.mysqlManager = mysqlManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageColorize(ConfigStorage.MESSAGES_USAGE));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (sender.hasPermission("MultiWhiteList.*")) {
            if (subCommand.equals("on") || subCommand.equals("off") || subCommand.equals("reload")
                    || subCommand.equals("status") || subCommand.equals("list") || subCommand.equals("add") || subCommand.equals("remove")) {
                handleSubcommand(sender, subCommand, args);
            } else {
                sender.sendMessage(MessageColorize(ConfigStorage.MESSAGES_USAGE));
            }
        } else {
            sender.sendMessage(MessageColorize(ConfigStorage.MESSAGES_NO$PERMISSION));
            return false;
        }
        return false;
    }

    /**
     * Handles subcommands of the /whitelist command.
     *
     * @param sender      Command sender.
     * @param subCommand  Subcommand entered by the player.
     * @param args        Command arguments.
     */
    private void handleSubcommand(CommandSender sender, String subCommand, String[] args) {
        if (subCommand.equals("on")) {
            toggleWhitelistStatus(sender, true);
        } else if (subCommand.equals("off")) {
            toggleWhitelistStatus(sender, false);
        } else if (subCommand.equals("reload")) {
            reloadWhitelist(sender);
        } else if (subCommand.equals("status")) {
            showWhitelistStatus(sender);
        } else if (subCommand.equals("list")) {
            showWhitelistedPlayers(sender);
        } else if (subCommand.equals("add") || subCommand.equals("remove")) {
            handleAddOrRemove(sender, subCommand, args);
        }
    }
    /**
     * Toggles whitelist status.
     *
     * @param player  Command sender.
     * @param status  New whitelist status (enabled or disabled).
     */
    private void toggleWhitelistStatus(CommandSender player, boolean status) {
        try {
            if (mysqlManager.connection != null) {
                if (whiteListManager.getWhitelistStatus() != status) {
                    String sql = "UPDATE whitelist_settings SET whitelist_status = ?";
                    PreparedStatement updateStatement = mysqlManager.connection.prepareStatement(sql);
                    updateStatement.setBoolean(1, status);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                    player.sendMessage(MessageColorize(status ? ConfigStorage.MESSAGES_ENABLE : ConfigStorage.MESSAGES_DISABLE));
                } else {
                    player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_ACTIVE.replace("%status%", status ? ConfigStorage.MESSAGES_CUSTOM$STATUS_ENABLE : ConfigStorage.MESSAGES_CUSTOM$STATUS_DISABLE)));
                }
            } else {
                player.sendMessage(MessageColorize("&c✘ Database connection null!"));
            }
        } catch (SQLException e) {
            handleDatabaseError(player, e);
        }
    }
    /**
     * Reloads whitelist settings and the database.
     *
     * @param player  Command sender.
     */
    private void reloadWhitelist(CommandSender player) {
        ConfigStorage.reload();
        if (mysqlManager.connection != null) {
            mysqlManager.disconnect();
        }
        mysqlManager.connect();
        try {
            if (mysqlManager.connection != null && !mysqlManager.connection.isClosed()) {
                mysqlManager.createTables(mysqlManager.connection);
            }
        } catch (SQLException e) {
            // empty
        }
        player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_RELOAD));
        try {
            if (mysqlManager.connection == null || mysqlManager.connection.isClosed()) {
                player.sendMessage(MessageColorize("&c✘ Database connection failed!"));
            } else {
                player.sendMessage(MessageColorize("&a✔ Connected to the database!"));
            }
        } catch (SQLException e) {
            // empty
        }
    }
    /**
     * Displays whitelist status.
     *
     * @param player  Command sender.
     */
    private void showWhitelistStatus(CommandSender player) {
        try {
            if (mysqlManager.connection != null && !mysqlManager.connection.isClosed()) {
                player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_STATUS.replace("%status%", (whiteListManager.getWhitelistStatus() ? ConfigStorage.MESSAGES_CUSTOM$STATUS_ENABLE : ConfigStorage.MESSAGES_CUSTOM$STATUS_DISABLE))));
            } else {
                player.sendMessage(MessageColorize("&c✘ Database connection null!"));
            }
        } catch (SQLException e) {
            handleDatabaseError(player, e);
        }
    }
    /**
     * Displays players on the whitelist.
     *
     * @param player  Command sender.
     */
    private void showWhitelistedPlayers(CommandSender player) {
        try {
            if (mysqlManager.connection != null && !mysqlManager.connection.isClosed()) {
                List<String> whitelistedPlayers = whiteListManager.getWhitelistedPlayers();
                player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_LIST
                        .replace("%size%", String.valueOf(whitelistedPlayers.size()))
                        .replace("%players%", String.valueOf(whitelistedPlayers))));
            } else {
                player.sendMessage(MessageColorize("&c✘ Database connection null!"));
            }
        } catch (SQLException e) {
            handleDatabaseError(player, e);
        }
    }
    /**
     * Handles adding or removing a player from the whitelist.
     *
     * @param player      Command sender.
     * @param subCommand  Subcommand (add or remove).
     * @param args        Command arguments.
     */
    private void handleAddOrRemove(CommandSender player, String subCommand, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_USAGE));
            return;
        }

        String playerName = args[1];
        UUID playerUUID = whiteListManager.fetchUUID(playerName);

        if (playerUUID == null) {
            player.sendMessage("Failed to fetch UUID for the player.");
            return;
        }

        if (subCommand.equals("add")) {
            handleAdd(player, playerName, playerUUID);
        } else if (subCommand.equals("remove")) {
            handleRemove(player, playerName, playerUUID);
        }
    }
    /**
     * Handles adding a player to the whitelist.
     *
     * @param player      Command sender.
     * @param playerName  Player name to be added.
     * @param playerUUID  Player UUID.
     */
    private void handleAdd(CommandSender player, String playerName, UUID playerUUID) {
        try {
            if (mysqlManager.connection != null && !mysqlManager.connection.isClosed()) {
                if (!whiteListManager.containPlayerWhitelist(playerUUID)) {
                    whiteListManager.addPlayerWhitelist(playerName, playerUUID);
                    player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_ADD.replace("%player%", playerName)));
                } else {
                    player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_ERROR$ADD.replace("%player%", playerName)));
                }
            } else {
                handleDatabaseWarning(player);
            }
        } catch (SQLException e) {
            handleDatabaseError(player, e);
        }
    }
    /**
     * Handles removing a player from the whitelist.
     *
     * @param player      Command sender.
     * @param playerName  Player name to be removed.
     * @param playerUUID  Player UUID.
     */
    private void handleRemove(CommandSender player, String playerName, UUID playerUUID) {
        try {
            if (mysqlManager.connection != null && !mysqlManager.connection.isClosed()) {
                int rowsAffected = whiteListManager.removePlayerWhitelist(playerUUID);

                if (rowsAffected > 0) {
                    player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_REMOVE.replace("%player%", playerName)));
                } else {
                    player.sendMessage(MessageColorize(ConfigStorage.MESSAGES_ERROR$REMOVE.replace("%player%", playerName)));
                }
            } else {
                handleDatabaseWarning(player);
            }
        } catch (SQLException e) {
            handleDatabaseError(player, e);
        }
    }
    /**
     * Handles the situation when a database connection could not be established.
     *
     * @param player  Command sender.
     */
    private void handleDatabaseWarning(CommandSender player) {
        getLogger().warning(MessageColorize("✘ Database connection null!"));
        player.sendMessage(MessageColorize("&c✘ Database connection null!"));
    }
    /**
     * Handles database errors.
     *
     * @param player  Command sender.
     * @param e       SQLException indicating the error.
     */
    private void handleDatabaseError(CommandSender player, SQLException e) {
        player.sendMessage(MessageColorize("&c✘ Database connection null!"));
        getLogger().warning(MessageColorize("✘ Database connection null!"));
        e.printStackTrace();
    }
}

