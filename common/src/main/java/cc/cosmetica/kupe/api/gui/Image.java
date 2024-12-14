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
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.ResizableElement;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Component to display an image.
 */
public class Image extends Component {
	/**
	 * Load the given image from the provided location.
	 * @param texture the resource location for the texture.
	 */
	public Image(ResourceLocation texture) {
		this(texture, RESOURCE_IMAGE_METADATA);
	}

	/**
	 * Load the given image from the provided location.
	 * @param texture the resource location for the texture.
	 * @param dimensionFetcher fetcher for the image dimensions.
	 */
	public Image(ResourceLocation texture, ImageDimensionLoader dimensionFetcher) {
		this.texture = texture;
		this.dimensionFetcher = dimensionFetcher;
	}

	private final ResourceLocation texture;
	private final ImageDimensionLoader dimensionFetcher;

	// TODO dont do io on the rendering thread
	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		try {
			Optional<Dimensions> dimensionData = context.getImageDimensions(this.texture);

			if (!dimensionData.isPresent()) {
				return Dimensions.NONE;
			}

			Dimensions dimensions = dimensionData.get();
			return this.tryDimensionsWithPreferredRatio(dimensions, padding, context);
		} catch (IOException e) {
			// could not load image
			return Dimensions.NONE;
		}
	}

	@Override
	public List<Component> build() {
		return ImmutableList.of();
	}

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		// nothing
	}

	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		canvas.drawTexture(region.getX(), region.getY(), region.getWidth(), region.getHeight(), 0.0f, this.texture);
	}

	/**
	 * Automatically determine the aspect ratio.
	 */
	private static final ImageDimensionLoader RESOURCE_IMAGE_METADATA = Context::getImageDimensions;

	@FunctionalInterface
	public interface ImageDimensionLoader {
		/**
		 * Get the dimensions of the given image.
		 * @param context the context in which we are getting the image.
		 * @param image the location of the image of which we are getting the dimensions.
		 * @return the dimensions of the image.
		 * @throws IOException if an I/O exception occurs while fetching dimensions.
		 */
		Optional<Dimensions> getDimensions(Context context, ResourceLocation image) throws IOException;
	}
}
