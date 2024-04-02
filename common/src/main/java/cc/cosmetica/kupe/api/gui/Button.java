package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Button extends Component<Stylesheet> {
	/**
	 * Create a new buttom with the given text.
	 * @param text the text to provide.
	 */
	public Button(Text text, Runnable onClicked) {
		super(Stylesheet.DEFAULT);
		this.text = text;
		this.onClicked = onClicked;
	}

	// properties
	private final Text text;
	public Runnable onClicked;
	// internal
	private net.minecraft.client.gui.components.Button minecraftButton;

	public Text getText() {
		return this.text;
	}

	@Override
	public List<Component<?>> build() {
		return ImmutableList.of();
	}

	@Override
	public void resize(Region region, List<? extends ResizableElement> children) {
		this.minecraftButton = new net.minecraft.client.gui.components.Button(
				region.getX(),
				region.getY(),
				region.getWidth(),
				region.getHeight(),
				this.text.toMinecraftComponent(),
				bn -> this.onClicked.run()
		);
	}

	@Override
	public void render(Canvas canvas, Region region, int mouseX, int mouseY) {
		canvas.renderMinecraftComponent(this.minecraftButton, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		this.minecraftButton.mouseClicked(x, y, button);
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(" + this.text.toString() + ")";
	}
}
