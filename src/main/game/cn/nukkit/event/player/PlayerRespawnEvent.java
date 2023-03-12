package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Position position;//Respawn Position
    @Deprecated
    private Position spawnBlock;

    @Deprecated
    private Position originalSpawnPosition;
    @Deprecated
    private boolean spawnBlockAvailable;

    private boolean firstSpawn;
    @Deprecated
    private boolean keepRespawnBlockPosition;
    @Deprecated
    private boolean keepRespawnPosition;
    @Deprecated
    private boolean sendInvalidRespawnBlockMessage = true;
    @Deprecated
    private boolean consumeCharge = true;

    public PlayerRespawnEvent(Player player, Position position) {
        this(player, position, false);
    }

    public PlayerRespawnEvent(Player player, Position position, boolean firstSpawn) {
        this.player = player;
        this.position = position;
        this.firstSpawn = firstSpawn;
    }

    public Position getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Position position) {
        this.position = position;
    }

    public boolean isFirstSpawn() {
        return firstSpawn;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Position getRespawnBlockPosition() {
        return spawnBlock;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRespawnBlockPosition(Position spawnBlock) {
        this.spawnBlock = spawnBlock;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isRespawnBlockAvailable() {
        return spawnBlockAvailable;
    }

    /**
     * Plugins not suggest use
     *
     * @param spawnBlockAvailable the spawn block available
     */
    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRespawnBlockAvailable(boolean spawnBlockAvailable) {
        this.spawnBlockAvailable = spawnBlockAvailable;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Position getOriginalRespawnPosition() {
        return originalSpawnPosition;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setOriginalRespawnPosition(Position originalSpawnPosition) {
        this.originalSpawnPosition = originalSpawnPosition;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isKeepRespawnBlockPosition() {
        return keepRespawnBlockPosition;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setKeepRespawnBlockPosition(boolean keepRespawnBlockPosition) {
        this.keepRespawnBlockPosition = keepRespawnBlockPosition;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isKeepRespawnPosition() {
        return keepRespawnPosition;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setKeepRespawnPosition(boolean keepRespawnPosition) {
        this.keepRespawnPosition = keepRespawnPosition;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSendInvalidRespawnBlockMessage() {
        return sendInvalidRespawnBlockMessage;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSendInvalidRespawnBlockMessage(boolean sendInvalidRespawnBlockMessage) {
        this.sendInvalidRespawnBlockMessage = sendInvalidRespawnBlockMessage;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isConsumeCharge() {
        return consumeCharge;
    }

    @Deprecated
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setConsumeCharge(boolean consumeCharge) {
        this.consumeCharge = consumeCharge;
    }
}
