package net.sandrohc.schematic4j.utils.iterators;

import java.util.Iterator;

public class Arr3DIterator<T> implements Iterator<T> {

	protected final T[][][] arr;
	protected final int width, height, length, total;
	protected int x, y, z, i;

	public Arr3DIterator(T[][][] arr, int width, int height, int length) {
		this.arr = arr;
		this.width = width;
		this.height = height;
		this.length = length;
		this.total = width * height * length;
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.i = 0;
	}

	@Override
	public boolean hasNext() {
		return i < total;
	}

	@Override
	public T next() {
		z = i % length;
		y = (i / length) % height;
		x = i / (height * length);
		++i;

		return arr[x][y][z];
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int length() {
		return length;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int z() {
		return z;
	}

}
