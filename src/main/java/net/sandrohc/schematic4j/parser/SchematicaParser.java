package net.sandrohc.schematic4j.parser;

import java.util.*;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.ShortTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicaSchematic.Builder;
import net.sandrohc.schematic4j.schematic.types.*;

import static java.util.stream.Collectors.toMap;
import static net.sandrohc.schematic4j.utils.TagUtils.*;
import static net.sandrohc.schematic4j.utils.TagUtils.getByteArrayOrThrow;

/**
 * Parses Schematica files (<i>.schematic</i>).
 * <p>
 * Specification:<br>
 * - <a href="https://minecraft.fandom.com/wiki/Schematic_file_format">https://minecraft.fandom.com/wiki/Schematic_file_format</a>
 * - <a href="https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java">https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java</a>
 */
public class SchematicaParser implements Parser {

	private static final Logger log = LoggerFactory.getLogger(SchematicaParser.class);

	public static final String NBT_MATERIALS = "Materials";

	public static final String NBT_ICON = "Icon";
	public static final String NBT_ICON_ID = "id";
	public static final String NBT_ICON_COUNT = "Count";
	public static final String NBT_ICON_DAMAGE = "Damage";
	public static final String NBT_BLOCKS = "Blocks";
	public static final String NBT_DATA = "Data";
	public static final String NBT_ADD_BLOCKS = "AddBlocks";
	public static final String NBT_ADD_BLOCKS_SCHEMATICA = "Add";
	public static final String NBT_WIDTH = "Width";
	public static final String NBT_LENGTH = "Length";
	public static final String NBT_HEIGHT = "Height";
	public static final String NBT_MAPPING_SCHEMATICA = "SchematicaMapping";
	public static final String NBT_TILE_ENTITIES = "TileEntities";
	public static final String NBT_ENTITIES = "Entities";

	public static final String DEFAULT_BLOCK_NAME = "minecraft:unknown";


	@Override
	public @NonNull Schematic parse(@Nullable CompoundTag nbt) throws ParsingException {
		log.debug("Parsing Schematica schematic");

		final Builder builder = new Builder();
		if (nbt == null) {
			return builder.build();
		}

		parseIcon(nbt, builder);
		parseBlocks(nbt, builder);
		parseBlockEntities(nbt, builder);
		parseEntities(nbt, builder);
		parseMaterials(nbt, builder);

		return builder.build();
	}

	private void parseIcon(CompoundTag root, Builder builder) {
		log.trace("Parsing icon");

		getCompound(root, NBT_ICON).ifPresent(iconTag -> builder.icon(new SchematicItem(
				getString(iconTag, NBT_ICON_ID).orElse("minecraft:dirt"),
				getByte(iconTag, NBT_ICON_COUNT).orElse((byte) 1),
				getShort(iconTag, NBT_ICON_DAMAGE).orElse((short) 0)
		)));
	}

	private void parseBlocks(CompoundTag root, Builder builder) throws ParsingException {
		log.trace("Parsing blocks");

		short width = getShortOrThrow(root, NBT_WIDTH);
		short height = getShortOrThrow(root, NBT_HEIGHT);
		short length = getShortOrThrow(root, NBT_LENGTH);

		builder.width(width).height(height).length(length);
		log.trace("Dimensions: width={}, height={}, length={}", width, height, length);

		/* Mappings */
		final CompoundTag mapping = getCompoundOrThrow(root, NBT_MAPPING_SCHEMATICA);
		log.trace("Mapping size: {}", mapping.size());
		Map<Integer, SchematicBlock> blocksById = new HashMap<>();
		Map<Integer, String> blockNamesById = mapping.entrySet().stream()
				.collect(toMap(
						entry -> ((ShortTag) entry.getValue()).asInt(), // ID
						Entry::getKey // Name
				));

		// Load the (optional) palette
		final byte[] blocks = getByteArrayOrThrow(root, NBT_BLOCKS);
		final byte[] blockMetadata = getByteArrayOrThrow(root, NBT_DATA);

		boolean extra = false;
		byte[] extraBlocks = null;
		if (root.containsKey(NBT_ADD_BLOCKS)) {
			byte[] extraBlocksNibble = getByteArrayOrThrow(root, NBT_ADD_BLOCKS);
			extraBlocks = new byte[extraBlocksNibble.length * 2];
			for (int i = 0; i < extraBlocksNibble.length; i++) {
				extraBlocks[i * 2] = (byte) ((extraBlocksNibble[i] >> 4) & 0xF);
				extraBlocks[i * 2 + 1] = (byte) (extraBlocksNibble[i] & 0xF);
			}
			extra = true;
		} else if (root.containsKey(NBT_ADD_BLOCKS_SCHEMATICA)) {
			extraBlocks = getByteArrayOrThrow(root, NBT_ADD_BLOCKS_SCHEMATICA);
			extra = true;
		}


		// Load the block data
		SchematicBlock[][][] blockData = new SchematicBlock[width][height][length];

		int expectedBlocks = width * height * length;
		if (blocks.length != expectedBlocks)
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedBlocks, blocks.length);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					final int index = x + (y * length + z) * width; // flatten (x,y,z) into a single dimension

					final int blockId = (blocks[index] & 0xFF) | (extra ? ((extraBlocks[index] & 0xFF) << 8) : 0);
					final int metadata = blockMetadata[index] & 0xFF;

					final SchematicBlock block = blocksById.computeIfAbsent(blockId + metadata * 10067/* prime number */, key -> {
						String blockName = blockNamesById.getOrDefault(blockId, DEFAULT_BLOCK_NAME);
						if (metadata != 0) blockName += "[metadata=" + metadata + ']';

						return new SchematicBlock(blockName);
					});

					blockData[x][y][z] = block;
				}
			}
		}

		builder.blocks(blockData);
		log.debug("Loaded {} blocks", width * height * length);
	}

	private void parseBlockEntities(CompoundTag root, Builder builder) {
		final ListTag<CompoundTag> blockEntitiesTag = getCompoundList(root, NBT_TILE_ENTITIES).orElse(null);
		if (blockEntitiesTag == null) {
			log.trace("No block entities found");
			return;
		}

		log.trace("Parsing block entities");
		final SchematicBlockEntity[] blockEntities = new SchematicBlockEntity[blockEntitiesTag.size()];

		int i = 0;
		for (CompoundTag blockEntityTag : blockEntitiesTag) {
			final SchematicBlockEntity blockEntity = SchematicBlockEntity.fromNbt(blockEntityTag);
			blockEntities[i++] = blockEntity;
		}

		log.debug("Loaded {} block entities", blockEntities.length);
		builder.blockEntities(blockEntities);
	}

	private void parseEntities(CompoundTag root, Builder builder) {
		final ListTag<CompoundTag> entitiesTag = getCompoundList(root, NBT_ENTITIES).orElse(null);
		if (entitiesTag == null) {
			log.trace("No entities found");
			return;
		}

		log.trace("Parsing entities");
		final SchematicEntity[] entities = new SchematicEntity[entitiesTag.size()];

		int i = 0;
		for (final CompoundTag entityTag : entitiesTag) {
			final SchematicEntity entity = SchematicEntity.fromNbt(entityTag);
			entities[i++] = entity;
		}

		log.debug("Loaded {} entities", entities.length);
		builder.entities(entities);
	}

	private void parseMaterials(CompoundTag root, Builder builder) {
		log.trace("Parsing materials");
		getString(root, NBT_MATERIALS).ifPresent(builder::materials);
	}

	@Override
	public String toString() {
		return "SchematicaParser";
	}
}
