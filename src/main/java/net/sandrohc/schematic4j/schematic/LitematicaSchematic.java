package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.sandrohc.schematic4j.builder.SchematicExporter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.schematic.types.Pair;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;

import static net.sandrohc.schematic4j.schematic.types.SchematicBlock.AIR;

/**
 * A Litematica schematic. Read more about it at
 * <a href="https://github.com/maruohon/litematica/issues/53#issuecomment-520279558">https://github.com/maruohon/litematica/issues/53#issuecomment-520279558</a>.
 */
public class LitematicaSchematic implements Schematic {

	/**
	 * The schematic format version being used.
	 **/
	public int version = 1;

	/**
	 * Specifies the data version of Minecraft that was used to create the schematic.
	 * <p>
	 * This is to allow for block and entity data to be validated and auto-converted from older versions.
	 * This is dependent on the Minecraft version, e.g. Minecraft 1.12.2's data version is
	 * <a href="https://minecraft.gamepedia.com/1.12.2">1343</a>.
	 */
	public @Nullable Integer minecraftDataVersion;

	/**
	 * The optional metadata about the schematic.
	 */
	public @NonNull Metadata metadata = new Metadata();

	/**
	 * The regions that compose this schematic. They can be thought of as their own little schematics.
	 */
	public Region @NonNull [] regions = new Region[0];

	/**
	 * A Litematica schematic.
	 */
	public LitematicaSchematic() {
	}

	@Override
	public @NonNull SchematicFormat format() {
		return SchematicFormat.LITEMATICA;
	}

	@Override
	public int width() {
		return metadata.enclosingSize != null ? metadata.enclosingSize.x : 0;
	}

	@Override
	public int height() {
		return metadata.enclosingSize != null ? metadata.enclosingSize.y : 0;
	}

	@Override
	public int length() {
		return metadata.enclosingSize != null ? metadata.enclosingSize.z : 0;
	}

	@Override
	public @NonNull SchematicBlockPos offset() {
		return SchematicBlockPos.ZERO;
	}

	@Override
	public @NonNull SchematicBlock block(int x, int y, int z) {
		for (Region region : regions) {
			if ((region.position.x <= x && region.position.x + region.size.x > x)
					&& (region.position.y <= y && region.position.y + region.size.y > y)
					&& (region.position.z <= z && region.position.z + region.size.z > z)) {

				final int blockStateIndex = region.posToIndex(x, y, z);
				final int paletteIndex = region.blockStates[blockStateIndex];
				return region.blockStatePalette[paletteIndex];
			}
		}

		return AIR; // outside bounds
	}

	@Override
	public @NonNull Stream<Pair<SchematicBlockPos, SchematicBlock>> blocks() {
		return Arrays.stream(regions).flatMap(region -> IntStream.range(0, region.blockStates.length).mapToObj(idx -> {
			final SchematicBlockPos pos = region.indexToPos(idx);
			final int paletteIdx = region.blockStates[idx];
			final SchematicBlock block = region.blockStatePalette[paletteIdx];
			return new Pair<>(pos, block);
		}));
	}

	@Override
	public @NonNull Stream<SchematicBlockEntity> blockEntities() {
		return Arrays.stream(regions).flatMap(r -> Arrays.stream(r.blockEntities));
	}

	@Override
	public @NonNull Stream<SchematicEntity> entities() {
		return Arrays.stream(regions).flatMap(r -> Arrays.stream(r.entities));
	}

	/**
	 * The regions that compose this schematic. They can be thought of as their own little schematics.
	 *
	 * @return The regions
	 */
	public @NonNull Region[] regions() {
		return regions;
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
		return metadata.timeCreated;
	}

	@Override
	public SchematicExporter export() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Specifies the data version of Minecraft that was used to create the schematic.
	 * <p>
	 * This is to allow for block and entity data to be validated and auto-converted from older versions.
	 * This is dependent on the Minecraft version, e.g. Minecraft 1.12.2's data version is
	 * <a href="https://minecraft.gamepedia.com/1.12.2">1343</a>.
	 *
	 * @return The Minecraft data version
	 */
	public @Nullable Integer dataVersion() {
		return minecraftDataVersion;
	}

	/**
	 * The optional metadata about the schematic.
	 *
	 * @return The schematic metadata
	 */
	public @NonNull Metadata metadata() {
		return metadata;
	}

	@Override
	public String toString() {
		return "SchematicLitematica[" +
				"name=" + name() +
				", version=" + version +
				", dataVersion=" + minecraftDataVersion +
				", metadata=" + metadata +
				", regions=" + Arrays.toString(regions) +
				']';
	}

	/**
	 * The schematic metadata.
	 */
	public static class Metadata {
		/**
		 * The name of the schematic.
		 */
		public @Nullable String name;

		/**
		 * The description of the schematic.
		 */
		public @Nullable String description;

		/**
		 * The name of the author of the schematic.
		 */
		public @Nullable String author;

		/**
		 * The date that this schematic was created on.
		 */
		public @Nullable LocalDateTime timeCreated;

		/**
		 * The date that this schematic was modified on.
		 */
		public @Nullable LocalDateTime timeModified;

		/**
		 * The size of the schematic including all regions.
		 */
		public @Nullable SchematicBlockPos enclosingSize;

		/**
		 * The number of regions inside this schematic.
		 */
		public @Nullable Integer regionCount;

		/**
		 * The total number of blocks from all the regions that compose this schematic. Does not include air blocks.
		 */
		public @Nullable Long totalBlocks;

		/**
		 * The total volume of blocks from all the regions that compose this schematic. This includes air blocks.
		 */
		public @Nullable Long totalVolume;

		/**
		 * Schematic thumbnail, if available.
		 */
		public int @Nullable [] previewImageData;

		/**
		 * Extra metadata not represented in the specification.
		 */
		public @NonNull Map<String, Object> extra = new TreeMap<>();

		public Metadata() {
		}

		@Override
		public String toString() {
			return "Metadata[" +
					"name='" + name + '\'' +
					", author='" + author + '\'' +
					", timeCreated=" + timeCreated +
					", timeModified=" + timeModified +
					']';
		}
	}

	/**
	 * A schematic region.
	 * <p>
	 * It can be thought of as its own little schematics, since it contains a unique size and block palette.
	 */
	public static class Region {
		/**
		 * The region name.
		 */
		public @Nullable String name;

		/**
		 * The region position in reference to the schematic origin at (0, 0, 0).
		 */
		public @NonNull SchematicBlockPos position = SchematicBlockPos.ZERO;

		/**
		 * The region size.
		 */
		public @NonNull SchematicBlockPos size = SchematicBlockPos.ZERO;

		/**
		 * The encoded (but unpacked) block states. Each index represents a block position and each value represents
		 * an index in the {@link Region#blockStatePalette}.
		 * <p>
		 * Each index is encoded as {@code x + (z * regionSize.x) (y * regionSize.x * regionSize.z)} and can be decoded as follows:
		 * <pre>
		 * int x = index % regionSize.x;
		 * int z = (index / regionSize.x) % regionSize.z;
		 * int y = index / (regionSize.x * regionSize.z);
		 * </pre>
		 *
		 * @see Region#indexToPos(int) to convert an index to a block position
		 * @see Region#posToIndex(int, int, int) to convert a block position to an index
		 */
		public int @NonNull [] blockStates = new int[0];

		/**
		 * The block state palette. Each entry in the array represents a unique block state in this schematic region.
		 * <p>
		 * The values in {@link Region#blockStates} are indices to this array.
		 */
		public SchematicBlock @NonNull [] blockStatePalette = new SchematicBlock[0];

		/**
		 * The block/tile entities in this schematic region.
		 */
		public SchematicBlockEntity @NonNull [] blockEntities = new SchematicBlockEntity[0];

		/**
		 * The entities in this schematic region.
		 */
		public SchematicEntity @NonNull [] entities = new SchematicEntity[0];

		/**
		 * The list of blocks with pending tick calculations.
		 */
		public PendingTicks @NonNull [] pendingBlockTicks = new PendingTicks[0];

		/**
		 * The list of fluids with pending tick calculations.
		 */
		public PendingTicks @NonNull [] pendingFluidTicks = new PendingTicks[0];

		public Region() {
		}

		public int posToIndex(int x, int y, int z) {
			return x + (z * size.x) + (y * size.x * size.z);
		}

		public @NonNull SchematicBlockPos indexToPos(int index) {
			final int x = index % size.x;
			final int z = (index / size.x) % size.z;
			final int y = index / (size.x * size.z);
			return new SchematicBlockPos(x, y, z);
		}

		/**
		 * The region name.
		 *
		 * @return The region name
		 */
		public @Nullable String name() {
			return name;
		}

		/**
		 * The region position in reference to the schematic origin at (0, 0, 0).
		 *
		 * @return The region position
		 */
		public @NonNull SchematicBlockPos position() {
			return position;
		}

		/**
		 * The region size.
		 *
		 * @return The region size
		 */
		public @NonNull SchematicBlockPos size() {
			return size;
		}

		/**
		 * The encoded (but unpacked) block states. Each index represents a block position and each value represents
		 * an index in the {@link Region#blockStatePalette}.
		 * <p>
		 * Each index is encoded as {@code x + (z * regionSize.x) (y * regionSize.x * regionSize.z)} and can be decoded as follows:
		 * <pre>
		 * int x = index % regionSize.x;
		 * int z = (index / regionSize.x) % regionSize.z;
		 * int y = index / (regionSize.x * regionSize.z);
		 * </pre>
		 *
		 * @return The encoded (but unpacked) block states
		 * @see Region#indexToPos(int) to convert an index to a block position
		 * @see Region#posToIndex(int, int, int) to convert a block position to an index
		 */
		public int @NonNull [] blockStates() {
			return blockStates;
		}

		/**
		 * The block state palette. Each entry in the array represents a unique block state in this schematic region.
		 * <p>
		 * The values in {@link Region#blockStates} are indices to this array.
		 *
		 * @return The block state palette
		 */
		public SchematicBlock @NonNull [] blockStatePalette() {
			return blockStatePalette;
		}

		/**
		 * The block/tile entities in this schematic region.
		 *
		 * @return The block entities in this region
		 */
		public SchematicBlockEntity @NonNull [] blockEntities() {
			return blockEntities;
		}

		/**
		 * The entities in this schematic region.
		 *
		 * @return The entities in this region
		 */
		public SchematicEntity @NonNull [] entities() {
			return entities;
		}

		/**
		 * The list of blocks with pending tick calculations.
		 *
		 * @return The blocks pending ticks
		 */
		public PendingTicks @NonNull [] pendingBlockTicks() {
			return pendingBlockTicks;
		}

		/**
		 * The list of fluids with pending tick calculations.
		 *
		 * @return The fluids pending ticks
		 */
		public PendingTicks @NonNull [] pendingFluidTicks() {
			return pendingFluidTicks;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Region region = (Region) o;

			if (!Objects.equals(name, region.name)) return false;
			if (!position.equals(region.position)) return false;
			return size.equals(region.size);
		}

		@Override
		public int hashCode() {
			int result = name != null ? name.hashCode() : 0;
			result = 31 * result + position.hashCode();
			result = 31 * result + size.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "Region[" +
					"name='" + name + '\'' +
					", position=" + position +
					", size=" + size +
					']';
		}
	}

	/**
	 * Represents a block or fluid pending tick calculations.
	 */
	public static class PendingTicks {
		/**
		 * The pending tick priority.
		 */
		public @Nullable Integer priority;

		/**
		 * The sub-tick.
		 */
		public @Nullable Long subTick;

		/**
		 * The time.
		 */
		public @Nullable Integer time;

		/**
		 * The X coordinate inside the region it is found.
		 */
		public @Nullable Integer x;

		/**
		 * The Y coordinate inside the region it is found.
		 */
		public @Nullable Integer y;

		/**
		 * The Z coordinate inside the region it is found.
		 */
		public @Nullable Integer z;
	}
}
