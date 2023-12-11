/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

public class StringTag extends Tag<String> implements Comparable<StringTag> {

	public static final byte ID = 8;
	public static final String ZERO_VALUE = "";

	public StringTag() {
		super(ZERO_VALUE);
	}

	public StringTag(String value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}

	@Override
	public String valueToString(int maxDepth) {
		return escapeString(getValue(), false);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((StringTag) other).getValue());
	}

	@Override
	public int compareTo(StringTag o) {
		return getValue().compareTo(o.getValue());
	}

	@Override
	public StringTag clone() {
		return new StringTag(getValue());
	}
}
