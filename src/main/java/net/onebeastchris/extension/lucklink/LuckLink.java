package net.onebeastchris.extension.lucklink;

import org.geysermc.event.FireResult;
import org.geysermc.event.bus.BaseBus;
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

public class LuckLink implements Extension {

    public static ExtensionLogger logger;

    public static ConfigLoader configLoader;

    @Subscribe
    public void onPreInit(GeyserPreInitializeEvent event) {
        // Check: Using platform that doesn't need this extension
        PlatformType platformType = this.geyserApi().platformType();
        if (platformType.equals(PlatformType.STANDALONE) || platformType.equals(PlatformType.SPIGOT)) {
            logger().warning("LuckLink is not needed on " + platformType.platformName() + ", since this platform registers permissions on its own.");
            disable();
        }

        // Check: Is LuckPerms installed?
        try {
            Class.forName("net.luckperms.api.LuckPerms");
        } catch (ClassNotFoundException e) {
            logger().error("LuckPerms API not found! Disabling LuckLink.");
            disable();
        }

        // Load config
        ConfigLoader.load(this);

        logger().info("Enabling LuckLink!");
        logger = logger();
    }

    @Subscribe
    public void onPostInit(GeyserPostInitializeEvent unused) {
        FireResult result = this.geyserApi().eventBus().fire(new GeyserRegisterPermissionsEventImpl());
        if (!result.success()) {
            logger().error("Please contact the developer of LuckLink & send them this log!");
            disable();
        }
    }

    @Subscribe
    public void onGeyserCommandsDefine(GeyserDefineCommandsEvent event) {
        event.register(Command.builder(this)
                .name("reload")
                .description("Reloads the LuckLink config and re-collects all permissions.")
                .permission("lucklink.reload", TriState.FALSE)
                .bedrockOnly(false)
                .executableOnConsole(true)
                .source(CommandSource.class)
                .executor((sender, command, args) -> {
                    // Load config
                    ConfigLoader.load(this);

                    // Trigger permission re-collecting
                    // Null event seems problematic, but it isn't used anyway
                    onPostInit(null);

                    sender.sendMessage("Reloaded LuckLink config!");
                })
                .build());
    }
}
