package cn.nukkit.level.generator.populator.impl.structure.utils.block.state;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

//\\ VanillaStates::FacingDirection
@PowerNukkitXOnly
@Since("1.19.21-r2")
public final class FacingDirection {

    public static final int DOWN = 0b000;
    public static final int UP = 0b001;
    public static final int NORTH = 0b010;
    public static final int SOUTH = 0b011;
    public static final int WEST = 0b100;
    public static final int EAST = 0b101;

    private FacingDirection() {

    }
}
