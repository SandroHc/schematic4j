package net.sandrohc.schematic4j;

import java.util.function.Supplier;

import net.sandrohc.schematic4j.exception.NoParserFoundException;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SpongeSchematicParser;

public enum SchematicFormat {
	SPONGE_V1(SpongeSchematicParser::new),
	SPONGE_V2(SpongeSchematicParser::new),
	LITEMATICA,
	SCHEMATICA,
	WORLD_EDITOR,
	MCEDIT,
	MCEDIT_UNIFIED,
	MCEDIT2,
	UNKNOWN;

	private final Supplier<Parser> parserGenerator;

	SchematicFormat(Supplier<Parser> parserGenerator) {
		this.parserGenerator = parserGenerator;
	}

	SchematicFormat() {
		this(null);
	}

	public Parser createParser() throws NoParserFoundException {
		if (parserGenerator == null)
			throw new NoParserFoundException(this);

		return parserGenerator.get();
	}

}
