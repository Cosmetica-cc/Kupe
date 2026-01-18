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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Used for API components that wrap minecraft components. This is not recommended to be instantiated directly.
 */
public abstract class MinecraftBuiltinComponent extends Component {
	// internal //
	protected AbstractWidget minecraftWidget;
	protected boolean disabled = false;

	// build //
	@Override
	public List<Component> build() {
		return ImmutableList.of();
	}

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		this.minecraftWidget = this.createMinecraftWidget(region, context);
		this.minecraftWidget.active = !this.disabled;
	}

	@LeavesSandbox
	abstract public AbstractWidget createMinecraftWidget(Region region, Context context);

	/**
	 * Set whether the component should be disabled.
	 * @param disabled whether to disable the component.
	 * @return this component.
	 */
	protected MinecraftBuiltinComponent setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (this.minecraftWidget != null) {
			this.minecraftWidget.active = !disabled;
		}
		return this;
	}

	// render //
	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		canvas.renderMinecraftComponent(this.minecraftWidget, mouseX, mouseY);
	}

	// interact //
	@Override
	public void mouseClicked(Element target, double x, double y, int button) {
		if (target.getComponent() == this) {
			if (target instanceof InternalFocusable focus && !(target.getComponent() instanceof Button)) {
				focus.setFocused(this, (c, b) -> {
					if (c instanceof MinecraftBuiltinComponent mc) {
						if (mc.minecraftWidget != null) {
							mc.minecraftWidget.setFocused(b);
						}
					}
				});
			}

			MouseButtonEvent event = new MouseButtonEvent(x, y, new MouseButtonInfo(button, KupeScreen.clickModifiers));
			this.minecraftWidget.mouseClicked(event, KupeScreen.doubleClick);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		KeyEvent e = new KeyEvent(keyCode, scanCode, modifiers);
		return this.minecraftWidget.keyPressed(e);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		KeyEvent e = new KeyEvent(keyCode, scanCode, modifiers);
		return this.minecraftWidget.keyReleased(e);
	}

	@Override
	public boolean charTyped(char symbol, int modifiers) {
		CharacterEvent e = new CharacterEvent(symbol, modifiers);
		return this.minecraftWidget.charTyped(e);
	}
}
