package cc.cosmetica.kupe.util;

import java.util.*;

/**
 * Bidirectional map that allows multiple mappings per key.
 */
public class BiMultiMap<A, B> {
	private final Map<A, List<B>> aToB = new HashMap<>();
	private final Map<B, List<A>> bToA = new HashMap<>();

	/**
	 * Put the given pair mapping into this BiMultiMap.
	 * @param a the first element of the pair.
	 * @param b the second element of the pair.
	 */
	public void put(A a, B b) {
		this.aToB.computeIfAbsent(a, $ -> new ArrayList<>()).add(b);
		this.bToA.computeIfAbsent(b, $ -> new ArrayList<>()).add(a);
	}// TODO create and acquire or fetch

	/**
	 * Get B values associated with the given A key.
	 * @param a the A key.
	 * @return the B values associated with the given A key. Don't modify values directly from this or risk breaking the
	 * 		   symmetry!
	 */
	public List<B> get(A a) {
		return this.aToB.getOrDefault(a, Collections.emptyList());
	}

	/**
	 * Get A values associated with the given B key.
	 * @param b the B key.
	 * @return the A values associated with the given B key. Don't modify values directly from this or risk breaking the
	 * 		   symmetry!
	 */
	public List<A> getReverse(B b) {
		return this.bToA.getOrDefault(b, Collections.emptyList());
	}

	/**
	 * Remove all mappings connected to the given A key.
	 * @param a the key.
	 */
	public void remove(A a) {
		Collection<B> bs = this.aToB.get(a);

		// clear reverse mappings
		for (B b : bs) {
			this.bToA.get(b).remove(a);
		}

		// clear main mapping
		this.aToB.remove(a);
	}
}
