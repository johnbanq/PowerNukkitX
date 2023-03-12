package cn.nukkit.block.customblock;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFallableMeta;
import cn.nukkit.block.BlockMeta;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * 继承这个类实现自定义方块,重写{@link Block}中的方法控制方块属性
 * <p>
 * Inherit this class to implement a custom block, override the methods in the {@link Block} to control the feature of the block.
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface CustomBlock {
    /**
     * 覆写该方法设置自定义方块的摩擦因数
     * <p>
     * {@code @Override} this method to set the friction factor of the custom block
     */
    double getFrictionFactor();

    /**
     * 覆写该方法设置自定义方块的爆炸抗性
     * <p>
     * {@code @Override} this method to set the Explosive resistance of the custom block
     */
    double getResistance();

    /**
     * 覆写该方法设置自定义方块的吸收光的等级
     * <p>
     * {@code @Override} this method to set the level of light absorption of the custom block
     */
    int getLightFilter();

    /**
     * 覆写该方法设置自定义方块的发出光的等级
     * <p>
     * {@code @Override} this method to set the level of light emitted by the custom block
     */
    int getLightLevel();

    /**
     * 覆写该方法设置自定义方块的硬度，这有助于自定义方块在服务端侧计算挖掘时间(硬度越大服务端侧挖掘时间越长)
     * <p>
     * {@code @Override} this method to set the hardness of the custom block, which helps to calculate the break time of the custom block on the server-side (the higher the hardness the longer the break time on the server-side)
     */
    double getHardness();

    /**
     * 覆写该方法设置自定义方块的命名空间ID
     * <p>
     * {@code @Override} this method to set the namespace ID of the custom block
     */
    @NotNull
    String getNamespaceId();

    /**
     * 一般不需要被覆写,继承父类会提供
     * <p>
     * Generally, it does not need to be {@code @Override}, extend from the parent class will provide
     */
    Item toItem();

    /**
     * 该方法设置自定义方块的定义
     * <p>
     * This method sets the definition of custom block
     */
    CustomBlockDefinition getDefinition();

    /* 下面两个方法需要被手动覆写,请使用接口的定义 */

    /**
     * 该方法必须被覆写为使用接口的定义，请使用
     * <p>
     * The method must be {@code @Override} to use the definition of the interface, please use the
     * <br>
     * {@code @Override}<br>{@code public int getId() {
     * return CustomBlock.super.getId();
     * } }
     */
    default int getId() {
        return Block.CUSTOM_BLOCK_ID_MAP.get(getNamespaceId().toLowerCase(Locale.ENGLISH));
    }

    /**
     * 该方法必须被覆写为使用接口的定义，请使用
     * <p>
     * The method must be {@code @Override} to use the definition of the interface, please use the
     * <br>
     * {@code @Override}<br>{@code public String getName() {
     * return CustomBlock.super.getName();
     * } }
     */
    default String getName() {
        return this.getNamespaceId().split(":")[1].toLowerCase(Locale.ENGLISH);
    }

    /**
     * Plugins do not need {@code @Override}
     *
     * @return the block
     */
    default Block toCustomBlock() {
        return ((Block) this).clone();
    }

    /**
     * Plugins do not need {@code @Override}
     *
     * @return the block
     */
    default Block toCustomBlock(int meta) {
        var block = toCustomBlock();
        if (block instanceof BlockMeta || block instanceof BlockFallableMeta) {
            block.getMutableState().setDataStorageFromInt(meta, true);
        }
        return block;
    }

    /**
     * @return 是否反转自定义方块属性解析的顺序<br>Whether to reverse the order of properties parsing
     */
    default boolean reverseSending() {
        return true;
    }

    /**
     * 获取自定义方块的挖掘时间，它是服务端侧和客户端侧挖掘时间的最小值。
     *
     * @param item   the item
     * @param player the player
     * @return the break time
     */
    default double getBreakTime(@NotNull Item item, @Nullable Player player) {
        var block = this.toCustomBlock();
        double breakTime = block.calculateBreakTime(item, player);
        var comp = this.getDefinition().nbt().getCompound("components");
        if (comp.containsCompound("minecraft:destructible_by_mining")) {
            var clientBreakTime = comp.getCompound("minecraft:destructible_by_mining").getFloat("value");
            if (player != null) {
                if (player.getServer().getTick() - player.getLastInAirTick() < 5) {
                    clientBreakTime *= 6;
                }
            }
            breakTime = Math.min(breakTime, clientBreakTime);
        }
        return breakTime;
    }
}
