/*
 * Copyright 2024 Cosmetica
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

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.FakePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.UUID;

public class NameTagAttachment implements FakePlayer.Attachment<Text> {
	@Override
	public void render(Canvas canvas, Text configuration, Quaternion cameraOrientation, MultiBufferSource bufferSource, int packedLight) {
		PoseStack stack = canvas.getStack().getMinecraftStack();

		float yPosition = EntityType.PLAYER.getDimensions().height + 0.5F;

		stack.translate(0.0D, yPosition, 0.0D);
		stack.mulPose(cameraOrientation);
		stack.scale(-0.025F, -0.025F, 0.025F);

		Component name = configuration.toMinecraftComponent();

		boolean fullyRender = true; // TODO !player.renderDiscreteNametag();
		int offsetForDeadmau5 = "deadmau5".equals(name.getString()) ? -10 : 0;

		Matrix4f pose = stack.last().pose();
		float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
		int k = (int)(backgroundOpacity * 255.0F) << 24;
		Font font = Minecraft.getInstance().font;
		float h = (float)(-font.width(name) / 2);
		font.drawInBatch(name, h, (float)offsetForDeadmau5, 0x20FFFFFF, false, pose, bufferSource, fullyRender, k, packedLight);

		if (fullyRender) {
			font.drawInBatch(name, h, (float)offsetForDeadmau5, -1, false, pose, bufferSource, false, 0, packedLight);
		}
		//todo additional name tags
	}

	@Override
	public Text getDynamicConfiguration(UUID uuid) {
		PlayerInfo loadedProfile = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);

		if (loadedProfile == null) {
			return Text.literal("Player");
		} else {
			return null;//todo
		}
	}

	@Override
	public boolean isNameTag() {
		return true;
	}

	@Override
	public boolean defaultEnable() {
		return false;
	}
}
