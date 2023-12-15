/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * A double NBT tag.
 */
public class DoubleTag extends NumberTag<Double> implements Comparable<DoubleTag> {

	/**
	 * The double tag discriminator.
	 */
	public static final byte ID = 6;

	/**
	 * The default value.
	 */
	public static final double ZERO_VALUE = 0.0D;

	/**
	 * A double tag with the default value.
	 */
	public DoubleTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A double tag.
	 *
	 * @param value The inner value
	 */
	public DoubleTag(double value) {
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
	public void setValue(double value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((DoubleTag) other).getValue());
	}

	@Override
	public int compareTo(DoubleTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public DoubleTag clone() {
		return new DoubleTag(getValue());
	}
}
