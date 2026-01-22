/*
 * Copyright 2024, 2025 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.impl.fabric.mixin;

import cc.cosmetica.kupe.impl.fakeplayer.FakePlayerGuiRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inject the FakePlayerGuiRenderer into the picture in picture renderers.
 */
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @Shadow
    @Final
    @Mutable
    private Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(
            GuiRenderState guiRenderState,
            MultiBufferSource.BufferSource bufferSource,
            List<PictureInPictureRenderer<?>> list, CallbackInfo ci) {
        this.pictureInPictureRenderers = new HashMap<>(this.pictureInPictureRenderers);
        this.pictureInPictureRenderers.put(FakePlayerGuiRenderer.State.class, new FakePlayerGuiRenderer(bufferSource));
    }
}
