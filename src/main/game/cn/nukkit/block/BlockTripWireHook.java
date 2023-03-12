package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.RedstoneComponent;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.BlockTripWire.ATTACHED;
import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;
import static cn.nukkit.blockproperty.CommonBlockProperties.POWERED;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockTripWireHook extends BlockTransparentMeta implements RedstoneComponent {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRECTION, ATTACHED, POWERED);

    public BlockTripWireHook() {
        this(0);
    }

    public BlockTripWireHook(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Tripwire Hook";
    }

    @Override
    public int getId() {
        return TRIPWIRE_HOOK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            var supportBlock = this.getSide(this.getFacing().getOpposite());
            if (!supportBlock.isNormalBlock() && !(supportBlock instanceof BlockGlass)) {
                this.level.useBreakOn(this);
            }

            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.calculateState(false, true, -1, null);
            return type;
        }

        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        var supportBlock = this.getSide(face.getOpposite());
        if (face == BlockFace.DOWN || face == BlockFace.UP || (!supportBlock.isNormalBlock() && !(supportBlock instanceof BlockGlass))) {
            return false;
        }

        if (face.getAxis().isHorizontal()) {
            this.setFace(face);
        }

        this.level.setBlock(this, this);

        if (player != null) {
            this.calculateState(false, false, -1, null);
        }
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        super.onBreak(item);
        boolean attached = isAttached();
        boolean powered = isPowered();

        if (attached || powered) {
            this.calculateState(true, false, -1, null);
        }

        if (powered) {
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(getFacing().getOpposite()));
        }

        return true;
    }

    public void calculateState(boolean onBreak, boolean updateAround, int pos, Block block) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return;
        }

        BlockFace facing = getFacing();
        Position position = this.getLocation();
        boolean attached = isAttached();
        boolean powered = isPowered();
        boolean canConnect = !onBreak;
        boolean nextPowered = false;
        int distance = 0;
        Block[] blocks = new Block[42];

        for (int i = 1; i < 42; ++i) {
            Vector3 vector = position.getSide(facing, i);
            Block b = this.level.getBlock(vector);

            if (b instanceof BlockTripWireHook) {
                if (((BlockTripWireHook) b).getFacing() == facing.getOpposite()) {
                    distance = i;
                }
                break;
            }

            if (b.getId() != Block.TRIP_WIRE && i != pos) {
                blocks[i] = null;
                canConnect = false;
            } else {
                if (i == pos) {
                    b = block != null ? block : b;
                }

                if (b instanceof BlockTripWire) {
                    boolean disarmed = !((BlockTripWire) b).isDisarmed();
                    boolean wirePowered = ((BlockTripWire) b).isPowered();
                    nextPowered |= disarmed && wirePowered;

                    if (i == pos) {
                        this.level.scheduleUpdate(this, 10);
                        canConnect &= disarmed;
                    }
                }
                blocks[i] = b;
            }
        }

        canConnect = canConnect & distance > 1;
        nextPowered = nextPowered & canConnect;
        BlockTripWireHook hook = (BlockTripWireHook) Block.get(BlockID.TRIPWIRE_HOOK);
        hook.setLevel(this.level);
        hook.setAttached(canConnect);
        hook.setPowered(nextPowered);


        if (distance > 0) {
            Position p = position.getSide(facing, distance);
            BlockFace face = facing.getOpposite();
            hook.setFace(face);
            this.level.setBlock(p, hook, true, true);
            RedstoneComponent.updateAroundRedstone(p);
            RedstoneComponent.updateAroundRedstone(p.getSide(face.getOpposite()));
            this.addSound(p, canConnect, nextPowered, attached, powered);
        }

        this.addSound(position, canConnect, nextPowered, attached, powered);

        if (!onBreak) {
            hook.setFace(facing);
            this.level.setBlock(position, hook, true, true);

            if (updateAround) {
                updateAroundRedstone();
                RedstoneComponent.updateAroundRedstone(position.getSide(facing.getOpposite()));
            }
        }

        if (attached != canConnect) {
            for (int i = 1; i < distance; i++) {
                Vector3 vc = position.getSide(facing, i);
                block = blocks[i];

                if (block != null && this.level.getBlockIdAt(vc.getFloorX(), vc.getFloorY(), vc.getFloorZ()) != Block.AIR) {
                    if (canConnect ^ ((block.getDamage() & 0x04) > 0)) {
                        block.setDamage(block.getDamage() ^ 0x04);
                    }

                    this.level.setBlock(vc, block, true, true);
                }
            }
        }
    }

    private void addSound(Vector3 pos, boolean canConnect, boolean nextPowered, boolean attached, boolean powered) {
        if (nextPowered && !powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_ON);
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        } else if (!nextPowered && powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_OFF);
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        } else if (canConnect && !attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_ATTACH);
        } else if (!canConnect && attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_DETACH);
        }
    }

    public boolean isAttached() {
        return (getDamage() & 0x04) > 0;
    }

    public boolean isPowered() {
        return (this.getDamage() & 0x08) > 0;
    }

    public void setPowered(boolean value) {
        if (value ^ this.isPowered()) {
            this.setDamage(this.getDamage() ^ 0x08);
            var pos = this.add(0.5, 0.5, 0.5);
            if (value) {
                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, VibrationType.BLOCK_ACTIVATE));
            } else {
                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, VibrationType.BLOCK_DEACTIVATE));
            }
        }
    }

    public void setAttached(boolean value) {
        if (value ^ this.isAttached()) {
            this.setDamage(this.getDamage() ^ 0x04);
            var pos = this.add(0.5, 0.5, 0.5);
            if (value) {
                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, VibrationType.BLOCK_ATTACH));
            } else {
                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos, VibrationType.BLOCK_DETACH));
            }
        }
    }

    public void setFace(BlockFace face) {
        this.setDamage(this.getDamage() - this.getDamage() % 4);
        this.setDamage(this.getDamage() | face.getHorizontalIndex());
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isPowered() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isPowered() ? 0 : getFacing() == side ? 15 : 0;
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
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }
}
