package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;

import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockFallableMeta extends BlockFallable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFallableMeta() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFallableMeta(int meta) {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true);
        }
    }

    @Since("1.4.0.0-PN")
    @NotNull
    @PowerNukkitOnly
    @Override
    public abstract BlockProperties getProperties();
}
