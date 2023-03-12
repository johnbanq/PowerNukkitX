package cn.nukkit.level.format.generic;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.ChunkSection3DBiome;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.updater.ChunkUpdater;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.collection.ByteArrayWrapper;
import cn.nukkit.utils.collection.FreezableArrayManager;

import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ParametersAreNonnullByDefault
public class EmptyChunkSection implements ChunkSection, ChunkSection3DBiome {
    @SuppressWarnings("java:S2386")
    public static final EmptyChunkSection[] EMPTY = new EmptyChunkSection[16];
    @SuppressWarnings("java:S2386")
    public static final EmptyChunkSection[] EMPTY24 = new EmptyChunkSection[24];
    public static final byte[] EMPTY_SKY_LIGHT_ARR = new byte[2048]; // Filled with 0xFF
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final byte[] EMPTY_ID_ARRAY = new byte[4096];
    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    public static final byte[] EMPTY_BIOME_ARRAY = new byte[4096];
    private static final String MODIFICATION_ERROR_MESSAGE = "Tried to modify an empty Chunk";
    private static final String BIOME_TAG_NAME = "Biomes";
    private static final byte[] EMPTY_2KB = new byte[2048];
    public static final byte[] EMPTY_LIGHT_ARR = EMPTY_2KB;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final byte[] EMPTY_DATA_ARRAY = EMPTY_2KB;
    private static final byte[] EMPTY_CHUNK_DATA;

    static {
        for (int y = 0; y < EMPTY.length; y++) {
            EMPTY[y] = new EmptyChunkSection(y);
        }
        for (int y = 0; y < EMPTY24.length; y++) {
            EMPTY24[y] = new EmptyChunkSection(y);
        }
    }

    static {
        Arrays.fill(EMPTY_SKY_LIGHT_ARR, (byte) 255);
    }

    static {
        BinaryStream stream = new BinaryStream();
        stream.putByte((byte) cn.nukkit.level.format.anvil.ChunkSection.STREAM_STORAGE_VERSION);
        stream.putByte((byte) 0);
        EMPTY_CHUNK_DATA = stream.getBuffer();
    }

    private final int y;
    private final ByteArrayWrapper biomeId;

    public EmptyChunkSection(int y) {
        this.y = y;
        this.biomeId = FreezableArrayManager.getInstance().createByteArray(4096);
    }

    public EmptyChunkSection(int y, byte[] biomeId) {
        this.y = y;
        this.biomeId = FreezableArrayManager.getInstance().wrapByteArray(biomeId);
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r5")
    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x; // YZX
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public final int getBlockId(int x, int y, int z) {
        return 0;
    }

    @PowerNukkitOnly
    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return 0;
    }

    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return BlockState.AIR;
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @PowerNukkitOnly
    @NotNull
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        if (block.getId() != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return Block.get(0);
    }

    @NotNull
    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        if (block.getId() != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return Block.get(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state) {
        if (!BlockState.AIR.equals(state)) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return BlockState.AIR;
    }

    @PowerNukkitOnly
    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        if (id != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @PowerNukkitOnly
    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        if (!state.equals(BlockState.AIR)) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public byte[] getSkyLightArray() {
        return EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        return EMPTY_LIGHT_ARR;
    }

    @Override
    public final void setBlockId(int x, int y, int z, int id) {
        if (id != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public final int getBlockData(int x, int y, int z) {
        return 0;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return 0;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int data) {
        if (data != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        if (data != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        if (fullId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        if (fullId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (level != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (level != 15) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void writeTo(@NotNull BinaryStream stream) {
        stream.put(EMPTY_CHUNK_DATA);
    }

    @Since("1.19.60-r1")
    @Override
    public long getBlockChanges() {
        return 0;
    }

    @Since("1.19.60-r1")
    @Override
    public void addBlockChange() {

    }

    @PowerNukkitOnly
    @Override
    public int getMaximumLayer() {
        return 0;
    }

    @PowerNukkitOnly
    @NotNull
    @Override
    public CompoundTag toNBT() {
        var s = new CompoundTag();
        s.putInt("Y", getY());
        s.putByteArray(BIOME_TAG_NAME, biomeId.getRawBytes());
        s.putByte("Version", -1);
        return s;
    }

    @NotNull
    @Override
    public EmptyChunkSection copy() {
        return new EmptyChunkSection(this.y, this.biomeId.getRawBytes());
    }

    @PowerNukkitOnly
    @Since("1.3.1.0-PN")
    @Override
    public int getContentVersion() {
        return ChunkUpdater.getCurrentContentVersion();
    }

    @PowerNukkitOnly
    @Since("1.3.1.0-PN")
    @Override
    public void setContentVersion(int contentVersion) {
        if (contentVersion != getContentVersion()) {
            throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        }
    }

    @PowerNukkitOnly
    @Override
    public int getBlockChangeStateAbove(int x, int y, int z) {
        return 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        return Collections.emptyList();
    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        return this.biomeId.getByte(getAnvilIndex(x, y, z));
    }

    @Override
    public void setBiomeId(int x, int y, int z, byte id) {
        this.biomeId.setByte(getAnvilIndex(x, y, z), id);
    }

    @Override
    public byte[] get3DBiomeDataArray() {
        return this.biomeId.getRawBytes();
    }

    @Override
    public void set3DBiomeDataArray(byte[] data) {
        if (data.length != 4096) {
            throw new ChunkException("Invalid biome data length, expected 4096, got " + data.length);
        }
        this.biomeId.setRawBytes(Arrays.copyOf(data, 4096));
    }
}
