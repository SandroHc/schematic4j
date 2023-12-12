package net.sandrohc.schematic4j.utils.iterators;

import java.util.Iterator;

/**
 * Iterates over a 2D array.
 * @param <T> The item type being iterated over
 */
public class Arr2DIterator<T> implements Iterator<T> {

	/** The inner array. */
	protected final T[][] arr;
	protected final int width, length, total;
	protected int x, z, i;

	public Arr2DIterator(T[][] arr) {
		this.arr = arr;
		this.width = arr != null ? arr.length : 0;
		this.length = arr != null && arr.length != 0 ? arr[0].length : 0;
		this.total = this.width * this.length;
		this.x = 0;
		this.z = 0;
		this.i = 0;
	}

	@Override
	public boolean hasNext() {
		return i < total;
	}

	@Override
	public T next() {
		x = i / width;
		z = i % length;
		++i;

		return arr[x][z];
	}

	public int width() {
		return width;
	}

	public int length() {
		return length;
	}

	public int x() {
		return x;
	}

	public int z() {
		return z;
	}

}
