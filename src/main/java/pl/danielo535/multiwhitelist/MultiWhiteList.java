package pl.danielo535.multiwhitelist;

import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.plugin.java.JavaPlugin;
import pl.danielo535.multiwhitelist.command.TabCompleteCommand;
import pl.danielo535.multiwhitelist.command.WhiteListCommand;
import pl.danielo535.multiwhitelist.config.ConfigStorage;
import pl.danielo535.multiwhitelist.listener.PlayerJoinListener;
import pl.danielo535.multiwhitelist.manager.*;

import java.sql.*;

public final class MultiWhiteList extends JavaPlugin {
    MysqlManager mysqlManager = new MysqlManager(this);
    WhiteListManager whiteListManager = new WhiteListManager(mysqlManager);
    @Override
    public void onEnable() {
        new Metrics(this,19649);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(whiteListManager, mysqlManager), this);
        getCommand("whitelist").setExecutor(new WhiteListCommand(whiteListManager,mysqlManager));
        getCommand("whitelist").setTabCompleter(new TabCompleteCommand());

        ConfigStorage.createDefaultFiles(this);
        ConfigStorage.load();

        try {
            mysqlManager.connect();
            if (mysqlManager.connection != null) {
                mysqlManager.createTables(mysqlManager.connection);
            }
            getLogger().info("---------------------------------");
            getLogger().info(" ");
            getLogger().info("✔ MultiWhiteList enabled...");
            if (mysqlManager.connection != null) {
                getLogger().info("✔ Connected to the database!");
            } else {
                getLogger().warning("✘ Database connection failed!");
            }
            getLogger().info(" ");
            getLogger().info("---------------------------------");

        } catch (SQLException e) {
            getLogger().severe("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (mysqlManager.connection != null) {
            mysqlManager.disconnect();
            getLogger().info("Database connection closed.");
        }
    }
    public static String MessageColorize(String message) {
        return ColorTranslator.translateColorCodes(message);
    }
}

