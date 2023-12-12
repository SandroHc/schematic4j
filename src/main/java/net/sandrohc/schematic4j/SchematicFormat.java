package net.sandrohc.schematic4j;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.NoParserFoundException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SchematicaParser;
import net.sandrohc.schematic4j.parser.SpongeSchematicParser;

import static net.sandrohc.schematic4j.utils.TagUtils.containsAllTags;

public enum SchematicFormat {
	SPONGE_V1      ("schem", SpongeSchematicParser::new),
	SPONGE_V2      ("schem", SpongeSchematicParser::new),
	LITEMATICA     ("litematic"),
	SCHEMATICA     ("schematic", SchematicaParser::new),
	WORLD_EDITOR   ("schematic"),
	MCEDIT         ("schematic"),
	MCEDIT_UNIFIED ("schematic"),
	MCEDIT2        ("schematic"),
	UNKNOWN;

	private static final Logger log = LoggerFactory.getLogger(SchematicFormat.class);

	public final String fileExtension;
	private final Supplier<Parser> parserGenerator;

	SchematicFormat(String fileExtension, Supplier<Parser> parserGenerator) {
		this.fileExtension = fileExtension;
		this.parserGenerator = parserGenerator;
	}

	SchematicFormat(String fileExtension) {
		this(fileExtension, null);
	}

	SchematicFormat() {
		this(null, null);
	}

	public Parser createParser() throws NoParserFoundException {
		if (parserGenerator == null)
			throw new NoParserFoundException(this);

		return parserGenerator.get();
	}

	/**
	 * Tries to guess the schematic format of the input.
	 *
	 * @param input The NBT input to check
	 * @return The format guesses from looking at the input, or {@link SchematicFormat#UNKNOWN} if no known format was found.
	 */
	// TODO: implement candidate system - candidate with most points gets selected
	@NonNull
	public static SchematicFormat guessFormat(@Nullable NamedTag input) {
		if (input == null) {
			return SchematicFormat.UNKNOWN;
		}
		if (!(input.getTag() instanceof CompoundTag)) {
			return SchematicFormat.UNKNOWN;
		}

		final CompoundTag rootTag = (CompoundTag) input.getTag();
		final String rootName = input.getName();

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
}
