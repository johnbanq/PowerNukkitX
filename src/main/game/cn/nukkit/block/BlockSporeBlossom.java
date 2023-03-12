package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSporeBlossom extends BlockTransparent {
    @Override
    public String getName() {
        return "Spore Blossom";
    }

    @Override
    public int getId() {
        return SPORE_BLOSSOM;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (target.isSolid() && face == BlockFace.DOWN) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
