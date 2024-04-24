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

package cc.cosmetica.kupe.util;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/**
 * Simple cache by key.
 */
public class MultiCache<T> {
	/**
	 * Create a simple cache, with the given backing array and cache items for the given duration.
	 * @param array the given backing array. Length should be a power of 2.
	 * @param duration the duration in milliseconds to cache values for. Expired items will still be held in memory until
	 *                 overwritten or the cache is no longer in use.
	 */
	public MultiCache(T[] array, long duration) {
		this.items = array;
		this.duration = duration;

		this.keys = new Metadata[array.length];
	}

	private final T[] items;
	private final Metadata[] keys;
	private final long duration;

	public T compute(ResourceLocation location, ResourceAcquisition<T> generator) throws IOException {
		final long access = System.currentTimeMillis();

		int hash = location.getNamespace().hashCode();
		hash ^= location.getPath().hashCode() + (hash >> 2);
		// limit hash to actual array indices
		hash &= (this.items.length - 1);

		Metadata metadata = keys[hash];

		if (metadata == null // no value
				|| (access - metadata.expiry > 0) // expiry
				|| (!location.equals(metadata.location)) // different key
		) {
			return items[hash] = generator.apply(location);
		}
		else {
			return items[hash];
		}
	}

	private static class Metadata {
		Metadata(ResourceLocation location, long expiry) {
			this.location = location;
			this.expiry = expiry;
		}

		final ResourceLocation location;
		final long expiry;
	}

	@FunctionalInterface
	public interface ResourceAcquisition<T> {
		T apply(ResourceLocation location) throws IOException;
	}
}
