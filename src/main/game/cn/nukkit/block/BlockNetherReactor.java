package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNetherReactor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockNetherReactor extends BlockSolid implements BlockEntityHolder<BlockEntityNetherReactor> {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockNetherReactor() {
        // Does nothing
    }
    
    @Override
    public int getId() {
        return NETHER_REACTOR;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.NETHER_REACTOR;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityNetherReactor> getBlockEntityClass() {
        return BlockEntityNetherReactor.class;
    }

    @Override
    public String getName() {
        return "Nether Reactor Core";
    }
    
    @Override
    public double getHardness() {
        return 10;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    Item.get(ItemID.DIAMOND, 0, 3),
                    Item.get(ItemID.IRON_INGOT, 0, 6)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }
}
