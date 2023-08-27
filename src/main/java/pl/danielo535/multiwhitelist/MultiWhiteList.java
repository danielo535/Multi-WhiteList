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
    
    private MysqlManager mysqlManager;
    private WhiteListManager whiteListManager;
    
    @Override
    public void onEnable() {
        mysqlManager = new MysqlManager(this);
        whiteListManager = new WhiteListManager(mysqlManager);
        
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
                getLogger().info("---------------------------------");
                getLogger().info(" ");
                getLogger().info("✔ MultiWhiteList enabled...");
                getLogger().info("✔ Connected to the database!");
                getLogger().info(" ");
                getLogger().info("---------------------------------");
            } else {
                getLogger().warning("✘ Database connection failed!");
            }
        } catch (SQLException e) {
            getLogger().severe("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (mysqlManager != null && mysqlManager.connection != null) {
            mysqlManager.disconnect();
            getLogger().info("Database connection closed.");
        }
    }
    
    public static String MessageColorize(String message) {
        return ColorTranslator.translateColorCodes(message);
    }
}

