package cn.nukkit.blockentity;

import cn.nukkit.api.Since;
import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class BlockEntityComparator extends BlockEntity {

    private int outputSignal;

    public BlockEntityComparator(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!namedTag.contains("OutputSignal")) {
            namedTag.putInt("OutputSignal", 0);
        }

        this.outputSignal = namedTag.getInt("OutputSignal");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock() instanceof BlockRedstoneComparator;
    }

    public int getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("OutputSignal", this.outputSignal);
    }
}
