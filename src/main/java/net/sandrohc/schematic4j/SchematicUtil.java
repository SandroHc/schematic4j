package net.sandrohc.schematic4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NBTUtil;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.ByteArrayTag;
import net.sandrohc.schematic4j.nbt.tag.ByteTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.nbt.tag.DoubleTag;
import net.sandrohc.schematic4j.nbt.tag.FloatTag;
import net.sandrohc.schematic4j.nbt.tag.IntArrayTag;
import net.sandrohc.schematic4j.nbt.tag.IntTag;
import net.sandrohc.schematic4j.nbt.tag.LongArrayTag;
import net.sandrohc.schematic4j.nbt.tag.LongTag;
import net.sandrohc.schematic4j.nbt.tag.ShortTag;
import net.sandrohc.schematic4j.nbt.tag.StringTag;
import net.sandrohc.schematic4j.nbt.tag.Tag;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SchematicaParser;
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

	// TODO: implement candidate system - candidate with most points gets selected
	public static SchematicFormat detectFormat(NamedTag root) {
		if (!(root.getTag() instanceof CompoundTag))
			return SchematicFormat.UNKNOWN;

		final CompoundTag rootTag = (CompoundTag) root.getTag();
		final String rootName = root.getName();

		// Check Sponge Schematic format
		if (rootName.equals(SpongeSchematicParser.NBT_ROOT) && containsAllTags(rootTag, "Version", "BlockData", "Palette")) {
			final int version = rootTag.getInt("Version");
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

		// Check Schematica format
		if (rootName.equals(SchematicaParser.NBT_ROOT) && containsAllTags(rootTag, "SchematicaMapping")) {
			return SchematicFormat.SCHEMATICA;
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
		} else if (value instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag) value;

			Map<String, Object> map = new LinkedHashMap<>(compoundTag.size());
			for (Map.Entry<String, Tag<?>> entry : compoundTag) map.put(entry.getKey(), unwrap(entry.getValue()));

			return map;
		} else if (value instanceof ListTag<?>) {
			ListTag<?> listTag = (ListTag<?>) value;

			List<Object> list = new ArrayList<>(listTag.size());
			for (Tag<?> tag : listTag) list.add(unwrap(tag));

			return list;
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
