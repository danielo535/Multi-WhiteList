package pl.danielo535.multiwhitelist.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteCommand implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("MultiWhiteList.*")) {
                List<String> tabCompletions = new ArrayList<>();
                String[] subCommands = {"on", "off", "add", "remove", "status", "list", "reload"};
                for (String subCommand : subCommands) {
                    if (subCommand.startsWith(args[0].toLowerCase())) {
                        tabCompletions.add(subCommand);
                    }
                }
                return tabCompletions;
            } else {
                return null;
            }
        }
        return null;
    }
}
