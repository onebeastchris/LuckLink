package net.onebeastchris.extension.lucklink;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.extension.ExtensionLogger;
import org.geysermc.geyser.api.util.PlatformType;

public class LuckLink implements Extension {

    public static ExtensionLogger logger;

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

        logger().info("Enabling LuckLink!");
        logger = logger();
    }

    @Subscribe
    public void onPostInit(GeyserPostInitializeEvent event) {
        this.geyserApi().eventBus().fire(new GeyserRegisterPermissionsEventImpl());
    }
}
