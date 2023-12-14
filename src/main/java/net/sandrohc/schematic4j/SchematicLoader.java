package net.sandrohc.schematic4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NBTUtil;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.schematic.Schematic;

/**
 * A collection of utility methods to load and parse schematics.
 */
public class SchematicLoader {

	private static final Logger log = LoggerFactory.getLogger(SchematicLoader.class);

	private SchematicLoader() {}

	/**
	 * Load a schematic from an input stream.
	 *
	 * @param is The input stream to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException in case of I/O error
	 * @see SchematicLoader#load(Path)
	 * @see SchematicLoader#load(File)
	 * @see SchematicLoader#load(String)
	 */
	public static @NonNull Schematic load(@NonNull InputStream is) throws ParsingException, IOException {
		final NamedTag rootTag = NBTUtil.Reader.read().from(is);
		return parse(rootTag);
	}

	/**
	 * Load a schematic from a file.
	 *
	 * @param path The file to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException in case of I/O error
	 * @see SchematicLoader#load(InputStream)
	 * @see SchematicLoader#load(File)
	 * @see SchematicLoader#load(String)
	 */
	public static @NonNull Schematic load(@NonNull Path path) throws ParsingException, IOException {
		try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
			return load(is);
		}
	}

	/**
	 * Load a schematic from a file.
	 *
	 * @param file The file to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException in case of I/O error
	 * @see SchematicLoader#load(InputStream)
	 * @see SchematicLoader#load(Path)
	 * @see SchematicLoader#load(String)
	 */
	public static @NonNull Schematic load(@NonNull File file) throws ParsingException, IOException {
		return load(file.toPath());
	}

	/**
	 * Load a schematic from a file.
	 *
	 * @param filePath The file path to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException in case of I/O error
	 * @see SchematicLoader#load(InputStream)
	 * @see SchematicLoader#load(Path)
	 * @see SchematicLoader#load(File)
	 */
	public static @NonNull Schematic load(@NonNull String filePath) throws ParsingException, IOException {
		return load(Paths.get(filePath));
	}

	/**
	 * Attempts to guess the schematic format and parse the input.
	 * <br>
	 * If you already know the format, consider parsing the NBT tag directly - i.e. {@code SchematicFormat.SPONGE_V2.createParser().parse(nbt)}.
	 *
	 * @param nbt The NBT root tag to parse.
	 * @return The parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 */
	public static @NonNull Schematic parse(@Nullable CompoundTag nbt) throws ParsingException {
		SchematicFormat format = SchematicFormat.guessFormat(nbt);
		log.info("Found format: {}", format);

		Parser parser = format.createParser();
		log.debug("Found parser: {}", parser);

		return parser.parse(nbt);
	}

	/**
	 * Attempts to guess the schematic format and parse the input.
	 * <br>
	 * If you already know the format, consider parsing the NBT tag directly - i.e. {@code SchematicFormat.SPONGE_V2.createParser().parse(nbt)}.
	 *
	 * @param input The NBT root tag to parse.
	 * @return The parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 */
	public static @NonNull Schematic parse(@Nullable NamedTag input) throws ParsingException {
		final CompoundTag nbt = input!=null&& input.getTag() instanceof CompoundTag? (CompoundTag) input.getTag() :null;
		return parse(nbt);
	}
}
