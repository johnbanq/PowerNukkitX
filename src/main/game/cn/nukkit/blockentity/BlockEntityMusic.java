package cn.nukkit.blockentity;

import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0);
        }
        if (!this.namedTag.contains("powered")) {
            this.namedTag.putBoolean("powered", false);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }

    public void changePitch() {
        this.namedTag.putByte("note", (this.namedTag.getByte("note") + 1) % 25);
    }

    public int getPitch() {
        return this.namedTag.getByte("note");
    }

    public void setPowered(boolean powered) {
        this.namedTag.putBoolean("powered", powered);
    }

    public boolean isPowered() {
        return this.namedTag.getBoolean("powered");
    }
}
