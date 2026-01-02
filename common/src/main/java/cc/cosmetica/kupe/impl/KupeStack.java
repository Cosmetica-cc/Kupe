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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import cc.cosmetica.kupe.impl.maths.Mat4f;
import org.joml.AxisAngle4f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * MatrixStack wrapping Matrix3x2fStack.
 */
public class KupeStack implements MatrixStack {
	public KupeStack(Matrix3x2fStack stack) {
		this.stack = stack;
	}

	private Matrix3x2fStack stack;

	@Override
	public void push() {
		this.stack.pushMatrix();
	}

	@Override
	public void pop() {
		this.stack.popMatrix();
	}

	@Override
	public Matrix4 peek() {
		throw new UnsupportedOperationException("Underlying representation is not Matrix4");
	}

	@Override
	public void translate(double x, double y, double z) {
		this.stack.translate((float) x, (float) y);
	}

	@Override
	public void rotate(Vec3 axis, float amount, boolean degrees) {
		float amount2 = axis.getZ() < 0 ? -amount : amount;
		this.stack.rotate(degrees ? (float) Math.toRadians(amount2) : amount2);
	}

	@Override
	public void scale(float x, float y, float z) {
		this.stack.scale(x, y);
	}

	@Override
	public Matrix3x2fStack getMinecraftStack() {
		return this.stack;
	}
}
