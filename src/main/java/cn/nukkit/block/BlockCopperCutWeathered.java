package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperCutWeathered extends BlockCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperCutWeathered() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Weathered Cut Copper";
    }

    @Override
    public int getId() {
        return WEATHERED_CUT_COPPER;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
