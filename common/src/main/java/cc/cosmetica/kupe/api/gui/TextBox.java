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
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MinecraftBuiltinComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

/**
 * A text box for entering text.
 */
public class TextBox extends MinecraftBuiltinComponent {
	/**
	 * Create a new button with the given text.
	 * @param placeholder the text to render as a placeholder.
	 * @param value the state to update when the text is changed.
	 * @param editable whether the text box should be editable.
	 * @param maxLength the maximum number of characters that can be entered into this box.
	 */
	public TextBox(Text placeholder, State<String> value, boolean editable, int maxLength) {
		this.placeholder = placeholder;
		this.value = value;
		this.editable = editable;
		this.maxLength = maxLength;
	}

	/**
	 * Set the function to run on enter.
	 * @param onEnter the code to run on enter.
	 * @return this text box.
	 */
	public TextBox onEnter(Consumer<String> onEnter) {
		this.onEnter = onEnter;
		return this;
	}

	// properties
	private final Text placeholder;
	private final State<String> value;
	private final boolean editable;
	private final int maxLength;
	private Consumer<String> onEnter;

	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		return this.tryFixed(DEFAULT_DIMENSIONS, padding, context);
	}

	@Override
	public AbstractWidget createMinecraftWidget(Region region, Context context) {
		/* NB this.minecraft.keyboardHandler.setSendRepeatsToGui(true); */
		EditBox box = new net.minecraft.client.gui.components.EditBox(
				Minecraft.getInstance().font,
				region.getX(),
				region.getY(),
				region.getWidth(),
				region.getHeight(),
				this.placeholder.toMinecraftComponent()
		);
		box.setEditable(this.editable);
		box.setMaxLength(this.maxLength);
		box.setResponder(this.value::set);
		return box;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.minecraftWidget.isFocused()) {
			if (keyCode == GLFW.GLFW_KEY_ENTER) {
				this.onEnter.accept(((EditBox)this.minecraftWidget).getValue());
			} else {
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(" + this.placeholder.toString() + ")";
	}

	private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(200, 20);
}
