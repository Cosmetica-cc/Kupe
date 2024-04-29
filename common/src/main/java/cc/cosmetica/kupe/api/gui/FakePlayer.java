package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import com.google.common.base.Preconditions;
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
	public FakePlayer(@NotNull ResourceLocation skin) {
		Preconditions.checkNotNull(skin, "Cannot provide a null skin.");

		this.uuid = null;
		this.skin = skin;
	}

	/**
	 * Create a new FakePlayer with the given UUID. All attachments will be enabled by default and configured by the
	 * provided UUID.
	 * @param uuid the uuid of the player to render.
	 */
	public FakePlayer(@NotNull UUID uuid) {
		Preconditions.checkNotNull(uuid, "Cannot provide a null UUID.");

		this.uuid = uuid;
		this.skin = null;
	}

	private final @Nullable UUID uuid;
	private final @Nullable ResourceLocation skin;

	/**
	 * Configure the given attachment on this FakePlayer.
	 * @param attachment the attachment to configure on this FakePlayer.
	 * @param configuration the configuration to provide for this attachment.
	 * @return this fake player.
	 */
	public <T> FakePlayer configureAttachment(Attachment<T> attachment, T configuration) {
		return this;
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

		/**
		 * Render this attachment on the fake player.
		 * @param canvas the canvas environment for rendering.
		 * @param configuration the configuration.
		 */
		void render(Canvas canvas, T configuration);

		/**
		 * Get the user configuration. This is called every frame, so can change dynamically.
		 * @return the configuration for the given user.
		 */
		T getUserConfiguration(UUID uuid);
	}
}
