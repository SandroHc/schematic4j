package net.sandrohc.schematic4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.NoParserFoundException;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SpongeSchematicParser;
import net.sandrohc.schematic4j.schematic.Schematic;

public class SchematicUtil {

	private static final Logger log = LoggerFactory.getLogger(SchematicUtil.class);


	public static Schematic load(String file) throws IOException {
		return load(Paths.get(file));
	}

	public static Schematic load(File file) throws IOException {
		return load(file.toPath());
	}

	public static Schematic load(Path path) throws IOException {
		try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
			return load(is);
		}
	}

	public static Schematic load(InputStream is) throws IOException {
		final NamedTag rootTag = NBTUtil.Reader.read().from(is);

		Parser parser = findParser(rootTag).orElseThrow(NoParserFoundException::new);
		log.debug("Found parser: {}", parser);

		return parser.parse(rootTag);

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
	}

	private static Optional<Parser> findParser(NamedTag root) {
		if (!root.getName().equals(Constants.NBT_ROOT))
			log.warn("Root tag does not follow the standard. Expected a tag named '{}' but got '{}'", Constants.NBT_ROOT, root.getName());

		if (root.getTag() instanceof CompoundTag) {
			final CompoundTag rootCompound = (CompoundTag) root.getTag();

			if (containsAllTags(rootCompound, "Version", "Width", "Height", "Length", "BlockData", "Palette")) {
				return Optional.of(new SpongeSchematicParser());
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
		} else if (value instanceof IntArrayTag) {
			return ((IntArrayTag) value).getValue();
		} else if (value instanceof ByteArrayTag) {
			return ((ByteArrayTag) value).getValue();
		} else if (value instanceof LongArrayTag) {
			return ((LongArrayTag) value).getValue();
		} else {
			return value;
		}
	}

	public static boolean containsAllTags(CompoundTag tag, String... requiredTags) throws ParsingException {
		return Arrays.stream(requiredTags).allMatch(tag::containsKey);
	}

	public static boolean containsTag(CompoundTag tag, String... optionalTags) throws ParsingException {
		return Arrays.stream(optionalTags).anyMatch(tag::containsKey);
	}

}
