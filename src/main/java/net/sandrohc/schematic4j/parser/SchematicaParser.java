package net.sandrohc.schematic4j.parser;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.ListTag;
import net.sandrohc.schematic4j.nbt.tag.ShortTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSchematica.Builder;
import net.sandrohc.schematic4j.schematic.types.*;

import static net.sandrohc.schematic4j.utils.TagUtils.*;
import static net.sandrohc.schematic4j.utils.TagUtils.getByteArrayOrThrow;

/**
 * Parses Schematica files (<i>.schematic</i>).
 * <p>
 * Specification:<br>
 *  - https://minecraft.fandom.com/wiki/Schematic_file_format
 *  - https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java
 */
public class SchematicaParser implements Parser {

	private static final Logger log = LoggerFactory.getLogger(SchematicaParser.class);

	public static final String NBT_ROOT = "Schematic";

	public static final String NBT_MATERIALS = "Materials";
	public static final String NBT_FORMAT_CLASSIC = "Classic";
	public static final String NBT_FORMAT_ALPHA = "Alpha";
	public static final String NBT_FORMAT_STRUCTURE = "Structure";

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
	public static final String NBT_TILE_ENTITIES_ID = "id";
	public static final String NBT_TILE_ENTITIES_X = "x";
	public static final String NBT_TILE_ENTITIES_Y = "y";
	public static final String NBT_TILE_ENTITIES_Z = "z";
	public static final String NBT_ENTITIES = "Entities";
	public static final String NBT_ENTITIES_ID = "id";
	public static final String NBT_ENTITIES_POS = "Pos";
	public static final String NBT_ENTITIES_TILE_X = "TileX";
	public static final String NBT_ENTITIES_TILE_Y = "TileY";
	public static final String NBT_ENTITIES_TILE_Z = "TileZ";
	public static final String NBT_EXTENDED_METADATA = "ExtendedMetadata";

	public static final String DEFAULT_BLOCK_NAME = "(missing block)";


	@Override
	public @NonNull Schematic parse(@Nullable NamedTag root) throws ParsingException {
		log.debug("Parsing Schematica schematic");

		final Builder builder = new Builder();
		if (root == null) {
			return builder.build();
		}

		final CompoundTag rootTag = (CompoundTag) root.getTag();

		parseIcon(rootTag, builder);
		parseBlocks(rootTag, builder);
		parseBlockEntities(rootTag, builder);
		parseEntities(rootTag, builder);

		return builder.build();
	}

	private void parseIcon(CompoundTag root, Builder builder) {
		log.trace("Parsing icon");

		getCompound(root, NBT_ICON).ifPresent(iconTag -> builder.icon(new SchematicItem(
				getString(iconTag, NBT_ICON_ID).orElse("(missing id)"),
				getByte(iconTag, NBT_ICON_COUNT).orElseGet(() -> (byte) 1),
				getShort(iconTag, NBT_ICON_DAMAGE).orElseGet(() -> (short) 0)
		)));
	}

	private void parseBlocks(CompoundTag root, Builder builder) throws ParsingException {
		log.trace("Parsing blocks");

		short width  = getShortOrThrow(root, NBT_WIDTH);
		short height = getShortOrThrow(root, NBT_HEIGHT);
		short length = getShortOrThrow(root, NBT_LENGTH);

		builder.width(width).height(height).length(length);
		log.trace("Dimensions: width={}, height={}, length={}", width, height, length);

		/* Mappings */
		final CompoundTag mapping = getCompoundOrThrow(root, NBT_MAPPING_SCHEMATICA);
		log.trace("Mapping size: {}", mapping.size());
		Map<Integer, SchematicBlock> blocksById = new HashMap<>();
		Map<Integer, String> blockNamesById = mapping.entrySet().stream()
				.collect(Collectors.toMap(
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
				extraBlocks[i * 2]     = (byte) ((extraBlocksNibble[i] >> 4) & 0xF);
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

	private void parseBlockEntities(CompoundTag root, Builder builder) throws ParsingException {
		log.trace("Parsing block entities");

		final Collection<SchematicBlockEntity> blockEntities;
		final Optional<ListTag<CompoundTag>> blockEntitiesListTag = getCompoundList(root, NBT_TILE_ENTITIES);

		if (blockEntitiesListTag.isPresent()) {
			final ListTag<CompoundTag> blockEntitiesTag = blockEntitiesListTag.get();

			blockEntities = new ArrayList<>(blockEntitiesTag.size());

			for (CompoundTag blockEntity : blockEntitiesTag) {
				final String id = getStringOrThrow(blockEntity, NBT_TILE_ENTITIES_ID);

				final int posX = getIntOrThrow(blockEntity, NBT_TILE_ENTITIES_X);
				final int posY = getIntOrThrow(blockEntity, NBT_TILE_ENTITIES_Y);
				final int posZ = getIntOrThrow(blockEntity, NBT_TILE_ENTITIES_Z);

				final Map<String, Object> extra = blockEntity.entrySet().stream()
						.filter(tag -> !tag.getKey().equals(NBT_TILE_ENTITIES_ID) &&
									   !tag.getKey().equals(NBT_TILE_ENTITIES_X) &&
									   !tag.getKey().equals(NBT_TILE_ENTITIES_Y) &&
									   !tag.getKey().equals(NBT_TILE_ENTITIES_Z))
						.collect(Collectors.toMap(Entry::getKey, e -> unwrap(e.getValue())));

				blockEntities.add(new SchematicBlockEntity(id, SchematicPosInt.from(posX, posY, posZ), extra));
			}

			log.debug("Loaded {} block entities", blockEntities.size());
		} else {
			log.trace("No block entities found");
			blockEntities = Collections.emptyList();
		}

		builder.blockEntities(blockEntities);
	}

	private void parseEntities(CompoundTag root, Builder builder) throws ParsingException {
		log.trace("Parsing entities");

		final Collection<SchematicEntity> entities;

		final Optional<ListTag<CompoundTag>> entitiesListTag = getCompoundList(root, NBT_ENTITIES);
		if (entitiesListTag.isPresent()) {
			final ListTag<CompoundTag> entitiesTag = entitiesListTag.get();

			entities = new ArrayList<>(entitiesTag.size());

			for (CompoundTag entity : entitiesTag) {
				final String id = getStringOrThrow(entity, NBT_ENTITIES_ID);

				// Position in the world
				final int posX = getInt(entity, NBT_ENTITIES_TILE_X).orElse(0);
				final int posY = getInt(entity, NBT_ENTITIES_TILE_Y).orElse(0);
				final int posZ = getInt(entity, NBT_ENTITIES_TILE_Z).orElse(0);

				// Position inside the block
				final double[] subPos = { 0, 0, 0 };
				getDoubleList(entity, NBT_ENTITIES_POS).ifPresent(subPosTag -> {
					subPos[0] = subPosTag.get(0).asDouble();
					subPos[1] = subPosTag.get(1).asDouble();
					subPos[2] = subPosTag.get(2).asDouble();
				});

				SchematicPosDouble pos = SchematicPosDouble.from(
						posX + subPos[0],
						posY + subPos[1],
						posZ + subPos[2]
				);

				final Map<String, Object> extra = entity.entrySet().stream()
						.filter(tag -> !tag.getKey().equals(NBT_ENTITIES_ID) &&
									   !tag.getKey().equals(NBT_ENTITIES_POS) &&
									   !tag.getKey().equals(NBT_ENTITIES_TILE_X) &&
									   !tag.getKey().equals(NBT_ENTITIES_TILE_Y) &&
									   !tag.getKey().equals(NBT_ENTITIES_TILE_Z))
						.collect(Collectors.toMap(Entry::getKey, e -> unwrap(e.getValue())));

				entities.add(new SchematicEntity(id, pos, extra));
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
		return "SchematicaParser";
	}
}
