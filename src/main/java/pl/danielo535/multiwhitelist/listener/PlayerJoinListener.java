package pl.danielo535.multiwhitelist.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.danielo535.multiwhitelist.config.ConfigStorage;
import pl.danielo535.multiwhitelist.manager.MysqlManager;
import pl.danielo535.multiwhitelist.manager.WhiteListManager;

import java.sql.SQLException;
import java.util.UUID;

import static pl.danielo535.multiwhitelist.MultiWhiteList.MessageColorize;

public class PlayerJoinListener implements Listener {
    private final WhiteListManager whiteListManager;
    private final MysqlManager mysqlManager;
    public PlayerJoinListener(WhiteListManager whiteListManager, MysqlManager mysqlManager) {
        this.whiteListManager = whiteListManager;
        this.mysqlManager = mysqlManager;
    }
    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        try {
            if (mysqlManager.connection != null && !mysqlManager.connection.isClosed()) {
                if (whiteListManager.getWhitelistStatus()) {
                    UUID playerUUID = whiteListManager.fetchUUID(event.getName());
                    if (playerUUID != null && !whiteListManager.containPlayerWhitelist(playerUUID)) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageColorize(ConfigStorage.KICK_REASON));
                    }
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageColorize(ConfigStorage.KICK_ERROR$DATABASE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
