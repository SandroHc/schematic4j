/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

public class ByteTag extends NumberTag<Byte> implements Comparable<ByteTag> {

	public static final byte ID = 1;
	public static final byte ZERO_VALUE = 0;

	/**
	 * An empty byte tag.
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

	public boolean asBoolean() {
		return getValue() > 0;
	}

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
