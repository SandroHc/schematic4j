/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt;

/**
 * A bi-function that may throw an exception.
 *
 * @param <T> First value type
 * @param <U> Second value type
 * @param <R> Return type
 * @param <E> Exception type
 */
@FunctionalInterface
public interface ExceptionBiFunction<T, U, R, E extends Exception> {

	/**
	 * @param t The first value
	 * @param u The second value
	 * @return The return value
	 * @throws E The exception
	 */
	R accept(T t, U u) throws E;
}
