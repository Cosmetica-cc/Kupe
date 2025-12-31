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

package cc.cosmetica.kupe.api.maths;

/**
 * A four-by-four matrix. Operations will modify the matrix in-place.
 */
public interface Matrix4 {
	/**
	 * Set this matrix to identity.
	 * @return the identity matrix.
	 */
	Matrix4 identity();

	/**
	 * Transpose the matrix.
	 * @return the transposed matrix.
	 */
	Matrix4 trans();

	/**
	 * Multiply with another matrix.
	 * @return the multiplied matrix.
	 */
	Matrix4 mul(Matrix4 other);

	/**
	 * Rotate around the given axis.
	 * @param axis the axis to rotate around.
	 * @param amount the amount to rotate by.
	 * @param degrees whether to interpret the amount as degrees. If this is false, it will use radians.
	 * @return a new, rotated matrix4.
	 */
	Matrix4 rot(Vec3 axis, float amount, boolean degrees);

	/**
	 * Create an identity matrix.
	 * @return a new identity matrix.
	 */
	static Matrix4 create() {
		return (Matrix4) new org.joml.Matrix4f();
	}
}