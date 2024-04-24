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

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import cc.cosmetica.kupe.impl.LeavesSandbox;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * A matrix stack, for manipulating the rendering matrix.
 */
public interface MatrixStack {
	/**
	 * Push a duplicate of the current matrix onto the stack. This matrix can then be manipulated for transformation, and
	 * the current one restored to with a later pop().
	 * @apiNote all push() calls should have an associated pop().
	 */
	void push();

	/**
	 * Pop the last matrix off the stack, restoring to a previous state.
	 */
	void pop();

	/**
	 * Get the current pose's transformation matrix.
	 * @return the current pose.
	 */
	Matrix4 peek();

	/**
	 * Translate the pose by the given amount.
	 * @param x the amount to translate in the x direction.
	 * @param y the amount to translate in the y direction.
	 * @param z the amount to translate in the z direction.
	 */
	void translate(double x, double y, double z);

	/**
	 * Rotate the pose around the given axis.
	 * @param axis the axis to rotate around.
	 * @param amount the amount to rotate by.
	 * @param degrees whether to interpret the amount as degrees. If this is false, it will use radians.
	 */
	void rotate(Vec3 axis, float amount, boolean degrees);

	/**
	 * Scale the pose by the given amount in each cardinal direction.
	 * @param x the amount to scale in x direction.
	 * @param y the amount to scale in y direction.
	 * @param z the amount to scale in z direction.
	 */
	void scale(float x, float y, float z);

	/**
	 * Get the minecraft {@link PoseStack} representation of this {@link MatrixStack}.
	 * @return the minecraft pose stack.
	 */
	@LeavesSandbox
	PoseStack getMinecraftStack();
}
