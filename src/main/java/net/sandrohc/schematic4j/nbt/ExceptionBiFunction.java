/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt;

@FunctionalInterface
public interface ExceptionBiFunction <T, U, R, E extends Exception> {

	R accept(T t, U u) throws E;
}
