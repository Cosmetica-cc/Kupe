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

package cc.cosmetica.kupe.impl.fakeplayer;

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class FakePlayerGuiRenderer extends PictureInPictureRenderer<FakePlayerGuiRenderer.State> {
    public FakePlayerGuiRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public @NotNull Class<State> getRenderStateClass() {
        return FakePlayerGuiRenderer.State.class;
    }

    // GuiEntityRenderer#renderToTexture
    @Override
    protected void renderToTexture(State arg, PoseStack stack) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        // InventoryScreen#renderEntityInInventory
        stack.pushPose();
        stack.translate(arg.x0, arg.y0, 1050.0D);
        stack.scale(2.0F, 2.0F, -1.0F);

        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(arg.extraScale, arg.extraScale, arg.extraScale);
        stack.mulPose(arg.zRotation);

        arg.renderer.renderFakePlayer(
                arg.player,
                arg.cameraOrientation,
                arg.context,
                stack,
                this.bufferSource,
                0.0D,
                0.0D,
                0.0D,
                0.0F,
                1.0F,
                15728880
        );
    }

    @Override
    protected @NotNull String getTextureLabel() {
        return "guiplayer";
    }

    @Override
    protected float getTranslateY(int i, int j) {
        return i / 2.0f;
    }

    public static class State implements PictureInPictureRenderState {
        public State(FakePlayerRenderer renderer, GUIPlayer player, float extraScale, Quaternionf cameraOrientation, Quaternionf zRotation, Context context, int left, int top,
                     int x0, int x1, int y0, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) {
            // PictureInPicture
            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;
            this.y1 = y1;
            this.scale = scale;
            this.scissorArea = scissorArea;
            this.bounds = bounds;
            // FakePlayerRenderer
            this.left = left;
            this.top = top;
            this.renderer = renderer;
            this.player = player;
            this.extraScale = extraScale;
            this.context = context;
            this.cameraOrientation = cameraOrientation;
            this.zRotation = zRotation;
        }

        private final int left, top, x0, x1, y0, y1;
        private final float scale, extraScale;
        private @Nullable ScreenRectangle scissorArea;
        private @Nullable ScreenRectangle bounds;
        private final FakePlayerRenderer renderer;
        private final GUIPlayer player;
        private final Context context;
        private final Quaternionf cameraOrientation, zRotation;

        @Override
        public int x0() {
            return this.x0;
        }

        @Override
        public int x1() {
            return this.x1;
        }

        @Override
        public int y0() {
            return this.y0;
        }

        @Override
        public int y1() {
            return this.y1;
        }

        @Override
        public float scale() {
            return this.scale;
        }

        @Override
        public @Nullable ScreenRectangle scissorArea() {
            return this.scissorArea;
        }

        @Override
        public @Nullable ScreenRectangle bounds() {
            return this.bounds;
        }
    }
}
