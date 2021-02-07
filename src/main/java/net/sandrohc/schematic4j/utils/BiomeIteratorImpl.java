package net.sandrohc.schematic4j.utils;

public abstract class BiomeIteratorImpl<T> implements BiomeIterator {

	protected final T arr;
	protected final int width, length, total;
	protected int x, z, i;

	public BiomeIteratorImpl(T arr, int width, int length) {
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
	public int width() {
		return width;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int z() {
		return z;
	}

}
