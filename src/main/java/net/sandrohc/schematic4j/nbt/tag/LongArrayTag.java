/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.util.Arrays;

/**
 * A long array NBT tag.
 */
public class LongArrayTag extends ArrayTag<long[]> implements Comparable<LongArrayTag> {

	/**
	 * The long array tag discriminator.
	 */
	public static final byte ID = 12;

	/**
	 * The default value.
	 */
	public static final long[] ZERO_VALUE = new long[0];

	/**
	 * An empty long array tag.
	 */
	public LongArrayTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A long array tag.
	 * @param value The inner value
	 */
	public LongArrayTag(long[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((LongArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(LongArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public LongArrayTag clone() {
		return new LongArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
