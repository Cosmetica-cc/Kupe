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

package cc.cosmetica.kupe.api;

import net.minecraft.network.chat.FormattedText;

import java.util.List;

/**
 * Provides access to information about the context in which we are drawing. Used to query information, but not draw.
 */
public interface Context {
	/**
	 * Get the width of the given text.
	 * @param text the text to compute the width of.
	 * @return the width, in pixels, of the given text.
	 */
	int getWidth(Text text);

	/**
	 * Get the line height of text.
	 * @return the line height of text.
	 */
	int getLineHeight();

	/**
	 * Get the height the given text would be if it can be at maximum the provided width.
	 * @param text the text.
	 * @param maxWidth the maximum width.
	 * @return the height, in pixels, this text would take up.
	 */
	int getTextHeight(Text text, int maxWidth);

	/**
	 * Split the given text into chunks with the given maximum width. That is, word wrap.
	 * @param text the text.
	 * @param maxWidth the maximum width.
	 * @return a list of chunks of text to render, as renderable elements. These renderable elements will have
	 * 		   {@link Renderable#width()} implemented.
	 */
	List<Renderable> split(Text text, int maxWidth);

	/**
	 * Get the window width.
	 * @return the width of the window.
	 */
	int getViewWidth();

	/**
	 * Get the window height.
	 * @return the height of the window.
	 */
	int getViewHeight();
}
