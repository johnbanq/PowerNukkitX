package cn.nukkit.resourcepacks;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

@Log4j2
public abstract class AbstractResourcePack implements ResourcePack {
    protected JsonObject manifest;
    protected UUID id = null;

    @Override
    public String getPackName() {
        return this.manifest.getAsJsonObject("header")
                .get("name").getAsString();
    }

    @Override
    public UUID getPackId() {
        if (id == null) {
            id = UUID.fromString(this.manifest.getAsJsonObject("header").get("uuid").getAsString());
        }
        return id;
    }

    @Override
    public String getPackVersion() {
        JsonArray version = this.manifest.getAsJsonObject("header")
                .get("version").getAsJsonArray();

        return String.join(".", version.get(0).getAsString(),
                version.get(1).getAsString(),
                version.get(2).getAsString());
    }

    protected boolean verifyManifest() {
        if (this.manifest.has("format_version") && this.manifest.has("header") && this.manifest.has("modules")) {
            JsonObject header = this.manifest.getAsJsonObject("header");
            return header.has("description") &&
                    header.has("name") &&
                    header.has("uuid") &&
                    header.has("version") &&
                    header.getAsJsonArray("version").size() == 3;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcePack anotherPack && this.id.equals(anotherPack.getPackId());
    }
}
