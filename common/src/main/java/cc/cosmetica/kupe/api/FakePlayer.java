package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.gui.Component;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Component to show a player in the inventory.
 */
public class FakePlayer extends Component {
	@Override
	public List<Component> build() {
		return ImmutableList.of();
	}

	// TODO
}
