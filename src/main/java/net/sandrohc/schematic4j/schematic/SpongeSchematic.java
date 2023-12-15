package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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

import static net.sandrohc.schematic4j.schematic.types.SchematicBlock.AIR;

/**
 * A Sponge schematic. Read more about it at <a href="https://github.com/SpongePowered/Schematic-Specification">https://github.com/SpongePowered/Schematic-Specification</a>.
 */
public class SpongeSchematic implements Schematic {

	public static final int[] DEFAULT_OFFSET = {0, 0, 0};

	/**
	 * The Sponge Schematic format version being used.
	 **/
	public final int version;

	/**
	 * Specifies the data version of Minecraft that was used to create the schematic. This is to allow for block and
	 * entity data to be validated and auto-converted from older versions. This is dependent on the Minecraft version,
	 * e.g. Minecraft 1.12.2's data version is <a href="https://minecraft.gamepedia.com/1.12.2">1343</a>.
	 */
	public final @Nullable Integer dataVersion;

	/**
	 * The optional metadata about the schematic.
	 */
	public final @NonNull Metadata metadata;

	/**
	 * The width (the size of the area in the X-axis) of the schematic.
	 */
	public final int width;

	/**
	 * The height (the size of the area in the Y-axis) of the schematic.
	 */
	public final int height;

	/**
	 * The length (the size of the area in the Z-axis) of the schematic.
	 */
	public final int length;

	/**
	 * The relative offset of the schematic from the paster. When pasting, if there is a reasonable location to use as
	 * a base position, implementations SHOULD offset the location of the paste by this vector. The default value if
	 * not provided is [0, 0, 0]. Example: If a player is pasting from 1, 2, 3, and the offset is 4, 5, 6, then the
	 * first block should be placed at 5, 7, 9
	 */
	public final int[] offset;

	public final @NonNull SchematicBlock[][][] blocks;
	public final SchematicBlockEntity[] blockEntities;
	public final SchematicEntity[] entities;
	public final SchematicBiome[][][] biomes;

	public SpongeSchematic(int version, @Nullable Integer dataVersion, @Nullable Metadata metadata, int width,
						   int height, int length, int[] offset, @NonNull SchematicBlock[][][] blocks,
						   @NonNull SchematicBlockEntity[] blockEntities, @NonNull SchematicEntity[] entities,
						   @NonNull SchematicBiome[][][] biomes) {
		this.version = version;
		this.dataVersion = dataVersion;
		this.metadata = metadata != null ? metadata : new Metadata();
		this.width = width;
		this.height = height;
		this.length = length;
		this.offset = offset != null ? offset : DEFAULT_OFFSET;
		this.blocks = blocks;
		this.blockEntities = blockEntities;
		this.entities = entities;
		this.biomes = biomes;
	}

	@Override
	public @NonNull SchematicFormat format() {
		switch (version) {
			case 1:
				return SchematicFormat.SPONGE_V1;
			case 2:
				return SchematicFormat.SPONGE_V2;
			default:
			case 3:
				return SchematicFormat.SPONGE_V3;
		}
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
			return AIR; // outside bounds
		}

		return blocks[x][y][z];
	}

	@Override
	public @NonNull Iterator<SchematicBlock> blocks() {
		return new Arr3DIterator<>(blocks);
	}

	public @NonNull SchematicBlock[][][] blockData() {
		return blocks;
	}

	@Override
	public @NonNull Iterator<SchematicBlockEntity> blockEntities() {
		return Arrays.stream(blockEntities).iterator();
	}

	public @NonNull SchematicBlockEntity[] blockEntityData() {
		return blockEntities;
	}

	@Override
	public @NonNull Iterator<SchematicEntity> entities() {
		return Arrays.stream(entities).iterator();
	}

	public @NonNull SchematicEntity[] entityData() {
		return entities;
	}

	@Override
	public @Nullable SchematicBiome biome(int x, int y, int z) {
		if ((x < 0 || x >= width) || (y < 0 || y >= height) || (z < 0 || z >= length)) {
			return new SchematicBiome("minecraft:air"); // outside bounds
		}

		return biomes[x][y][z];
	}

	@Override
	public @NonNull Iterator<SchematicBiome> biomes() {
		return new Arr3DIterator<>(biomes);
	}

	public @NonNull SchematicBiome[][][] biomeData() {
		return biomes;
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

	public @Nullable Integer dataVersion() {
		return dataVersion;
	}

	/**
	 * @deprecated Use {@link SpongeSchematic#dataVersion()} instead
	 */
	@Deprecated
	public @Nullable Integer getDataVersion() {
		return dataVersion();
	}

	public @NonNull Metadata metadata() {
		return metadata;
	}

	/**
	 * @deprecated Use {@link SpongeSchematic#metadata()} instead
	 */
	@Deprecated
	public @NonNull Metadata getMetadata() {
		return metadata();
	}

	@Override
	public String toString() {
		return "SchematicSponge[" +
				"name=" + name() +
				", version=" + version +
				", width=" + width +
				", height=" + height +
				", length=" + length +
				']';
	}


	public static class Metadata {
		/**
		 * The name of the schematic.
		 */
		public final @Nullable String name;

		/**
		 * The name of the author of the schematic.
		 */
		public final @Nullable String author;

		/**
		 * The date that this schematic was created on.
		 */
		public final @Nullable LocalDateTime date;

		/**
		 * An array of mod IDs.
		 */
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

		public Metadata() {
			this(null, null, null, new String[0], Collections.emptyMap());
		}

		@Override
		public String toString() {
			return "Metadata[" +
					"name='" + name + '\'' +
					", author='" + author + '\'' +
					", date=" + date +
					", requiredMods=" + Arrays.toString(requiredMods) +
					", extra=" + extra +
					']';
		}
	}

	public static class Builder {
		private Integer version;
		private Integer dataVersion;
		private Metadata metadata;
		private Integer width;
		private Integer height;
		private Integer length;
		private int[] offset = DEFAULT_OFFSET;
		private SchematicBlock[][][] blocks = new SchematicBlock[0][0][0];
		private SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[0];
		private SchematicEntity[] entities = new SchematicEntity[0];
		private SchematicBiome[][][] biomes = new SchematicBiome[0][0][0];

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

		public Builder blocks(@NonNull SchematicBlock[][][] blocks) {
			this.blocks = blocks;
			return this;
		}

		public Builder blockEntities(@NonNull SchematicBlockEntity[] blockEntities) {
			this.blockEntities = blockEntities;
			return this;
		}

		public Builder entities(@NonNull SchematicEntity[] entities) {
			this.entities = entities;
			return this;
		}

		public Builder biomes(@NonNull SchematicBiome[][][] biomes) {
			this.biomes = biomes;
			return null;
		}

		public SpongeSchematic build() {
			if (version == null)
				throw new SchematicBuilderException("version must be set");
			if (width == null)
				throw new SchematicBuilderException("width must be set");
			if (height == null)
				throw new SchematicBuilderException("height must be set");
			if (length == null)
				throw new SchematicBuilderException("length must be set");
			if (blocks == null)
				throw new SchematicBuilderException("blocks must be set");

			return new SpongeSchematic(version, dataVersion, metadata, width, height, length, offset, blocks, blockEntities, entities, biomes);
		}
	}
}
