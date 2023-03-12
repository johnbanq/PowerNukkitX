package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * 用于标准陆地行走实体的方块评估器
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class WalkingPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block) {
        //居中坐标
        var blockCenter = block.add(0.5, 1, 0.5);
        //检查是否可到达
        if (!isPassable(entity, blockCenter))
            return false;
        if (entity.hasWaterAt(0) && blockCenter.getY() - entity.getY() > 1)//实体在水中不能移动到一格高以上的方块
            return false;
        //TODO: 检查碰头
        //脚下不能是伤害性方块
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.STILL_LAVA || block.getId() == Block.CACTUS)
            return false;
        //不能是栏杆
        if (block instanceof BlockFence || block instanceof BlockFenceGate)
            return false;
        //水特判
        if (block.getId() == Block.STILL_WATER || block.getId() == Block.FLOWING_WATER)
            return true;
        //必须可以站立
        return !block.canPassThrough();
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     */
    //todo: 此方法会造成大量开销，原因是碰撞检查，有待优化
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) / 2;
        float height = entity.getHeight() * entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
