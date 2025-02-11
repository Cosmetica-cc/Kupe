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

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Position;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.Arrays;
import java.util.List;

/**
 * A layered space of components that all occupy the same region.
 */
public class LayeredSpace extends Component {
	public LayeredSpace(boolean fill, Component... children) {
		this.fill = fill;
		this.children = Arrays.asList(children);
	}

	private boolean fill;
	private final List<Component> children;

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		if (this.fill) {
			// default behaviour: lay children to fill, if possible
			super.resize(region, sizedElement, children, context);
		} else {
			// lay children with preferred sizes, if possible
			final Position start = new Position(region.getX(), region.getY());

			for (ResizableElement child : children) {
				Dimensions preferred = child.getPreferredSize();
				Region childRegion = layChildToPreferredSize(region, start, preferred, child);
				child.setRenderRegion(childRegion);
			}
		}
	}

	@Override
	public List<Component> build() {
		return this.children;
	}

	public boolean isVisible(Region region, int x, int y) {
		return this.getStyle().get(CommonProperties.BACKGROUND_COLOUR).isPresent() && super.isVisible(region, x, y);
	}
}
