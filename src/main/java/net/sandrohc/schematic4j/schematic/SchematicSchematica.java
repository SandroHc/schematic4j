package net.sandrohc.schematic4j.schematic;

import java.util.Collection;
import java.util.Collections;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicItem;
import net.sandrohc.schematic4j.utils.iterators.Arr3DIterator;

public class SchematicSchematica implements Schematic {

	private final int width;
	private final int height;
	private final int length;
	private final SchematicBlock[][][] blocks;
	private final Collection<SchematicBlockEntity> blockEntities;
	private final Collection<SchematicEntity> entities;
	private final SchematicItem icon;


	public SchematicSchematica(int width, int height, int length, SchematicBlock[][][] blocks,
							   Collection<SchematicBlockEntity> blockEntities, Collection<SchematicEntity> entities,
							   SchematicItem icon) {

		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = blocks;
		this.blockEntities = Collections.unmodifiableCollection(blockEntities);
		this.entities = Collections.unmodifiableCollection(entities);
		this.icon = icon;
	}

	@Override
	public SchematicFormat getFormat() {
		return SchematicFormat.SCHEMATICA;
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

	@Override
	public int[] getOffset() {
		return new int[] { 0, 0, 0 };
	}

	@Override
	public SchematicBlock getBlock(int x, int y, int z) {
		if ((x < 0 || x >= width) || (y < 0 || y >= height) || (z < 0 || z >= length))
			throw new ArrayIndexOutOfBoundsException("invalid position");

		return blocks[x][y][z];
	}

	@Override
	public Arr3DIterator<SchematicBlock> getBlocks() {
		return new Arr3DIterator<>(blocks, blocks.length, blocks[0].length, blocks[0][0].length);
	}

	@Override
	public Collection<SchematicBlockEntity> getBlockEntities() {
		return blockEntities;
	}

	@Override
	public Collection<SchematicEntity> getEntities() {
		return entities;
	}

	@Override
	public SchematicItem icon() {
		return icon;
	}

	@Override
	public String toString() {
		return "SchematicSchematica(" +
			   "icon=" + icon +
			   ", width=" + width +
			   ", height=" + height +
			   ", length=" + length +
			   ')';
	}

	public static class Builder {

		private Integer width;
		private Integer height;
		private Integer length;
		private SchematicBlock[][][] blocks;
		private Collection<SchematicBlockEntity> blockEntities;
		private Collection<SchematicEntity> entities;
		private SchematicItem icon;


		public Builder() {
		}

		public Builder icon(SchematicItem icon) {
			this.icon = icon;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder height(int height) {
			this.height = height;
			return this;
		}

		public Builder length(int length) {
			this.length = length;
			return this;
		}

		public Builder blocks(SchematicBlock[][][] blocks) {
			this.blocks = blocks;
			return this;
		}

		public Builder blockEntities(Collection<SchematicBlockEntity> blockEntities) {
			this.blockEntities = blockEntities;
			return this;
		}

		public Builder entities(Collection<SchematicEntity> entities) {
			this.entities = entities;
			return this;
		}

		public SchematicSchematica build() {
			if (width == null)
				throw new SchematicBuilderException("width must be set");
			if (height == null)
				throw new SchematicBuilderException("height must be set");
			if (length == null)
				throw new SchematicBuilderException("length must be set");
			if (blocks == null)
				throw new SchematicBuilderException("blocks must be set");

			return new SchematicSchematica(width, height, length, blocks, blockEntities, entities, icon);
		}
	}

}
