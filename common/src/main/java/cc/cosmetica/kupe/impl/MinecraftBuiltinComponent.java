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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.ResizableElement;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;
import java.util.OptionalInt;

/**
 * Used for API components that wrap minecraft components. This is not recommended to be instantiated directly.
 */
public abstract class MinecraftBuiltinComponent extends Component {
	// internal //
	protected AbstractWidget minecraftWidget;

	// build //
	@Override
	public List<Component> build() {
		return ImmutableList.of();
	}

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		this.minecraftWidget = this.createMinecraftWidget(region, context);
	}

	@LeavesSandbox
	abstract public AbstractWidget createMinecraftWidget(Region region, Context context);

	// render //
	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		canvas.renderMinecraftComponent(this.minecraftWidget, mouseX, mouseY);
	}

	// interact //
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		return this.minecraftWidget.mouseClicked(x, y, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.minecraftWidget.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.minecraftWidget.keyReleased(keyCode, scanCode, modifiers);
	}
}
