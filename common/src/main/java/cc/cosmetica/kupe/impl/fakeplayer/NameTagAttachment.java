package cc.cosmetica.kupe.impl.fakeplayer;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.FakePlayer;

import java.util.UUID;

public class NameTagAttachment implements FakePlayer.Attachment<Text> {
	@Override
	public void render(Canvas canvas, Text configuration) {

	}

	@Override
	public Text getUserConfiguration(UUID uuid) {
		return null;
	}
}
