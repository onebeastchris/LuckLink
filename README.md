# LuckLink

This Geyser Extension allows you to automatically register permissions defined by Geyser - e.g. Geyser commands - 
or other Geyser extensions to Luckperms. This only needs to be done on platforms that do not have a native permission manager.
These include Velocity, BungeeCord, and Fabric. Spigot/Paper, NeoForge, Geyser-Standalone and Geyser-ViaProxy have native permission managers.

## This extensions only works with Geyser's build 631+

### Installation
Download the latest .jar from the releases tab here, and place it in your Geyser's `extensions` folder.

### Configuration
No configuration necessary. Just ensure that you have `Luckperms` installed on your server.

There are some configuration options though:
- `add-unset-permissions` : If true, all permissions that by default are not set would be registered as false. If false, they are ignored
- `default-group`: The luckperms group to register permissions to. Can be the default luckperms group, or a custom one.
- `log-permissions`: If set to true, all permissions that are registered will be logged to the console.
- `debug`: Enables debug mode.

### Commands
- `/lucklink reload`: Reloads the extension's configuration & re-registers all permissions. 
Permission: `lucklink.reload`
- `/lucklink permissions`: Lists all permissions that the extension received (includes Geyser commands/extension permissions)
Permission: `lucklink.permissions`

### Notes
This extension basically has a one-time use: Register permissions once to Luckperms, so commands/extensions can function properly. 
If any permission is already set - either true/false - then this extension will *not* overwrite them!
However, the side effect is that you can't just remove a default permission from Luckperms, as it will be re-added on the next reload. 
Hence, you would need to overwrite default true permissions by setting them manually to false.

Example:
If you e.g. want to restrict people from using default commands such as `/geyser offhand` (given to everyone by default),
you would need to set the permission `geyser.command.offhand` to false in Luckperms. The extension will then not overwrite it with the default of "true".

For support with this extension, please join this [support discord](https://discord.gg/WdmrRHRJhS).