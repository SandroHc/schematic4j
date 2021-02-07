package net.sandrohc.schematic4j.utils;

public abstract class BlockIteratorImpl<T> implements BlockIterator {

	protected final T blocks;
	protected final int width, height, length, total;
	protected int x, y, z, i;

	public BlockIteratorImpl(T blocks, int width, int height, int length) {
		this.blocks = blocks;
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
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
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
	public int y() {
		return y;
	}

	@Override
	public int z() {
		return z;
	}

}
