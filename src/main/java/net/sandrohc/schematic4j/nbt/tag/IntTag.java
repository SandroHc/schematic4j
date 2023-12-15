/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * An int NBT tag.
 */
public class IntTag extends NumberTag<Integer> implements Comparable<IntTag> {

	/**
	 * The int tag discriminator.
	 */
	public static final byte ID = 3;

	/**
	 * The default value.
	 */
	public static final int ZERO_VALUE = 0;

	/**
	 * An int tag with the default value.
	 */
	public IntTag() {
		super(ZERO_VALUE);
	}

	/**
	 * An int tag.
	 * @param value The inner value
	 */
	public IntTag(int value) {
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
	public void setValue(int value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asInt() == ((IntTag) other).asInt();
	}

	@Override
	public int compareTo(IntTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public IntTag clone() {
		return new IntTag(getValue());
	}
}
