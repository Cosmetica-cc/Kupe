package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.impl.fakeplayer.AttachmentsImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Component to show a player, like in the inventory.
 * Elements like Cape/Elytra are added as attachments.
 */
public class FakePlayer extends Component {
	/**
	 * Display a fake player with the given skin texture. No attachments by default.
	 * @param skin the resource location of the skin texture.
	 */
	public FakePlayer(ResourceLocation skin) {
		this.uuid = null;
	}

	/**
	 * Create a new FakePlayer with the given UUID. All attachments will be enabled by default and configured by the
	 * provided UUID.
	 * @param uuid the uuid of the player to render.
	 */
	public FakePlayer(@NotNull UUID uuid) {
		this.uuid = uuid;

		for (Attachment<?> attachment : AttachmentsImpl.getAttachments()) {
			attachment.inferDefaultConfiguration(uuid);
		}
	}

	private final @Nullable UUID uuid;

	/**
	 * Configure the given attachment on this FakePlayer.
	 * @param attachment the attachment to configure on this FakePlayer.
	 * @param configuration the configuration to provide for this attachment.
	 */
	public <T> void configureAttachment(Attachment<T> attachment, T configuration) {

	}

	/**
	 * Show the given attachments on this FakePlayer.
	 * @param attachments a list of attachments to show on this fake player. Leave blank to show all attachments.
	 */
	public void showAttachments(Attachment<?>... attachments) {
	}

	/**
	 * Hide the given attachments on this FakePlayer.
	 * @param attachments a list of attachments to hide on this fake player. Leave blank to hide all attachments.
	 */
	public void hideAttachments(Attachment<?>... attachments) {
	}

	@Override
	public List<Component> build() {
		return Collections.emptyList();
	}

	/**
	 * An attachment for the FakePlayer.
	 * @param <T> the configuration object for this attachment.
	 */
	public interface Attachment<T> {
		// CapeAttachment -- requires ResourceLocation or can look up via UUID
		// ElytraAttachment -- "
		// NameTagAttachment -- String or look up via UUID
		T inferDefaultConfiguration(@Nullable UUID uuid);
	}
}
