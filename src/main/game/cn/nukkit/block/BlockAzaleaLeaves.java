package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockAzaleaLeaves extends BlockLeaves {

    public static final BlockProperties PROPERTIES = new BlockProperties(BlockLeaves.PERSISTENT, BlockLeaves.UPDATE);

    public BlockAzaleaLeaves() {
        this(0);
    }

    public BlockAzaleaLeaves(int meta) {
        super(meta);
    }


    @Override
    public int getId() {
        return AZALEA_LEAVES;
    }

    @Override
    public String getName() {
        return "Azalea Leaves";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean canHarvest(Item item) {
        return item.isShears();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    /*这里写木质类型为OAK只是为了获取凋落物时的概率正确，并不代表真的就是橡木*/
    @Override
    public WoodType getType() {
        return WoodType.OAK;
    }

    @Override
    protected Item getSapling() {
        return Block.get(AZALEA).toItem();
    }
}
