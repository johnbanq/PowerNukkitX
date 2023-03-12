package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kevims KCodeYT
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockEntitySculkShrieker extends BlockEntity {

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public BlockEntitySculkShrieker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SHRIEKER;
    }

}
