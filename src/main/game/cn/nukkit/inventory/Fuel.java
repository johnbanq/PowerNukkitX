package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.StringItem;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitXDifference(info = "Add blocks and items.", since = "1.19.50-r3")
public abstract class Fuel {
    public static final Map<Integer, Short> duration = new TreeMap<>();
    public static final Object2IntMap<String> durationByStringId = new Object2IntAVLTreeMap<>();

    static {
        addItem(ItemID.COAL, (short) 1600);
        addBlock(BlockID.COAL_BLOCK, (short) 16000);
        addBlock(BlockID.LOG, (short) 300);
        addBlock(BlockID.PLANKS, (short) 300);
        addBlock(BlockID.SAPLING, (short) 100);
        addItem(ItemID.WOODEN_AXE, (short) 200);
        addItem(ItemID.WOODEN_PICKAXE, (short) 200);
        addItem(ItemID.WOODEN_SWORD, (short) 200);
        addItem(ItemID.WOODEN_SHOVEL, (short) 200);
        addItem(ItemID.WOODEN_HOE, (short) 200);
        addItem(ItemID.STICK, (short) 100);
        addBlock(BlockID.FENCE, (short) 300);
        addBlock(BlockID.FENCE_GATE, (short) 300);
        addBlock(BlockID.FENCE_GATE_SPRUCE, (short) 300);
        addBlock(BlockID.FENCE_GATE_BIRCH, (short) 300);
        addBlock(BlockID.FENCE_GATE_JUNGLE, (short) 300);
        addBlock(BlockID.FENCE_GATE_ACACIA, (short) 300);
        addBlock(BlockID.FENCE_GATE_DARK_OAK, (short) 300);
        addBlock(BlockID.OAK_STAIRS, (short) 300);
        addBlock(BlockID.SPRUCE_STAIRS, (short) 300);
        addBlock(BlockID.BIRCH_STAIRS, (short) 300);
        addBlock(BlockID.JUNGLE_STAIRS, (short) 300);
        addBlock(BlockID.TRAPDOOR, (short) 300);
        addBlock(BlockID.CRAFTING_TABLE, (short) 300);
        addBlock(BlockID.BOOKSHELF, (short) 300);
        addBlock(BlockID.CHEST, (short) 300);
        addItem(ItemID.BUCKET, (short) 20000);
        addBlock(BlockID.LADDER, (short) 300);
        addItem(ItemID.BOW, (short) 200);
        addItem(ItemID.BOWL, (short) 100);
        addBlock(BlockID.WOOD2, (short) 300);
        addBlock(BlockID.WOODEN_PRESSURE_PLATE, (short) 300);
        addBlock(BlockID.ACACIA_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.DARK_OAK_WOOD_STAIRS, (short) 300);
        addBlock(BlockID.TRAPPED_CHEST, (short) 300);
        addBlock(BlockID.DAYLIGHT_DETECTOR, (short) 300);
        addBlock(BlockID.DAYLIGHT_DETECTOR_INVERTED, (short) 300);
        addBlock(BlockID.JUKEBOX, (short) 300);
        addBlock(BlockID.NOTEBLOCK, (short) 300);
        addBlock(BlockID.WOOD_SLAB, (short) 300);
        addBlock(BlockID.DOUBLE_WOOD_SLAB, (short) 300);
        addItem(ItemID.BOAT, (short) 1200);
        addItem(ItemID.BLAZE_ROD, (short) 2400);
        addBlock(BlockID.BROWN_MUSHROOM_BLOCK, (short) 300);
        addBlock(BlockID.RED_MUSHROOM_BLOCK, (short) 300);
        addItem(ItemID.FISHING_ROD, (short) 300);
        addBlock(BlockID.WOODEN_BUTTON, (short) 100);
        addItem(ItemID.WOODEN_DOOR, (short) 200);
        addItem(ItemID.SPRUCE_DOOR, (short) 200);
        addItem(ItemID.BIRCH_DOOR, (short) 200);
        addItem(ItemID.JUNGLE_DOOR, (short) 200);
        addItem(ItemID.ACACIA_DOOR, (short) 200);
        addItem(ItemID.DARK_OAK_DOOR, (short) 200);
        addItem(ItemID.BANNER, (short) 300);
        addBlock(BlockID.DEAD_BUSH, (short) 100);
        addItem(ItemID.SIGN, (short) 200);
        addItem(ItemID.ACACIA_SIGN, (short) 200);
        addItem(ItemID.BIRCH_SIGN, (short) 200);
        addItem(ItemID.SPRUCE_SIGN, (short) 200);
        addItem(ItemID.DARK_OAK_SIGN, (short) 200);
        addItem(ItemID.JUNGLE_SIGN, (short) 200);
        addBlock(BlockID.DRIED_KELP_BLOCK, (short) 4000);
        addItem(ItemID.CROSSBOW, (short) 200);
        addBlock(BlockID.BEE_NEST, (short) 300);
        addBlock(BlockID.BEEHIVE, (short) 300);
        addBlock(BlockID.BAMBOO, (short) 50);
        addBlock(BlockID.SCAFFOLDING, (short) 50);
        addBlock(BlockID.CARTOGRAPHY_TABLE, (short) 300);
        addBlock(BlockID.FLETCHING_TABLE, (short) 300);
        addBlock(BlockID.SMITHING_TABLE, (short) 300);
        addBlock(BlockID.LOOM, (short) 300);
        addBlock(BlockID.LECTERN, (short) 300);
        addBlock(BlockID.COMPOSTER, (short) 300);
        addBlock(BlockID.BARREL, (short) 300);
        addBlock(BlockID.AZALEA, (short) 100);
    }

    private static void addItem(int itemID, short fuelDuration) {
        duration.put(itemID, fuelDuration);
    }

    private static void addBlock(int blockID, short fuelDuration) {
        duration.put(blockID > 255 ? 255 - blockID : blockID, fuelDuration); // ItemBlock have a negative ID
    }

    private static void addItem(Item item, short fuelDuration) {
        if (!(item instanceof StringItem)) {
            duration.put(item.getId(), fuelDuration);
        }
        durationByStringId.put(item.getNamespaceId(), fuelDuration);
    }

    /**
     * @param item item
     * @return fuel duration, if it cannot be used as fuel, return -1.
     */
    public static short getFuelDuration(@NotNull Item item) {
        var d = duration.get(item.getId());
        if (d == null) {
            return (short) durationByStringId.getOrDefault(item.getNamespaceId(), -1);
        }
        return duration.getOrDefault(item.getId(), (short) 0);
    }

    public static boolean isFuel(@NotNull Item item) {
        return duration.containsKey(item.getId()) || durationByStringId.containsKey(item.getNamespaceId());
    }
}
