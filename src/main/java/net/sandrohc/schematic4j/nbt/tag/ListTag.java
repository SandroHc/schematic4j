/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sandrohc.schematic4j.nbt.MaxDepthIO;

/**
 * ListTag represents a typed List in the nbt structure.
 * An empty {@link ListTag} will be of type {@link EndTag} (unknown type).
 * The type of empty untyped {@link ListTag} can be set by using any of the {@code add()}
 * methods or any of the {@code as...List()} methods.
 *
 * @param <T> The type of the list entries
 */
public class ListTag<T extends Tag<?>> extends Tag<List<T>> implements Iterable<T>, Comparable<ListTag<T>>, MaxDepthIO {

	/**
	 * The list tag discriminator.
	 */
	public static final byte ID = 9;

	/**
	 * The type of the list entries.
	 */
	private Class<?> typeClass = null;

	/**
	 * A list tag.
	 */
	private ListTag() {
		super(createEmptyValue(3));
	}

	@Override
	public byte getID() {
		return ID;
	}

	/**
	 * <p>Creates a non-type-safe ListTag. Its element type will be set after the first
	 * element was added.</p>
	 *
	 * <p>This is an internal helper method for cases where the element type is not known
	 * at construction time. Use {@link #ListTag(Class)} when the type is known.</p>
	 *
	 * @param typeClass The type of the list entries
	 * @return A new non-type-safe ListTag
	 */
	public static ListTag<?> createUnchecked(Class<?> typeClass) {
		ListTag<?> list = new ListTag<>();
		list.typeClass = typeClass;
		return list;
	}

	/**
	 * <p>Creates an empty mutable list to be used as empty value of ListTags.</p>
	 *
	 * @param <T>             Type of the list elements
	 * @param initialCapacity The initial capacity of the returned List
	 * @return An instance of {@link java.util.List} with an initial capacity of 3
	 */
	private static <T> List<T> createEmptyValue(int initialCapacity) {
		return new ArrayList<>(initialCapacity);
	}

	/**
	 * @param typeClass The exact class of the elements
	 * @throws IllegalArgumentException When {@code typeClass} is {@link EndTag}{@code .class}
	 * @throws NullPointerException     When {@code typeClass} is {@code null}
	 */
	public ListTag(Class<? super T> typeClass) throws IllegalArgumentException, NullPointerException {
		super(createEmptyValue(3));
		if (typeClass == EndTag.class) {
			throw new IllegalArgumentException("cannot create ListTag with EndTag elements");
		}
		this.typeClass = Objects.requireNonNull(typeClass);
	}

	/**
	 * Get the type of the entries in this list.
	 *
	 * @return The type of the entries in this list
	 */
	public Class<?> getTypeClass() {
		return typeClass == null ? EndTag.class : typeClass;
	}

	/**
	 * Returns the number of elements in this list. If this list contains more than Integer.MAX_VALUE elements,
	 * returns Integer.MAX_VALUE.
	 *
	 * @return the number of elements in this list
	 */
	public int size() {
		return getValue().size();
	}

	/**
	 * Removes the element at the specified position in this list (optional operation). Shifts any subsequent elements
	 * to the left (subtracts one from their indices). Returns the element that was removed from the list.
	 *
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 */
	public T remove(int index) {
		return getValue().remove(index);
	}

	/**
	 * Removes all the elements from this list (optional operation). The list will be empty after this call returns.
	 */
	public void clear() {
		getValue().clear();
	}

	/**
	 * Returns true if this list contains the specified element. More formally, returns true if and only if this list
	 * contains at least one element e such that (o==null ? e==null : o.equals(e)).
	 *
	 * @param t element whose presence in this list is to be tested
	 * @return true if this list contains the specified element
	 */
	public boolean contains(T t) {
		return getValue().contains(t);
	}

	/**
	 * Returns true if this list contains all of the elements of the specified collection.
	 *
	 * @param tags collection to be checked for containment in this list
	 * @return true if this list contains all the elements of the specified collection
	 */
	public boolean containsAll(Collection<Tag<?>> tags) {
		return getValue().containsAll(tags);
	}

	/**
	 * Sorts this list according to the order induced by the specified Comparator.
	 * <p>
	 * All elements in this list must be mutually comparable using the specified comparator (that is, c.compare(e1, e2)
	 * must not throw a ClassCastException for any elements e1 and e2 in the list).
	 * <p>
	 * If the specified comparator is null then all elements in this list must implement the Comparable interface and
	 * the elements' natural ordering should be used.
	 * <p>
	 * This list must be modifiable, but need not be resizable.
	 *
	 * @param comparator the Comparator used to compare list elements. A null value indicates that the elements' natural ordering should be used
	 */
	public void sort(Comparator<T> comparator) {
		getValue().sort(comparator);
	}

	@Override
	public @NonNull Iterator<T> iterator() {
		return getValue().iterator();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		getValue().forEach(action);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element (optional operation).
	 *
	 * @param index index of the element to replace element
	 * @param t element to be stored at the specified position
	 * @return the element previously at the specified position
	 */
	public T set(int index, T t) {
		return getValue().set(index, Objects.requireNonNull(t));
	}

	/**
	 * Adds a Tag to this ListTag after the last index.
	 *
	 * @param t The element to be added.
	 */
	public void add(T t) {
		add(size(), t);
	}

	/**
	 * Inserts the specified element at the specified position in this list (optional operation). Shifts the element
	 * currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted element
	 * @param t element to be inserted
	 */
	public void add(int index, T t) {
		Objects.requireNonNull(t);
		if (getTypeClass() == EndTag.class) {
			typeClass = t.getClass();
		} else if (typeClass != t.getClass()) {
			throw new ClassCastException(
					String.format("cannot add %s to ListTag<%s>",
							t.getClass().getSimpleName(),
							typeClass.getSimpleName()));
		}
		getValue().add(index, t);
	}

	/**
	 * Add all entries in collection to the list tag.
	 *
	 * @param t Entries to add
	 */
	public void addAll(Collection<T> t) {
		for (T tt : t) {
			add(tt);
		}
	}

	/**
	 * Add all entries in collection to the list tag at a specific index.
	 *
	 * @param index The index to insert the new values
	 * @param t     Entries to add
	 */
	public void addAll(int index, Collection<T> t) {
		int i = 0;
		for (T tt : t) {
			add(index + i, tt);
			i++;
		}
	}

	/**
	 * Add a boolean value.
	 *
	 * @param value The new value
	 */
	public void addBoolean(boolean value) {
		addUnchecked(new ByteTag(value));
	}

	/**
	 * Add a byte value.
	 *
	 * @param value The new value
	 */
	public void addByte(byte value) {
		addUnchecked(new ByteTag(value));
	}

	/**
	 * Add a short value.
	 *
	 * @param value The new value
	 */
	public void addShort(short value) {
		addUnchecked(new ShortTag(value));
	}

	/**
	 * Add an int value.
	 *
	 * @param value The new value
	 */
	public void addInt(int value) {
		addUnchecked(new IntTag(value));
	}

	/**
	 * Add a long value.
	 *
	 * @param value The new value
	 */
	public void addLong(long value) {
		addUnchecked(new LongTag(value));
	}

	/**
	 * Add a float value.
	 *
	 * @param value The new value
	 */
	public void addFloat(float value) {
		addUnchecked(new FloatTag(value));
	}

	/**
	 * Add a double value.
	 *
	 * @param value The new value
	 */
	public void addDouble(double value) {
		addUnchecked(new DoubleTag(value));
	}

	/**
	 * Add a string value.
	 *
	 * @param value The new value
	 */
	public void addString(String value) {
		addUnchecked(new StringTag(value));
	}

	/**
	 * Add a byte array value.
	 *
	 * @param value The new value
	 */
	public void addByteArray(byte[] value) {
		addUnchecked(new ByteArrayTag(value));
	}

	/**
	 * Add an int array value.
	 *
	 * @param value The new value
	 */
	public void addIntArray(int[] value) {
		addUnchecked(new IntArrayTag(value));
	}

	/**
	 * Add a long array value.
	 *
	 * @param value The new value
	 */
	public void addLongArray(long[] value) {
		addUnchecked(new LongArrayTag(value));
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<code>index &lt; 0 || index &gt;= size()</code>)
	 */
	public T get(int index) {
		return getValue().get(index);
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the lowest index i such that
	 * (o==null ? get(i)==null : o.equals(get(i))), or -1 if there is no such index.
	 *
	 * @param t element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element
	 * @throws ClassCastException   if the type of the specified element is incompatible with this list (optional)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements (optional)
	 */
	public int indexOf(T t) {
		return getValue().indexOf(t);
	}

	/**
	 * Coerces this list tag into a specific type.
	 *
	 * @param type The type to coerce into
	 * @param <L> The type to coerce into
	 * @return The coerced list tag
	 */
	@SuppressWarnings("unchecked")
	public <L extends Tag<?>> ListTag<L> asTypedList(Class<L> type) {
		checkTypeClass(type);
		return (ListTag<L>) this;
	}

	/**
	 * Coerces this list tag into a byte tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<ByteTag> asByteTagList() {
		return asTypedList(ByteTag.class);
	}

	/**
	 * Coerces this list tag into a short tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<ShortTag> asShortTagList() {
		return asTypedList(ShortTag.class);
	}

	/**
	 * Coerces this list tag into an int tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<IntTag> asIntTagList() {
		return asTypedList(IntTag.class);
	}

	/**
	 * Coerces this list tag into a long tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<LongTag> asLongTagList() {
		return asTypedList(LongTag.class);
	}

	/**
	 * Coerces this list tag into a float tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<FloatTag> asFloatTagList() {
		return asTypedList(FloatTag.class);
	}

	/**
	 * Coerces this list tag into a double tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<DoubleTag> asDoubleTagList() {
		return asTypedList(DoubleTag.class);
	}

	/**
	 * Coerces this list tag into a string tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<StringTag> asStringTagList() {
		return asTypedList(StringTag.class);
	}

	/**
	 * Coerces this list tag into a byte array tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<ByteArrayTag> asByteArrayTagList() {
		return asTypedList(ByteArrayTag.class);
	}

	/**
	 * Coerces this list tag into an int array tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<IntArrayTag> asIntArrayTagList() {
		return asTypedList(IntArrayTag.class);
	}

	/**
	 * Coerces this list tag into a long array tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<LongArrayTag> asLongArrayTagList() {
		return asTypedList(LongArrayTag.class);
	}

	/**
	 * Coerces this list tag into a list of list tags.
	 *
	 * @return The coerced list tag
	 */
	@SuppressWarnings("unchecked")
	public ListTag<ListTag<?>> asListTagList() {
		checkTypeClass(ListTag.class);
		typeClass = ListTag.class;
		return (ListTag<ListTag<?>>) this;
	}

	/**
	 * Coerces this list tag into a compound tag list.
	 *
	 * @return The coerced list tag
	 */
	public ListTag<CompoundTag> asCompoundTagList() {
		return asTypedList(CompoundTag.class);
	}

	@Override
	public String valueToString(int maxDepth) {
		StringBuilder sb = new StringBuilder("{\"type\":\"").append(getTypeClass().getSimpleName()).append("\",\"list\":[");
		for (int i = 0; i < size(); i++) {
			sb.append(i > 0 ? "," : "").append(get(i).valueToString(decrementMaxDepth(maxDepth)));
		}
		sb.append("]}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other) || size() != ((ListTag<?>) other).size() || getTypeClass() != ((ListTag<?>) other).getTypeClass()) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(((ListTag<?>) other).get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTypeClass().hashCode(), getValue().hashCode());
	}

	@Override
	public int compareTo(ListTag<T> o) {
		return Integer.compare(size(), o.getValue().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListTag<T> clone() {
		ListTag<T> copy = new ListTag<>();
		// assure type safety for clone
		copy.typeClass = typeClass;
		for (T t : getValue()) {
			copy.add((T) t.clone());
		}
		return copy;
	}

	/**
	 * Inserts the specified element without confirming if it is the same type as the list.
	 *
	 * @param tag element to be inserted
	 */
	@SuppressWarnings("unchecked")
	public void addUnchecked(Tag<?> tag) {
		if (getTypeClass() != EndTag.class && typeClass != tag.getClass()) {
			throw new IllegalArgumentException(String.format(
					"cannot add %s to ListTag<%s>",
					tag.getClass().getSimpleName(), typeClass.getSimpleName()));
		}
		add(size(), (T) tag);
	}

	/**
	 * Check the type of the entries on this list tag.
	 * @param clazz The expected type
	 */
	private void checkTypeClass(Class<?> clazz) {
		if (getTypeClass() != EndTag.class && typeClass != clazz) {
			throw new ClassCastException(String.format(
					"cannot cast ListTag<%s> to ListTag<%s>",
					typeClass.getSimpleName(), clazz.getSimpleName()));
		}
	}
}
