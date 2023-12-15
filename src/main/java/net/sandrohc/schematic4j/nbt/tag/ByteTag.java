/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * A byte NBT tag.
 */
public class ByteTag extends NumberTag<Byte> implements Comparable<ByteTag> {

	/**
	 * The byte tag discriminator.
	 */
	public static final byte ID = 1;

	/**
	 * The default value.
	 */
	public static final byte ZERO_VALUE = 0;

	/**
	 * A byte tag with the default value.
	 */
	public ByteTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A byte tag.
	 * @param value The inner value
	 */
	public ByteTag(byte value) {
		super(value);
	}

	/**
	 * A byte tag.
	 * @param value The inner value
	 */
	public ByteTag(boolean value) {
		super((byte) (value ? 1 : 0));
	}

	@Override
	public byte getID() {
		return ID;
	}

	/**
	 * Convert this byte into a boolean value. Values greater than zero map to true.
	 *
	 * @return {@code true} if greater than 0, {@code false} otherwise
	 */
	public boolean asBoolean() {
		return getValue() > 0;
	}

	/**
	 * Sets the inner byte value.
	 *
	 * @param value The new value
	 */
	public void setValue(byte value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asByte() == ((ByteTag) other).asByte();
	}

	@Override
	public int compareTo(ByteTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public ByteTag clone() {
		return new ByteTag(getValue());
	}
}
