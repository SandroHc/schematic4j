package net.sandrohc.schematic4j.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicBuilder;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicEntity;
import net.sandrohc.schematic4j.schematic.types.SchematicPos;

/**
 * Parses Sponge Schematic Format (<i>.SCHEM</i>) version 2.
 *
 * The SCHEM format replaced the .SCHEMATIC format in versions 1.13+ of Minecraft Java Edition.
 *
 * Specification:
 *  - https://github.com/SpongePowered/Schematic-Specification
 */
// TODO: create different parsers for v1 and v2, and extract common code to an abstract intermediate class
public class SpongeSchematicVersion2Parser implements Parser {

	public static final String NBT_WIDTH = "Width";
	public static final String NBT_HEIGHT = "Height";
	public static final String NBT_LENGTH = "Length";
	public static final String NBT_PALETTE = "Palette";
	public static final String NBT_PALETTE_MAX = "PaletteMax";
	public static final String NBT_BLOCK_DATA = "BlockData";
	public static final String NBT_BLOCK_ENTITIES = "BlockEntities";
	public static final String NBT_BLOCK_ENTITIES_ID = "Id";
	public static final String NBT_BLOCK_ENTITIES_POS = "Pos";
	public static final String NBT_ENTITIES = "Entities";

	private static final Logger log = LoggerFactory.getLogger(SpongeSchematicVersion2Parser.class);

	@Override
	public Schematic parse(NamedTag root) {
		log.debug("Parsing schema");

		final CompoundTag rootTag = (CompoundTag) root.getTag();
		final SchematicBuilder builder = new SchematicBuilder();

		parseBlocks(rootTag, builder);
		parseBlockEntities(rootTag, builder);
		parseEntities(rootTag, builder);

		return builder.build();
	}

	private boolean exists(CompoundTag tag, String... requiredTags) throws ParsingException {
		for (String requiredTag : requiredTags) {
			if (!tag.containsKey(requiredTag)) {
				return false;
//				throw new ParsingException("Missing tag '" + requiredTag + '\'');
			}
		}

		return true;
	}

	private void parseBlocks(CompoundTag root, SchematicBuilder builder) {
		log.debug("Parsing blocks");

		if (!exists(root, NBT_WIDTH, NBT_HEIGHT, NBT_LENGTH, NBT_PALETTE, NBT_BLOCK_DATA)) {
			log.debug("Did not have block data");
			return;
		}

		short width  = root.getShort(NBT_WIDTH);
		short height = root.getShort(NBT_HEIGHT);
		short length = root.getShort(NBT_LENGTH);

		builder.width(width).height(height).length(length);
		log.trace("Dimensions: width={}, height={}, length={}", width, height, length);

		final CompoundTag palette = root.getCompoundTag(NBT_PALETTE);
		log.trace("Palette size: {}", palette.size());

		final int paletteMax = root.getInt(NBT_PALETTE_MAX);
		if (palette.size() != paletteMax)
			log.warn("Palette actual size does not match expected size. Expected {} but got {}", paletteMax, palette.size());

		Map<Integer, SchematicBlock> blockById = palette.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> ((IntTag) entry.getValue()).asInt(),
						entry -> new SchematicBlock(entry.getKey())
				));


		byte[] blockData = root.getByteArray(NBT_BLOCK_DATA);
		SchematicBlock[][][] loadedBlockData = new SchematicBlock[width][height][length];

		int expectedBlocks = width * height * length;
		if (blockData.length != expectedBlocks)
			log.warn("Number of blocks does not match expected. Expected {} blocks, but got {}", expectedBlocks, blockData.length);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					final int index = x + z*width + y*width*length; // Flatten (x,y,z) into a single dimension

					final int blockId = blockData[index] & 0xFF;
					final SchematicBlock block = blockById.get(blockId);

					loadedBlockData[x][y][z] = block;
				}
			}
		}

		builder.blocks(loadedBlockData);
	}

	@SuppressWarnings("unchecked")
	private void parseBlockEntities(CompoundTag root, SchematicBuilder builder) {
		log.debug("Parsing block entities");

		if (!exists(root, NBT_BLOCK_ENTITIES)) {
			log.debug("Did not have block entity data");
			return;
		}

		final ListTag<CompoundTag> blockEntities = root.getListTag(NBT_BLOCK_ENTITIES).asCompoundTagList();
		final Collection<SchematicBlockEntity> loadedBlockEntities = new ArrayList<>(blockEntities.size());

		for (CompoundTag blockEntity : blockEntities) {
			String id = blockEntity.getString(NBT_BLOCK_ENTITIES_ID);
			int[] pos = blockEntity.getIntArray(NBT_BLOCK_ENTITIES_POS);

			loadedBlockEntities.add(new SchematicBlockEntity(id, SchematicPos.from(pos)));
		}

		builder.blockEntities(loadedBlockEntities);
	}

	private void parseEntities(CompoundTag root, SchematicBuilder builder) {
		log.debug("Parsing entities");

		if (!exists(root, NBT_ENTITIES)) {
			log.debug("Did not have entity data");
			return;
		}

		final ListTag<CompoundTag> entities = root.getListTag(NBT_ENTITIES).asCompoundTagList();
		final Collection<SchematicEntity> loadedEntities = new ArrayList<>(entities.size());

		for (CompoundTag entity : entities) {
			String id = entity.getString(NBT_BLOCK_ENTITIES_ID);
			ListTag<DoubleTag> pos = entity.getListTag(NBT_BLOCK_ENTITIES_POS).asDoubleTagList();

//			Map<String, Object> extra = new HashMap<>();
//
//			for (Entry<String, Tag<?>> entry : entity.entrySet()) {
//				final String key = entry.getKey();
//				final Tag<?> tag = entry.getValue();
//
//				// TODO: sort cases alphabetically
//				switch (key) {
//				case "Facing":
//					((ByteTag) tag).asByte();
//				case "itemRotation":
//					((ByteTag) tag).asByte();
//				case "Invulnerable":
//					((ByteTag) tag).asByte();
//				case "PortalCooldown":
//					((IntTag) tag).asInt();
//				case "Item":
//					final CompoundTag itemTag = (CompoundTag) tag;
//					final String itemId = itemTag.getString("id");
//					final byte count = itemTag.getByte("Count");
//					final SchematicItem item = new SchematicItem(id, count);
//				case "FallDistance":
//					((FloatTag) tag).asFloat();
//				case "WorldUUIDMost":
//					((LongTag) tag).asLong(); // TODO: convert to UUID
//				case "WorldUUIDLeast":
//					((LongTag) tag).asLong(); // TODO: convert to UUID
//				case "UUID":
//					((IntArrayTag) tag).getValue(); // length == 4 // TODO: convert to UUID
//				case "TileX":
//					((IntTag) tag).asInt();
//				case "TileY":
//					((IntTag) tag).asInt();
//				case "Invisible":
//					((ByteTag) tag).asByte();
//				case "Fixed":
//					((ByteTag) tag).asByte();
//				case "Motion":
//					((ListTag<DoubleTag>) tag).toString(); // length == 3
//				case "OnGround":
//					((ByteTag) tag).asByte();
//				case "Air":
//					((ShortTag) tag).asShort();
//				case "Rotation":
//					((ListTag<FloatTag>) tag).toString(); // length == 3
//				case "ItemDropChance":
//					((FloatTag) tag).asFloat();
//				case "Pos":
//					((ListTag<DoubleTag>) tag).asByte(); // length == 3 // TODO: remove in favour of the hardcoded values? NAAAH
//				case "Fire":
//					((ShortTag) tag).asShort();
//				case "Id":
//					((StringTag) tag).getValue(); // TODO: remove in favour of the hardcoded values? NAAAH
//				default:
//					extra.put(key, unwrap(tag));
//				}
//			}

			loadedEntities.add(new SchematicEntity(id, null)); // TODO: use pos as SchematicPos.from(pos)
		}

		builder.entities(loadedEntities);
	}

	@Override
	public String toString() {
		return "ParserUnk()";
	}

}
