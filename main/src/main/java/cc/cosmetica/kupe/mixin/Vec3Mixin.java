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

import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3.class)
public abstract class Vec3Mixin implements cc.cosmetica.kupe.api.maths.Vec3 {
	@Shadow public abstract Vec3 add(double d, double e, double f);

	@Shadow @Final public double x;

	@Shadow @Final public double y;

	@Shadow @Final public double z;

	@Override
	public cc.cosmetica.kupe.api.maths.Vec3 plus(double dx, double dy, double dz) {
		return (cc.cosmetica.kupe.api.maths.Vec3) this.add(dx, dy, dz);
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getZ() {
		return this.z;
	}
}