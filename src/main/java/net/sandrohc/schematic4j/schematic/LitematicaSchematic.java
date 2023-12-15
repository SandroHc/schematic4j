package net.sandrohc.schematic4j.schematic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;

import static net.sandrohc.schematic4j.schematic.types.SchematicBlock.AIR;

/**
 * A Litematica schematic. Read more about it at <a href="https://github.com/maruohon/litematica/issues/53#issuecomment-520279558">https://github.com/maruohon/litematica/issues/53#issuecomment-520279558</a>.
 */
public class LitematicaSchematic implements Schematic {

	/**
	 * The schematic format version being used.
	 **/
	public int version = 1;

	/**
	 * Specifies the data version of Minecraft that was used to create the schematic. This is to allow for block and
	 * entity data to be validated and auto-converted from older versions. This is dependent on the Minecraft version,
	 * e.g. Minecraft 1.12.2's data version is <a href="https://minecraft.gamepedia.com/1.12.2">1343</a>.
	 */
	public @Nullable Integer minecraftDataVersion;

	/**
	 * The optional metadata about the schematic.
	 */
	public @NonNull Metadata metadata = new Metadata();

	public Region @NonNull [] regions = new Region[0];

	public LitematicaSchematic() {
	}

	public LitematicaSchematic(int version, @Nullable Integer minecraftDataVersion, @Nullable Metadata metadata,
							   Region @Nullable [] regions) {
		this.version = version;
		this.minecraftDataVersion = minecraftDataVersion;
		this.metadata = metadata != null ? metadata : new Metadata();
		this.regions = regions != null ? regions : new Region[0];
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
	public int[] offset() {
		return new int[]{0, 0, 0};
	}

	@Override
	public @Nullable SchematicBlock block(int x, int y, int z) {
		for (Region region : regions) {
			if (region.position != null
					&& region.size != null
					&& (region.position.x <= x && region.position.x + region.size.x > x)
					&& (region.position.y <= y && region.position.y + region.size.y > y)
					&& (region.position.z <= z && region.position.z + region.size.z > z)) {

				final int sizeLayer = region.size.x * region.size.z;
				final int blockStateIndex = (y * sizeLayer) + z * region.size.x + x;
				final int paletteIndex = region.blockStates[blockStateIndex];
				return region.blockStatePalette[paletteIndex];
			}
		}

		return AIR; // outside bounds
	}

	@Override
	public @NonNull Iterator<SchematicBlock> blocks() {
		return Arrays.stream(regions).flatMap(r -> Arrays.stream(r.blockStates).mapToObj(idx -> r.blockStatePalette[idx])).iterator();
	}

	@Override
	public @NonNull Iterator<SchematicBlockEntity> blockEntities() {
		return Arrays.stream(regions).flatMap(r -> Arrays.stream(r.blockEntities)).iterator();
	}

	@Override
	public @NonNull Iterator<SchematicEntity> entities() {
		return Arrays.stream(regions).flatMap(r -> Arrays.stream(r.entities)).iterator();
	}

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

	public @Nullable Integer dataVersion() {
		return minecraftDataVersion;
	}

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

		public @Nullable SchematicBlockPos enclosingSize;

		public @Nullable Integer regionCount;

		public @Nullable Long totalBlocks;

		public @Nullable Long totalVolume;

		public int @Nullable [] previewImageData;

		public @NonNull Map<String, Object> extra;

		public Metadata(@Nullable String name, @Nullable String description, @Nullable String author,
						@Nullable LocalDateTime timeCreated, @Nullable LocalDateTime timeModified,
						@Nullable SchematicBlockPos enclosingSize, @Nullable Integer regionCount,
						@Nullable Long totalBlocks, @Nullable Long totalVolume, int @Nullable [] previewImageData,
						@Nullable Map<String, Object> extra) {
			this.name = name;
			this.description = description;
			this.author = author;
			this.timeCreated = timeCreated;
			this.timeModified = timeModified;
			this.enclosingSize = enclosingSize;
			this.regionCount = regionCount;
			this.totalBlocks = totalBlocks;
			this.totalVolume = totalVolume;
			this.previewImageData = previewImageData;
			this.extra = extra != null ? extra : new TreeMap<>();
		}

		public Metadata() {
			this(null, null, null, null, null, null, null, null, null, null, Collections.emptyMap());
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

	public static class Region {
		public @Nullable String name;
		public @Nullable SchematicBlockPos position;
		public @Nullable SchematicBlockPos size;
		public int @NonNull [] blockStates;
		public SchematicBlock @NonNull [] blockStatePalette;
		public SchematicBlockEntity @NonNull [] blockEntities;
		public SchematicEntity @NonNull [] entities;
		public PendingTicks @NonNull [] pendingBlockTicks;
		public PendingTicks @NonNull [] pendingFluidTicks;

		public Region(@Nullable String name, @Nullable SchematicBlockPos position, @Nullable SchematicBlockPos size,
					  int[] blockStates, @Nullable SchematicBlock[] blockStatePalette,
					  @Nullable SchematicBlockEntity[] blockEntities, @Nullable SchematicEntity[] entities,
					  @Nullable PendingTicks[] pendingBlockTicks, @Nullable PendingTicks[] pendingFluidTicks) {
			this.name = name;
			this.position = position;
			this.size = size;
			this.blockStates = blockStates != null ? blockStates : new int[0];
			this.blockStatePalette = blockStatePalette != null ? blockStatePalette : new SchematicBlock[0];
			this.blockEntities = blockEntities != null ? blockEntities : new SchematicBlockEntity[0];
			this.entities = entities != null ? entities : new SchematicEntity[0];
			this.pendingBlockTicks = pendingBlockTicks != null ? pendingBlockTicks : new PendingTicks[0];
			this.pendingFluidTicks = pendingFluidTicks != null ? pendingFluidTicks : new PendingTicks[0];
		}

		public Region() {
			this(null, null, null, null, null, null, null, null, null);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Region region = (Region) o;

			if (!Objects.equals(name, region.name)) return false;
			if (!Objects.equals(position, region.position)) return false;
			return Objects.equals(size, region.size);
		}

		@Override
		public int hashCode() {
			int result = name != null ? name.hashCode() : 0;
			result = 31 * result + (position != null ? position.hashCode() : 0);
			result = 31 * result + (size != null ? size.hashCode() : 0);
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

	public static class PendingTicks {
		public @Nullable Integer priority;
		public @Nullable Long subTick;
		public @Nullable Integer time;
		public @Nullable Integer x;
		public @Nullable Integer y;
		public @Nullable Integer z;
	}
}
