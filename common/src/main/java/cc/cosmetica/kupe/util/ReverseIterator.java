package cc.cosmetica.kupe.util;

import java.util.Iterator;
import java.util.List;

/**
 * Backwards iterator for lists.
 * @param <T> the type parameter of the list.
 */
public class ReverseIterator<T> implements Iterator<T> {
	public ReverseIterator(List<T> list) {
		this.list = list;
		this.index = list.size() - 1;
	}

	private final List<T> list;
	private int index;

	@Override
	public boolean hasNext() {
		return this.index >= 0;
	}

	@Override
	public T next() {
		return this.list.get(this.index--);
	}
}
