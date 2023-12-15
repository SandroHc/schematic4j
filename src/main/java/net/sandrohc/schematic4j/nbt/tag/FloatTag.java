/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

/**
 * A float NBT tag.
 */
public class FloatTag extends NumberTag<Float> implements Comparable<FloatTag> {

	/**
	 * The float tag discriminator.
	 */
	public static final byte ID = 5;

	/**
	 * The default value.
	 */
	public static final float ZERO_VALUE = 0.0F;

	/**
	 * A float tag with the default value.
	 */
	public FloatTag() {
		super(ZERO_VALUE);
	}

	/**
	 * A float tag.
	 * @param value The inner value
	 */
	public FloatTag(float value) {
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
	public void setValue(float value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((FloatTag) other).getValue());
	}

	@Override
	public int compareTo(FloatTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public FloatTag clone() {
		return new FloatTag(getValue());
	}
}
