package net.sandrohc.schematic4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SpongeSchematicParser;
import net.sandrohc.schematic4j.schematic.Schematic;

public class SchematicUtil {

	private static final Logger log = LoggerFactory.getLogger(SchematicUtil.class);


	public static Schematic load(String file) throws ParsingException, IOException {
		return load(Paths.get(file));
	}

	public static Schematic load(File file) throws ParsingException, IOException {
		return load(file.toPath());
	}

	public static Schematic load(Path path) throws ParsingException, IOException {
		try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
			return load(is);
		}
	}

	public static Schematic load(InputStream is) throws ParsingException, IOException {
		final NamedTag rootTag = NBTUtil.Reader.read().from(is);
		return parse(rootTag);
	}

	public static Schematic parse(NamedTag root) throws ParsingException {
		SchematicFormat format = detectFormat(root);
		log.info("Found format: {}", format);

		Parser parser = format.createParser();
		log.debug("Found parser: {}", parser);

		return parser.parse(root);
	}

	public static SchematicFormat detectFormat(NamedTag root) {
		if (!root.getName().equals(SpongeSchematicParser.NBT_ROOT))
			log.warn("Root tag does not follow the standard. Expected a tag named '{}' but got '{}'", SpongeSchematicParser.NBT_ROOT, root.getName());

		if (root.getTag() instanceof CompoundTag) {
			final CompoundTag rootCompound = (CompoundTag) root.getTag();

			// Check Sponge Schematic format
			if (containsAllTags(rootCompound, "Version", "Width", "Height", "Length", "BlockData", "Palette")) {
				final int version = rootCompound.getInt("Version");
				switch (version) {
				case 1:
					return SchematicFormat.SPONGE_V1;
				case 2:
					return SchematicFormat.SPONGE_V2;
				default:
					log.warn("Found Sponge Schematic with version {}, which is not supported. Using parser for version 2", version);
					return SchematicFormat.SPONGE_V2;
				}
			}
		}

		return SchematicFormat.UNKNOWN;
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

	public static boolean containsAllTags(CompoundTag tag, String... requiredTags) {
		return Arrays.stream(requiredTags).allMatch(tag::containsKey);
	}

	public static boolean containsTag(CompoundTag tag, String... optionalTags) {
		return Arrays.stream(optionalTags).anyMatch(tag::containsKey);
	}
}
