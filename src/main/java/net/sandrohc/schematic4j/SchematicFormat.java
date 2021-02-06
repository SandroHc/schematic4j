package net.sandrohc.schematic4j;

import java.util.function.Supplier;

import net.sandrohc.schematic4j.exception.NoParserFoundException;
import net.sandrohc.schematic4j.parser.Parser;
import net.sandrohc.schematic4j.parser.SpongeSchematicParser;

public enum SchematicFormat {
	SPONGE_V1      ("schem", SpongeSchematicParser::new),
	SPONGE_V2      ("schem", SpongeSchematicParser::new),
	LITEMATICA     ("litematic"),
	SCHEMATICA     ("schematic"),
	WORLD_EDITOR   ("schematic"),
	MCEDIT         ("schematic"),
	MCEDIT_UNIFIED ("schematic"),
	MCEDIT2        ("schematic"),
	UNKNOWN;


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

}
