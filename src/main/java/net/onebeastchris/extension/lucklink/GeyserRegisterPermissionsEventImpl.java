package net.onebeastchris.extension.lucklink;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.geysermc.geyser.api.event.lifecycle.GeyserRegisterPermissionsEvent;
import org.geysermc.geyser.api.util.TriState;

import static net.onebeastchris.extension.lucklink.LuckLink.logger;

public class GeyserRegisterPermissionsEventImpl implements GeyserRegisterPermissionsEvent {
    private final LuckPerms luckperms;
    private final Group group;

    public GeyserRegisterPermissionsEventImpl() {
        this.luckperms = LuckPermsProvider.get();
        this.group = luckperms.getGroupManager().getGroup(ConfigLoader.config.getDefaultGroup());
        if (group == null) {
            logger.warning("Unable to find the default group to add permissions!");
        }
    }

    @Override
    public void register(String permission, TriState defaultValue) {

        // Safety net in case no default group is found. Should never occur.
        if (group == null) return;

        if (ConfigLoader.config.isDebug()) {
            logger.info("[Debug] Registering permission " + permission + " with default value " + defaultValue.name() + " for group " + group.getName());
        }

        if (defaultValue.equals(TriState.NOT_SET)) {
            if (ConfigLoader.config.isAddUnsetPermissions()) {
                defaultValue = TriState.FALSE;
            } else {
                return;
            }
        }

        // Create the permission node
        PermissionNode node = PermissionNode
                .builder(permission)
                .value(Boolean.TRUE.equals(defaultValue.toBoolean()))
                .build();

        // Check if the permission is already set
        Tristate tristate = group.data().contains(node, NodeEqualityPredicate.ONLY_KEY);
        if (!tristate.equals(Tristate.UNDEFINED)) {
            // Permission already exists as either true/false; no need to add it
            return;
        }

        // Add the permission, log if it fails
        DataMutateResult result = group.data().add(node);
        if (!result.wasSuccessful()) {
            logger.warning("Failed to register permission " + permission + " for default group due to " + result.name() + " !");
        }

        // Save the group to apply the changes
        luckperms.getGroupManager().saveGroup(group);
    }
}
