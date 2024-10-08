package dev.onechris.extension.lucklink;

import org.geysermc.event.FireResult;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.command.Command;
import org.geysermc.geyser.api.command.CommandSource;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCommandsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.extension.ExtensionLogger;
import org.geysermc.geyser.api.util.PlatformType;
import org.geysermc.geyser.api.util.TriState;

import java.util.*;

public class LuckLink implements Extension {

    private static final Set<PlatformType> SUPPORTED_PLATFORMS = Set.of(PlatformType.BUNGEECORD,
            PlatformType.FABRIC, PlatformType.VELOCITY);

    public static ExtensionLogger logger;

    public Map<String, TriState> permissions = new HashMap<>();

    @Subscribe
    public void onPreInit(GeyserPreInitializeEvent event) {
        // Check: Using platform that doesn't need this extension
        PlatformType platformType = this.geyserApi().platformType();
        if (!SUPPORTED_PLATFORMS.contains(platformType)) {
            logger().warning("LuckLink is not needed on " + platformType.platformName() + ", since this platform registers permissions on its own.");
            disable();
            return;
        }

        // Check: Is LuckPerms installed?
        try {
            Class.forName("net.luckperms.api.LuckPerms");
        } catch (ClassNotFoundException e) {
            logger().error("LuckPerms not found! Disabling LuckLink.");
            disable();
            return;
        }

        // Check: Is Geyser updated?
        try {
            Class.forName("org.geysermc.geyser.api.event.lifecycle.GeyserRegisterPermissionsEvent");
        } catch (ClassNotFoundException e) {
            logger().error("Geyser is not updated! Disabling LuckLink.");
            disable();
            return;
        }

        // Load config
        ConfigLoader.load(this);

        logger().info("Enabling LuckLink!");
        logger = logger();
    }

    @Subscribe
    public void onPostInit(GeyserPostInitializeEvent unused) {
        // Check: Config loaded?
        if (ConfigLoader.config == null) {
            logger().error("Config not loaded! Disabling LuckLink.");
            disable();
            return;
        }

        if (ConfigLoader.config.getDefaultGroup().equals("CHANGEME")) {
            logger().error("Default group not configured!");
            logger().error("Please open the LuckLink config, and configure it.");
            logger().error("Disabling LuckLink.");
            this.disable();
            return;
        }

        GeyserRegisterPermissionsEventImpl event = new GeyserRegisterPermissionsEventImpl();
        FireResult result = this.geyserApi().eventBus().fire(event);
        this.permissions = event.getPermissions();
        if (!result.success()) {
            logger().error("Please contact the developer of LuckLink & send them this log!");
            disable();
        }
    }

    @Subscribe
    public void onGeyserCommandsDefine(GeyserDefineCommandsEvent event) {

        // reload command
        event.register(Command.builder(this)
                .name("reload")
                .description("Reloads the LuckLink config and re-collects all permissions.")
                .permission("lucklink.reload", TriState.FALSE)
                .bedrockOnly(false)
                .source(CommandSource.class)
                .executor((sender, command, args) -> {
                    // Load config
                    ConfigLoader.load(this);
                    if (ConfigLoader.config == null) {
                        logger().error("Config not loaded! Disabling LuckLink.");
                        sender.sendMessage("An issue occurred while loading the LuckLink config! Please re-generate it.");
                        disable();
                        return;
                    }

                    // Trigger permission re-collecting
                    // Null event seems problematic, but it isn't used anyway
                    onPostInit(null);
                    sender.sendMessage("Reloaded LuckLink config!");
                })
                .build());

        // Permissions command
        event.register(Command.builder(this)
                .name("permissions")
                .aliases(Collections.singletonList("perms"))
                .bedrockOnly(false)
                .source(CommandSource.class)
                .permission("lucklink.permissions", TriState.NOT_SET)
                .executor((source, command, args) -> {
                    if (this.permissions.isEmpty()) {
                        source.sendMessage("No permissions received!");
                    } else {
                        source.sendMessage("Registered permissions:");
                        for (Map.Entry<String, TriState> entry : this.permissions.entrySet()) {
                            source.sendMessage(entry.getKey() + " with default value: " + entry.getValue().name());
                        }
                    }
                })
                .build());
    }
}