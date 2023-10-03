package net.onebeastchris.extension.lucklink;

import lombok.Getter;
import org.geysermc.geyser.api.extension.Extension;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class ConfigLoader {
    public static LuckLinkConfig config;
    public static void load(Extension extension) {

        if (!extension.dataFolder().toFile().exists()) {
            boolean success = extension.dataFolder().toFile().mkdirs();
            if (!success) {
                extension.logger().severe("Could not create data folder! Not loading config.");
                extension.disable();
                return;
            } else {
                extension.logger().info("""
                    Created data folder! Check out the config.conf file under /extensions/lucklink/ for LuckLink configuration options.
                    """);
            }
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(extension.dataFolder().resolve("config.conf"))
                .defaultOptions(opts -> opts.header("LuckLink Config "))
                .prettyPrinting(true)
                .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(LuckLinkConfig.class);
            loader.save(node);
        } catch (ConfigurateException e) {
            extension.logger().severe("Could not load config!");
            e.printStackTrace();
            return;
        }
        extension.logger().info("Loaded config!");
    }
}
