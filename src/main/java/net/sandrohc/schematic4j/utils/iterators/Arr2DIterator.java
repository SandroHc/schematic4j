package net.sandrohc.schematic4j.utils.iterators;

import java.util.Iterator;

public class Arr2DIterator<T> implements Iterator<T> {

	protected final T[][] arr;
	protected final int width, length, total;
	protected int x, z, i;

	public Arr2DIterator(T[][] arr, int width, int length) {
		this.arr = arr;
		this.width = width;
		this.length = length;
		this.total = width * length;
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
