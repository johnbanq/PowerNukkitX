package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockDoubleSlabBase extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabBase(int meta) {
        super(meta);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public BlockDoubleSlabBase(){}

    @Override
    public String getName() {
        return "Double "+getSlabName()+" Slab";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract String getSlabName();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getSingleSlabId();

    @Override
    public Item toItem() {
        return getCurrentState().forItem().withBlockId(getSingleSlabId()).asItemBlock();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean isCorrectTool(Item item) {
        return canHarvestWithHand() || canHarvest(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isCorrectTool(item)) {
            Item slab = toItem();
            slab.setCount(2);
            return new Item[]{ slab };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
