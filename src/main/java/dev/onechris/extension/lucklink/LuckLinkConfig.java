package dev.onechris.extension.lucklink;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class LuckLinkConfig {

    @Comment("""
        The luckperms group to add the permissions to.
        By default, it is the luckperms default group.
        """)
    private String defaultGroup = "CHANGEME";

    @Comment("""
        If this is true, unset permissions (permissions that are not true or false) will be added to the default group as false.
        This can be useful to see which permissions exist, but can also be annoying if you have a lot of permissions.
        """)
    private boolean addUnsetPermissions = false;

    @Comment("""
        If this is true, the LuckLink extension will log every permission it adds to the default group.
        """)
    private boolean logPermissions = false;

    @Comment("""
        If true, debug messages will be logged.
        """)
    private boolean debug = false;

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public boolean isAddUnsetPermissions() {
        return addUnsetPermissions;
    }

    public boolean isLogPermissions() {
        return logPermissions;
    }

    public boolean isDebug() {
        return debug;
    }
}
