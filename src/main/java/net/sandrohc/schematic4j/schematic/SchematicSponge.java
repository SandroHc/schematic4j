package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.types.*;
import net.sandrohc.schematic4j.utils.iterators.Arr2DIterator;
import net.sandrohc.schematic4j.utils.iterators.Arr3DIterator;

public class SchematicSponge implements Schematic {

	private static final int[] DEFAULT_OFFSET = { 0, 0, 0 };

	/** The Sponge Schematic format version. **/
	private final int version;

	/** The data version, used in world save data. */
	private final int dataVersion;

	/** The optional additional meta information about the schematic. */
	private final Metadata metadata;

	private final int width;
	private final int height;
	private final int length;
	private final int[] offset;
	private final SchematicBlock[][][] blocks;
	private final Collection<SchematicBlockEntity> blockEntities;
	private final Collection<SchematicEntity> entities;
	private final SchematicBiome[][] biomes;


	public SchematicSponge(int version, int dataVersion, Metadata metadata, int width, int height, int length, int[] offset, SchematicBlock[][][] blocks, Collection<SchematicBlockEntity> blockEntities, Collection<SchematicEntity> entities, SchematicBiome[][] biomes) {
		this.version = version;
		this.dataVersion = dataVersion;
		this.metadata = metadata != null ? metadata : new Metadata(null, null, null, new String[0], Collections.emptyMap());
		this.width = width;
		this.height = height;
		this.length = length;
		this.offset = offset != null ? offset : DEFAULT_OFFSET;
		this.blocks = blocks;
		this.blockEntities = Collections.unmodifiableCollection(blockEntities);
		this.entities = Collections.unmodifiableCollection(entities);
		this.biomes = biomes;
	}

	@Override
	public SchematicFormat getFormat() {
		return version == 1 ? SchematicFormat.SPONGE_V1 : SchematicFormat.SPONGE_V2;
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
		return offset;
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
	public SchematicBiome getBiome(int x, int z) {
		if ((x < 0 || x >= width) || (z < 0 || z >= length))
			throw new ArrayIndexOutOfBoundsException("invalid position");

		return biomes[x][z];
	}

	@Override
	public Arr2DIterator<SchematicBiome> getBiomes() {
		return new Arr2DIterator<>(biomes, biomes.length, biomes.length != 0 ? biomes[0].length : 0);
	}

	@Override
	public String getName() {
		return metadata.name;
	}

	@Override
	public String author() {
		return metadata.author;
	}

	@Override
	public LocalDateTime date() {
		return metadata.date;
	}

	public int getDataVersion() {
		return dataVersion;
	}

	public Metadata getMetadata() {
		return metadata;
	}


	public static class Metadata {
		/** The name of the schematic. */
		public final String name;

		/** The name of the author of the schematic. */
		public final String author;

		/** The date that this schematic was created on. */
		public final LocalDateTime date;

		/** An array of mod IDs. */
		public final String[] requiredMods;

		public final Map<String, Object> extra;

		public Metadata(String name, String author, LocalDateTime date, String[] requiredMods, Map<String, Object> extra) {
			this.name = name;
			this.author = author;
			this.date = date;
			this.requiredMods = requiredMods;
			this.extra = Collections.unmodifiableMap(extra);
		}

		@Override
		public String toString() {
			return "Metadata(" +
				   "name='" + name + '\'' +
				   ", author='" + author + '\'' +
				   ')';
		}
	}

	@Override
	public String toString() {
		return "SchematicSponge(" +
			   "format=" + getFormat() +
			   ", name=" + getName() +
			   ", author=" + author() +
			   ", width=" + width +
			   ", height=" + height +
			   ", length=" + length +
			   ')';
	}

	public static class Builder {

		private Integer version;
		private Integer dataVersion;
		private Metadata metadata;
		private Integer width;
		private Integer height;
		private Integer length;
		private int[] offset = DEFAULT_OFFSET;
		private SchematicBlock[][][] blocks;
		private Collection<SchematicBlockEntity> blockEntities;
		private Collection<SchematicEntity> entities;
		private SchematicBiome[][] biomes;


		public Builder() {
		}

		public Builder version(Integer version) {
			this.version = version;
			return this;
		}

		public Builder dataVersion(Integer dataVersion) {
			this.dataVersion = dataVersion;
			return this;
		}

		public Builder metadata(Metadata metadata) {
			this.metadata = metadata;
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

		public Builder offset(int[] offset) {
			if (offset.length != 3)
				throw new IllegalArgumentException("offset must have exactly three values");

			this.offset = offset;
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

		public Builder biomes(SchematicBiome[][] biomes) {
			this.biomes = biomes;
			return null;
		}

		public SchematicSponge build() {
			if (version == null)
				throw new SchematicBuilderException("version must be set");
			if (dataVersion == null)
				throw new SchematicBuilderException("dataVersion must be set");
			if (width == null)
				throw new SchematicBuilderException("width must be set");
			if (height == null)
				throw new SchematicBuilderException("height must be set");
			if (length == null)
				throw new SchematicBuilderException("length must be set");
			if (blocks == null)
				throw new SchematicBuilderException("blocks must be set");

			return new SchematicSponge(version, dataVersion, metadata, width, height, length, offset, blocks, blockEntities, entities, biomes);
		}
	}

}
