package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * http://minecraft.gamepedia.com/End_Rod
 *
 * @author PikyCZ
 */
public class BlockEndRod extends BlockTransparentMeta implements Faceable {

    @Since("1.5.0.0-PN")
    @PowerNukkitOnly
    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    public BlockEndRod() {
        this(0);
    }

    public BlockEndRod(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "End Rod";
    }

    @Override
    public int getId() {
        return END_ROD;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return this.x + 0.4;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.4;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        int[] faces = {0, 1, 3, 2, 5, 4};
        this.setDamage(faces[player != null ? face.getIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @PowerNukkitDifference(info = "Fixed the direction", since = "1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

}
