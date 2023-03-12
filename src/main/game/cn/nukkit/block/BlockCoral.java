package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.CoralType;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.blockproperty.CommonBlockProperties.PERMANENTLY_DEAD;

@PowerNukkitOnly
public class BlockCoral extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<CoralType> COLOR = new ArrayBlockProperty<>("coral_color", true, CoralType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(COLOR, PERMANENTLY_DEAD);

    @PowerNukkitOnly public static final int TYPE_TUBE = 0;
    @PowerNukkitOnly public static final int TYPE_BRAIN = 1;
    @PowerNukkitOnly public static final int TYPE_BUBBLE = 2;
    @PowerNukkitOnly public static final int TYPE_FIRE = 3;
    @PowerNukkitOnly public static final int TYPE_HORN = 4;

    @PowerNukkitOnly
    public BlockCoral() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockCoral(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CORAL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    public boolean isDead() {
        return (getDamage() & 0x8) == 0x8;
    }

    @PowerNukkitOnly
    public void setDead(boolean dead) {
        if (dead) {
            setDamage(getDamage() | 0x8);
        } else {
            setDamage(getDamage() ^ 0x8);
        }
    }
    
    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this);
            } else if (!isDead()) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead() && !(getLevelBlockAtLayer(1) instanceof BlockWater)  && !(getLevelBlockAtLayer(1) instanceof BlockIceFrosted)) {
                BlockFadeEvent event = new BlockFadeEvent(this, new BlockCoral(getDamage() | 0x8));
                if (!event.isCancelled()) {
                    setDead(true);
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block layer1 = block.getLevelBlockAtLayer(1);
        boolean hasWater = layer1 instanceof BlockWater;
        int waterDamage;
        if (layer1.getId() != Block.AIR && (!hasWater || ((waterDamage = layer1.getDamage()) != 0) && waterDamage != 8)) {
            return false;
        }

        if (hasWater && layer1.getDamage() == 8) {
            this.getLevel().setBlock(this, 1, new BlockWater(), true, false);
        }
        
        if (down.isSolid()) {
            this.getLevel().setBlock(this, 0, this, true, true);
            return true;
        }
        return false;
    }
    
    @Override
    public String getName() {
        String[] names = new String[] {
                "Tube Coral",
                "Brain Coral",
                "Bubble Coral",
                "Fire Coral",
                "Horn Coral",
                // Invalid
                "Tube Coral",
                "Tube Coral",
                "Tube Coral"
        };
        String name = names[getDamage() & 0x7];
        if (isDead()) {
            return "Dead " + name;
        } else {
            return name;
        }
    }
    
    @Override
    public BlockColor getColor() {
        if (isDead()) {
            return BlockColor.GRAY_BLOCK_COLOR;
        }
        
        BlockColor[] colors = new BlockColor[] {
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.PINK_BLOCK_COLOR,
                BlockColor.PURPLE_BLOCK_COLOR,
                BlockColor.RED_BLOCK_COLOR,
                BlockColor.YELLOW_BLOCK_COLOR,
                // Invalid
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.BLUE_BLOCK_COLOR
        };
        return colors[getDamage() & 0x7];
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
