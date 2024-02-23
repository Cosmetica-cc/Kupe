package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Position;
import cc.cosmetica.kupe.api.maths.Vec3;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathsImpl {
	public static Vec3 createVec3(double x, double y, double z) {
		return (Vec3) new net.minecraft.world.phys.Vec3(x, y, z);
	}

	/**
	 * Calculate the size for absolute positioning.
	 * @param children the children of the given widget. Not all are guaranteed to be in the positions map.
	 * @param positions the absolute positions of the components.
	 * @return the size of the components.
	 */
	public static Dimensions calculateSizeAbsolute(
			List<Tuple<Component<?>, Dimensions>> children,
			Map<Component<?>, Position> positions) {
		boolean first = true;
		int x0 = 0;
		int x1 = 0;
		int y0 = 0;
		int y1 = 0;

		for (Tuple<Component<?>, Dimensions> component : children) {
			Position position = positions.get(component.getA());

			// if this component is absolutely positioned take it into account.
			if (position != null) {
				Dimensions dimensions = component.getB();

				if (first) {
					first = false;

					// initially, it's just this widget's position.
					x0 = position.x;
					y0 = position.y;
					x1 = x0 + dimensions.getWidth();
					y1 = y0 + dimensions.getHeight();
				} else {
					// check if we need to expand the region for the new widget
					// First, does the start need to be moved back?
					int wx = position.x;
					int wy = position.y;

					if (wx < x0) {
						x0 = wx;
					}

					if (wy < y0) {
						y0 = wy;
					}

					// Second, does the end need to be moved forward?
					wx = wx + dimensions.getWidth();
					wy = wy + dimensions.getHeight();

					if (wx > x1) {
						x1 = wx;
					}

					if (wy > y1) {
						y1 = wy;
					}
				}
			}
		}

		return new Dimensions(x1 - x0, y1 - y0);
	}
}
