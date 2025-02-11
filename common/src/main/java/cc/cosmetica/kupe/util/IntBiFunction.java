package cc.cosmetica.kupe.util;

/**
 * Represents a function of the form (int, int) -> T.
 * @param <T> the return type.
 */
@FunctionalInterface
public interface IntBiFunction<T> {
    /**
     * Apply this function to the given argument.
     * @param a the first integer.
     * @param b the second integer.
     * @return the function result.
     */
    T apply(int a, int b);
}
