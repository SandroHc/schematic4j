package net.sandrohc.schematic4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import net.sandrohc.schematic4j.builder.SchematicBuilder;
import net.sandrohc.schematic4j.builder.SpongeSchematicBuilder;
import net.sandrohc.schematic4j.exception.NoBuilderFoundException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.exception.NoParserFoundException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.parser.LitematicaParser;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SchematicaParser;
import net.sandrohc.schematic4j.parser.SpongeParser;

public enum SchematicFormat {
	SPONGE_V1("schem", SpongeParser::new, SpongeSchematicBuilder::new),
	SPONGE_V2("schem", SpongeParser::new, SpongeSchematicBuilder::new),
	SPONGE_V3("schem", SpongeParser::new, SpongeSchematicBuilder::new),
	LITEMATICA("litematic", LitematicaParser::new, null),
	SCHEMATICA("schematic", SchematicaParser::new, null),
	UNKNOWN;

	private static final Logger log = LoggerFactory.getLogger(SchematicFormat.class);

	public final String fileExtension;
	private final Supplier<Parser> parserGenerator;
	private final Supplier<SchematicBuilder> builderGenerator;

	SchematicFormat(String fileExtension, Supplier<Parser> parserGenerator, Supplier<SchematicBuilder> builder) {
		this.fileExtension = fileExtension;
		this.parserGenerator = parserGenerator;
		this.builderGenerator = builder;
	}

	SchematicFormat(String fileExtension) {
		this(fileExtension, null, null);
	}

	SchematicFormat() {
		this(null, null, null);
	}

	public Parser createParser() throws NoParserFoundException {
		if (parserGenerator == null)
			throw new NoParserFoundException(this);

		return parserGenerator.get();
	}

	public SchematicBuilder createBuilder() throws NoBuilderFoundException {
		if (builderGenerator == null)
			throw new NoBuilderFoundException(this);

		return builderGenerator.get();
	}

	/**
	 * Tries to guess the schematic format of the input.
	 *
	 * @param nbt The NBT input to check
	 * @return The format guesses from looking at the input, or {@link SchematicFormat#UNKNOWN} if no known format was found.
	 */
	public static @NonNull SchematicFormat guessFormat(@Nullable CompoundTag nbt) {
		if (nbt == null) {
			return SchematicFormat.UNKNOWN;
		}

		final Candidates<SchematicFormat> candidates = new Candidates<>();
		guessSpongeFormat(candidates, nbt);
		guessLitematicaFormat(candidates, nbt);
		guessSchematicaFormat(candidates, nbt);

		final SchematicFormat guess = candidates.best().orElse(SchematicFormat.UNKNOWN);
		log.debug("Guessed {} as the format", guess);
		return guess;
	}

	private static void guessSpongeFormat(Candidates<SchematicFormat> candidates, @NonNull CompoundTag rootTag) {
		if (rootTag.containsKey(SpongeParser.NBT_VERSION)) {
			final int version = rootTag.getInt(SpongeParser.NBT_VERSION);
			switch (version) {
				case 1:
					candidates.increment(SchematicFormat.SPONGE_V1, 5);
				case 2:
					candidates.increment(SchematicFormat.SPONGE_V2, 5);
				case 3:
					candidates.increment(SchematicFormat.SPONGE_V3, 5);
			}
		} else {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.exclude(SchematicFormat.SPONGE_V2);
			candidates.exclude(SchematicFormat.SPONGE_V3);
		}
		if (rootTag.containsKey(SpongeParser.NBT_DATA_VERSION)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		} else {
			candidates.exclude(SchematicFormat.SPONGE_V2);
			candidates.exclude(SchematicFormat.SPONGE_V3);
		}
		if (rootTag.containsKey(SpongeParser.NBT_WIDTH)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_HEIGHT)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_LENGTH)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_PALETTE)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_PALETTE_MAX)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_BLOCK_DATA)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_BIOME_DATA)) {
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_TILE_ENTITIES)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_BLOCK_ENTITIES)) {
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_V3_BLOCKS)) {
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_V3_BIOMES)) {
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		}
		if (rootTag.containsKey(SpongeParser.NBT_METADATA)) {
			candidates.increment(SchematicFormat.SPONGE_V1, 1);
			candidates.increment(SchematicFormat.SPONGE_V2, 1);
			candidates.increment(SchematicFormat.SPONGE_V3, 1);
		}
	}

	private static void guessLitematicaFormat(Candidates<SchematicFormat> candidates, @NonNull CompoundTag nbt) {
		if (nbt.containsKey(LitematicaParser.NBT_MINECRAFT_DATA_VERSION)) {
			candidates.increment(SchematicFormat.LITEMATICA, 1);
		}
		if (nbt.containsKey(LitematicaParser.NBT_VERSION)) {
			candidates.increment(SchematicFormat.LITEMATICA, 1);
		}
		if (nbt.containsKey(LitematicaParser.NBT_METADATA)) {
			candidates.increment(SchematicFormat.LITEMATICA, 1);
		}
		if (nbt.containsKey(LitematicaParser.NBT_REGIONS)) {
			candidates.increment(SchematicFormat.LITEMATICA, 2);
		} else {
			candidates.exclude(SchematicFormat.LITEMATICA);
		}
	}

	private static void guessSchematicaFormat(Candidates<SchematicFormat> candidates, @NonNull CompoundTag nbt) {
		if (nbt.containsKey(SchematicaParser.NBT_MAPPING_SCHEMATICA)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 10);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
		if (nbt.containsKey(SchematicaParser.NBT_WIDTH)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 1);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
		if (nbt.containsKey(SchematicaParser.NBT_HEIGHT)) {
			candidates.increment(SchematicFormat.SCHEMATICA, 1);
		} else {
			candidates.exclude(SchematicFormat.SCHEMATICA);
		}
		if (nbt.containsKey(SchematicaParser.NBT_LENGTH)) {
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
			log.trace("Format candidates: {}", candidates);
			log.trace("Excluded formats: {}", excluded);
			log.trace("Best candidate: {}", best);
			return best.map(Map.Entry::getKey);
		}

		@Override
		public String toString() {
			return "Candidates[candidates=" + candidates + ", excluded=" + excluded + ']';
		}
	}
}
