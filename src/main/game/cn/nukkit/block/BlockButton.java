package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.api.*;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author CreeperFace
 * @since 27. 11. 2016
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public abstract class BlockButton extends BlockFlowable implements RedstoneComponent, Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected static final BooleanBlockProperty BUTTON_PRESSED = new BooleanBlockProperty("button_pressed_bit", false);
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(
            FACING_DIRECTION,
            BUTTON_PRESSED
    );

    @UsedByReflection
    public BlockButton() {
        this(0);
    }

    @UsedByReflection
    public BlockButton(int meta) {
        super(meta);
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
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(info = "Now, can be placed on solid blocks", since = "1.4.0.0-PN")
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!BlockLever.isSupportValid(target, face)) {
            return false;
        }

        setBlockFace(face);
        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (!player.getAdventureSettings().get(AdventureSettings.Type.DOORS_AND_SWITCHED))
            return false;
        if (this.isActivated()) {
            return false;
        }

        this.level.scheduleUpdate(this, 30);

        setActivated(true, player);
        this.level.setBlock(this, this, true, false);
        this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_POWER_ON, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()));
        if (this.level.getServer().isRedstoneEnabled()) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));

            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(getSide(getFacing().getOpposite()), getFacing());
        }

        return true;
    }

    @PowerNukkitDifference(info = "Now, can be placed on solid blocks", since = "1.4.0.0-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace thisFace = getFacing();
            BlockFace touchingFace = thisFace.getOpposite();
            Block side = this.getSide(touchingFace);
            if (!BlockLever.isSupportValid(side, thisFace)) {
                this.level.useBreakOn(this, Item.get(Item.WOODEN_PICKAXE));
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.isActivated()) {
                setActivated(false);
                this.level.setBlock(this, this, true, false);
                this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_POWER_OFF, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()));

                if (this.level.getServer().isRedstoneEnabled()) {
                    this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                    updateAroundRedstone();
                    RedstoneComponent.updateAroundRedstone(getSide(getFacing().getOpposite()), getFacing());
                }
            }
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public boolean isActivated() {
        return getBooleanValue(BUTTON_PRESSED);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setActivated(boolean activated) {
        setActivated(activated, null);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setActivated(boolean activated, @Nullable Player player) {
        setBooleanValue(BUTTON_PRESSED, activated);
        var pos = this.add(0.5, 0.5, 0.5);
        if (activated) {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, pos, VibrationType.BLOCK_ACTIVATE));
        } else {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, pos, VibrationType.BLOCK_DEACTIVATE));
        }
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActivated() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isActivated() ? 0 : (getFacing() == side ? 15 : 0);
    }

    public BlockFace getFacing() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }

    @Override
    public boolean onBreak(Item item) {
        if (isActivated()) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        }

        return super.onBreak(item);
    }

    @Override
    public Item toItem() {
        return Item.get(this.getItemId());
    }

    @PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return getFacing();
    }
}
