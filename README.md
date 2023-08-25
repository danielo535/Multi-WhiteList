<div align="center">

![Logo](https://imgur.com/62ajjCx.png)

</div>


# Multi-WhiteList Plugin

![Plugin Version](https://img.shields.io/badge/Version-1.0-blue.svg)
![Minecraft Version](https://img.shields.io/badge/Minecraft-1.18.2-green.svg)
![Spigot Version](https://img.shields.io/badge/Spigot-1.18.2-orange.svg)

<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/supported/spigot_46h.png" height="35"></a>
<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/supported/paper_46h.png" height="35"></a>
<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/supported/purpur_46h.png" height="35"></a>

## Description

The **Multi-WhiteList Plugin** is a tool designed to simplify the management of player whitelists across multiple Minecraft servers. This plugin enables shared management of a whitelist containing players authorized to join any of the connected servers.

## Features

- Shared player whitelist across Minecraft servers.
- Easy addition and removal of players from the whitelist.
- Configurable language-specific messages for various actions.
- Command `/whitelist` support for whitelist management.
- Protection against abuse through permissions.

## Installation

1. Download the latest version of the plugin from [releases](link_to_releases).
2. Place the plugin file into the `plugins` folder within your Minecraft server directory.
3. Start or restart the server.

## Commands

- `/whitelist on|off` - Enables or disables the whitelist.
- `/whitelist add <player>` - Adds a player to the whitelist.
- `/whitelist remove <player>` - Removes a player from the whitelist.
- `/whitelist list` - Displays the list of players on the whitelist.
- `/whitelist reload` - Reloads the plugin configuration.

## Configuration

In the `config.yml` file, you can customize various plugin settings, such as messages, database settings, etc.

## Permissions

- `MultiWhitelist.*` - Allows the usage of all plugin commands.


## Key Features

- **Centralized Whitelisting:** Multi-WhiteList allows you to maintain a single, centralized whitelist database that can be accessed by all the servers in your network. No more redundant management of whitelists on each individual server.

- **Effortless Configuration:** Easily configure the plugin through a user-friendly configuration file. Define database connection details, customize messages, and tailor the behavior of the plugin to suit your server's needs.

- **Dynamic Access Control:** Grant or revoke access to players across all servers simultaneously. Whether you're adding new players or removing old ones, Multi-WhiteList ensures consistent access control across your network.

- **Real-time Synchronization:** Thanks to its intelligent synchronization mechanism, changes to the whitelist are instantly propagated to all connected servers. No more delays or inconsistencies in player access.

- **Customizable Messages:** Craft personalized messages to communicate with players as they interact with the whitelist. Whether it's enabling, disabling, or managing players, the plugin's customizable messages ensure a seamless player experience.

- **Error Handling:** Multi-WhiteList comes equipped with robust error handling, providing clear and informative error messages when issues arise. This helps administrators troubleshoot problems quickly and efficiently.

- **User-Friendly Commands:** Intuitive commands make it easy for administrators to manage the whitelist. Add, remove, enable, disable, and reload the whitelist with simple, easy-to-remember commands.


## Author

Written by [danielo535](https://github.com/danielo535).

## Links
* [SpigotMC](https://www.spigotmc.org/resources/multi-whitelist.112275/)
* [bStats](https://bstats.org/plugin/bukkit/Multi-WhiteList/19649)

