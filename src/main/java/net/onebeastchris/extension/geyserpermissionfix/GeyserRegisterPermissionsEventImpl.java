package net.onebeastchris.extension.geyserpermissionfix;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.geysermc.geyser.api.event.lifecycle.GeyserRegisterPermissionsEvent;
import org.geysermc.geyser.api.util.TriState;

import static net.onebeastchris.extension.geyserpermissionfix.GeyserPermissionFix.logger;

public class GeyserRegisterPermissionsEventImpl implements GeyserRegisterPermissionsEvent {
    private final LuckPerms luckperms;
    private final Group group;

    public GeyserRegisterPermissionsEventImpl() {
        this.luckperms = LuckPermsProvider.get();
        this.group = luckperms.getGroupManager().getGroup("default");
        if (group == null) {
            logger.warning("Unable to find default group to add permissions to!");
        }
    }

    @Override
    public void register(String permission, TriState defaultValue) {
        PermissionNode node = PermissionNode
                .builder(permission)
                .value(Boolean.TRUE.equals(defaultValue.toBoolean()))
                .build();

        Tristate tristate = group.data().contains(node, NodeEqualityPredicate.ONLY_KEY);
        if (!tristate.equals(Tristate.UNDEFINED)) {
            // Permission already exists as either true/false; no need to add it
            return;
        }

        DataMutateResult result = group.data().add(node);
        if (!result.wasSuccessful()) {
            logger.warning("Failed to register permission " + permission + " for default group due to " + result.name() + " !");
        }

        luckperms.getGroupManager().saveGroup(group);
    }
}
