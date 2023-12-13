package net.sandrohc.schematic4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
	 * @param nbt The NBT input to check
	 * @return The format guesses from looking at the input, or {@link SchematicFormat#UNKNOWN} if no known format was found.
	 */
	public static @NonNull SchematicFormat guessFormat(@Nullable NamedTag nbt) {
		if (nbt == null || !(nbt.getTag() instanceof CompoundTag)) {
			return SchematicFormat.UNKNOWN;
		}
		final CompoundTag rootTag = (CompoundTag) nbt.getTag();

		final Candidates<SchematicFormat> candidates = new Candidates<>();

		guessSpongeFormat(candidates, nbt, rootTag);
		guessSchematicaFormat(candidates, nbt, rootTag);

		return candidates.best().orElse(SchematicFormat.UNKNOWN);
	}

	private static void guessSpongeFormat(Candidates<SchematicFormat> candidates, @NonNull NamedTag nbt, CompoundTag rootTag) {
		if (nbt.getName().equals(SpongeSchematicParser.NBT_ROOT)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 2);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_VERSION)) {
			final int version = rootTag.getInt("Version");
			switch (version) {
				case 1:
					candidates.increment(SchematicFormat.SPONGE_V1, 1);
				case 2:
					candidates.increment(SchematicFormat.SPONGE_V2, 1);
			}
		} else {
			candidates.exclude(SchematicFormat.SPONGE_V1);
			candidates.exclude(SchematicFormat.SPONGE_V2);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_DATA_VERSION)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		} else {
			candidates.exclude(SchematicFormat.SPONGE_V2);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_WIDTH)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_HEIGHT)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_LENGTH)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_PALETTE)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeSchematicParser.NBT_BLOCK_DATA)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
	}

	private static void guessSchematicaFormat(Candidates<SchematicFormat> candidates, @NonNull NamedTag nbt, CompoundTag rootTag) {
		if (nbt.getName().equals(SchematicaParser.NBT_ROOT)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 1);
		}
		if (rootTag.containsKey(SchematicaParser.NBT_MAPPING_SCHEMATICA)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 10);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
		if (rootTag.containsKey(SchematicaParser.NBT_WIDTH)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 1);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
		if (rootTag.containsKey(SchematicaParser.NBT_HEIGHT)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 1);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
		if (rootTag.containsKey(SchematicaParser.NBT_LENGTH)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 1);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
	}

	protected static class Candidates<T> {
		protected final Map<T, Integer> candidates = new HashMap<>();
		protected final Set<T> excluded = new HashSet<>();

		public void increment(T candidate, int weight) {
			if (!excluded.contains(candidate)) {
				final int currentWeight = candidates.getOrDefault(candidate, 0);
				candidates.put(candidate, currentWeight + weight);
				log.trace("Format candidate {} prioritized by {} (total: {})", candidate, weight, currentWeight + weight);
			}
		}

		public void exclude(T candidate) {
			log.trace("Excluded format: {}", candidate);
			excluded.add(candidate);
			candidates.remove(candidate);
		}

		public Optional<T> best() {
			final Optional<Map.Entry<T, Integer>> best = candidates.entrySet().stream().reduce((a, b) -> {
				if (a.getValue() >= b.getValue()) {
					return a;
				} else {
					return b;
				}
			});
			log.trace("Excluded formats: {}", excluded);
			log.trace("Format candidates: {}", candidates);
			log.trace("Best candidate: {}", best);
			return best.map(Map.Entry::getKey);
		}
	}
}
