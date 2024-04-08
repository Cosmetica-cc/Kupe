package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class Button extends Component {
	/**
	 * Create a new buttom with the given text.
	 * @param text the text to provide.
	 */
	public Button(Text text, Runnable onClicked) {
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
	public List<Component> build() {
		return ImmutableList.of();
	}

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children) {
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

	private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(200, 20);
	private static final Style DEFAULT_STYLE = Style.create()
			.set(CommonProperties.WIDTH, (vw, vh) -> OptionalInt.of(DEFAULT_DIMENSIONS.getWidth()))
			.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(DEFAULT_DIMENSIONS.getHeight()));

	static {
		RootStylesheet.setDefaultOverrides(Button.class, DEFAULT_STYLE);
	}
}
