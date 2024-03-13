package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * The screen that controls the Kupe gui rendering. This handles the rendering and event calls to the root component.
 */
public final class KupeScreen extends Screen {
	public KupeScreen(ResourceLocation location, Component<?> rootComponent) {
		super(new TranslatableComponent("screens." + location.getNamespace() + "." + location.getPath()));

		this.root = rootComponent;
	}

	private final Component<?> root;

	@Override
	public void init(Minecraft minecraft, int i, int j) {
		// TODO build and resize
	}

	@Override
	public void resize(Minecraft minecraft, int i, int j) {
		// TODO resize
	}

	@Override
	public void render(PoseStack poseStack, int i, int j, float f) {
		// TODO render component
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return super.mouseClicked(d, e, i); // returns whether to consume click and not pass to game
		// TODO implement this
	}

	@Override
	public void mouseMoved(double d, double e) {
		// TODO implement this
	}
}
