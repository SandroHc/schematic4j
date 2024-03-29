/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.lang.reflect.Array;

/**
 * ArrayTag is an abstract representation of any NBT array tag.
 * For implementations see {@link ByteArrayTag}, {@link IntArrayTag}, {@link LongArrayTag}.
 * @param <T> The array type.
 * */
public abstract class ArrayTag<T> extends Tag<T> {
	/**
	 * An array tag.
	 * @param value The inner array
	 */
	public ArrayTag(T value) {
		super(value);
		if (!value.getClass().isArray()) {
			throw new UnsupportedOperationException("type of array tag must be an array");
		}
	}

	/**
	 * Get ghe array length, or size.
	 * @return The array length
	 */
	public int length() {
		return Array.getLength(getValue());
	}

	@Override
	public T getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(T value) {
		super.setValue(value);
	}

	@Override
	public String valueToString(int maxDepth) {
		return arrayToString("", "");
	}

	/**
	 * @param prefix The item prefix
	 * @param suffix The item suffix
	 * @return The generated string
	 */
	protected String arrayToString(String prefix, String suffix) {
		StringBuilder sb = new StringBuilder("[").append(prefix).append("".equals(prefix) ? "" : ";");
		for (int i = 0; i < length(); i++) {
			sb.append(i == 0 ? "" : ",").append(Array.get(getValue(), i)).append(suffix);
		}
		sb.append("]");
		return sb.toString();
	}
}
