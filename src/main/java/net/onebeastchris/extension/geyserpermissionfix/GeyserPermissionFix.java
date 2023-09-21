package net.onebeastchris.extension.geyserpermissionfix;

import org.geysermc.event.bus.BaseBus;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.extension.ExtensionLogger;
import org.geysermc.geyser.api.util.PlatformType;

public class GeyserPermissionFix implements Extension {

    public static ExtensionLogger logger;

    @Subscribe
    public void onPreInit(GeyserPreInitializeEvent event) {

        // Check: Using platform that doesn't need this extension
        PlatformType platformType = this.geyserApi().platformType();
        if (platformType.equals(PlatformType.STANDALONE) || platformType.equals(PlatformType.SPIGOT)) {
            logger().warning("GeyserPermissionFix is not needed on " + platformType.platformName() + " , since it registers Permissions on it's own.");
            disable();
        }

        // Check: Is LuckPerms installed?
        try {
            Class.forName("net.luckperms.api.LuckPerms");
        } catch (ClassNotFoundException e) {
            logger().error("LuckPerms API not found! Disabling GeyserPermissionFix.");
            disable();
        }

        logger().info("GeyserPermissionFix is enabled!");
        logger = logger();
    }

    @Subscribe
    public void onPostInit(GeyserPostInitializeEvent event) {
        // Doesnt work, thorws a method not found :/
        //this.geyserApi().eventBus().fire(new GeyserRegisterPermissionsEventImpl());

        ((BaseBus) this.geyserApi().eventBus()).fire(new GeyserRegisterPermissionsEventImpl());
    }
}
