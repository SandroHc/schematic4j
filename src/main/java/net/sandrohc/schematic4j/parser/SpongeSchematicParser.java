package net.sandrohc.schematic4j.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSchem;
import net.sandrohc.schematic4j.schematic.SchematicSchem.Builder;
import net.sandrohc.schematic4j.schematic.SchematicSchem.Metadata;
import net.sandrohc.schematic4j.schematic.types.*;

import static net.sandrohc.schematic4j.SchematicUtil.containsAllTags;
import static net.sandrohc.schematic4j.SchematicUtil.unwrap;
import static net.sandrohc.schematic4j.utils.TagUtils.*;

/**
 * Parses Sponge Schematic Format (<i>.SCHEM</i>).
 *
 * The SCHEM format replaced the .SCHEMATIC format in versions 1.13+ of Minecraft Java Edition.
 *
 * Specification:
 *  - https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md
 */
// TODO: create different parsers for v1 and v2, and extract common code to an abstract intermediate class
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

	private static final Logger log = LoggerFactory.getLogger(SpongeSchematicParser.class);


	@Override
	public Schematic parse(NamedTag root) {
		log.debug("Parsing SCHEM schematic");

		final CompoundTag rootTag = (CompoundTag) root.getTag();
		final SchematicSchem.Builder builder = new SchematicSchem.Builder();

		final int version = getIntRequired(rootTag, NBT_VERSION);
		if (version > 2)
			log.warn("Sponge Schematic version {} is not officially supported. Use at your own risk", version);

		parseDataVersion(rootTag, builder, version);
		parseMetadata(rootTag, builder);
		parseOffset(rootTag, builder);
		parseBlocks(rootTag, builder);
		parseBlockEntities(rootTag, builder, version);
		parseEntities(rootTag, builder);
		parseBiomes(rootTag, builder);

		return builder.build();
	}

	private void parseDataVersion(CompoundTag root, Builder builder, int version) {
		log.trace("Parsing data version");

		// Data Version is optional for v1, but required for v2
		if (version == 1) {
			getInt(root, NBT_DATA_VERSION).ifPresent(builder::dataVersion);
		} else {
			builder.dataVersion(getIntRequired(root, NBT_DATA_VERSION));
		}
	}

	private void parseMetadata(CompoundTag root, Builder builder) {
		log.trace("Parsing metadata");

		String name = null;
		String author = null;
		LocalDateTime date = null;
		String[] requiredMods = new String[0];
		Map<String, Object> extra = new LinkedHashMap<>();

		final Optional<CompoundTag> metadataTag = getCompound(root, NBT_METADATA);
		if (metadataTag.isPresent()) {
			for (Entry<String, Tag<?>> entry : metadataTag.get()) {
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
					long dateEpochMillis = ((LongTag) tag).asLong(); // milliseconds since the Unix epoch
					date = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateEpochMillis), ZoneId.systemDefault());
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
		} else {
			log.debug("No metadata found");
		}

		builder.metadata(new Metadata(name, author, date, requiredMods, extra));
	}

	private void parseOffset(CompoundTag root, Builder builder) {
		log.trace("Parsing offset");

		getIntArray(root, NBT_OFFSET).ifPresent(offset -> {
			builder.offset(offset);
			log.debug("Loaded offset is {}", offset);
		});
	}

	private void parseBlocks(CompoundTag root, SchematicSchem.Builder builder) {
		log.trace("Parsing blocks");

		if (!containsAllTags(root, NBT_BLOCK_DATA, NBT_PALETTE)) {
			log.trace("Did not have block data");
			builder.blocks(new SchematicBlock[0][0][0]);
			return;
		}

		short width  = getShortRequired(root, NBT_WIDTH);
		short height = getShortRequired(root, NBT_HEIGHT);
		short length = getShortRequired(root, NBT_LENGTH);

		builder.width(width).height(height).length(length);
		log.trace("Dimensions: width={}, height={}, length={}", width, height, length);

		// Load the (optional) palette
		final CompoundTag palette = root.getCompoundTag(NBT_PALETTE);
		log.trace("Palette size: {}", palette.size());
		Map<Integer, SchematicBlock> blockById = palette.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> ((IntTag) entry.getValue()).asInt(), // ID
						entry -> new SchematicBlock(entry.getKey()) // Blockstate
				));

		final int paletteMax = root.getInt(NBT_PALETTE_MAX);
		if (palette.size() != paletteMax)
			log.warn("Palette actual size does not match expected size. Expected {} but got {}", paletteMax, palette.size());


		// Load the block data
		byte[] blockDataRaw = getByteArrayRequired(root, NBT_BLOCK_DATA);
		SchematicBlock[][][] blockData = new SchematicBlock[width][height][length];

		int expectedBlocks = width * height * length;
		if (blockDataRaw.length != expectedBlocks)
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedBlocks, blockDataRaw.length);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					final int index = x + z*width + y*width*length; // flatten (x,y,z) into a single dimension

					final int blockId = blockDataRaw[index] & 0xFF;
					final SchematicBlock block = blockById.get(blockId);

					blockData[x][y][z] = block;
				}
			}
		}

		builder.blocks(blockData);
		log.debug("Loaded {} blocks", width * height * length);
	}

	private void parseBiomes(CompoundTag root, SchematicSchem.Builder builder) {
		log.trace("Parsing biomes");

		if (!containsAllTags(root, NBT_BIOME_DATA, NBT_BIOME_PALETTE)) {
			log.trace("Did not have biome data");
			builder.biomes(new SchematicBiome[0][0]);
			return;
		}

		short width  = getShortRequired(root, NBT_WIDTH);
		short length = getShortRequired(root, NBT_LENGTH);

		// Load the (optional) palette
		final CompoundTag palette = root.getCompoundTag(NBT_BIOME_PALETTE);
		log.trace("Biome palette size: {}", palette.size());
		Map<Integer, SchematicBiome> biomeById = palette.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> ((IntTag) entry.getValue()).asInt(), // ID
						entry -> new SchematicBiome(entry.getKey()) // Blockstate
				));

		final int paletteMax = root.getInt(NBT_BIOME_PALETTE_MAX);
		if (palette.size() != paletteMax)
			log.warn("Biome palette actual size does not match expected size. Expected {} but got {}", paletteMax, palette.size());


		// Load the block data
		byte[] biomeDataRaw = getByteArrayRequired(root, NBT_BLOCK_DATA);
		SchematicBiome[][] biomeData = new SchematicBiome[width][length];

		int expectedBlocks = width * length;
		if (biomeDataRaw.length != expectedBlocks)
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedBlocks, biomeDataRaw.length);

		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				final int index = x + z*width; // flatten (x,z) into a single dimension

				final int blockId = biomeDataRaw[index] & 0xFF;
				final SchematicBiome block = biomeById.get(blockId);

				biomeData[x][z] = block;
			}
		}

		builder.biomes(biomeData);
		log.debug("Loaded {} biomes", width * length);
	}

	private void parseBlockEntities(CompoundTag root, Builder builder, int version) {
		log.trace("Parsing block entities");

		final Collection<SchematicBlockEntity> blockEntities;
		final Optional<ListTag<CompoundTag>> blockEntitiesListTag = getCompoundList(root, version == 1 ? NBT_TILE_ENTITIES : NBT_BLOCK_ENTITIES);

		if (blockEntitiesListTag.isPresent()) {
			final ListTag<CompoundTag> blockEntitiesTag = blockEntitiesListTag.get();

			blockEntities = new ArrayList<>(blockEntitiesTag.size());

			for (CompoundTag blockEntity : blockEntitiesTag) {
				final String id = getStringRequired(blockEntity, NBT_BLOCK_ENTITIES_ID);
				final int[] pos = getIntArrayRequired(blockEntity, NBT_BLOCK_ENTITIES_POS);
				final Map<String, Object> extra = blockEntity.entrySet().stream()
						.filter(e -> !e.getKey().equals(NBT_BLOCK_ENTITIES_ID) && !e.getKey().equals(NBT_BLOCK_ENTITIES_POS))
						.collect(Collectors.toMap(Entry::getKey, e -> unwrap(e.getValue())));

				blockEntities.add(new SchematicBlockEntity(id, SchematicPosInteger.from(pos), extra));
			}

			log.debug("Loaded {} block entities", blockEntities.size());
		} else {
			log.trace("No block entities found");
			blockEntities = Collections.emptyList();
		}

		builder.blockEntities(blockEntities);
	}

	private void parseEntities(CompoundTag root, SchematicSchem.Builder builder) {
		log.trace("Parsing entities");

		final Collection<SchematicEntity> entities;

		final Optional<ListTag<CompoundTag>> entitiesListTag = getCompoundList(root, NBT_ENTITIES);
		if (entitiesListTag.isPresent()) {
			final ListTag<CompoundTag> entitiesTag = entitiesListTag.get();

			entities = new ArrayList<>(entitiesTag.size());

			for (CompoundTag entity : entitiesTag) {
				final String id = getStringRequired(entity, NBT_ENTITIES_ID);

				final ListTag<DoubleTag> pos = getDoubleListRequired(entity, NBT_ENTITIES_POS);
				final double posX = pos.get(0).asDouble();
				final double posY = pos.get(1).asDouble();
				final double posZ = pos.get(2).asDouble();

				final Map<String, Object> extra = entity.entrySet().stream()
						.filter(e -> !e.getKey().equals(NBT_ENTITIES_ID) && !e.getKey().equals(NBT_ENTITIES_POS))
						.collect(Collectors.toMap(Entry::getKey, e -> unwrap(e.getValue())));

				entities.add(new SchematicEntity(id, SchematicPosDouble.from(posX, posY, posZ), extra));
			}

			log.debug("Loaded {} entities", entities.size());
		} else {
			log.trace("No entities found");
			entities = Collections.emptyList();
		}

		builder.entities(entities);
	}

	@Override
	public String toString() {
		return "SpongeSchematicParser()";
	}

}
