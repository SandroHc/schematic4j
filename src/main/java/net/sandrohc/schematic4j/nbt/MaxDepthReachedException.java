/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt;

/**
 * Exception indicating that the maximum (de-)serialization depth has been reached.
 */
@SuppressWarnings("serial")
public class MaxDepthReachedException extends RuntimeException {

	public MaxDepthReachedException(String msg) {
		super(msg);
	}
}
