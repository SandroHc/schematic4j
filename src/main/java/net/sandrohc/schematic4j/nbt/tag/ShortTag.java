/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * A short NBT tag.
 */
public class ShortTag extends NumberTag<Short> implements Comparable<ShortTag> {

	/**
	 * The short tag discriminator.
	 */
	public static final byte ID = 2;

	/**
	 * The default value.
	 */
	public static final short ZERO_VALUE = 0;

	/**
	 * A short tag with the default value.
	 */
	public ShortTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A short tag.
	 * @param value The inner value
	 */
	public ShortTag(short value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	/**
	 * Set a new value.
	 *
	 * @param value The new value
	 */
	public void setValue(short value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asShort() == ((ShortTag) other).asShort();
	}

	@Override
	public int compareTo(ShortTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public ShortTag clone() {
		return new ShortTag(getValue());
	}
}
