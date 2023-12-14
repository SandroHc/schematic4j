package net.sandrohc.schematic4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.schematic.Schematic;

/**
 * A collection of utility methods to work with schematics.
 *
 * @deprecated Use {@link SchematicLoader} instead.
 */
@Deprecated
public class SchematicUtil {

	private SchematicUtil() {
	}

	/**
	 * Load a schematic from an input stream.
	 *
	 * @param is The input stream to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException      in case of I/O error
	 * @see SchematicLoader#load(Path)
	 * @see SchematicLoader#load(File)
	 * @see SchematicLoader#load(String)
	 * @deprecated Use {@link SchematicLoader#load(InputStream)} instead.
	 */
	@Deprecated
	public static @NonNull Schematic load(@NonNull InputStream is) throws ParsingException, IOException {
		return SchematicLoader.load(is);
	}

	/**
	 * Load a schematic from a file.
	 *
	 * @param path The file to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException      in case of I/O error
	 * @see SchematicLoader#load(InputStream)
	 * @see SchematicLoader#load(File)
	 * @see SchematicLoader#load(String)
	 * @deprecated Use {@link SchematicLoader#load(Path)} instead.
	 */
	@Deprecated
	public static @NonNull Schematic load(@NonNull Path path) throws ParsingException, IOException {
		return SchematicLoader.load(path);
	}

	/**
	 * Load a schematic from a file.
	 *
	 * @param file The file to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException      in case of I/O error
	 * @see SchematicLoader#load(InputStream)
	 * @see SchematicLoader#load(Path)
	 * @see SchematicLoader#load(String)
	 * @deprecated Use {@link SchematicLoader#load(File)} instead.
	 */
	@Deprecated
	public static @NonNull Schematic load(@NonNull File file) throws ParsingException, IOException {
		return SchematicLoader.load(file);
	}

	/**
	 * Load a schematic from a file.
	 *
	 * @param filePath The file path to load the schematic from.
	 * @return The loaded and parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @throws IOException      in case of I/O error
	 * @see SchematicLoader#load(InputStream)
	 * @see SchematicLoader#load(Path)
	 * @see SchematicLoader#load(File)
	 * @deprecated Use {@link SchematicLoader#load(String)} instead.
	 */
	@Deprecated
	public static @NonNull Schematic load(@NonNull String filePath) throws ParsingException, IOException {
		return SchematicLoader.load(filePath);
	}

	/**
	 * Attempts to guess the schematic format and parse the input.
	 * <br>
	 * If you already know the format, consider parsing the NBT tag directly - i.e. {@code SchematicFormat.SPONGE_V2.createParser().parse(nbt)}.
	 *
	 * @param input The NBT root tag to parse.
	 * @return The parsed schematic
	 * @throws ParsingException in case no supported parses was found or there was a parsing error
	 * @deprecated Use {@link SchematicLoader#parse(NamedTag)} instead.
	 */
	@Deprecated
	public static @NonNull Schematic parse(@Nullable NamedTag input) throws ParsingException {
		return SchematicLoader.parse(input);
	}

	/**
	 * Tries to guess the schematic format of the input.
	 *
	 * @param input The NBT input to check
	 * @return The format guesses from looking at the input, or {@link SchematicFormat#UNKNOWN} if no known format was found.
	 * @deprecated Use {@link SchematicFormat#guessFormat(CompoundTag)} instead
	 */
	@Deprecated
	@NonNull
	public static SchematicFormat detectFormat(@Nullable NamedTag input) {
		final CompoundTag nbt = input != null && input.getTag() instanceof CompoundTag ? (CompoundTag) input.getTag() : null;
		return SchematicFormat.guessFormat(nbt);
	}
}
