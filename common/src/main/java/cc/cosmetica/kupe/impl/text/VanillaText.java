package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Text;
import net.minecraft.network.chat.Component;

/**
 * Wrapper around a minecraft component.
 */
public class VanillaText implements Text {
    public VanillaText(Component component) {
        this.component = component;
    }

    private final Component component;

    @Override
    public String getString() {
        return this.component.toString();
    }

    @Override
    public String getDisplayString() {
        return this.component.getString();
    }

    @Override
    public Component toMinecraftComponent() {
        return this.component;
    }
}
