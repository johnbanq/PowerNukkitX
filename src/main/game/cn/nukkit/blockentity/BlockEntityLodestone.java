package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.positiontracking.PositionTracking;
import cn.nukkit.positiontracking.PositionTrackingService;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.extern.log4j.Log4j2;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.OptionalInt;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Log4j2
public class BlockEntityLodestone extends BlockEntitySpawnable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockEntityLodestone(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (namedTag.containsInt("trackingHandler")) {
            namedTag.put("trackingHandle", namedTag.removeAndGet("trackingHandler"));
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public OptionalInt getTrackingHandler() {
        if (namedTag.containsInt("trackingHandle")) {
            return OptionalInt.of(namedTag.getInt("trackingHandle"));
        }
        return OptionalInt.empty();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int requestTrackingHandler() throws IOException {
        OptionalInt opt = getTrackingHandler();
        PositionTrackingService positionTrackingService = getLevel().getServer().getPositionTrackingService();
        Position floor = floor();
        if (opt.isPresent()) {
            int handler = opt.getAsInt();
            PositionTracking position = positionTrackingService.getPosition(handler);
            if (position != null && position.matchesNamedPosition(floor)) {
                return handler;
            }
        }

        int handler = positionTrackingService.addOrReusePosition(floor);
        namedTag.putInt("trackingHandle", handler);
        return handler;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.LODESTONE;
    }

    @Override
    public void onBreak() {
        IntList handlers;
        PositionTrackingService positionTrackingService = Server.getInstance().getPositionTrackingService();
        try {
            handlers = positionTrackingService.findTrackingHandlers(this);
            if (handlers.isEmpty()) {
                return;
            }
        } catch (IOException e) {
            log.error("Failed to remove the tracking position handler for {}", getLocation());
            return;
        }
        
        int size = handlers.size();
        for (int i = 0; i < size; i++) {
            int handler = handlers.getInt(i);
            try {
                positionTrackingService.invalidateHandler(handler);
            } catch (IOException e) {
                log.error("Failed to remove the tracking handler {} for position {}", handler, getLocation(), e);
            }
        }
    }
}
