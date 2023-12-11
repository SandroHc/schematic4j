/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt;

@FunctionalInterface
public interface ExceptionTriConsumer<T, U, V, E extends Exception> {

	void accept(T t, U u, V v) throws E;
}
