package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/**
 * An example component that uses a reactive state.
 */
public class HelloHolaComponent extends Component {
	private final State<String[]> messages = new State<>(new String[] { "Hello", "Hola" });

	@Override
	public List<Component> build() {
		String[] messages = this.messages.acquire(this);

		return Collections.singletonList(
				new Div(
						new Button(Text.literal("Say " + messages[0] + ", World!"), () -> this.printMessage(messages[0])),
						new Button(Text.literal("Say " + messages[1] + ", World!"), () -> this.printMessage(messages[1]))
				).withStyle(new Stylesheet()
						.self(Style.create()
								.set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
								.set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN)
								.setFixed(CommonProperties.WIDTH, OptionalInt.of(200))
								.setFixed(CommonProperties.MARGINS, new Margins(20, 0)))
						.component(Button.class, Style.create()
								.setFixed(CommonProperties.WIDTH, OptionalInt.of(90))))
		);
	}

	private void printMessage(String message) {
		System.out.println(message + ", World!");

		String[] newMessages = Arrays.stream(allMessages).filter(m -> !message.equals(m)).toArray(String[]::new);
		this.messages.set(newMessages);
	}

	private static final String[] allMessages = {
			"Hello",
			"Hola",
			"Guten Tag"
	};
}
