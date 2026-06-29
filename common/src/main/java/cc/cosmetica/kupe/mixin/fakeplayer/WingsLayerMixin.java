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

package cc.cosmetica.kupe.mixin.fakeplayer;

import cc.cosmetica.kupe.impl.fakeplayer.ElytraAttachment;
import cc.cosmetica.kupe.impl.fakeplayer.GuiPlayerAvatarRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public class WingsLayerMixin {
    @Shadow
    @Final
    private ElytraModel elytraModel;
    @Shadow
    @Final
    private EquipmentLayerRenderer equipmentRenderer;

    @Inject(at = @At("RETURN"), method = "submit")
    private void submit(
            final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords,
            final HumanoidRenderState state,
            final float yRot, final float xRot,
            CallbackInfo info
    ) {
        if (state instanceof GuiPlayerAvatarRenderState guitar) {
            if (guitar.elytraProperties != null) {
                ElytraAttachment.submitWings(
                        guitar.elytraProperties,
                        (AvatarRenderState) state,
                        this.elytraModel,
                        state.outlineColor,
                        poseStack,
                        submitNodeCollector,
                        lightCoords,
                        ((EquipmentLayerRendererAccessor)this.equipmentRenderer).getEquipmentAssets()
                );
            }
        }
    }
}
