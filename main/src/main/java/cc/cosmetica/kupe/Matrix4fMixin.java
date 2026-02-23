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

import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract class Matrix4fMixin implements cc.cosmetica.kupe.api.maths.Matrix4 {
	@Shadow public abstract Matrix4f copy();

	@Shadow public abstract void transpose();

	@Shadow public abstract void multiply(Matrix4f matrix4f);

	@Shadow public abstract void multiply(Quaternion quaternion);

	@Shadow public abstract void setIdentity();

	@Override
	public Matrix4 identity() {
		this.setIdentity();
		return this;
	}

	@Override
	public Matrix4 trans() {
		this.transpose();
		return this;
	}

	@Override
	public Matrix4 mul(Matrix4 other) {
		this.multiply((Matrix4f) (Object) other);
		return this;
	}

	@Override
	public Matrix4 rot(Vec3 axis, float amount, boolean degrees) {
		Vector3f vec3f = new Vector3f((float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
		Quaternion quaternion = new Quaternion(vec3f, amount, degrees);
		this.multiply(quaternion);
		return this;
	}
}