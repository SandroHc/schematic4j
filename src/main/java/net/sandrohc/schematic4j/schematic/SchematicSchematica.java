package net.sandrohc.schematic4j.schematic;

import java.util.Collection;
import java.util.Collections;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicItem;
import net.sandrohc.schematic4j.utils.iterators.Arr3DIterator;

public class SchematicSchematica implements Schematic {

	public static final String MATERIAL_CLASSIC = "Classic";
	public static final String MATERIAL_ALPHA = "Alpha";
	public static final String MATERIAL_STRUCTURE = "Structure";

	public final int width;
	public final int height;
	public final int length;
	public final SchematicBlock[][][] blocks;
	public final Collection<SchematicBlockEntity> blockEntities;
	public final Collection<SchematicEntity> entities;
	public final SchematicItem icon;
	public final String materials;

	public SchematicSchematica(int width, int height, int length, SchematicBlock[][][] blocks,
							   Collection<SchematicBlockEntity> blockEntities, Collection<SchematicEntity> entities,
							   SchematicItem icon, String materials) {

		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = blocks;
		this.blockEntities = Collections.unmodifiableCollection(blockEntities);
		this.entities = Collections.unmodifiableCollection(entities);
		this.icon = icon;
		this.materials = materials;
	}

	@Override
	public @NonNull SchematicFormat format() {
		return SchematicFormat.SCHEMATICA;
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
	public int[] offset() {
		return new int[] { 0, 0, 0 };
	}

	@Override
	public @Nullable SchematicBlock block(int x, int y, int z) {
		if ((x < 0 || x >= width) || (y < 0 || y >= height) || (z < 0 || z >= length))
			throw new ArrayIndexOutOfBoundsException("invalid position");

		return blocks[x][y][z];
	}

	@Override
	public @NonNull Arr3DIterator<SchematicBlock> blocks() {
		return new Arr3DIterator<>(blocks);
	}

	@Override
	public @NonNull Collection<SchematicBlockEntity> blockEntities() {
		return blockEntities;
	}

	@Override
	public @NonNull Collection<SchematicEntity> entities() {
		return entities;
	}

	@Override
	public @Nullable SchematicItem icon() {
		return icon;
	}

	public @Nullable String materials() {
		return materials;
	}

	@Override
	public String toString() {
		return "SchematicSchematica[" +
			   "icon=" + icon +
			   ", width=" + width +
			   ", height=" + height +
			   ", length=" + length +
			   ']';
	}

	public static class Builder {
		private Integer width;
		private Integer height;
		private Integer length;
		private SchematicBlock[][][] blocks;
		private Collection<SchematicBlockEntity> blockEntities;
		private Collection<SchematicEntity> entities;
		private SchematicItem icon;
		private String materials;

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

		public Builder materials(String materials) {
			this.materials = materials;
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

			return new SchematicSchematica(width, height, length, blocks, blockEntities, entities, icon, materials);
		}
	}
}
