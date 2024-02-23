package cc.cosmetica.kupe.api.maths;

import cc.cosmetica.kupe.impl.MathsImpl;

/**
 * A 3d vector of doubles.
 */
public interface Vec3 {
	double x();
	double y();
	double z();

	static Vec3 of(double x, double y, double z) {
		return MathsImpl.createVec3(x, y, z);
	}
}
