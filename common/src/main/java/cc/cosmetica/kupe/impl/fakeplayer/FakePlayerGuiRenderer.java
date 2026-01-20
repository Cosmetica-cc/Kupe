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
import cc.cosmetica.kupe.api.maths.Region;
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
        // not sure if this exactly lines up with previous versions but it fits within the box as I rotate it around.
        stack.translate(0, (float) (arg.y1 - arg.y0) /2 + 10, 0);
        stack.scale(2.0F, 2.0F, 2.0F);

//        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(arg.extraScale, arg.extraScale, arg.extraScale);
        stack.mulPose(arg.zRotation);

        final float h = (float)Math.atan(arg.lookX / 40.0F);
        final float l = (float)Math.atan(arg.lookY / 40.0F);
        final GUIPlayer player = arg.player;

        // -------------------------------------------------//
        // InventoryScreen#renderEntityInInventoryFollowsMouse
        float rotationBody = 180.0F + h * 20.0F;
        float rotationMain = 180.0F + h * 40.0F;

        // rotate player to face lookX, lookY
        player.pose.yRotBody += rotationBody;
        player.pose.yRotHead += rotationMain;// yRotHead = yRot = getYRot(0);
        float xRotOld = player.pose.xRot;
        player.pose.xRot += -l * 20.0F;
        // -------------------------------------------------//

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

        // restore
        player.pose.yRotBody -= rotationBody;
        player.pose.yRotHead -= rotationMain;
        player.pose.xRot = xRotOld;

        stack.popPose();
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
        public State(FakePlayerRenderer renderer, GUIPlayer player, float extraScale, Quaternionf cameraOrientation, Quaternionf zRotation,
                     Context context, int left, int top, float lookX, float lookY,
                     Region region, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) {
            // PictureInPicture
            this.x0 = region.getX();
            this.x1 = region.getEndX();
            this.y0 = region.getY();
            this.y1 = region.getEndY();
            this.scale = scale;
            this.scissorArea = scissorArea;
            this.bounds = bounds;
            this.lookX = lookX;
            this.lookY = lookY;
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
        private final float scale, extraScale, lookX, lookY;
        private final @Nullable ScreenRectangle scissorArea;
        private final @Nullable ScreenRectangle bounds;
        private final FakePlayerRenderer renderer;
        private final GUIPlayer player;
        private final Context context;
        private final Quaternionf cameraOrientation, zRotation;

        @Override
        public int x0() {
            return this.x0 - 25;
        }

        @Override
        public int x1() {
            return this.x1 + 25;
        }

        @Override
        public int y0() {
            return this.y0 - 40; // fit nametag(s) in scissor
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
