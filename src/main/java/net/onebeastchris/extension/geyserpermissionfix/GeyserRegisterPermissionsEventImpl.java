package net.onebeastchris.extension.geyserpermissionfix;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PermissionNode;
import org.geysermc.geyser.api.event.lifecycle.GeyserRegisterPermissionsEvent;
import org.geysermc.geyser.api.util.TriState;

import static net.onebeastchris.extension.geyserpermissionfix.GeyserPermissionFix.logger;

public class GeyserRegisterPermissionsEventImpl implements GeyserRegisterPermissionsEvent {

    private final LuckPerms luckperms;

    public GeyserRegisterPermissionsEventImpl() {
        this.luckperms = LuckPermsProvider.get();
    }

    @Override
    public void register(String permission, TriState defaultValue) {
        PermissionNode.Builder node = PermissionNode
                .builder(permission);

        if (defaultValue.toBoolean() != null) {
            node.value(defaultValue.toBoolean());
        }

        Group group = luckperms.getGroupManager().getGroup("default");
        if (group != null) {
            DataMutateResult result = group.data().add(node.build());
            if (!result.wasSuccessful()) {
                logger.warning("Failed to register permission " + permission + " for group default! : " + result.name());
            }
            luckperms.getGroupManager().saveGroup(group);
        }
    }
}
