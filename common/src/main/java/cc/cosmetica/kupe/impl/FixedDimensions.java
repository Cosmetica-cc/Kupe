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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.style.CommonProperties;

/**
 * Fixed value of a dimension operator. Makes debug easier by allowing debug print of such 'functions'.
 */
public final class FixedDimensions<T> implements CommonProperties.DimensionsOperator<T> {
	public FixedDimensions(T t) {
		this.t = t;
	}

	private final T t;

	@Override
	public T apply(int vw, int vh) {
		return this.t;
	}

	@Override
	public String toString() {
		return String.valueOf(this.t);
	}
}
