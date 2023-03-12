package cn.nukkit.blockproperty.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperty;

import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNullableByDefault;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
public class InvalidBlockPropertyException extends IllegalArgumentException {
    private static final long serialVersionUID = -6934630506175381230L;
    
    private final BlockProperty<?> property;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyException(@NotNull BlockProperty<?> property) {
        super(buildMessage(property));
        this.property = property;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyException(@NotNull BlockProperty<?> property, String message) {
        super(buildMessage(property) + ". " + message);
        this.property = property;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyException(@NotNull BlockProperty<?> property, String message, Throwable cause) {
        super(buildMessage(property) + ". " + message, cause);
        this.property = property;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyException(@NotNull BlockProperty<?> property, Throwable cause) {
        super(buildMessage(property), cause);
        this.property = property;
    }

    private static String buildMessage(@NotNull BlockProperty<?> property) {
        return "Property: " + property.getName();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public BlockProperty<?> getProperty() {
        return property;
    }
}
