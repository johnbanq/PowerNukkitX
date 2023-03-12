package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;

import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockStem extends BlockLog {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStem(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }
}
