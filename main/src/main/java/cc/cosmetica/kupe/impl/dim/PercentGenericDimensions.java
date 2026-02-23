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

package cc.cosmetica.kupe.impl.dim;

import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.util.IntBiFunction;

import java.util.OptionalInt;

public class PercentGenericDimensions<T> implements CommonProperties.DimensionsOperator<T> {
	public PercentGenericDimensions(float widthPercent, float heightPercent, IntBiFunction<T> factory) {
		this.factory = factory;
		this.w = widthPercent / 100.0f;
		this.h = heightPercent / 100.0f;
	}

	private final IntBiFunction<T> factory;
	private final float w, h;

	@Override
	public T apply(int vw, int vh, int pw, int ph) {
		return this.factory.apply((int)(this.w * pw), (int)(this.h * ph));
	}

	@Override
	public String toString() {
		return String.format("%.3f%% x %.3f%%", this.w * 100.0f, this.h * 100.0f);
	}
}
