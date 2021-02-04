package net.sandrohc.schematic4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.NoParserFoundException;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SpongeSchematicVersion2Parser;
import net.sandrohc.schematic4j.schematic.Schematic;

public class SchematicUtil {

	private static final Logger log = LoggerFactory.getLogger(SchematicUtil.class);

	public static Schematic load(Path path) throws IOException {
		final File file = path.toFile();
		final NamedTag rootTag = NBTUtil.read(file);

		Optional<Parser> parser = findSuitableParser(rootTag);
		return parser.orElseThrow(NoParserFoundException::new).parse(rootTag);

//		Tag<?> width = rootTag.get("Width");
//		Tag<?> height = rootTag.get("Height");
//		Tag<?> length = rootTag.get("Length");
//		Tag<?> materials = rootTag.get("Materials");
//		Tag<?> blocks = rootTag.get("Blocks");
//		Tag<?> addBlocks = rootTag.get("AddBlocks");
//		Tag<?> add = rootTag.get("Add");
//		Tag<?> data = rootTag.get("Data");
//		Tag<?> entities = rootTag.get("Entities");
//		Tag<?> tileEntities = rootTag.get("TileEntities");
//		Tag<?> icon = rootTag.get("Icon");
//		Tag<?> schematicaMapping = rootTag.get("SchematicaMapping");
//		Tag<?> extendedMetadata = rootTag.get("ExtendedMetadata");
//		Tag<?> weOriginX = rootTag.get("WEOriginX");
//		Tag<?> weOriginY = rootTag.get("WEOriginY");
//		Tag<?> weOriginZ = rootTag.get("WEOriginZ");


//		final int dataVersion = ((IntTag) rootTag.get("DataVersion")).asInt();
//
//		CompoundTag metadata = ((CompoundTag) rootTag.get("Metadata"));
//		int weOffsetX = ((IntTag) metadata.get("WEOffsetX")).asInt();
//		int weOffsetY = ((IntTag) metadata.get("WEOffsetY")).asInt();
//		int weOffsetZ = ((IntTag) metadata.get("WEOffsetZ")).asInt();
//
//		ListTag<CompoundTag> blockEntities = ((ListTag<CompoundTag>) rootTag.get("BlockEntities"));
//		ListTag<CompoundTag> entities = ((ListTag<CompoundTag>) rootTag.get("Entities"));
//
//		int[] offset = ((IntArrayTag) rootTag.get("Offset")).getValue();
//		int offsetX = offset[0]; // TODO: check if it's the correct index
//		int offsetY = offset[1]; // TODO: check if it's the correct index
//		int offsetZ = offset[2]; // TODO: check if it's the correct index
//
//		return readBlockData(rootTag);
	}

	private static Optional<Parser> findSuitableParser(NamedTag root) {
		if (!root.getName().equals(Constants.NBT_ROOT))
			log.warn("Root tag does not follow the standard. Expected a tag named '{}' but got '{}'", Constants.NBT_ROOT, root.getName());

		if (root.getTag() instanceof CompoundTag) {
			final CompoundTag rootCompound = (CompoundTag) root.getTag();

			if (rootCompound.containsKey("Version")) {
				final int version = rootCompound.get("Version", IntTag.class).asInt();

				if (version == 2) {
					return Optional.of(new SpongeSchematicVersion2Parser());
				}
			}
		}

		return Optional.empty();
	}

	public static Object unwrap(Tag<?> value) {
		if (value instanceof StringTag) {
			return ((StringTag) value).getValue();
		} else if (value instanceof LongTag) {
			return ((LongTag) value).asLong();
		} else if (value instanceof IntTag) {
			return ((IntTag) value).asInt();
		} else if (value instanceof ShortTag) {
			return ((ShortTag) value).asShort();
		} else if (value instanceof ByteTag) {
			return ((ByteTag) value).asByte();
		} else if (value instanceof FloatTag) {
			return ((FloatTag) value).asFloat();
		} else if (value instanceof DoubleTag) {
			return ((DoubleTag) value).asDouble();
		} else {
			return value;
		}
	}

}
