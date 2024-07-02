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

package cc.cosmetica.kupe.util;

import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

/**
 * Adapted from code in Cosmetica. This allows for easier porting between 1.16.5 and 1.17+.
 * <a href="https://github.com/Cosmetica-cc/Cosmetica/blob/1.16.5/src/main/java/cc/cosmetica/cosmetica/utils/GlobalPoseStack.java">Source</a>.
 */
@SuppressWarnings("deprecated")
public final class GlobalMatrixStack extends PoseStack implements MatrixStack {
	private GlobalMatrixStack() {
	}

	// Shared
	@Override
	public void translate(double d, double e, double f) {
		RenderSystem.translated(d, e, f);
	}

	@Override
	public void scale(float f, float g, float h) {
		RenderSystem.scalef(f, g, h);
	}

	// MatrixStack
	@Override
	public void push() {
		RenderSystem.pushMatrix();
	}

	@Override
	public void pop() {
		RenderSystem.popMatrix();
	}

	@Override
	public PoseStack getMinecraftStack() {
		return this;
	}

	@Override
	public Matrix4 peek() {
		throw new UnsupportedOperationException("Cannot peek at pose on the global matrix stack.");
	}

	@Override
	public void rotate(Vec3 axis, float amount, boolean degrees) {
		throw new UnsupportedOperationException("Cannot rotate the global matrix stack.");
	}

	// PoseStack
	@Override
	public void pushPose() {
		RenderSystem.pushMatrix();
	}

	@Override
	public void popPose() {
		RenderSystem.popMatrix();
	}

	@Override
	public void mulPose(Quaternion quaternion) {
		throw new UnsupportedOperationException("Cannot mulPose on the global pose stack.");
	}

	@Override
	public Pose last() {
		throw new UnsupportedOperationException("Cannot get last pose on the global pose stack.");
	}

	@Override
	public boolean clear() {
		throw new UnsupportedOperationException("Cannot clear the global pose stack.");
	}

	public static final GlobalMatrixStack INSTANCE = new GlobalMatrixStack();
}

