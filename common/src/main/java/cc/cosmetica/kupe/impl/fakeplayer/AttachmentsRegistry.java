package cc.cosmetica.kupe.impl.fakeplayer;

import cc.cosmetica.kupe.api.gui.FakePlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttachmentsRegistry {
	private static final List<FakePlayer.Attachment<?>> attachments = new ArrayList<>();

	public static Collection<FakePlayer.Attachment<?>> getAll() {
		return attachments;
	}

	public static void register(FakePlayer.Attachment<?> attachment) {
		attachments.add(attachment);
	}
}
