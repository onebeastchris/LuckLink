package dev.onechris.extension.lucklink;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.geyser.api.event.lifecycle.GeyserRegisterPermissionsEvent;
import org.geysermc.geyser.api.util.TriState;

import java.util.HashMap;
import java.util.Map;

public class GeyserRegisterPermissionsEventImpl implements GeyserRegisterPermissionsEvent {
    private final LuckPerms luckperms;
    private final Group group;

    boolean debug;
    boolean logPermissions;
    boolean addUnsetPermissions;

    public Map<String, TriState> permissions = new HashMap<>();

    public GeyserRegisterPermissionsEventImpl() {
        this.debug = ConfigLoader.config.isDebug();
        this.logPermissions = ConfigLoader.config.isLogPermissions();
        this.addUnsetPermissions = ConfigLoader.config.isAddUnsetPermissions();

        this.luckperms = LuckPermsProvider.get();
        this.group = luckperms.getGroupManager().getGroup(ConfigLoader.config.getDefaultGroup());
        if (group == null) {
            LuckLink.logger.warning("Unable to find the default group to add permissions!");
        } else {
            if (debug) LuckLink.logger.info("Found default group " + group.getName());
        }
    }

    @Override
    public void register(@NonNull String permission, @NonNull TriState defaultValue) {
        //noinspection ConstantValue - enforcing api
        if (permission == null || defaultValue == null) {
            LuckLink.logger.error("Permission (%s) or default value (%s) is null! These cannot be null. "
                    .formatted(permission, defaultValue));
            return;
        }

        // Safety net in case no default group is found. Should never occur.
        if (group == null) return;

        if (debug) LuckLink.logger.info(String.format("[Debug] permission: %s, default value: %s, group: %s", permission, defaultValue.name(), group.getName()));

        // For the permissions command
        permissions.put(permission, defaultValue);

        TriState value = defaultValue;
        if (value.equals(TriState.NOT_SET)) {
            if (addUnsetPermissions) {
                value = TriState.FALSE;
                if (debug) LuckLink.logger.info(String.format("[Debug] Default value was not set, setting permission %s to false", permission));
            } else {
                return;
            }
        }

        // Create the permission node
        PermissionNode node = PermissionNode
                .builder(permission)
                .value(value.equals(TriState.TRUE)) // should technically never be tristate.not_set at this point
                .build();

        // Check if the permission is already set
        Tristate tristate = group.data().contains(node, NodeEqualityPredicate.ONLY_KEY);
        if (!tristate.equals(Tristate.UNDEFINED)) {
            // Permission already exists as either true/false; no need to add it
            if (debug) LuckLink.logger.info(String.format("[Debug] Permission %s already exists with value %s", permission, tristate.name()));
            return;
        }

        // Add the permission, log if it fails
        DataMutateResult result = group.data().add(node);
        if (result.wasSuccessful()) {
            if (logPermissions) {
                String permissionValue = addUnsetPermissions && defaultValue.equals(TriState.NOT_SET) ? "FALSE (originally NOT_SET)" : defaultValue.name();
                LuckLink.logger.info(String.format("Registered permission %s with value %s",
                        permission, permissionValue));
            }
        } else {
            LuckLink.logger.warning(String.format("Failed to register permission %s for %s group with default value %s. Got response: %s.",
                    permission, group.getName(), defaultValue.name(), result.name()));
        }

        // Save the group to apply the changes
        luckperms.getGroupManager().saveGroup(group);
    }

    public Map<String, TriState> getPermissions() {
        return permissions;
    }
}
