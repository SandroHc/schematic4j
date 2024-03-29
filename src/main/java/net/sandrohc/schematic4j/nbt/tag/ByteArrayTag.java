/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.util.Arrays;

/**
 * A byte array NBT tag.
 */
public class ByteArrayTag extends ArrayTag<byte[]> implements Comparable<ByteArrayTag> {

	/**
	 * The byte array tag discriminator.
	 */
	public static final byte ID = 7;

	/**
	 * The default value.
	 */
	public static final byte[] ZERO_VALUE = new byte[0];

	/**
	 * An empty byte array tag.
	 */
	public ByteArrayTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A byte array tag.
	 * @param value The inner array
	 */
	public ByteArrayTag(byte[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((ByteArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(ByteArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public ByteArrayTag clone() {
		return new ByteArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
