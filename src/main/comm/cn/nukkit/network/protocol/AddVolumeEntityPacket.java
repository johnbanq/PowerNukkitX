package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitOnly
@Since("1.5.0.0-PN")
public class AddVolumeEntityPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.ADD_VOLUME_ENTITY_PACKET;

    private long id;
    private CompoundTag data;
    /**
     * @since v465
     */
    private String engineVersion;
    /**
     * @since v485
     */
    private String identifier;
    /**
     * @since v485
     */
    private String instanceName;

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public AddVolumeEntityPacket() {
        // Does nothing
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        id = getUnsignedVarInt();
        data = getTag();
        engineVersion = getString();
        identifier = getString();
        instanceName = getString();
    }

    @Override
    public void encode() {
        reset();
        putUnsignedVarInt(id);
        putTag(data);
        putString(engineVersion);
        putString(identifier);
        putString(instanceName);
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public long getId() {
        return id;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setId(long id) {
        this.id = id;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public CompoundTag getData() {
        return data;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setData(CompoundTag data) {
        this.data = data;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public String getEngineVersion() {
        return engineVersion;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public String getIdentifier() {
        return identifier;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public String getInstanceName() {
        return instanceName;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}
