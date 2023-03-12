package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityGoat extends EntityAnimal implements EntityWalkable {

    public static final int NETWORK_ID = 128;

    public EntityGoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return this.isBaby() ? 0.65f : 1.3f;
    }

    @Override
    public float getWidth() {
        return this.isBaby() ? 0.45f : 0.9f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Goat";
    }
}
