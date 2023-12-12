package net.sandrohc.schematic4j.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.IntTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.LongTag;
import net.sandrohc.schematic4j.nbt.tag.StringTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSponge;
import net.sandrohc.schematic4j.schematic.SchematicSponge.Builder;
import net.sandrohc.schematic4j.schematic.SchematicSponge.Metadata;
import net.sandrohc.schematic4j.schematic.types.SchematicBiome;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicPosDouble;
import net.sandrohc.schematic4j.schematic.types.SchematicPosInt;

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
 *  - <a href="https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md">https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md</a>
 */
// TODO: create different parsers for v1 and v2, and extract common code to an abstract intermediate class
// TODO: implement v3
public class SpongeSchematicParser implements Parser {

	public static final String NBT_ROOT = "Schematic";

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
	public @NonNull Schematic parse(@Nullable NamedTag root) throws ParsingException {
		log.debug("Parsing Sponge schematic");

		final SchematicSponge.Builder builder = new SchematicSponge.Builder();
		if (root == null) {
			return builder.build();
		}

		final CompoundTag rootTag = (CompoundTag) root.getTag();

		final int version = getIntOrThrow(rootTag, NBT_VERSION);
		builder.version(version);
		if (version > 2) {
			log.warn("Sponge Schematic version {} is not officially supported. Use at your own risk", version);
		}

		parseDataVersion(rootTag, builder, version);
		parseMetadata(rootTag, builder);
		parseOffset(rootTag, builder);
		parseBlocks(rootTag, builder);
		parseBlockEntities(rootTag, builder, version);
		parseEntities(rootTag, builder);
		parseBiomes(rootTag, builder);

		return builder.build();
	}

	private void parseDataVersion(CompoundTag root, Builder builder, int version) throws ParsingException {
		log.trace("Parsing data version");

		// Data Version is optional for v1, but required for v2
		if (version == 1) {
			getInt(root, NBT_DATA_VERSION).ifPresent(builder::dataVersion);
		} else {
			builder.dataVersion(getIntOrThrow(root, NBT_DATA_VERSION));
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

	private void parseBlocks(CompoundTag root, SchematicSponge.Builder builder) throws ParsingException {
		log.trace("Parsing blocks");

		if (!containsAllTags(root, NBT_BLOCK_DATA, NBT_PALETTE)) {
			log.trace("Did not have block data");
			builder.blocks(new SchematicBlock[0][0][0]);
			return;
		}

		final short width  = getShortOrThrow(root, NBT_WIDTH);
		final short height = getShortOrThrow(root, NBT_HEIGHT);
		final short length = getShortOrThrow(root, NBT_LENGTH);

		builder.width(width).height(height).length(length);
		log.trace("Dimensions: width={}, height={}, length={}", width, height, length);

		// Load the (optional) palette
		final CompoundTag palette = getCompoundOrThrow(root, NBT_PALETTE);
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
		byte[] blockDataRaw = getByteArrayOrThrow(root, NBT_BLOCK_DATA);
		SchematicBlock[][][] blockData = new SchematicBlock[width][height][length];

		int expectedBlocks = width * height * length;
		if (blockDataRaw.length != expectedBlocks)
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedBlocks, blockDataRaw.length);

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

			// index = (y * length + z) * width + x
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

	private void parseBlockEntities(CompoundTag root, Builder builder, int version) throws ParsingException {
		log.trace("Parsing block entities");

		final Collection<SchematicBlockEntity> blockEntities;
		final Optional<ListTag<CompoundTag>> blockEntitiesListTag = getCompoundList(root, version == 1 ? NBT_TILE_ENTITIES : NBT_BLOCK_ENTITIES);

		if (blockEntitiesListTag.isPresent()) {
			final ListTag<CompoundTag> blockEntitiesTag = blockEntitiesListTag.get();

			blockEntities = new ArrayList<>(blockEntitiesTag.size());

			for (CompoundTag blockEntity : blockEntitiesTag) {
				final String id = getStringOrThrow(blockEntity, NBT_BLOCK_ENTITIES_ID);
				final int[] pos = getIntArray(blockEntity, NBT_BLOCK_ENTITIES_POS).orElseGet(() -> new int[] { 0, 0, 0 });

				final Map<String, Object> extra = blockEntity.entrySet().stream()
						.filter(tag -> !tag.getKey().equals(NBT_ENTITIES_ID) &&
									   !tag.getKey().equals(NBT_ENTITIES_POS))
						.collect(Collectors.toMap(Entry::getKey, e -> unwrap(e.getValue())));

				blockEntities.add(new SchematicBlockEntity(id, SchematicPosInt.from(pos), extra));
			}

			log.debug("Loaded {} block entities", blockEntities.size());
		} else {
			log.trace("No block entities found");
			blockEntities = Collections.emptyList();
		}

		builder.blockEntities(blockEntities);
	}

	private void parseEntities(CompoundTag root, SchematicSponge.Builder builder) throws ParsingException {
		log.trace("Parsing entities");

		final Collection<SchematicEntity> entities;

		final Optional<ListTag<CompoundTag>> entitiesListTag = getCompoundList(root, NBT_ENTITIES);
		if (entitiesListTag.isPresent()) {
			final ListTag<CompoundTag> entitiesTag = entitiesListTag.get();

			entities = new ArrayList<>(entitiesTag.size());

			for (CompoundTag entity : entitiesTag) {
				final String id = getStringOrThrow(entity, NBT_ENTITIES_ID);

				final double[] pos = { 0, 0, 0 };
				getDoubleList(entity, NBT_ENTITIES_POS).ifPresent(posTag -> {
					pos[0] = posTag.get(0).asDouble();
					pos[1] = posTag.get(1).asDouble();
					pos[2] = posTag.get(2).asDouble();
				});

				final Map<String, Object> extra = entity.entrySet().stream()
						.filter(tag -> !tag.getKey().equals(NBT_ENTITIES_ID) &&
									   !tag.getKey().equals(NBT_ENTITIES_POS))
						.collect(Collectors.toMap(Entry::getKey, e -> unwrap(e.getValue())));

				entities.add(new SchematicEntity(id, SchematicPosDouble.from(pos), extra));
			}

			log.debug("Loaded {} entities", entities.size());
		} else {
			log.trace("No entities found");
			entities = Collections.emptyList();
		}

		builder.entities(entities);
	}

	private void parseBiomes(CompoundTag root, SchematicSponge.Builder builder) throws ParsingException {
		log.trace("Parsing biomes");

		if (!containsAllTags(root, NBT_BIOME_DATA, NBT_BIOME_PALETTE)) {
			log.trace("Did not have biome data");
			builder.biomes(new SchematicBiome[0][0][0]);
			return;
		}

		short width  = getShortOrThrow(root, NBT_WIDTH);
		short length = getShortOrThrow(root, NBT_LENGTH);

		// Load the (optional) palette
		final CompoundTag palette = getCompoundOrThrow(root, NBT_BIOME_PALETTE);
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
		byte[] biomeDataRaw = getByteArrayOrThrow(root, NBT_BLOCK_DATA);
		SchematicBiome[][][] biomeData = new SchematicBiome[width][1][length];

		int expectedBlocks = width * length;
		if (biomeDataRaw.length != expectedBlocks)
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedBlocks, biomeDataRaw.length);

		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				final int index = x + z*width; // flatten (x,z) into a single dimension

				final int blockId = biomeDataRaw[index] & 0xFF;
				final SchematicBiome block = biomeById.get(blockId);

				biomeData[x][0][z] = block;
			}
		}

		builder.biomes(biomeData);
		log.debug("Loaded {} biomes", width * length);
	}

	@Override
	public String toString() {
		return "SpongeSchematicParser";
	}
}
