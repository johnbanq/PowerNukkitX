package cn.nukkit.level;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXInternal;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitMath;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum EnumLevel {
    OVERWORLD,
    NETHER,
    THE_END;

    Level level;

    @PowerNukkitXDifference(info="content has been moved, no-op for compatibility, plugins shouldn't call this anyway")
    public static void initLevels() {}

    @PowerNukkitXInternal
    public static void acceptLevelSingletons(Level overworld, Level nether, Level theEnd) {
        OVERWORLD.level = overworld;
        NETHER.level = nether;
        THE_END.level = theEnd;
    }

    public static Level getOtherNetherPair(Level current) {
        if (current == OVERWORLD.level || current.getDimension() == Level.DIMENSION_OVERWORLD) {
            return NETHER.level;
        } else if (current == NETHER.level || current.getDimension() == Level.DIMENSION_NETHER) {
            return OVERWORLD.level;
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position convertPosBetweenNetherAndOverworld(Position current) {
        if (NETHER.level == null) {
            return null;
        } else {
            if (current.level == OVERWORLD.level || current.level.getDimension() == Level.DIMENSION_OVERWORLD) {
                return new Position(current.getFloorX() >> 3, NukkitMath.clamp(current.getFloorY(), 70, 118), current.getFloorZ() >> 3, NETHER.level);
            } else if (current.level == NETHER.level || current.level.getDimension() == Level.DIMENSION_NETHER) {
                return new Position(current.getFloorX() << 3, NukkitMath.clamp(current.getFloorY(), 70, 246), current.getFloorZ() << 3, OVERWORLD.level);
            } else {
                throw new IllegalArgumentException("Neither overworld nor nether given!");
            }
        }
    }

    private static final int mRound(int value, int factor) {
        return Math.round((float) value / factor) * factor;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @PowerNukkitXDifference(since = "1.18.30", info = "Supporting World Dimension Judgment")
    public static Level getOtherTheEndPair(Level current) {
        if (current == OVERWORLD.level || current.getDimension() == Level.DIMENSION_OVERWORLD) {
            return THE_END.level;
        } else if (current == THE_END.level || current.getDimension() == Level.DIMENSION_THE_END) {
            return OVERWORLD.level;
        } else {
            throw new IllegalArgumentException("Neither overworld nor the end given!");
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @PowerNukkitXDifference(since = "1.18.30", info = "Supporting World Dimension Judgment")
    public static Position moveToTheEnd(Position current) {
        if (THE_END.level == null) {
            return null;
        } else {
            if (current.level == OVERWORLD.level || current.level.getDimension() == Level.DIMENSION_OVERWORLD) {
                return new Position(100, 49, 0, THE_END.level);
            } else if (current.level == THE_END.level || current.level.getDimension() == Level.DIMENSION_THE_END) {
                return OVERWORLD.level.getSpawnLocation();
            } else {
                throw new IllegalArgumentException("Neither overworld nor the end given!");
            }
        }
    }

    public Level getLevel() {
        return level;
    }
}
