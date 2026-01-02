package cc.cosmetica.kupe.mixin;

import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
    @Accessor
    GuiRenderState getGuiRenderState();
}
