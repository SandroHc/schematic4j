/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt;

/**
 * A tri-consumer that may throw an exception.
 *
 * @param <T> First value type
 * @param <U> Second value type
 * @param <V> Third value type
 * @param <E> Exception type
 */
@FunctionalInterface
public interface ExceptionTriConsumer<T, U, V, E extends Exception> {

	/**
	 * @param t The first value
	 * @param u The second value
	 * @param v The third value
	 * @throws E The exception
	 */
	void accept(T t, U u, V v) throws E;
}
