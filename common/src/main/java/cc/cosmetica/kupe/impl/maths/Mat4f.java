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

package cc.cosmetica.kupe.impl.maths;

import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Mat4f implements Matrix4 {
    public Mat4f() {
        this.matrix = new Matrix4f();
    }

    private Mat4f(Matrix4f m) {
        this.matrix = m;
    }

    private final Matrix4f matrix;

    @Override
    public Matrix4 identity() {
        this.matrix.identity();
        return this;
    }

    @Override
    public Matrix4 trans() {
        this.matrix.transpose();
        return this;
    }

    @Override
    public Matrix4 mul(Matrix4 other) {
        this.matrix.mul(((Mat4f)other).matrix);
        return this;
    }

    @Override
    public Matrix4 rot(Vec3 axis, float amount, boolean degrees) {
        Vector3f vec3f = new Vector3f((float)axis.getX(), (float)axis.getY(), (float)axis.getZ());
        Quaternionf quaternion = new Quaternionf(new AxisAngle4f(degrees ? (float) Math.toRadians(amount) : amount, vec3f));
        this.matrix.rotate(quaternion);
        return this;
    }
}
