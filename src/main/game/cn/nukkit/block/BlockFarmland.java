package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.event.block.FarmLandDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockFarmland extends BlockTransparentMeta {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final IntBlockProperty MOISTURIZED_AMOUNT = new IntBlockProperty("moisturized_amount", false, 7);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(MOISTURIZED_AMOUNT);

    public BlockFarmland() {
        this(0);
    }

    public BlockFarmland(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Farmland";
    }

    @Override
    public int getId() {
        return FARMLAND;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.up().isSolid()) {
                var farmEvent = new FarmLandDecayEvent(null, this);
                this.level.getServer().getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return 0;

                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true);

                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Vector3 v = new Vector3();
            if (this.level.getBlock(v.setComponents(x, this.y + 1, z)) instanceof BlockCrops) {
                return 0;
            }

            boolean found = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                for (int x = (int) this.x - 4; x <= this.x + 4; x++) {
                    for (int z = (int) this.z - 4; z <= this.z + 4; z++) {
                        for (int y = (int) this.y; y <= this.y + 1; y++) {
                            if (z == this.z && x == this.x && y == this.y) {
                                continue;
                            }

                            v.setComponents(x, y, z);
                            int block = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ());

                            if (block == FLOWING_WATER || block == STILL_WATER || block == ICE_FROSTED) {
                                found = true;
                                break;
                            } else {
                                block = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ(), 1);
                                if (block == FLOWING_WATER || block == STILL_WATER || block == ICE_FROSTED) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            Block block = this.level.getBlock(v.setComponents(x, y - 1, z));
            int damage = this.getDamage();
            if (found || block instanceof BlockWater || block instanceof BlockIceFrosted) {
                if (damage < 7) {
                    this.setDamage(7);
                    this.level.setBlock(this, this, false, damage == 0);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (damage > 0) {
                this.setDamage(damage - 1);
                this.level.setBlock(this, this, false, damage == 1);
            } else {
                var farmEvent = new FarmLandDecayEvent(null, this);
                this.level.getServer().getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return 0;
                this.level.setBlock(this, Block.get(Block.DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.DIRT));
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return true")
    @Override
    public boolean isTransparent() {
        return true;
    }
}
