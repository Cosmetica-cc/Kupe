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
import cc.cosmetica.kupe.api.PolyBuilder;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Component to display an image.
 */
public class Image extends Component {
	/**
	 * Load the given image from the provided location.
	 * @param texture the resource location for the texture.
	 */
	public Image(ResourceKey texture) {
		this(texture, RESOURCE_IMAGE_METADATA);
	}

	/**
	 * Load the given image from the provided location.
	 * @param texture the resource location for the texture.
	 * @param dimensionFetcher fetcher for the image dimensions.
	 */
	public Image(ResourceKey texture, ImageDimensionLoader dimensionFetcher) {
		this.texture = texture;
		this.dimensionFetcher = dimensionFetcher;
	}

	/**
	 * Enable transparency on the image and set the opacity.
	 * @param opacity a value between 0.0f and 1.0f which gives the opacity of the image.
	 * @return this object.
	 */
	public Image setTransparent(float opacity) {
		this.opacity = opacity;
		return this;
	}

	/**
	 * Crop the image by the given percentages in each direction.
	 * @param top the amount to crop from the top (%).
	 * @param right the amount to crop from the right (%).
	 * @param bottom the amount to crop from the bottom (%).
	 * @param left the amount to crop from the left (%).
	 * @return this image.
	 */
	public Image crop(float top, float right, float bottom, float left) {
		this.uv[0] = left;
		this.uv[1] = top;
		this.uv[2] = 1.0f - right;
		this.uv[3] = 1.0f - bottom;
		return this;
	}

	private float opacity = -1.0f;
	private final ResourceKey texture;
	private final ImageDimensionLoader dimensionFetcher;
	private final float[] uv = {0.0f, 0.0f, 1.0f, 1.0f};

	// TODO dont do io on the rendering thread
	// could make a way to prefetch data and have it available
	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		try {
			Optional<Dimensions> dimensionData = this.dimensionFetcher.getDimensions(context, this.texture);

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
	public int shrinkHeight(int newWidth, int height, Context context) {
		try {
			Optional<Dimensions> dimensionData = this.dimensionFetcher.getDimensions(context, this.texture);
			if (!dimensionData.isPresent()) return height;

			float aspectRatio = (float) dimensionData.get().getHeight() / dimensionData.get().getWidth();
			return (int) (aspectRatio * newWidth);
		} catch (IOException e) {
			return height;
		}
	}

	@Override
	public int shrinkWidth(int newHeight, int width, Context context) {
		try {
			Optional<Dimensions> dimensionData = this.dimensionFetcher.getDimensions(context, this.texture);
			if (!dimensionData.isPresent()) return width;

			float aspectRatio = (float) dimensionData.get().getWidth() / dimensionData.get().getHeight();
			return (int) (aspectRatio * newHeight);
		} catch (IOException e) {
			return width;
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
		if (this.opacity == -1.0f) {
			this.drawTexture(canvas, region.getX(), region.getY(), region.getWidth(), region.getHeight(), this.texture);
		} else {
			canvas.setTransparency(this.opacity);
			this.drawTexture(canvas, region.getX(), region.getY(), region.getWidth(), region.getHeight(), this.texture);
			canvas.disableTransparency();
		}
	}

	private void drawTexture(Canvas canvas, int x0, int y0, int width, int height, ResourceKey texture) {
		canvas.setTexture(texture);
		PolyBuilder bufferBuilder = canvas.drawQuads(PolyBuilder.Mode.POSITION_TEXTURE);

		// x1, y1 exclusive because for some reason minecraft works this way
		int x1 = x0 + width;
		int y1 = y0 + height;

		bufferBuilder.vertex((float)x0, (float)y1, 0.0f).uv(this.uv[0], this.uv[3]).endVertex();
		bufferBuilder.vertex((float)x1, (float)y1, 0.0f).uv(this.uv[2], this.uv[3]).endVertex();
		bufferBuilder.vertex((float)x1, (float)y0, 0.0f).uv(this.uv[2], this.uv[1]).endVertex();
		bufferBuilder.vertex((float)x0, (float)y0, 0.0f).uv(this.uv[0], this.uv[1]).endVertex();

		bufferBuilder.build();
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
		Optional<Dimensions> getDimensions(Context context, ResourceKey image) throws IOException;
	}

	static {
		RootStylesheet.setDefaultOverrides(Image.class, Style.create().set(CommonProperties.FLEX_SHRINK, 0));
	}
}
