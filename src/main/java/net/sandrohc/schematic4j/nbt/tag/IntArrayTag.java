/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.util.Arrays;

/**
 * An int array NBT tag.
 */
public class IntArrayTag extends ArrayTag<int[]> implements Comparable<IntArrayTag> {

	/**
	 * The int array tag discriminator.
	 */
	public static final byte ID = 11;

	/**
	 * The default value.
	 */
	public static final int[] ZERO_VALUE = new int[0];

	/**
	 * An empty int array tag.
	 */
	public IntArrayTag() {
		super(ZERO_VALUE);
	}

	/**
	 * An int array tag.
	 * @param value The inner value
	 */
	public IntArrayTag(int[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((IntArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(IntArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public IntArrayTag clone() {
		return new IntArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
