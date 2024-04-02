package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.maths.Region;
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

		this.tree = new ComponentTree(rootComponent);
	}

	private final ComponentTree tree;

	@Override
	public void init(Minecraft minecraft, int w, int h) {
		this.tree.buildAll();
		this.resize(minecraft, w, h);
	}

	@Override
	public void resize(Minecraft minecraft, int w, int h) {
		this.tree.resizeAll(new Region(0, 0, w, h));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
		this.tree.render(new PoseCanvas(poseStack, this.minecraft, tickDelta), mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button); // returns whether to consume click and not pass to game
		// TODO implement this
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.tree.mouseMoved(mouseX, mouseY);
	}
}
