package cc.cosmetica.kupe.impl.fakeplayer;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.FakePlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class CapeAttachment implements FakePlayer.Attachment<ResourceLocation> {
	@Override
	public void render(Canvas canvas, ResourceLocation configuration) {

	}

	@Override
	public ResourceLocation getUserConfiguration(UUID uuid) {
		return null;
	}
}
