package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityPhysical;
import cn.nukkit.math.Vector3;

import java.util.Arrays;

/**
 * 处理陆地行走实体运动
 * todo: 有待解耦
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class WalkController implements IController {

    protected static final int JUMP_COOL_DOWN = 10;

    protected int currentJumpCoolDown = 0;

    @Override
    public boolean control(EntityIntelligent entity) {
        currentJumpCoolDown++;
        if (entity.hasMoveDirection() && !entity.isShouldUpdateMoveDirection()) {
            //clone防止异步导致的NPE
            Vector3 direction = entity.getMoveDirectionEnd().clone();
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_MOVING, false);
                return false;
            }
            var relativeVector = direction.clone().setComponents(direction.x - entity.x,
                    direction.y - entity.y, direction.z - entity.z);
            var xzLengthSquared = relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z;
            if (Math.abs(xzLengthSquared) < EntityPhysical.PRECISION) {
                entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_MOVING, false);
                return false;
            }
            var xzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z);
            var k = speed / xzLength * 0.33;
            var dx = relativeVector.x * k;
            var dz = relativeVector.z * k;
            var dy = 0.0d;
            if (direction.y > entity.y && collidesBlocks(entity, dx, 0, dz) && currentJumpCoolDown > JUMP_COOL_DOWN) {
                if (entity.isOnGround() || entity.isTouchingWater()) {
                    //note: 从对BDS的抓包信息来看，台阶的碰撞箱在服务端和半砖一样，高度都为0.5
                    Block[] collisionBlocks = entity.level.getTickCachedCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), false, false, b -> !b.canPassThrough());
                    double maxY = Arrays.stream(collisionBlocks).map(b -> b.getCollisionBoundingBox().getMaxY()).max(Double::compareTo).orElse(entity.getY());
                    if (entity.hasWaterAt(0) && maxY - entity.y <= 1) {//防止实体在水中浮起的时候尝试跳跃一格高的方块
                        dy += 0.6;//水中上岸稍微跳高一点
                        currentJumpCoolDown = 0;
                    } else if (maxY - entity.getY() - 0.5 > 0.01) {//0.5格以下可以直接走上去
                        dy += 0.5;
                        currentJumpCoolDown = 0;
                    }
                }
            }
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_MOVING, true);
            if (xzLength < speed) {
                needNewDirection(entity);
                return false;
            }
            return true;
        } else {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_MOVING, false);
            return false;
        }
    }

    protected void needNewDirection(EntityIntelligent entity) {
        //通知需要新的移动目标
        entity.setShouldUpdateMoveDirection(true);
    }

    protected boolean collidesBlocks(EntityIntelligent entity, double dx, double dy, double dz) {
        return entity.level.getTickCachedCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }
}
