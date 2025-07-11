/*
 * Copyright 2024, 2025 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.LeavesSandbox;
import cc.cosmetica.kupe.impl.fakeplayer.*;
import com.google.common.base.Preconditions;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Component to show a player, like in the inventory.
 * Elements like Cape/Elytra are added as attachments.
 */
public class GUIPlayer extends Component {
	/**
	 * Display a 'static' GUI player with the given skin texture. No attachments by default.
	 * @param skin the resource location of the skin texture.
	 * @param name the username text of the player.
	 * @param slim whether to use the slim player model.
	 * @param followsMouse whether the player should follow the mouse.
	 */
	public GUIPlayer(@NotNull ResourceKey skin, Text name, boolean slim, boolean followsMouse) {
		Preconditions.checkNotNull(skin, "Cannot provide a null skin.");

		this.followsMouse = followsMouse;
		this.uuid = null;
		this.renderer.skin = new PlayerUtils.Skin(skin.toResourceLocation(), slim);

		// nametag
		this.renderer.username = new Nametag(name, 1.0f);
		this.renderer.nametags.add(this.renderer.username);
	}

	/**
	 * Create a new, 'dynamic' {@link GUIPlayer} with the given UUID. All attachments will be dynamically configured by the
	 * provided UUID, and set to their default enable state.
	 * @param uuid the uuid of the player to render.
	 * @param followsMouse whether the player should follow the mouse.
	 */
	public GUIPlayer(@NotNull UUID uuid, boolean followsMouse) {
		Preconditions.checkNotNull(uuid, "Cannot provide a null UUID.");

		this.followsMouse = followsMouse;
		this.uuid = uuid;

		this.renderer.skin = new PlayerUtils.Skin(
				DefaultPlayerSkin.getDefaultSkin(uuid),
				"slim".equals(DefaultPlayerSkin.getSkinModelName(uuid))
		);
		// model parts shown
		if (Minecraft.getInstance().level != null) {
			Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
			if (player != null) {
				this.renderer.shownParts = this.renderer.shownParts.stream()
						.filter(player::isModelPartShown)
						.collect(Collectors.toSet());
			}
		}

		// nametag
		Text username = PlayerUtils.getUsername(uuid);
		this.renderer.username = new Nametag(username, 1.0f);
		this.renderer.nametags.add(this.renderer.username);
		this.loadedName = !username.getString().equals("Player");

		this.showAttachments(AttachmentsRegistry.getAll().stream().filter(Attachment::defaultEnable).toArray(Attachment[]::new));
	}

	/**
	 * Modify the posture of the player.
	 */
	public final Posture pose = new Posture();

	private final boolean followsMouse;
	private final FakePlayerRenderer renderer = new FakePlayerRenderer();
	private final @Nullable UUID uuid;
	private final Map<Attachment<?>, Object> configurations = new HashMap<>();
	private final Set<Attachment<?>> shown = new HashSet<>();
	private boolean loadedName = false;

	// ============= //
	//  API Methods  //
	// ============= //

	/**
	 * Configure the given attachment on this FakePlayer.
	 * @param attachment the attachment to configure on this FakePlayer.
	 * @param configuration the configuration to provide for this attachment. If null, will remove the configuration.
	 * @return this GUI player.
	 */
	public <T> GUIPlayer configureOverride(Attachment<T> attachment, @Nullable T configuration) {
		if (configuration == null) {
			this.configurations.remove(attachment);
		} else {
			this.configurations.put(attachment, configuration);
		}

		return this;
	}

	/**
	 * Add additional nametags to the fake player.
	 * @param nametagText the nametag text.
	 * @param scale the scale multiplier.
	 * @return this GUI player.
	 */
	public GUIPlayer addNametag(Text nametagText, float scale) {
		this.renderer.nametags.add(new Nametag(nametagText, scale));
		return this;
		// TODO Check in Cosmetica: does prefix/suffix automatically show?
	}

	/**
	 * Sets whether the player's nametags are shown. Hidden by default.
	 * @param show whether to show the player's nametags.
	 * @return this GUI player.
	 */
	public GUIPlayer showNametag(boolean show) {
		this.renderer.showNametag = show;
		return this;
	}

	/**
	 * Change the username nametag.
	 * @param text the text of the nametag.
	 * @param scale the scale multiplier.
	 * @return this GUI player.
	 */
	public GUIPlayer setUsername(Text text, float scale) {
		this.loadedName = true;
		this.renderer.username = new Nametag(text, scale);
		this.renderer.nametags.remove(0);
		this.renderer.nametags.add(0, this.renderer.username);
		return this;
	}

	public Iterator<Attachment<?>> getRenderingAttachments() {
		return this.shown.iterator();
	}

	@Nullable
	public <T> T getConfiguration(Attachment<T> attachment) {
		T config = (T) this.configurations.get(attachment);
		if (config == null) {
			if (this.uuid == null) {
				return null;
			}
			return attachment.getDynamicConfiguration(this.uuid);
		}
		return config;
	}

	/**
	 * Show the given attachments on this FakePlayer.
	 * @param attachments a list of attachments to show on this fake player. Leave blank to show all attachments.
	 */
	public GUIPlayer showAttachments(Attachment<?>... attachments) {
		if (attachments.length == 0) {
			this.shown.addAll(AttachmentsRegistry.getAll());
		} else {
			Collections.addAll(this.shown, attachments);
		}

		return this;
	}

	/**
	 * Hide the given attachments on this FakePlayer.
	 * @param attachments a list of attachments to hide on this fake player. Leave blank to hide all attachments.
	 */
	public GUIPlayer hideAttachments(Attachment<?>... attachments) {
		if (attachments.length == 0) {
			this.shown.clear();
		} else for (Attachment<?> attachment : attachments) {
			this.shown.remove(attachment);
		}

		return this;
	}

	// ======== //
	// Internal //
	// ======== //

	@Override
	public List<Component> build() {
		return Collections.emptyList();
	}

	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		return this.tryDimensionsWithPreferredRatio(DEFAULT_DIMENSIONS, padding, context);
	}

	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		int centreX = (region.getX() + region.getFinalX())/2;

		int lookX, lookY;

		if (this.followsMouse) {
			lookX = centreX - mouseX;
			// approximate eye height as 4/5 up the default bounding box that encapsulates the player
			// actual rendering size is based off of width not height of the region
			int defaultTop = region.getFinalY() - (region.getWidth() * 90/55);
			lookY = ((defaultTop * 4 + region.getFinalY()) / 5) - mouseY;
		} else {
			lookX = 0;
			lookY = 0;
		}

		int footY = region.getFinalY() - 3; // players feet dip a bit lower when rotating to look up/down
		this.renderer.skin = PlayerUtils.getSkin(uuid, this.renderer.skin);
		if (!this.loadedName && this.uuid != null) {
			this.renderer.nametags.get(0).text = PlayerUtils.getUsername(uuid);
		}
		this.renderer.render(this, canvas.getDrawingContext(), centreX, footY, region.getWidth() / 2.5f, lookX, lookY);
	}

	public static Attachment<ResourceLocation> CAPE = registerAttachment(new CapeAttachment());
	public static Attachment<ElytraProperties> ELYTRA = registerAttachment(new ElytraAttachment());

	public static <T> Attachment<T> registerAttachment(Attachment<T> attachment) {
		AttachmentsRegistry.register(attachment);
		return attachment;
	}

	private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(55, 90);

	/**
	 * An attachment for the FakePlayer.
	 * @param <T> the configuration object for this attachment.
	 */
	public interface Attachment<T> {
		/**
		 * Render this attachment on the fake player.
		 * Leaves sandbox due to MultiBufferSource and Quaternion parameter.
		 * @param playerModel the player model on which to render.
		 * @param posture the posture of the player being rendered.
		 * @param canvas the canvas environment for rendering.
		 * @param configuration the configuration.
		 * @param packedLight packed light for rendering.
		 */
		@LeavesSandbox
		void render(PlayerModel playerModel, GUIPlayer.Posture posture, Canvas canvas, T configuration, Quaternion cameraOrientation, MultiBufferSource bufferSource, int packedLight);

		/**
		 * Get the dynamic user configuration. This is called every tick for an enabled component of a UUID FakePlayer.
		 * @return the configuration for the given user.
		 */
		T getDynamicConfiguration(UUID uuid);

		/**
		 * Whether this attachment should be shown by default in UUID-configured fake players.
		 * @return whether to enable this by default.
		 */
		default boolean defaultEnable() {
			return true;
		}
	}

	/**
	 * Contains variables which alter the posture of the player.
	 */
	public static class Posture {
		public boolean sneaking = false;
		public boolean isLeftHanded = false;
		public boolean leftArmRaised = false;
		public boolean rightArmRaised = false;
		public boolean upsideDown = false;
		public float yRotBody = 0;
		public float yRotHead = 0;
		public float xRot = 0;
	}

	public static class Nametag {
		public Nametag(Text text, float scale) {
			this.text = text;
			this.scale = scale;
		}

		public Text text;
		public final float scale;
	}

	/**
	 * Contains Elytra properties.
	 */
	public final static class ElytraProperties {
		public ElytraProperties(ResourceLocation texture, boolean glint, boolean translucent) {
			this.texture = texture;
			this.glint = glint;
			this.translucent = translucent;
		}

		public final ResourceLocation texture;
		public final boolean glint, translucent;

		public static final ElytraProperties DEFAULT = new ElytraProperties(new ResourceLocation("textures/entity/elytra.png"), false, false);
	}
}
