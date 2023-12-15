/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * A long NBT tag.
 */
public class LongTag extends NumberTag<Long> implements Comparable<LongTag> {

	/**
	 * The long tag discriminator.
	 */
	public static final byte ID = 4;

	/**
	 * The default value.
	 */
	public static final long ZERO_VALUE = 0L;

	/**
	 * A long tag with the default value.
	 */
	public LongTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A long tag.
	 * @param value The inner value
	 */
	public LongTag(long value) {
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
	public void setValue(long value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asLong() == ((LongTag) other).asLong();
	}

	@Override
	public int compareTo(LongTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public LongTag clone() {
		return new LongTag(getValue());
	}
}
