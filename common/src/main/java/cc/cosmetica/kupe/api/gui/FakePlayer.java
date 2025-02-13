/*
 * Copyright 2024 Cosmetica
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Component to show a player, like in the inventory.
 * Elements like Cape/Elytra are added as attachments.
 */
public class FakePlayer extends Component {
	/**
	 * Display a 'static' fake player with the given skin texture. No attachments by default.
	 * @param skin the resource location of the skin texture.
	 */
	public FakePlayer(@NotNull ResourceKey skin, boolean slim, boolean followsMouse) {
		Preconditions.checkNotNull(skin, "Cannot provide a null skin.");

		this.followsMouse = followsMouse;
		this.uuid = null;
		this.renderer.skin = skin.toResourceLocation();
		this.renderer.slim = slim;
	}

	/**
	 * Create a new, 'dynamic' FakePlayer with the given UUID. All attachments will be dynamically configured by the
	 * provided UUID, and set to their default enable state.
	 * @param uuid the uuid of the player to render.
	 */
	public FakePlayer(@NotNull UUID uuid, boolean followsMouse) {
		Preconditions.checkNotNull(uuid, "Cannot provide a null UUID.");

		this.followsMouse = followsMouse;
		this.uuid = uuid;
		this.renderer.skin = DefaultPlayerSkin.getDefaultSkin(uuid); // todo get actual skin
		this.renderer.slim = "slim".equals(DefaultPlayerSkin.getSkinModelName(uuid));

		this.showAttachments(AttachmentsRegistry.getAll().stream().filter(Attachment::defaultEnable).toArray(Attachment[]::new));
	}

	private final boolean followsMouse;
	private final FakePlayerRenderer renderer = new FakePlayerRenderer();
	private final @Nullable UUID uuid;
	private final Map<Attachment<?>, Object> configurations = new HashMap<>();
	private final Set<Attachment<?>> shown = new HashSet<>();

	// ============= //
	//  API Methods  //
	// ============= //

	/**
	 * Configure the given attachment on this FakePlayer.
	 * @param attachment the attachment to configure on this FakePlayer.
	 * @param configuration the configuration to provide for this attachment. If null, will remove the configuration.
	 * @return this fake player.
	 */
	public <T> FakePlayer configureAttachment(Attachment<T> attachment, @Nullable T configuration) {
		if (configuration == null) {
			this.configurations.remove(attachment);
		} else {
			this.configurations.put(attachment, configuration);
		}

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
	public FakePlayer showAttachments(Attachment<?>... attachments) {
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
	public FakePlayer hideAttachments(Attachment<?>... attachments) {
		if (attachments.length == 0) {
			this.shown.clear();
		} else for (Attachment<?> attachment : attachments) {
			this.shown.remove(attachment);
		}

		return this;
	}

	public FakePlayer setSneaking(boolean sneaking) {
		this.renderer.sneaking = sneaking;
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
		this.renderer.render(this, canvas.getDrawingContext(), centreX, footY, region.getWidth() / 2.5f, lookX, lookY);
	}

	public static Attachment<ResourceLocation> CAPE = registerAttachment(new CapeAttachment());
	public static Attachment<ResourceLocation> ELYTRA = registerAttachment(new ElytraAttachment());
	public static Attachment<Text> NAMETAG = registerAttachment(new NameTagAttachment());

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
		 * @param canvas the canvas environment for rendering.
		 * @param configuration the configuration.
		 * @param packedLight packed light for rendering.
		 */
		@LeavesSandbox
		// TODO refactor so it doesn't leave sandbox
		void render(Canvas canvas, T configuration, Quaternion cameraOrientation, MultiBufferSource bufferSource, int packedLight);

		/**
		 * Get the dynamic user configuration. This is called every tick.
		 * @return the configuration for the given user.
		 */
		T getDynamicConfiguration(UUID uuid);

		/**
		 * Whether this is a name tag attachment.
		 * @return whether this attachment should render with nametag. Otherwise renders with model.
		 */
		default boolean isNameTag() {
			return false;
		}

		/**
		 * Whether this attachment should be shown by default in UUID-configured fake players.
		 * @return whether to enable this by default.
		 */
		default boolean defaultEnable() {
			return true;
		}
	}
}
