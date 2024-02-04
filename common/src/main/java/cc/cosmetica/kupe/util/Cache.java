package cc.cosmetica.kupe.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Cache<T> {
	public Cache() {
		this.value = null;
	}

	private @Nullable T value;

	@Nullable
	public T get(Supplier<T> otherwise) {
		if (this.value != null) {
			return this.value;
		} else {
			return this.value = otherwise.get();
		}
	}
}
