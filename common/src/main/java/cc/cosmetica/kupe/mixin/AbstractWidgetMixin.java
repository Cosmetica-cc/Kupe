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

package cc.cosmetica.kupe.mixin;

import cc.cosmetica.kupe.impl.KupeScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Stop AbstractWidgets using depth test to draw text and buttons in Kupe screens.
 * Kupe implements Z-ordering via draw order.
 */
@Mixin(AbstractButton.class)
public class AbstractWidgetMixin {
    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V")
    )
    private void onEnableDepthTest() {
        if (!(Minecraft.getInstance().screen instanceof KupeScreen)) {
            RenderSystem.enableDepthTest();
        }
    }
}
