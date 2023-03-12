package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.blockstate.BlockState;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockWoodMangrove extends BlockWood {
    public static final String STRIPPED_BIT = "stripped_bit";

    public static final BlockProperties PROPERTIES = new BlockProperties(
            new BooleanBlockProperty(STRIPPED_BIT, true),
            PILLAR_AXIS
    );

    public BlockWoodMangrove() {
    }

    @Override
    public int getId() {
        return MANGROVE_WOOD;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return (isStripped() ? "Stripped " : "") + "Mangrove Wood";
    }

    @PowerNukkitOnly
    @Override
    public WoodType getWoodType() {
        return null;
    }

    @PowerNukkitOnly
    @Override
    public void setWoodType(WoodType woodType) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isStripped() {
        return getBooleanValue(STRIPPED_BIT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStripped(boolean stripped) {
        setBooleanValue(STRIPPED_BIT, stripped);
    }

    @PowerNukkitOnly
    @Override
    protected BlockState getStrippedState() {
        return BlockState.of(STRIPPED_MANGROVE_WOOD).withProperty(PILLAR_AXIS, getPillarAxis());
    }
}
