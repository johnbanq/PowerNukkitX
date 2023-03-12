package cn.nukkit.item;

import cn.nukkit.api.API;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Verify.verify;

/**
 * Responsible for mapping item full ids, item network ids and item namespaced ids between each other.
 * <ul>
 * <li>A <b>full id</b> is a combination of <b>item id</b> and <b>item damage</b>.
 * The way they are combined may change in future, so you should not combine them by yourself and neither store them
 * permanently. It's mainly used to preserve backward compatibility with plugins that don't support <em>namespaced ids</em>.
 * <li>A <b>network id</b> is an id that is used to communicated with the client, it may change between executions of the
 * same server version depending on how the plugins are setup.
 * <li>A <b>namespaced id</b> is the new way Mojang saves the ids, a string like <code>minecraft:stone</code>. It may change
 * in Minecraft updates but tends to be permanent, unless Mojang decides to change them for some random reasons...
 */
@Log4j2
@Since("1.4.0.0-PN")
public class RuntimeItemMapping {

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private final Collection<RuntimeItems.Entry> entries;

    private final Int2IntMap legacyNetworkMap;
    private final Int2IntMap networkLegacyMap;
    private byte[] itemDataPalette;

    private final Map<String, OptionalInt> namespaceNetworkMap;
    private final Int2ObjectMap<String> networkNamespaceMap;

    private final Map<String, Supplier<Item>> namespacedIdItem = new LinkedHashMap<>();

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private final HashMap<String, RuntimeItems.Entry> customItemEntries = new HashMap<>();

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private final HashMap<String, RuntimeItems.Entry> customBlockEntries = new HashMap<>();

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Deprecated
    public RuntimeItemMapping(byte[] itemDataPalette, Int2IntMap legacyNetworkMap, Int2IntMap networkLegacyMap) {
        this.entries = null;
        this.itemDataPalette = itemDataPalette;
        this.legacyNetworkMap = legacyNetworkMap;
        this.networkLegacyMap = networkLegacyMap;
        this.legacyNetworkMap.defaultReturnValue(-1);
        this.networkLegacyMap.defaultReturnValue(-1);
        this.namespaceNetworkMap = new LinkedHashMap<>();
        this.networkNamespaceMap = new Int2ObjectOpenHashMap<>();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @API(definition = API.Definition.INTERNAL, usage = API.Usage.BLEEDING)
    @Deprecated
    public RuntimeItemMapping(
            byte[] itemDataPalette, Int2IntMap legacyNetworkMap, Int2IntMap networkLegacyMap,
            Map<String, Integer> namespaceNetworkMap, Int2ObjectMap<String> networkNamespaceMap) {
        this.entries = null;
        this.itemDataPalette = itemDataPalette;
        this.legacyNetworkMap = legacyNetworkMap;
        this.networkLegacyMap = networkLegacyMap;
        this.legacyNetworkMap.defaultReturnValue(-1);
        this.networkLegacyMap.defaultReturnValue(-1);
        this.networkNamespaceMap = networkNamespaceMap;
        this.namespaceNetworkMap = namespaceNetworkMap.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), OptionalInt.of(e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public RuntimeItemMapping(Collection<RuntimeItems.Entry> entries) {
        this.entries = entries;

        this.legacyNetworkMap = new Int2IntOpenHashMap();
        this.networkLegacyMap = new Int2IntOpenHashMap();
        LinkedHashMap<String, OptionalInt> namespaceNetworkMap = new LinkedHashMap<>();
        this.networkNamespaceMap = new Int2ObjectOpenHashMap<>();

        for (RuntimeItems.Entry entry : entries) {
            namespaceNetworkMap.put(entry.name, OptionalInt.of(entry.id));
            networkNamespaceMap.put(entry.id, entry.name);
            if (entry.oldId != null) {
                boolean hasData = entry.oldData != null;
                int fullId = RuntimeItems.getFullId(entry.oldId, hasData ? entry.oldData : 0);
                if (entry.deprecated != Boolean.TRUE) {
                    verify(legacyNetworkMap.put(fullId, (entry.id << 1) | (hasData ? 1 : 0)) == 0,
                            "Conflict while registering an item runtime id!"
                    );
                }
                verify(networkLegacyMap.put(entry.id, fullId | (hasData ? 1 : 0)) == 0,
                        "Conflict while registering an item runtime id!"
                );
            }
        }

        this.namespaceNetworkMap = namespaceNetworkMap.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.legacyNetworkMap.defaultReturnValue(-1);
        this.networkLegacyMap.defaultReturnValue(-1);

        this.generatePalette();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private synchronized void generatePalette() {
        if (this.entries == null) {
            return;
        }
        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(this.entries.size());
        for (RuntimeItems.Entry entry : this.entries) {
            paletteBuffer.putString(entry.name.replace("minecraft:", ""));
            paletteBuffer.putLShort(entry.id);
            paletteBuffer.putBoolean(entry.isComponentItem); // Component item
        }
        this.itemDataPalette = paletteBuffer.getBuffer();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public synchronized void registerCustomItem(CustomItem customItem, Supplier<Item> constructor) {
        var runtimeId = CustomItemDefinition.getRuntimeId(customItem.getNamespaceId());
        RuntimeItems.Entry entry = new RuntimeItems.Entry(
                customItem.getNamespaceId(),
                runtimeId,
                null,
                null,
                false,
                true
        );
        this.customItemEntries.put(customItem.getNamespaceId(), entry);
        this.entries.add(entry);
        this.registerNamespacedIdItem(customItem.getNamespaceId(), constructor);
        this.namespaceNetworkMap.put(customItem.getNamespaceId(), OptionalInt.of(runtimeId));
        this.networkNamespaceMap.put(runtimeId, customItem.getNamespaceId());
        this.generatePalette();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public synchronized void deleteCustomItem(CustomItem customItem) {
        RuntimeItems.Entry entry = this.customItemEntries.remove(customItem.getNamespaceId());
        if (entry != null) {
            this.entries.remove(entry);
            this.namespaceNetworkMap.remove(customItem.getNamespaceId());
            this.networkNamespaceMap.remove(CustomItemDefinition.getRuntimeId(customItem.getNamespaceId()));
            this.generatePalette();
        }
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public synchronized void registerCustomBlock(List<CustomBlock> blocks) {
        for (var block : blocks) {
            int id = 255 - block.getId();//方块物品id等于 255-方块id(即-750开始递减)
            RuntimeItems.Entry entry = new RuntimeItems.Entry(
                    block.getNamespaceId(),//方块命名空间也是方块物品命名空间
                    id,
                    id,
                    null,
                    null,
                    false
            );
            this.customBlockEntries.put(block.getNamespaceId(), entry);
            this.entries.add(entry);
            this.namespacedIdItem.put(block.getNamespaceId(), block::toItem);
            this.namespaceNetworkMap.put(block.getNamespaceId(), OptionalInt.of(id));
            this.networkNamespaceMap.put(id, block.getNamespaceId());
            int fullId = RuntimeItems.getFullId(id, 0);
            legacyNetworkMap.put(fullId, id << 1);//todo 实现多状态方块需要在这里加入数据值判断
            networkLegacyMap.put(id, fullId);
        }
        this.generatePalette();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public synchronized void deleteCustomBlock(List<CustomBlock> blocks) {
        for (var block : blocks) {
            RuntimeItems.Entry entry = this.customBlockEntries.remove(block.getNamespaceId());
            if (entry != null) {
                this.entries.remove(entry);
                this.namespaceNetworkMap.remove(block.getNamespaceId());
                this.networkNamespaceMap.remove(255 - block.getId());
            }
        }
        this.generatePalette();
    }

    /**
     * Returns the <b>network id</b> based on the <b>full id</b> of the given item.
     *
     * @param item Given item
     * @return The <b>network id</b>
     * @throws IllegalArgumentException If the mapping of the <b>full id</b> to the <b>network id</b> is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getNetworkFullId(Item item) {
        if (item instanceof StringItem) {
            return namespaceNetworkMap.getOrDefault(item.getNamespaceId(), OptionalInt.empty()).orElseThrow(() -> new IllegalArgumentException("Unknown item mapping " + item)) << 1;
        }

        int fullId = RuntimeItems.getFullId(item.getId(), item.hasMeta() ? item.getDamage() : -1);
        int networkFullId = this.legacyNetworkMap.get(fullId);
        if (networkFullId == -1 && !item.hasMeta() && item.getDamage() != 0) { // Fuzzy crafting recipe of a remapped item, like charcoal
            networkFullId = this.legacyNetworkMap.get(RuntimeItems.getFullId(item.getId(), item.getDamage()));
        }
        if (networkFullId == -1) {
            networkFullId = this.legacyNetworkMap.get(RuntimeItems.getFullId(item.getId(), 0));
        }
        if (networkFullId == -1) {
            throw new IllegalArgumentException("Unknown item mapping " + item);
        }

        return networkFullId;
    }

    /**
     * Returns the <b>full id</b> of a given <b>network id</b>.
     *
     * @param networkId The given <b>network id</b>
     * @return The <b>full id</b>
     * @throws IllegalArgumentException If the mapping of the <b>full id</b> to the <b>network id</b> is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getLegacyFullId(int networkId) {
        int fullId = networkLegacyMap.get(networkId);
        if (fullId == -1) {
            throw new IllegalArgumentException("Unknown network mapping: " + networkId);
        }
        return fullId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public byte[] getItemDataPalette() {
        return this.itemDataPalette;
    }

    /**
     * Returns the <b>namespaced id</b> of a given <b>network id</b>.
     *
     * @param networkId The given <b>network id</b>
     * @return The <b>namespace id</b> or {@code null} if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public String getNamespacedIdByNetworkId(int networkId) {
        return networkNamespaceMap.get(networkId);
    }

    /**
     * Returns the <b>network id</b> of a given <b>namespaced id</b>.
     *
     * @param namespaceId The given <b>namespaced id</b>
     * @return A <b>network id</b> wrapped in {@link OptionalInt} or an empty {@link OptionalInt} if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public OptionalInt getNetworkIdByNamespaceId(@NotNull String namespaceId) {
        return namespaceNetworkMap.getOrDefault(namespaceId, OptionalInt.empty());
    }

    /**
     * Creates a new instance of the respective {@link Item} by the <b>namespaced id</b>.
     *
     * @param namespaceId The namespaced id
     * @param amount      How many items will be in the stack.
     * @return The correct {@link Item} instance with the write <b>item id</b> and <b>item damage</b> values.
     * @throws IllegalArgumentException If there are unknown mappings in the process.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public Item getItemByNamespaceId(@NotNull String namespaceId, int amount) {
        Supplier<Item> constructor = this.namespacedIdItem.get(namespaceId.toLowerCase(Locale.ENGLISH));
        if (constructor != null) {
            try {
                Item item = constructor.get();
                item.setCount(amount);
                return item;
            } catch (Exception e) {
                log.warn("Could not create a new instance of {} using the namespaced id {}", constructor, namespaceId, e);
            }
        }

        int legacyFullId;
        try {
            legacyFullId = getLegacyFullId(
                    getNetworkIdByNamespaceId(namespaceId)
                            .orElseThrow(() -> new IllegalArgumentException("The network id of \"" + namespaceId + "\" is unknown"))
            );
        } catch (IllegalArgumentException e) {
            log.debug("Found an unknown item {}", namespaceId, e);
            Item item = new StringItemUnknown(namespaceId);
            item.setCount(amount);
            return item;
        }

        if (RuntimeItems.hasData(legacyFullId)) {
            return Item.get(RuntimeItems.getId(legacyFullId), RuntimeItems.getData(legacyFullId), amount);
        } else {
            Item item = Item.get(RuntimeItems.getId(legacyFullId));
            item.setCount(amount);
            return item;
        }
    }


    @SneakyThrows
    @PowerNukkitOnly
    public void registerNamespacedIdItem(@NotNull Class<? extends StringItem> item) {
        Constructor<? extends StringItem> declaredConstructor = item.getDeclaredConstructor();
        var Item = declaredConstructor.newInstance();
        registerNamespacedIdItem(Item.getNamespaceId(), stritemSupplier(declaredConstructor));
    }

    @PowerNukkitOnly
    public void registerNamespacedIdItem(@NotNull String namespacedId, @NotNull Constructor<? extends Item> constructor) {
        Preconditions.checkNotNull(namespacedId, "namespacedId is null");
        Preconditions.checkNotNull(constructor, "constructor is null");
        this.namespacedIdItem.put(namespacedId.toLowerCase(Locale.ENGLISH), itemSupplier(constructor));
    }

    @SneakyThrows
    @PowerNukkitOnly
    public void registerNamespacedIdItem(@NotNull String namespacedId, @NotNull Supplier<Item> constructor) {
        Preconditions.checkNotNull(namespacedId, "namespacedId is null");
        Preconditions.checkNotNull(constructor, "constructor is null");
        this.namespacedIdItem.put(namespacedId.toLowerCase(Locale.ENGLISH), constructor);
    }

    @NotNull
    private static Supplier<Item> itemSupplier(@NotNull Constructor<? extends Item> constructor) {
        return () -> {
            try {
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new UnsupportedOperationException(e);
            }
        };
    }

    @Since("1.19.60-r1")
    @PowerNukkitXOnly
    @NotNull
    private static Supplier<Item> stritemSupplier(@NotNull Constructor<? extends StringItem> constructor) {
        return () -> {
            try {
                return (Item) constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new UnsupportedOperationException(e);
            }
        };
    }
}
