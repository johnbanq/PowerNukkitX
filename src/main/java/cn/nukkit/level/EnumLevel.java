package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.Since;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitMath;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum EnumLevel {
    OVERWORLD,
    NETHER,
    THE_END;

    Level level;

    public static void initLevels() {
        Level overworld = Server.getInstance().getDefaultLevel();
        // attempt to load the nether world if it is allowed in server properties
        if (Server.getInstance().isNetherAllowed() && !Server.getInstance().loadLevel("nether")) {

            // Nether is allowed, and not found, create the default nether world
            log.info("No level called \"nether\" found, creating default nether level.");

            // Generate seed for nether and get nether generator
            long seed = System.currentTimeMillis();
            Class<? extends Generator> generator = Generator.getGenerator("nether");

            // Generate the nether world
            Server.getInstance().generateLevel("nether", seed, generator);

            // Finally, load the level if not already loaded and set the level
            if (!Server.getInstance().isLevelLoaded("nether")) {
                Server.getInstance().loadLevel("nether");
            }

        }
        Level nether = Server.getInstance().getLevelByName("nether");
        if (nether == null) {
            // Nether is not found or disabled
            log.warn("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.");
        }

        // The End
        if (Server.getInstance().isTheEndAllowed() && !Server.getInstance().loadLevel("the_end")) {
            Server.getInstance().getLogger().info("No level called \"the_end\" found, creating default the end level.");
            long seed = System.currentTimeMillis();
            Class<? extends Generator> generator = Generator.getGenerator("the_end");
            Server.getInstance().generateLevel("the_end", seed, generator);
            if (!Server.getInstance().isLevelLoaded("the_end")) {
                Server.getInstance().loadLevel("the_end");
            }
        }
        Level theEnd = Server.getInstance().getLevelByName("the_end");
        if (theEnd == null) {
            Server.getInstance().getLogger().alert("No level called \"the_end\" found or the end is disabled in server properties! The End functionality will be disabled.");
        }

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
