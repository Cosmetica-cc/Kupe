package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.maths.Vec3;

public class MathsImpl {
	public static Vec3 createVec3(double x, double y, double z) {
		return (Vec3) new net.minecraft.world.phys.Vec3(x, y, z);
	}
}
