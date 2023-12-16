package net.sandrohc.schematic4j.parser;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.IntTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.NumberTag;
import net.sandrohc.schematic4j.nbt.tag.StringTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SpongeSchematic;
import net.sandrohc.schematic4j.schematic.types.SchematicBiome;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;

import static java.util.stream.Collectors.toMap;
import static net.sandrohc.schematic4j.utils.DateUtils.epochToDate;
import static net.sandrohc.schematic4j.utils.TagUtils.getByteArray;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompound;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompoundList;
import static net.sandrohc.schematic4j.utils.TagUtils.getInt;
import static net.sandrohc.schematic4j.utils.TagUtils.getIntArray;
import static net.sandrohc.schematic4j.utils.TagUtils.getShort;
import static net.sandrohc.schematic4j.utils.TagUtils.unwrap;

/**
 * Parses Sponge Schematic files (<i>.schem</i>).
 * <p>
 * The SCHEM format replaced the .SCHEMATIC format in versions 1.13+ of Minecraft Java Edition.
 *
 * <h2>Specification</h2>
 * <ul>
 *     <li><a href="https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md">Version 1</a></li>
 *     <li><a href="https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md">Version 2</a></li>
 *     <li><a href="https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-3.md">Version 3</a></li>
 * </ul>
 */
public class SpongeParser implements Parser {

	public static final String NBT_VERSION = "Version";
	public static final String NBT_DATA_VERSION = "DataVersion";
	public static final String NBT_METADATA = "Metadata";
	public static final String NBT_METADATA_NAME = "Name";
	public static final String NBT_METADATA_AUTHOR = "Author";
	public static final String NBT_METADATA_DATE = "Date";
	public static final String NBT_METADATA_REQUIRED_MODS = "RequiredMods";
	public static final String NBT_WIDTH = "Width";
	public static final String NBT_HEIGHT = "Height";
	public static final String NBT_LENGTH = "Length";
	public static final String NBT_OFFSET = "Offset";
	public static final String NBT_PALETTE = "Palette";
	public static final String NBT_PALETTE_MAX = "PaletteMax";
	public static final String NBT_BLOCK_DATA = "BlockData";
	public static final String NBT_BLOCK_ENTITIES = "BlockEntities";
	public static final String NBT_BLOCK_ENTITIES_ID = "Id";
	public static final String NBT_BLOCK_ENTITIES_POS = "Pos";
	public static final String NBT_TILE_ENTITIES = "TileEntities";
	public static final String NBT_BIOME_PALETTE = "BiomePalette";
	public static final String NBT_BIOME_PALETTE_MAX = "BiomePaletteMax";
	public static final String NBT_BIOME_DATA = "BiomeData";
	public static final String NBT_ENTITIES = "Entities";
	public static final String NBT_ENTITIES_ID = "Id";
	public static final String NBT_ENTITIES_POS = "Pos";
	public static final String NBT_ENTITIES_EXTRA = "Extra";
	public static final String NBT_V3_BLOCKS = "Blocks";
	public static final String NBT_V3_BIOMES = "Biomes";
	public static final String NBT_V3_DATA = "Data";

	private static final Logger log = LoggerFactory.getLogger(SpongeParser.class);

	@Override
	public @NonNull Schematic parse(@Nullable CompoundTag nbt) throws ParsingException {
		log.debug("Parsing Sponge schematic");

		final SpongeSchematic schematic = new SpongeSchematic();
		if (nbt == null) {
			return schematic;
		}

		parseVersion(nbt, schematic);
		parseDataVersion(nbt, schematic);
		parseMetadata(nbt, schematic);
		parseOffset(nbt, schematic);
		parseBlocks(nbt, schematic);
		parseBlockEntities(nbt, schematic);
		parseEntities(nbt, schematic);
		parseBiomes(nbt, schematic);

		return schematic;
	}

	protected void parseVersion(CompoundTag rootTag, SpongeSchematic schematic) {
		// Default to version 1 if none provided
		schematic.version = getInt(rootTag, NBT_VERSION).orElse(1);
		if (schematic.version > 3) {
			log.warn("Sponge Schematic version {} is not officially supported. Use at your own risk", schematic.version);
		}
	}

	protected void parseDataVersion(CompoundTag root, SpongeSchematic schematic) {
		log.trace("Parsing data version");

		// Data Version is optional for v1, but required for v2 and v3
		getInt(root, NBT_DATA_VERSION).ifPresent(dataVersion -> schematic.dataVersion = dataVersion);
	}

	protected void parseMetadata(CompoundTag root, SpongeSchematic schematic) {
		final CompoundTag metadataTag = getCompound(root, NBT_METADATA).orElse(null);
		if (metadataTag == null) {
			log.debug("No metadata found");
			return;
		}

		log.trace("Parsing metadata");
		for (final Entry<String, Tag<?>> entry : metadataTag) {
			final String key = entry.getKey();
			final Tag<?> tag = entry.getValue();
			switch (key) {
				case NBT_METADATA_NAME:
					schematic.metadata.name = ((StringTag) tag).getValue();
					break;
				case NBT_METADATA_AUTHOR:
					schematic.metadata.author = ((StringTag) tag).getValue();
					break;
				case NBT_METADATA_DATE:
					long dateEpochMillis = ((NumberTag<?>) tag).asLong();
					schematic.metadata.date = epochToDate(dateEpochMillis);
					break;
				case NBT_METADATA_REQUIRED_MODS:
					final ListTag<StringTag> stringTags = ((ListTag<?>) tag).asStringTagList();
					schematic.metadata.requiredMods = StreamSupport.stream(stringTags.spliterator(), false)
							.map(StringTag::getValue)
							.toArray(String[]::new);
					break;
				default:
					schematic.metadata.extra.put(key, unwrap(tag));
			}
		}
	}

	protected void parseOffset(CompoundTag root, SpongeSchematic schematic) {
		log.trace("Parsing offset");
		getIntArray(root, NBT_OFFSET).ifPresent(offset -> {
			schematic.offset = SchematicBlockPos.from(offset);
			log.debug("Loaded offset is {}", schematic.offset);
		});
	}

	protected void parseBlocks(CompoundTag root, SpongeSchematic schematic) {
		log.trace("Parsing blocks");

		schematic.width = (int) getShort(root, NBT_WIDTH).orElse((short) 0);
		schematic.height = (int) getShort(root, NBT_HEIGHT).orElse((short) 0);
		schematic.length = (int) getShort(root, NBT_LENGTH).orElse((short) 0);

		final CompoundTag blocksTag = getBlocksTag(root, schematic.version);
		if (blocksTag == null) {
			log.debug("No block container");
			return;
		}

		// Load the block palette
		getCompound(blocksTag, NBT_PALETTE).ifPresent(blockPaletteTag -> {
			log.trace("Block palette size: {}", blockPaletteTag.size());

			final int paletteMax = blocksTag.getInt(NBT_PALETTE_MAX);
			if (paletteMax > 0 && blockPaletteTag.size() != paletteMax) {
				log.warn("Palette actual size does not match expected size. Expected {} but got {}", paletteMax, blockPaletteTag.size());
			}

			final SchematicBlock[] blockPalette = new SchematicBlock[blockPaletteTag.size()];
			for (Entry<String, Tag<?>> entry : blockPaletteTag) {
				final SchematicBlock block = new SchematicBlock(entry.getKey());
				final int index = ((IntTag) entry.getValue()).asInt();
				blockPalette[index] = block;
			}

			schematic.blockPalette = blockPalette;
		});

		// Load the block data
		getByteArray(blocksTag, schematic.version >= 3 ? NBT_V3_DATA : NBT_BLOCK_DATA).ifPresent(blockDataRaw -> {
			int[] blockData = new int[schematic.width * schematic.height * schematic.length];

			// --- Uses code from https://github.com/SpongePowered/Sponge/blob/aa2c8c53b4f9f40297e6a4ee281bee4f4ce7707b/src/main/java/org/spongepowered/common/data/persistence/SchematicTranslator.java#L147-L175
			int index = 0;
			int i = 0;
			while (i < blockDataRaw.length) {
				int value = 0;
				int varintLength = 0;
				while (true) {
					value |= (blockDataRaw[i] & 127) << (varintLength++ * 7);
					if (varintLength > 5) {
						log.warn("VarInt for block index is too big; probably corrupted data");
						continue;
					}
					if ((blockDataRaw[i] & 128) != 128) {
						i++;
						break;
					}
					i++;
				}

				blockData[index] = value;
				index++;
			}
			// ---

			schematic.blocks = blockData;
			log.debug("Loaded {} blocks", blockData.length);
		});

	}

	protected static CompoundTag getBlocksTag(CompoundTag root, int version) {
		if (version >= 3) {
			return root.getCompoundTag(NBT_V3_BLOCKS);
		} else {
			return root;
		}
	}

	protected void parseBlockEntities(CompoundTag root, SpongeSchematic schematic) {
		final CompoundTag blocksTag = getBlocksTag(root, schematic.version);
		final String blockEntitiesTagName = schematic.version == 1 ? NBT_TILE_ENTITIES : NBT_BLOCK_ENTITIES;
		final Optional<ListTag<CompoundTag>> blockEntitiesListTag = getCompoundList(blocksTag, blockEntitiesTagName);

		if (!blockEntitiesListTag.isPresent()) {
			log.trace("No block entities found");
			return;
		}

		log.trace("Parsing block entities");
		final ListTag<CompoundTag> blockEntitiesTag = blockEntitiesListTag.get();
		final SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[blockEntitiesTag.size()];

		int i = 0;
		for (CompoundTag blockEntityTag : blockEntitiesTag) {
			final String id = blockEntityTag.getString(NBT_BLOCK_ENTITIES_ID);
			final SchematicBlockPos pos = getIntArray(blockEntityTag, NBT_BLOCK_ENTITIES_POS)
					.map(SchematicBlockPos::from)
					.orElse(SchematicBlockPos.ZERO);
			final Map<String, Object> data = blockEntityTag.entrySet().stream()
					.filter(tag -> !tag.getKey().equals(NBT_ENTITIES_ID) && !tag.getKey().equals(NBT_ENTITIES_POS))
					.collect(toMap(Entry::getKey, e -> unwrap(e.getValue()), (a, b) -> b, TreeMap::new));

			blockEntities[i] = new SchematicBlockEntity(id, pos, data);
			i++;
		}

		schematic.blockEntities = blockEntities;
		log.debug("Loaded {} block entities", blockEntities.length);
	}

	@SuppressWarnings("unchecked")
	protected void parseEntities(CompoundTag root, SpongeSchematic schematic) {
		final ListTag<CompoundTag> entitiesTag = getCompoundList(root, NBT_ENTITIES).orElse(null);
		if (entitiesTag == null) {
			log.trace("No entities found");
			return;
		}

		log.trace("Parsing entities");
		final SchematicEntity[] entities = new SchematicEntity[entitiesTag.size()];

		int i = 0;
		for (CompoundTag entityTag : entitiesTag) {
			final SchematicEntity entity = SchematicEntity.fromNbt(entityTag);
			if (entity != null) {
				// Entity NBT stored in v2
				final Object entityExtra = entity.data.get(NBT_ENTITIES_EXTRA);
				if (entityExtra instanceof Map<?, ?>) {
					entity.data.remove(NBT_ENTITIES_EXTRA);
					entity.data.putAll(((Map<String, ?>) entityExtra));
				}

				// Entity NBT stored in v3
				final Object entityData = entity.data.get(NBT_V3_DATA);
				if (entityData instanceof Map<?, ?>) {
					entity.data.remove(NBT_V3_DATA);
					entity.data.putAll(((Map<String, ?>) entityData));
				}
			}

			entities[i++] = entity;
		}

		schematic.entities = entities;
		log.debug("Loaded {} entities", entities.length);
	}

	protected void parseBiomes(CompoundTag root, SpongeSchematic schematic) {
		log.trace("Parsing biomes");

		final int version = schematic.version;

		final CompoundTag biomesTag = getBiomesTag(root, version);
		if (biomesTag == null) {
			log.trace("Did not have biome data");
			return;
		}

		// Load the palette
		getCompound(biomesTag, version >= 3 ? NBT_PALETTE : NBT_BIOME_PALETTE).ifPresent(biomePaletteTag -> {
			log.trace("Biome palette size: {}", biomePaletteTag.size());

			final int paletteMax = biomesTag.getInt(NBT_BIOME_PALETTE_MAX);
			if (paletteMax > 0 && biomePaletteTag.size() != paletteMax) {
				log.warn("Biome palette actual size does not match expected size. Expected {} but got {}", paletteMax, biomePaletteTag.size());
			}

			final SchematicBiome[] biomePalette = new SchematicBiome[biomePaletteTag.size()];
			for (Entry<String, Tag<?>> entry : biomePaletteTag) {
				final SchematicBiome biome = new SchematicBiome(entry.getKey());
				final int index = ((IntTag) entry.getValue()).asInt();
				biomePalette[index] = biome;
			}

			schematic.biomePalette = biomePalette;
		});

		// Load the biome data
		getByteArray(biomesTag, version >= 3 ? NBT_V3_DATA : NBT_BIOME_DATA).ifPresent(biomeDataRaw -> {
			final int biomeWidth = schematic.width;
			final int biomeHeight = version >= 3 ? schematic.height : 1;
			final int biomeLength = schematic.length;

			int[] biomeData = new int[biomeWidth * biomeHeight * biomeLength];

			int index = 0;
			int i = 0;
			while (i < biomeDataRaw.length) {
				int value = 0;
				int varintLength = 0;
				while (true) {
					value |= (biomeDataRaw[i] & 127) << (varintLength++ * 7);
					if (varintLength > 5) {
						log.warn("VarInt for biome index is too big; probably corrupted data");
						continue;
					}
					if ((biomeDataRaw[i] & 128) != 128) {
						i++;
						break;
					}
					i++;
				}

				biomeData[index] = value;
				index++;
			}

			schematic.biomes = biomeData;
			log.debug("Loaded {} biomes", biomeWidth * biomeLength);
		});
	}

	protected static CompoundTag getBiomesTag(CompoundTag root, int version) {
		if (version >= 3) {
			return root.getCompoundTag(NBT_V3_BIOMES);
		} else {
			return root;
		}
	}

	@Override
	public String toString() {
		return "SpongeSchematicParser";
	}
}
