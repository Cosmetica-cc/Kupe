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
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.LeavesSandbox;
import cc.cosmetica.kupe.impl.fakeplayer.AttachmentsRegistry;
import cc.cosmetica.kupe.impl.fakeplayer.CapeAttachment;
import cc.cosmetica.kupe.impl.fakeplayer.ElytraAttachment;
import cc.cosmetica.kupe.impl.fakeplayer.NameTagAttachment;
import com.google.common.base.Preconditions;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
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
	public FakePlayer(@NotNull ResourceLocation skin) {
		Preconditions.checkNotNull(skin, "Cannot provide a null skin.");

		this.uuid = null;
		this.skin = skin;
	}

	/**
	 * Create a new, 'dynamic' FakePlayer with the given UUID. All attachments except ELYTRA will be enabled by default
	 * and dynamically configured by the provided UUID.
	 * @param uuid the uuid of the player to render.
	 */
	public FakePlayer(@NotNull UUID uuid) {
		Preconditions.checkNotNull(uuid, "Cannot provide a null UUID.");

		this.uuid = uuid;
		this.skin = null;

		this.showAttachments();
		this.hideAttachments(ELYTRA);
	}

	private final @Nullable UUID uuid;
	private final @Nullable ResourceLocation skin;
	private final Map<Attachment<?>, Object> configurations = new HashMap<>();
	private final Set<Attachment<?>> shown = new HashSet<>();

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

	public Iterator<Map.Entry<Attachment<?>, Object>> getRenderingAttachments() {
		return this.configurations.entrySet().stream().filter(e -> this.shown.contains(e.getKey())).iterator();
	}

	/**
	 * Show the given attachments on this FakePlayer.
	 * @param attachments a list of attachments to show on this fake player. Leave blank to show all attachments.
	 */
	public void showAttachments(Attachment<?>... attachments) {
		if (attachments.length == 0) {
			this.shown.addAll(AttachmentsRegistry.getAll());
		} else {
			Collections.addAll(this.shown, attachments);
		}
	}

	/**
	 * Hide the given attachments on this FakePlayer.
	 * @param attachments a list of attachments to hide on this fake player. Leave blank to hide all attachments.
	 */
	public void hideAttachments(Attachment<?>... attachments) {
		if (attachments.length == 0) {
			this.shown.clear();
		} else for (Attachment<?> attachment : attachments) {
			this.shown.remove(attachment);
		}
	}

	@Override
	public List<Component> build() {
		return Collections.emptyList();
	}

	@Override
	public void render(Canvas canvas, Region region, int mouseX, int mouseY) {
	}

	public static Attachment<ResourceLocation> CAPE = registerAttachment(new CapeAttachment());
	public static Attachment<ResourceLocation> ELYTRA = registerAttachment(new ElytraAttachment());
	public static Attachment<Text> NAMETAG = registerAttachment(new NameTagAttachment());

	public static <T> Attachment<T> registerAttachment(Attachment<T> attachment) {
		AttachmentsRegistry.register(attachment);
		return attachment;
	}

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
		 * Get the user configuration. This is called every frame, so can change dynamically.
		 * @return the configuration for the given user.
		 */
		T getUserConfiguration(UUID uuid);

		/**
		 * Whether this is a name tag attachment.
		 * @return whether this attachment should render with nametag. Otherwise renders with model.
		 */
		default boolean isNameTag() {
			return false;
		}
	}
}
