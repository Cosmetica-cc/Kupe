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

package cc.cosmetica.kupe.util;

import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Adapted from code in Cosmetica. This allows for easier porting between 1.16.5 and 1.17+.
 * <a href="https://github.com/Cosmetica-cc/Cosmetica/blob/1.16.5/src/main/java/cc/cosmetica/cosmetica/utils/GlobalPoseStack.java">Source</a>.
 */
@SuppressWarnings("deprecated")
public final class GlobalMatrixStack implements MatrixStack {
	private GlobalMatrixStack() {
	}

	// Shared
	@Override
	public void translate(double d, double e, double f) {
		GlStateManager.translate(d, e, f);
	}

	@Override
	public void scale(float f, float g, float h) {
		GlStateManager.scale(f, g, h);
	}

	// MatrixStack
	@Override
	public void push() {
		GlStateManager.pushMatrix();
	}

	@Override
	public void pop() {
		GlStateManager.popMatrix();
	}

	@Override
	public Matrix4 peek() {
		throw new UnsupportedOperationException("Cannot peek at pose on the global matrix stack.");
	}

	@Override
	public void rotate(Vec3 axis, float amount, boolean degrees) {
		float angle = degrees ? (float) Math.toRadians(amount) : amount;

		// Normalize axis
		float length = (float) Math.sqrt(axis.getX() * axis.getX() + axis.getY() * axis.getY() + axis.getZ() * axis.getZ());
		if (length == 0.0f) {
			return;
		}

		org.lwjgl.util.vector.Vector4f axisAngle = new org.lwjgl.util.vector.Vector4f(
				(float)(axis.getX() / length),
				(float)(axis.getY() / length),
				(float)(axis.getZ() / length),
				angle
		);

		org.lwjgl.util.vector.Quaternion rotation = new org.lwjgl.util.vector.Quaternion();
		rotation.setFromAxisAngle(axisAngle);

		GlStateManager.rotate(rotation);
	}

	public static final GlobalMatrixStack INSTANCE = new GlobalMatrixStack();
}

