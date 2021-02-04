package net.sandrohc.schematic4j.schematic;

import java.util.Collection;

import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicPos;

public class SchematicImpl implements Schematic {

	private final int width;
	private final int height;
	private final int length;
	private final SchematicBlock[][][] blocks;
	private final Collection<SchematicBlockEntity> blockEntities;
	private final Collection<SchematicEntity> entities;

	public SchematicImpl(int width, int height, int length, SchematicBlock[][][] blocks, Collection<SchematicBlockEntity> blockEntities, Collection<SchematicEntity> entities) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = blocks;
		this.blockEntities = blockEntities;
		this.entities = entities;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getLength() {
		return length;
	}

	public SchematicBlock getBlock(SchematicPos pos) {
		if (!isValid(pos))
			throw new ArrayIndexOutOfBoundsException("invalid position");

		return blocks[pos.x][pos.y][pos.z];
	}

	private boolean isValid(SchematicPos pos) {
		return pos.x >= 0 && pos.x < width
			   && pos.y >= 0 && pos.y < height
			   && pos.z >= 0 && pos.z < length;
	}

	@Override
	public SchematicBlockEntity getBlockEntity(SchematicPos pos) {
		return blockEntities.stream()
				.filter(be -> be.pos.equals(pos))
				.findAny().orElse(null);
	}

	@Override
	public SchematicEntity getEntity(SchematicPos pos) {
		return entities.stream()
				.filter(e -> e.pos.equals(pos))
				.findAny().orElse(null);
	}

	@Override
	public String toString() {
		return "SchematicImpl(" +
			   "width=" + width +
			   ", height=" + height +
			   ", length=" + length +
			   ')';
	}
}
