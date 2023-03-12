package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import lombok.Getter;

//存储最近的玩家的Memory
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class NearestPlayerSensor implements ISensor {

    protected double range;

    protected double minRange;

    protected int period;

    public NearestPlayerSensor(double range, double minRange) {
        this(range, minRange, 1);
    }

    public NearestPlayerSensor(double range, double minRange, int period) {
        this.range = range;
        this.minRange = minRange;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        Player player = null;
        double rangeSquared = this.range * this.range;
        double minRangeSquared = this.minRange * this.minRange;
        //寻找范围内最近的玩家
        for (Player p : entity.getLevel().getPlayers().values()) {
            if (entity.distanceSquared(p) <= rangeSquared && entity.distanceSquared(p) >= minRangeSquared) {
                if (player == null) {
                    player = p;
                } else {
                    if (entity.distanceSquared(p) < entity.distanceSquared(player)) {
                        player = p;
                    }
                }
            }
        }
        entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_PLAYER, player);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
