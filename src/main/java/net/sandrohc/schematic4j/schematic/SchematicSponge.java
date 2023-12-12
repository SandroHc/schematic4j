package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.SchematicBuilderException;
import net.sandrohc.schematic4j.schematic.types.SchematicBiome;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.utils.iterators.Arr3DIterator;

public class SchematicSponge implements Schematic {

	public static final int[] DEFAULT_OFFSET = { 0, 0, 0 };

	/** The Sponge Schematic format version. **/
	public final int version;

	/** The data version, used in world save data. */
	public final int dataVersion;

	/** The optional additional meta information about the schematic. */
	public final Metadata metadata;

	public final int width;
	public final int height;
	public final int length;
	public final int[] offset;
	public final SchematicBlock[][][] blocks;
	public final Collection<SchematicBlockEntity> blockEntities;
	public final Collection<SchematicEntity> entities;
	public final SchematicBiome[][][] biomes;

	public SchematicSponge(int version, int dataVersion, Metadata metadata, int width, int height, int length,
						   int[] offset, SchematicBlock[][][] blocks, Collection<SchematicBlockEntity> blockEntities,
						   Collection<SchematicEntity> entities, SchematicBiome[][][] biomes) {
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
	public @NonNull SchematicFormat format() {
		return version == 1 ? SchematicFormat.SPONGE_V1 : SchematicFormat.SPONGE_V2;
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
		return offset;
	}

	@Override
	public @Nullable SchematicBlock block(int x, int y, int z) {
		if ((x < 0 || x >= width) || (y < 0 || y >= height) || (z < 0 || z >= length)) {
			throw new ArrayIndexOutOfBoundsException(String.format("position out of bounds: %d, %d, %d", x, y, z));
		}

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
	public @Nullable SchematicBiome biome(int x, int y, int z) {
		if ((x < 0 || x >= width) || (y < 0 || y >= height) || (z < 0 || z >= length)) {
			throw new ArrayIndexOutOfBoundsException(String.format("position out of bounds: %d, %d, %d", x, y, z));
		}

		return biomes[x][y][z];
	}

	@Override
	public @NonNull Arr3DIterator<SchematicBiome> biomes() {
		return new Arr3DIterator<>(biomes);
	}

	@Override
	public @Nullable String name() {
		return metadata.name;
	}

	@Override
	public @Nullable String author() {
		return metadata.author;
	}

	@Override
	public @Nullable LocalDateTime date() {
		return metadata.date;
	}

	public int dataVersion() {
		return dataVersion;
	}

	/**
	 * @deprecated Use {@link SchematicSponge#dataVersion()} instead
	 */
	@Deprecated
	public int getDataVersion() {
		return dataVersion();
	}

	public @NonNull Metadata metadata() {
		return metadata;
	}

	/**
	 * @deprecated Use {@link SchematicSponge#metadata()} instead
	 */
	@Deprecated
	public @NonNull Metadata getMetadata() {
		return metadata();
	}


	public static class Metadata {
		/** The name of the schematic. */
		public final @Nullable String name;

		/** The name of the author of the schematic. */
		public final @Nullable String author;

		/** The date that this schematic was created on. */
		public final @Nullable LocalDateTime date;

		/** An array of mod IDs. */
		public final @NonNull String[] requiredMods;

		public final @NonNull Map<String, Object> extra;

		public Metadata(@Nullable String name, @Nullable String author, @Nullable LocalDateTime date,
						@NonNull String[] requiredMods, @NonNull Map<String, Object> extra) {
			this.name = name;
			this.author = author;
			this.date = date;
			this.requiredMods = requiredMods;
			this.extra = Collections.unmodifiableMap(extra);
		}

		@Override
		public String toString() {
			return "Metadata[name='" + name + "', author='" + author + "']";
		}
	}

	@Override
	public String toString() {
		return "SchematicSponge[" +
			   "format=" + format() +
			   ", name=" + name() +
			   ", author=" + author() +
			   ", width=" + width +
			   ", height=" + height +
			   ", length=" + length +
			   ']';
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
		private SchematicBiome[][][] biomes;

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

		public Builder biomes(SchematicBiome[][][] biomes) {
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
