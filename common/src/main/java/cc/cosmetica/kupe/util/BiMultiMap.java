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

import java.util.*;

/**
 * Bidirectional map that allows multiple mappings per key. Cannot contain duplicates.
 */
public class BiMultiMap<A, B> {
	private final Map<A, Collection<B>> aToB = new HashMap<>();
	private final Map<B, Collection<A>> bToA = new HashMap<>();

	/**
	 * Put the given pair mapping into this BiMultiMap.
	 * @param a the first element of the pair.
	 * @param b the second element of the pair.
	 */
	public void put(A a, B b) {
		// hashset disallows duplicates
		this.aToB.computeIfAbsent(a, $ -> new HashSet<>()).add(b);
		this.bToA.computeIfAbsent(b, $ -> new HashSet<>()).add(a);
	}

	/**
	 * Get B values associated with the given A key.
	 * @param a the A key.
	 * @return the B values associated with the given A key. Don't modify values directly from this or risk breaking the
	 * 		   symmetry!
	 */
	public Iterable<B> get(A a) {
		return this.aToB.getOrDefault(a, Collections.emptyList());
	}

	/**
	 * Get A values associated with the given B key.
	 * @param b the B key.
	 * @return the A values associated with the given B key. Don't modify values directly from this or risk breaking the
	 * 		   symmetry!
	 */
	public Iterable<A> getReverse(B b) {
		return this.bToA.getOrDefault(b, Collections.emptyList());
	}

	/**
	 * Remove all mappings connected to the given A key.
	 * @param a the key.
	 */
	public void remove(A a) {
		Collection<B> bs = this.aToB.get(a);

		if (bs != null) {
			// clear reverse mappings
			for (B b : bs) {
				Collection<A> aMappings = this.bToA.get(b);
				aMappings.remove(a);

				// free memory when empty
				if (aMappings.isEmpty()) {
					this.bToA.remove(b);
				}
			}

			// clear main mapping
			this.aToB.remove(a);
		}
	}

	public void clear() {
		this.aToB.clear();
		this.bToA.clear();
	}
}
