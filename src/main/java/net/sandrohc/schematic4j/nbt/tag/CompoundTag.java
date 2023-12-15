/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sandrohc.schematic4j.nbt.MaxDepthIO;

/**
 * A compound NBT tag. Works like a map.
 */
public class CompoundTag extends Tag<Map<String, Tag<?>>> implements Iterable<Map.Entry<String, Tag<?>>>, Comparable<CompoundTag>, MaxDepthIO {

	/**
	 * The compound tag discriminator.
	 */
	public static final byte ID = 10;

	/**
	 * An empty compound tag.
	 */
	public CompoundTag() {
		super(createEmptyValue());
	}

	@Override
	public byte getID() {
		return ID;
	}

	private static Map<String, Tag<?>> createEmptyValue() {
		return new HashMap<>(8);
	}

	/**
	 * Get the number of entries.
	 *
	 * @return The number of entries
	 */
	public int size() {
		return getValue().size();
	}

	/**
	 * Check if this compound tag is empty.
	 *
	 * @return Whether this compound tag is empty
	 */
	public boolean isEmpty() {
		return getValue().isEmpty();
	}

	/**
	 * Removes an entry by key.
	 *
	 * @param key The entry key to remove
	 * @return The removed entry
	 */
	public Tag<?> remove(String key) {
		return getValue().remove(key);
	}

	/**
	 * Remove all entries.
	 */
	public void clear() {
		getValue().clear();
	}

	/**
	 * Returns true if this compound tag contains a mapping for the specified key.
	 *
	 * @param key key whose presence in this map is to be tested
	 * @return {@code true} if this map contains a mapping for the specified key
	 */
	public boolean containsKey(String key) {
		return getValue().containsKey(key);
	}

	/**
	 * Returns true if this map maps one or more keys to the specified value. More formally, returns true if and only
	 * if this map contains at least one mapping to a value v such that (value==null ? v==null : value.equals(v)).
	 *
	 * @param value value whose presence in this map is to be tested
	 * @return true if this map maps one or more keys to the specified value
	 */
	public boolean containsValue(Tag<?> value) {
		return getValue().containsValue(value);
	}

	/**
	 * Returns a Collection view of the values contained in this compound tag.
	 *
	 * @return a collection view of the values contained in this compound tag
	 */
	public Collection<Tag<?>> values() {
		return getValue().values();
	}

	/**
	 * Returns a Set view of the keys contained in this compound tag.
	 *
	 * @return a set view of the keys contained in this compound tag
	 */
	public Set<String> keySet() {
		return getValue().keySet();
	}

	/**
	 * Returns a Set view of the mappings contained in this compound tag.
	 *
	 * @return a set view of the mappings contained in this compound tag
	 */
	public Set<Map.Entry<String, Tag<?>>> entrySet() {
		return new NonNullEntrySet<>(getValue().entrySet());
	}

	@Override
	public @NonNull Iterator<Map.Entry<String, Tag<?>>> iterator() {
		return entrySet().iterator();
	}

	/**
	 * Performs the given action for each entry in this map until all entries have been processed or the action throws an exception.
	 *
	 * @param action The action to be performed for each entry
	 */
	public void forEach(BiConsumer<String, Tag<?>> action) {
		getValue().forEach(action);
	}

	/**
	 * Returns the value to which the specified key is mapped coerced into a type, or null if this map contains no
	 * mapping for the key.
	 *
	 * @param key the key whose associated value is to be returned
	 * @param type the type of the value
	 * @param <C> the type of the value
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public <C extends Tag<?>> C get(String key, Class<C> type) {
		Tag<?> t = getValue().get(key);
		if (t != null) {
			return type.cast(t);
		}
		return null;
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public Tag<?> get(String key) {
		return getValue().get(key);
	}

	/**
	 * Get value as a byte array tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public ByteTag getByteTag(String key) {
		return get(key, ByteTag.class);
	}

	/**
	 * Get value as a short tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public ShortTag getShortTag(String key) {
		return get(key, ShortTag.class);
	}

	/**
	 * Get value as an int tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public IntTag getIntTag(String key) {
		return get(key, IntTag.class);
	}

	/**
	 * Get value as a long tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public LongTag getLongTag(String key) {
		return get(key, LongTag.class);
	}

	/**
	 * Get value as a float tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public FloatTag getFloatTag(String key) {
		return get(key, FloatTag.class);
	}

	/**
	 * Get value as a double tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public DoubleTag getDoubleTag(String key) {
		return get(key, DoubleTag.class);
	}

	/**
	 * Get value as a string tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public StringTag getStringTag(String key) {
		return get(key, StringTag.class);
	}

	/**
	 * Get value as a byte array tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public ByteArrayTag getByteArrayTag(String key) {
		return get(key, ByteArrayTag.class);
	}

	/**
	 * Get value as an int array tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public IntArrayTag getIntArrayTag(String key) {
		return get(key, IntArrayTag.class);
	}

	/**
	 * Get value as a long array tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public LongArrayTag getLongArrayTag(String key) {
		return get(key, LongArrayTag.class);
	}

	/**
	 * Get value as a list tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public ListTag<?> getListTag(String key) {
		return get(key, ListTag.class);
	}

	/**
	 * Get value as a compound tag.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public CompoundTag getCompoundTag(String key) {
		return get(key, CompoundTag.class);
	}

	/**
	 * Get value as a boolean.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public boolean getBoolean(String key) {
		Tag<?> t = get(key);
		return t instanceof ByteTag && ((ByteTag) t).asBoolean();
	}

	/**
	 * Get value as a byte.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public byte getByte(String key) {
		ByteTag t = getByteTag(key);
		return t == null ? ByteTag.ZERO_VALUE : t.asByte();
	}

	/**
	 * Get value as a short.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public short getShort(String key) {
		ShortTag t = getShortTag(key);
		return t == null ? ShortTag.ZERO_VALUE : t.asShort();
	}

	/**
	 * Get value as an int.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public int getInt(String key) {
		IntTag t = getIntTag(key);
		return t == null ? IntTag.ZERO_VALUE : t.asInt();
	}

	/**
	 * Get value as a long.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public long getLong(String key) {
		LongTag t = getLongTag(key);
		return t == null ? LongTag.ZERO_VALUE : t.asLong();
	}

	/**
	 * Get value as a float.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public float getFloat(String key) {
		FloatTag t = getFloatTag(key);
		return t == null ? FloatTag.ZERO_VALUE : t.asFloat();
	}

	/**
	 * Get value as a double.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public double getDouble(String key) {
		DoubleTag t = getDoubleTag(key);
		return t == null ? DoubleTag.ZERO_VALUE : t.asDouble();
	}

	/**
	 * Get value as a string.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public String getString(String key) {
		StringTag t = getStringTag(key);
		return t == null ? StringTag.ZERO_VALUE : t.getValue();
	}

	/**
	 * Get value as a byte array.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public byte[] getByteArray(String key) {
		ByteArrayTag t = getByteArrayTag(key);
		return t == null ? ByteArrayTag.ZERO_VALUE : t.getValue();
	}

	/**
	 * Get value as an int array.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public int[] getIntArray(String key) {
		IntArrayTag t = getIntArrayTag(key);
		return t == null ? IntArrayTag.ZERO_VALUE : t.getValue();
	}

	/**
	 * Get value as a long array.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public long[] getLongArray(String key) {
		LongArrayTag t = getLongArrayTag(key);
		return t == null ? LongArrayTag.ZERO_VALUE : t.getValue();
	}

	/**
	 * Associates the specified value with the specified key in this map (optional operation). If the map previously
	 * contained a mapping for the key, the old value is replaced by the specified value.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param tag value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> put(String key, Tag<?> tag) {
		return getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(tag));
	}

	/**
	 * Inserts the boolean value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putBoolean(String key, boolean value) {
		return put(key, new ByteTag(value));
	}

	/**
	 * Inserts the byte value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putByte(String key, byte value) {
		return put(key, new ByteTag(value));
	}

	/**
	 * Inserts the short value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putShort(String key, short value) {
		return put(key, new ShortTag(value));
	}

	/**
	 * Inserts the int value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putInt(String key, int value) {
		return put(key, new IntTag(value));
	}

	/**
	 * Inserts the long value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putLong(String key, long value) {
		return put(key, new LongTag(value));
	}

	/**
	 * Inserts the float value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putFloat(String key, float value) {
		return put(key, new FloatTag(value));
	}

	/**
	 * Inserts the double value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putDouble(String key, double value) {
		return put(key, new DoubleTag(value));
	}

	/**
	 * Inserts the string value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putString(String key, String value) {
		return put(key, new StringTag(value));
	}

	/**
	 * Inserts the byte array value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putByteArray(String key, byte[] value) {
		return put(key, new ByteArrayTag(value));
	}

	/**
	 * Inserts the int array value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putIntArray(String key, int[] value) {
		return put(key, new IntArrayTag(value));
	}

	/**
	 * Inserts the long array value into this compound tag.
	 *
	 * @param key key with which the specified value is to be associated value
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Tag<?> putLongArray(String key, long[] value) {
		return put(key, new LongArrayTag(value));
	}

	@Override
	public String valueToString(int maxDepth) {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
			sb.append(first ? "" : ",")
					.append(escapeString(e.getKey(), false)).append(":")
					.append(e.getValue().toString(decrementMaxDepth(maxDepth)));
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other) || size() != ((CompoundTag) other).size()) {
			return false;
		}
		for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
			Tag<?> v;
			if ((v = ((CompoundTag) other).get(e.getKey())) == null || !e.getValue().equals(v)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(CompoundTag o) {
		return Integer.compare(size(), o.getValue().size());
	}

	@Override
	public CompoundTag clone() {
		CompoundTag copy = new CompoundTag();
		for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
			copy.put(e.getKey(), e.getValue().clone());
		}
		return copy;
	}
}
