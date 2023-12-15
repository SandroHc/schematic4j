package net.sandrohc.schematic4j.parser;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.IntArrayTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.NumberTag;
import net.sandrohc.schematic4j.nbt.tag.StringTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;
import net.sandrohc.schematic4j.schematic.LitematicaSchematic.PendingTicks;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.LitematicaSchematic;
import net.sandrohc.schematic4j.schematic.LitematicaSchematic.Metadata;
import net.sandrohc.schematic4j.schematic.LitematicaSchematic.Region;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;

import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.SIZED;
import static net.sandrohc.schematic4j.utils.DateUtils.epochToDate;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompound;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompoundList;
import static net.sandrohc.schematic4j.utils.TagUtils.getInt;
import static net.sandrohc.schematic4j.utils.TagUtils.getLong;
import static net.sandrohc.schematic4j.utils.TagUtils.getLongArray;
import static net.sandrohc.schematic4j.utils.TagUtils.unwrap;

/**
 * Parses Litematica files (<i>.litematic</i>).
 * <p>
 * Specification:<br>
 * - <a href="https://github.com/maruohon/litematica/issues/53#issuecomment-520279558">https://github.com/maruohon/litematica/issues/53#issuecomment-520279558</a>
 * - <a href="https://github.com/maruohon/litematica/blob/pre-rewrite/fabric/1.20.x/src/main/java/fi/dy/masa/litematica/schematic/LitematicaSchematic.java">https://github.com/maruohon/litematica/blob/pre-rewrite/fabric/1.20.x/src/main/java/fi/dy/masa/litematica/schematic/LitematicaSchematic.java</a>
 */
public class LitematicaParser implements Parser {

	private static final Logger log = LoggerFactory.getLogger(LitematicaParser.class);

	public static final String NBT_MINECRAFT_DATA_VERSION = "MinecraftDataVersion";
	public static final String NBT_VERSION = "Version";

	public static final String NBT_METADATA = "Metadata";
	public static final String NBT_METADATA_NAME = "Name";
	public static final String NBT_METADATA_DESCRIPTION = "Description";
	public static final String NBT_METADATA_AUTHOR = "Author";
	public static final String NBT_METADATA_TIME_CREATED = "TimeCreated";
	public static final String NBT_METADATA_TIME_MODIFIED = "TimeModified";
	public static final String NBT_METADATA_ENCLOSING_SIZE = "EnclosingSize";
	public static final String NBT_METADATA_REGION_COUNT = "RegionCount";
	public static final String NBT_METADATA_TOTAL_BLOCKS = "TotalBlocks";
	public static final String NBT_METADATA_TOTAL_VOLUME = "TotalVolume";
	public static final String NBT_METADATA_PREVIEW_IMAGE_DATA = "PreviewImageData";

	public static final String NBT_REGIONS = "Regions";
	public static final String NBT_REGION_POSITION = "Position";
	public static final String NBT_REGION_SIZE = "Size";
	public static final String NBT_REGION_BLOCK_STATES = "BlockStates";
	public static final String NBT_REGION_BLOCK_STATE_PALETTE = "BlockStatePalette";
	public static final String NBT_REGION_TILE_ENTITIES = "TileEntities";
	public static final String NBT_REGION_ENTITIES = "Entities";
	public static final String NBT_REGION_PENDING_BLOCK_TICKS = "PendingBlockTicks";
	public static final String NBT_REGION_PENDING_FLUID_TICKS = "PendingFluidTicks";

	@Override
	public @NonNull Schematic parse(@Nullable CompoundTag nbt) throws ParsingException {
		log.debug("Parsing Litematica schematic");

		final LitematicaSchematic schematic = new LitematicaSchematic();
		if (nbt == null) {
			return schematic;
		}

		parseVersion(nbt, schematic);
		parseMinecraftDataVersion(nbt, schematic);
		parseMetadata(nbt, schematic);
		parseRegions(nbt, schematic);

		return schematic;
	}

	protected void parseVersion(CompoundTag nbt, LitematicaSchematic schematic) {
		// Default to version 1 if none provided
		schematic.version = getInt(nbt, NBT_VERSION).orElse(1);
	}

	protected void parseMinecraftDataVersion(CompoundTag nbt, LitematicaSchematic schematic) {
		getInt(nbt, NBT_MINECRAFT_DATA_VERSION).ifPresent(dataVersion -> schematic.minecraftDataVersion = dataVersion);
	}

	protected void parseMetadata(CompoundTag nbt, LitematicaSchematic schematic) {
		final CompoundTag metadataTag = getCompound(nbt, NBT_METADATA).orElse(null);
		if (metadataTag == null) {
			return;
		}

		final Metadata metadata = new Metadata();

		for (final Map.Entry<String, Tag<?>> entry : metadataTag) {
			final String key = entry.getKey();
			final Tag<?> tag = entry.getValue();
			switch (key) {
				case NBT_METADATA_NAME:
					metadata.name = ((StringTag) tag).getValue();
					break;
				case NBT_METADATA_DESCRIPTION:
					metadata.description = ((StringTag) tag).getValue();
				case NBT_METADATA_AUTHOR:
					metadata.author = ((StringTag) tag).getValue();
					break;
				case NBT_METADATA_TIME_CREATED:
					long timeCreatedEpochMillis = ((NumberTag<?>) tag).asLong();
					metadata.timeCreated = epochToDate(timeCreatedEpochMillis);
					break;
				case NBT_METADATA_TIME_MODIFIED:
					long timeModifiedEpochMillis = ((NumberTag<?>) tag).asLong();
					metadata.timeModified = epochToDate(timeModifiedEpochMillis);
					break;
				case NBT_METADATA_ENCLOSING_SIZE:
					final SchematicBlockPos pos = SchematicBlockPos.from(tag);
					if (pos != null) {
						metadata.enclosingSize = pos;
					}
					break;
				case NBT_METADATA_REGION_COUNT:
					metadata.regionCount = ((NumberTag<?>) tag).asInt();
					break;
				case NBT_METADATA_TOTAL_BLOCKS:
					metadata.totalBlocks = ((NumberTag<?>) tag).asLong();
					break;
				case NBT_METADATA_TOTAL_VOLUME:
					metadata.totalVolume = ((NumberTag<?>) tag).asLong();
					break;
				case NBT_METADATA_PREVIEW_IMAGE_DATA:
					metadata.previewImageData = ((IntArrayTag) tag).getValue();
				default:
					metadata.extra.put(key, unwrap(tag));
			}
		}

		schematic.metadata = metadata;
	}

	protected void parseRegions(CompoundTag nbt, LitematicaSchematic schematic) {
		final CompoundTag regionsTag = nbt.getCompoundTag(NBT_REGIONS);
		if (regionsTag == null) {
			return;
		}

		final Region[] regions = new Region[regionsTag.size()];

		int i = 0;
		for (final Map.Entry<String, Tag<?>> entry : regionsTag) {
			final String regionName = entry.getKey();
			final Tag<?> regionTag = entry.getValue();
			if (regionTag instanceof CompoundTag) {
				final Region region = parseRegion(((CompoundTag) regionTag), regionName);
				regions[i] = region;
			} else {
				log.warn("Invalid region found; expected a compound NBT tag but got {}", regionTag != null ? regionTag.getClass().getName() : null);
			}
			i++;
		}

		schematic.regions = regions;
	}

	protected Region parseRegion(CompoundTag regionTag, String regionName) {
		final Region region = new Region();
		region.name = regionName;

		final SchematicBlockPos position = SchematicBlockPos.from(regionTag.getCompoundTag(NBT_REGION_POSITION));
		if (position != null) {
			region.position = position;
		}

		final SchematicBlockPos size = SchematicBlockPos.from(regionTag.getCompoundTag(NBT_REGION_SIZE));
		if (size != null) {
			region.size = size;
		}

		parseBlocks(regionTag, region);
		parseBlockEntities(regionTag, region);
		parseEntities(regionTag, region);
		parsePendingBlockTicks(regionTag, region);
		parsePendingFluidTicks(regionTag, region);

		return region;
	}

	protected void parseBlocks(CompoundTag regionTag, Region region) {
		final ListTag<CompoundTag> paletteTag = getCompoundList(regionTag, NBT_REGION_BLOCK_STATE_PALETTE).orElse(null);
		if (paletteTag != null) {
			final Spliterator<CompoundTag> paletteSpliterator = Spliterators.spliterator(paletteTag.iterator(), paletteTag.size(), DISTINCT | SIZED | NONNULL | IMMUTABLE);
			region.blockStatePalette = StreamSupport.stream(paletteSpliterator, false)
					.map(LitematicaParser::readBlockPaletteEntry)
					.toArray(SchematicBlock[]::new);
		}

		final long[] packedBlockStates = getLongArray(regionTag, NBT_REGION_BLOCK_STATES).orElse(null);
		if (packedBlockStates != null) {
			final SchematicBlockPos regionSize = getRegionSize(region);
			final int totalVolume = regionSize.x * regionSize.y * regionSize.z;
			final int bitsPerEntry = Math.max(2, Integer.SIZE - Integer.numberOfLeadingZeros(region.blockStatePalette.length - 1));
			final long maxEntryValue = (1L << bitsPerEntry) - 1L;

			final int[] blockStates = new int[totalVolume];

			for (int unpackedIdx = 0; unpackedIdx < totalVolume; unpackedIdx++) {
				long startOffset = (long) unpackedIdx * bitsPerEntry;
				int startArrIndex = (int) (startOffset >> 6); // startOffset / 64
				int endArrIndex = (int) (((unpackedIdx + 1L) * (long) bitsPerEntry - 1L) >> 6);
				int startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64

				final int value;
				if (startArrIndex == endArrIndex) {
					value = (int) (packedBlockStates[startArrIndex] >>> startBitOffset & maxEntryValue);
				} else {
					int endOffset = 64 - startBitOffset;
					value = (int) ((packedBlockStates[startArrIndex] >>> startBitOffset | packedBlockStates[endArrIndex] << endOffset) & maxEntryValue);
				}

				blockStates[unpackedIdx] = value;
			}

			region.blockStates = blockStates;
		}
	}

	@SuppressWarnings("unchecked")
	protected void parseBlockEntities(CompoundTag regionTag, Region region) {
		final ListTag<CompoundTag> blockEntitiesTag = getCompoundList(regionTag, NBT_REGION_TILE_ENTITIES).orElse(null);
		if (blockEntitiesTag == null) {
			return;
		}

		final SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[blockEntitiesTag.size()];

		int i = 0;
		for (final Tag<?> blockEntityTag : blockEntitiesTag) {
			final SchematicBlockEntity blockEntity = SchematicBlockEntity.fromNbt(blockEntityTag);
			if (blockEntity != null) {
				final Object blockEntityNbtData = blockEntity.data.get("TileNBT");
				if (blockEntityNbtData instanceof Map<?, ?>) {
					blockEntity.data.remove("TileNBT");
					blockEntity.data.putAll(((Map<String, ?>) blockEntityNbtData));
				}
			}

			blockEntities[i++] = blockEntity;
		}

		region.blockEntities = blockEntities;
	}

	@SuppressWarnings("unchecked")
	protected void parseEntities(CompoundTag regionTag, Region region) {
		final ListTag<CompoundTag> entitiesTag = getCompoundList(regionTag, NBT_REGION_ENTITIES).orElse(null);
		if (entitiesTag == null) {
			return;
		}

		final SchematicEntity[] entities = new SchematicEntity[entitiesTag.size()];

		int i = 0;
		for (final Tag<?> entityTag : entitiesTag) {
			final SchematicEntity entity = SchematicEntity.fromNbt(entityTag);
			if (entity != null) {
				final Object entityNbtData = entity.data.get("EntityData");
				if (entityNbtData instanceof Map<?, ?>) {
					entity.data.remove("EntityData");
					entity.data.putAll(((Map<String, ?>) entityNbtData));
				}
			}

			entities[i++] = entity;
		}

		region.entities = entities;
	}

	protected void parsePendingBlockTicks(CompoundTag regionTag, Region region) {
		final ListTag<CompoundTag> pendingTicksTag = getCompoundList(regionTag, NBT_REGION_PENDING_BLOCK_TICKS).orElse(null);
		region.pendingBlockTicks = readPendingTicks(pendingTicksTag);
	}

	protected void parsePendingFluidTicks(CompoundTag regionTag, Region region) {
		final ListTag<CompoundTag> pendingTicksTag = getCompoundList(regionTag, NBT_REGION_PENDING_FLUID_TICKS).orElse(null);
		region.pendingBlockTicks = readPendingTicks(pendingTicksTag);
	}

	public static @Nullable SchematicBlock readBlockPaletteEntry(CompoundTag nbt) {
		if (nbt == null) {
			return null;
		}

		final String name = nbt.getString("Name");
		final Map<String, String> states = new TreeMap<>();

		final CompoundTag propertiesTag = nbt.getCompoundTag("Properties");
		if (propertiesTag != null) {
			for (final Map.Entry<String, Tag<?>> entry : propertiesTag) {
				states.put(entry.getKey(), unwrap(entry.getValue()).toString());
			}
		}

		return new SchematicBlock(name, states);
	}

	public static @NonNull SchematicBlockPos getRegionSize(Region region) {
		int posEndRelX = region.size.x;
		int posEndRelY = region.size.y;
		int posEndRelZ = region.size.z;
		posEndRelX = posEndRelX >= 0 ? posEndRelX - 1 : posEndRelX + 1;
		posEndRelY = posEndRelY >= 0 ? posEndRelY - 1 : posEndRelY + 1;
		posEndRelZ = posEndRelZ >= 0 ? posEndRelZ - 1 : posEndRelZ + 1;

		posEndRelX += region.position.x;
		posEndRelY += region.position.y;
		posEndRelZ += region.position.z;

		int posMinX = Math.min(region.position.x, posEndRelX);
		int posMinY = Math.min(region.position.y, posEndRelY);
		int posMinZ = Math.min(region.position.z, posEndRelZ);

		int posMaxX = Math.max(region.position.x, posEndRelX);
		int posMaxY = Math.max(region.position.y, posEndRelY);
		int posMaxZ = Math.max(region.position.z, posEndRelZ);

		return new SchematicBlockPos(posMaxX - posMinX + 1, posMaxY - posMinY + 1, posMaxZ - posMinZ + 1);
	}

	@NonNull
	private static PendingTicks[] readPendingTicks(ListTag<CompoundTag> pendingTicksListTag) {
		if (pendingTicksListTag == null) {
			return new PendingTicks[0];
		}

		final PendingTicks[] pendingTicks = new PendingTicks[pendingTicksListTag.size()];

		int i = 0;
		for (final CompoundTag pendingTicksTag : pendingTicksListTag) {
			final PendingTicks pendingTick = new PendingTicks();
			pendingTick.priority = getInt(pendingTicksTag, "Priority").orElse(null);
			pendingTick.subTick = getLong(pendingTicksTag, "SubTick").orElse(null);
			pendingTick.time = getInt(pendingTicksTag, "Time").orElse(null);
			pendingTick.x = getInt(pendingTicksTag, "x").orElse(null);
			pendingTick.y = getInt(pendingTicksTag, "y").orElse(null);
			pendingTick.z = getInt(pendingTicksTag, "z").orElse(null);

			pendingTicks[i++] = pendingTick;
		}
		return pendingTicks;
	}

	@Override
	public String toString() {
		return "LitematicaParser";
	}
}
