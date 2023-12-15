/* Vendored version of Quertz NBT 6.1 - https://github.com/Querz/NBT */
package net.sandrohc.schematic4j.nbt.tag;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A decorator for the Set returned by CompoundTag#entrySet()
 * that disallows setting null values.
 * */
class NonNullEntrySet<K, V> implements Set<Map.Entry<K, V>> {

	/**
	 * The inner set.
	 */
	private final Set<Map.Entry<K, V>> set;

	NonNullEntrySet(Set<Map.Entry<K, V>> set) {
		this.set = set;
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public @NonNull Iterator<Map.Entry<K, V>> iterator() {
		return new NonNullEntrySetIterator(set.iterator());
	}

	@Override
	public Object @NonNull [] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T @NonNull [] toArray(T @NonNull [] a) {
		return set.toArray(a);
	}

	@Override
	public boolean add(Map.Entry<K, V> kvEntry) {
		return set.add(kvEntry);
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean containsAll(@NonNull Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(@NonNull Collection<? extends Map.Entry<K, V>> c) {
		return set.addAll(c);
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> c) {
		return set.retainAll(c);
	}

	@Override
	public boolean removeAll(@NonNull Collection<?> c) {
		return set.removeAll(c);
	}

	@Override
	public void clear() {
		set.clear();
	}

	class NonNullEntrySetIterator implements Iterator<Map.Entry<K, V>> {

		private final Iterator<Map.Entry<K, V>> iterator;

		NonNullEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Map.Entry<K, V> next() {
			return new NonNullEntry(iterator.next());
		}
	}

	class NonNullEntry implements Map.Entry<K, V> {

		private final Map.Entry<K, V> entry;

		NonNullEntry(Map.Entry<K, V> entry) {
			this.entry = entry;
		}

		@Override
		public K getKey() {
			return entry.getKey();
		}

		@Override
		public V getValue() {
			return entry.getValue();
		}

		@Override
		public V setValue(V value) {
			if (value == null) {
				throw new NullPointerException(getClass().getSimpleName() + " does not allow setting null");
			}
			return entry.setValue(value);
		}

		@Override
		public boolean equals(Object o) {
			return entry.equals(o);
		}

		@Override
		public int hashCode() {
			return entry.hashCode();
		}
	}
}