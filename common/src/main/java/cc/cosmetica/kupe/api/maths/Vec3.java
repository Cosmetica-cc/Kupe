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

package cc.cosmetica.kupe.api.maths;

import cc.cosmetica.kupe.impl.MathsImpl;

/**
 * A 3d vector of doubles. Operations generally create a new vector object.
 */
public interface Vec3 {
	double getX();
	double getY();
	double getZ();

	/**
	 * Add the given coordinates to the given vector.
	 * @param dx the change in x to this vector.
	 * @param dy the change in y to this vector.
	 * @param dz the change in z to this vector.
	 * @return a new Vec3 with the given changes.
	 */
	Vec3 plus(double dx, double dy, double dz);

	static Vec3 of(double x, double y, double z) {
		return MathsImpl.createVec3(x, y, z);
	}
}
