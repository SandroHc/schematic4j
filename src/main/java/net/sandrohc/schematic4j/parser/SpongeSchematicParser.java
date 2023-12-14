package net.sandrohc.schematic4j.parser;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
import net.sandrohc.schematic4j.schematic.SpongeSchematic.Builder;
import net.sandrohc.schematic4j.schematic.SpongeSchematic.Metadata;
import net.sandrohc.schematic4j.schematic.types.SchematicBiome;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicPosDouble;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;

import static java.util.stream.Collectors.toMap;
import static net.sandrohc.schematic4j.utils.DateUtils.epochToDate;
import static net.sandrohc.schematic4j.utils.TagUtils.containsAllTags;
import static net.sandrohc.schematic4j.utils.TagUtils.getByteArrayOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompound;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompoundList;
import static net.sandrohc.schematic4j.utils.TagUtils.getCompoundOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.getDoubleList;
import static net.sandrohc.schematic4j.utils.TagUtils.getInt;
import static net.sandrohc.schematic4j.utils.TagUtils.getIntArray;
import static net.sandrohc.schematic4j.utils.TagUtils.getIntOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.getShortOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.getStringOrThrow;
import static net.sandrohc.schematic4j.utils.TagUtils.unwrap;

/**
 * Parses Sponge Schematic files (<i>.schem</i>).
 * <p>
 * The SCHEM format replaced the .SCHEMATIC format in versions 1.13+ of Minecraft Java Edition.
 * <p>
 * Specification:<br>
 * - <a href="https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md">https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md</a>
 */
public class SpongeSchematicParser implements Parser {

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

	private static final Logger log = LoggerFactory.getLogger(SpongeSchematicParser.class);

	@Override
	public @NonNull Schematic parse(@Nullable CompoundTag nbt) throws ParsingException {
		log.debug("Parsing Sponge schematic");

		final SpongeSchematic.Builder builder = new SpongeSchematic.Builder();
		if (nbt == null) {
			return builder.build();
		}

		final int version = parseVersion(nbt);
		builder.version(version);
		if (version > 3) {
			log.warn("Sponge Schematic version {} is not officially supported. Use at your own risk", version);
		}

		parseDataVersion(nbt, builder, version);
		parseMetadata(nbt, builder);
		parseOffset(nbt, builder);
		parseBlocks(nbt, builder, version);
		parseBlockEntities(nbt, builder, version);
		parseEntities(nbt, builder, version);
		parseBiomes(nbt, builder, version);

		return builder.build();
	}

	protected int parseVersion(CompoundTag rootTag) {
		// Default to version 1 if none provided
		return getInt(rootTag, NBT_VERSION).orElse(1);
	}

	protected void parseDataVersion(CompoundTag root, Builder builder, int version) throws ParsingException {
		log.trace("Parsing data version");

		// Data Version is optional for v1, but required for v2 and v3
		if (version == 1) {
			getInt(root, NBT_DATA_VERSION).ifPresent(builder::dataVersion);
		} else {
			builder.dataVersion(getIntOrThrow(root, NBT_DATA_VERSION));
		}
	}

	protected void parseMetadata(CompoundTag root, Builder builder) {
		final CompoundTag metadataTag = getCompound(root, NBT_METADATA).orElse(null);
		if (metadataTag == null) {
			log.debug("No metadata found");
			return;
		}

		log.trace("Parsing metadata");

		String name = null;
		String author = null;
		LocalDateTime date = null;
		String[] requiredMods = new String[0];
		Map<String, Object> extra = new LinkedHashMap<>();

		for (final Entry<String, Tag<?>> entry : metadataTag) {
			final String key = entry.getKey();
			final Tag<?> tag = entry.getValue();

			switch (key) {
				case NBT_METADATA_NAME:
					name = ((StringTag) tag).getValue();
					break;
				case NBT_METADATA_AUTHOR:
					author = ((StringTag) tag).getValue();
					break;
				case NBT_METADATA_DATE:
					long dateEpochMillis = ((NumberTag<?>) tag).asLong();
					date = epochToDate(dateEpochMillis);
					break;
				case NBT_METADATA_REQUIRED_MODS:
					final ListTag<StringTag> stringTags = ((ListTag<?>) tag).asStringTagList();
					requiredMods = StreamSupport.stream(stringTags.spliterator(), false)
							.map(StringTag::getValue)
							.toArray(String[]::new);
					break;
				default:
					extra.put(key, unwrap(tag));
			}
		}

		builder.metadata(new Metadata(name, author, date, requiredMods, extra));
	}

	protected void parseOffset(CompoundTag root, Builder builder) {
		log.trace("Parsing offset");

		getIntArray(root, NBT_OFFSET).ifPresent(offset -> {
			builder.offset(offset);
			log.debug("Loaded offset is {}", offset);
		});
	}

	protected void parseBlocks(CompoundTag root, SpongeSchematic.Builder builder, int version) throws ParsingException {
		log.trace("Parsing blocks");

		final short width = getShortOrThrow(root, NBT_WIDTH);
		final short height = getShortOrThrow(root, NBT_HEIGHT);
		final short length = getShortOrThrow(root, NBT_LENGTH);
		final CompoundTag blocksTag = getBlocksTag(root, version);

		builder.width(width).height(height).length(length);
		log.trace("Dimensions: width={}, height={}, length={}", width, height, length);


		// Load the block palette
		final CompoundTag palette = getCompoundOrThrow(blocksTag, NBT_PALETTE);
		log.trace("Palette size: {}", palette.size());
		Map<Integer, SchematicBlock> blockById = palette.entrySet().stream()
				.collect(toMap(
						entry -> ((IntTag) entry.getValue()).asInt(), // ID
						entry -> new SchematicBlock(entry.getKey()) // Blockstate
				));

		final int paletteMax = blocksTag.getInt(NBT_PALETTE_MAX);
		if (paletteMax > 0 && palette.size() != paletteMax)
			log.warn("Palette actual size does not match expected size. Expected {} but got {}", paletteMax, palette.size());


		// Load the block data
		final String blockDataKey = version >= 3 ? NBT_V3_DATA : NBT_BLOCK_DATA;
		byte[] blockDataRaw = getByteArrayOrThrow(blocksTag, blockDataKey);
		SchematicBlock[][][] blockData = new SchematicBlock[width][height][length];

		// --- Uses code from https://github.com/SpongePowered/Sponge/blob/aa2c8c53b4f9f40297e6a4ee281bee4f4ce7707b/src/main/java/org/spongepowered/common/data/persistence/SchematicTranslator.java#L147-L175
		int index = 0;
		int i = 0;
		while (i < blockDataRaw.length) {
			int value = 0;
			int varintLength = 0;
			while (true) {
				value |= (blockDataRaw[i] & 127) << (varintLength++ * 7);
				if (varintLength > 5) {
					throw new ParsingException("VarInt too big (probably corrupted data)");
				}
				if ((blockDataRaw[i] & 128) != 128) {
					i++;
					break;
				}
				i++;
			}

			// index =  x + (z * width) + (y * width * length)
			int y = index / (width * length);
			int z = (index % (width * length)) / width;
			int x = (index % (width * length)) % width;
			SchematicBlock block = blockById.get(value);
			blockData[x][y][z] = block;

			index++;
		}
		// ---

		builder.blocks(blockData);
		log.debug("Loaded {} blocks", width * height * length);
	}

	protected static CompoundTag getBlocksTag(CompoundTag root, int version) {
		if (version >= 3) {
			return root.getCompoundTag(NBT_V3_BLOCKS);
		} else {
			return root;
		}
	}

	protected void parseBlockEntities(CompoundTag root, Builder builder, int version) throws ParsingException {
		final CompoundTag blocksTag = getBlocksTag(root, version);
		final String blockEntitiesTagName = version == 1 ? NBT_TILE_ENTITIES : NBT_BLOCK_ENTITIES;
		final Optional<ListTag<CompoundTag>> blockEntitiesListTag = getCompoundList(blocksTag, blockEntitiesTagName);

		if (!blockEntitiesListTag.isPresent()) {
			log.trace("No block entities found");
			return;
		}

		log.trace("Parsing block entities");
		final ListTag<CompoundTag> blockEntitiesTag = blockEntitiesListTag.get();
		final SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[blockEntitiesTag.size()];

		int i = 0;
		for (CompoundTag blockEntity : blockEntitiesTag) {
			final String id = getStringOrThrow(blockEntity, NBT_BLOCK_ENTITIES_ID);
			final int[] pos = getIntArray(blockEntity, NBT_BLOCK_ENTITIES_POS).orElseGet(() -> new int[]{0, 0, 0});

			final Map<String, Object> extra = blockEntity.entrySet().stream()
					.filter(tag -> !tag.getKey().equals(NBT_ENTITIES_ID) &&
							!tag.getKey().equals(NBT_ENTITIES_POS))
					.collect(toMap(Entry::getKey, e -> unwrap(e.getValue()), (a, b) -> b, TreeMap::new));

			blockEntities[i] = new SchematicBlockEntity(id, SchematicBlockPos.from(pos), extra);
			i++;
		}

		builder.blockEntities(blockEntities);
		log.debug("Loaded {} block entities", blockEntities.length);
	}

	protected void parseEntities(CompoundTag root, Builder builder, int version) throws ParsingException {
		final Optional<ListTag<CompoundTag>> entitiesListTag = getCompoundList(root, NBT_ENTITIES);
		if (!entitiesListTag.isPresent()) {
			log.trace("No entities found");
			return;
		}

		log.trace("Parsing entities");
		final ListTag<CompoundTag> entitiesTag = entitiesListTag.get();
		final SchematicEntity[] entities = new SchematicEntity[entitiesTag.size()];

		int i = 0;
		for (CompoundTag entity : entitiesTag) {
			final String id = getStringOrThrow(entity, NBT_ENTITIES_ID);

			final double[] pos = {0, 0, 0};
			getDoubleList(entity, NBT_ENTITIES_POS).ifPresent(posTag -> {
				pos[0] = posTag.get(0).asDouble();
				pos[1] = posTag.get(1).asDouble();
				pos[2] = posTag.get(2).asDouble();
			});

			final Map<String, Object> extra = new TreeMap<>();
			final String extraTagName = version >= 3 ? NBT_V3_DATA : NBT_ENTITIES_EXTRA;
			getCompound(entity, extraTagName).ifPresent(extraTag -> {
				entity.entrySet().forEach(e -> extra.put(e.getKey(), unwrap(e.getValue())));
			});

			entities[i] = new SchematicEntity(id, SchematicPosDouble.from(pos), extra);
			i++;
		}

		builder.entities(entities);
		log.debug("Loaded {} entities", entities.length);
	}

	protected void parseBiomes(CompoundTag root, SpongeSchematic.Builder builder, int version) throws ParsingException {
		log.trace("Parsing biomes");

		if (((version == 1 || version == 2) && !containsAllTags(root, NBT_BIOME_DATA, NBT_BIOME_PALETTE)) || (version == 3 && !root.containsKey(NBT_V3_BIOMES))) {
			log.trace("Did not have biome data");
			return;
		}

		final short width = getShortOrThrow(root, NBT_WIDTH);
		final short height = version >= 3 ? getShortOrThrow(root, NBT_HEIGHT) : 1;
		final short length = getShortOrThrow(root, NBT_LENGTH);
		final CompoundTag biomesTag = getBiomesTag(root, version);

		// Load the (optional) palette
		final String biomePaletteKey = version >= 3 ? NBT_PALETTE : NBT_BIOME_PALETTE;
		final CompoundTag palette = getCompoundOrThrow(biomesTag, biomePaletteKey);
		log.trace("Biome palette size: {}", palette.size());
		Map<Integer, SchematicBiome> biomeById = palette.entrySet().stream()
				.collect(toMap(
						entry -> ((IntTag) entry.getValue()).asInt(), // ID
						entry -> new SchematicBiome(entry.getKey()) // Blockstate
				));

		final int paletteMax = biomesTag.getInt(NBT_BIOME_PALETTE_MAX);
		if (paletteMax > 0 && palette.size() != paletteMax)
			log.warn("Biome palette actual size does not match expected size. Expected {} but got {}", paletteMax, palette.size());


		// Load the biome data
		final String biomeDataKey = version >= 3 ? NBT_V3_DATA : NBT_BIOME_DATA;
		byte[] biomeDataRaw = getByteArrayOrThrow(biomesTag, biomeDataKey);
		SchematicBiome[][][] biomeData = new SchematicBiome[width][height][length];

		int index = 0;
		int i = 0;
		while (i < biomeDataRaw.length) {
			int value = 0;
			int varintLength = 0;
			while (true) {
				value |= (biomeDataRaw[i] & 127) << (varintLength++ * 7);
				if (varintLength > 5) {
					throw new ParsingException("VarInt too big (probably corrupted data)");
				}
				if ((biomeDataRaw[i] & 128) != 128) {
					i++;
					break;
				}
				i++;
			}

			if (version >= 3) {
				// index = x + (z * width) + (y * width * length)
				int y = index / (width * length);
				int z = (index % (width * length)) / width;
				int x = (index % (width * length)) % width;
				biomeData[x][y][z] = biomeById.get(value);
			} else {
				// index = x + (z * width)
				int x = index % width;
				int z = index / width;
				biomeData[x][0][z] = biomeById.get(value);
			}

			index++;
		}

		builder.biomes(biomeData);
		log.debug("Loaded {} biomes", width * length);
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
