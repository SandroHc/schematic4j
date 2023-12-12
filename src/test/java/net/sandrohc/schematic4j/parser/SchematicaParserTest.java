package net.sandrohc.schematic4j.parser;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NBTUtil;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;

public class SchematicaParserTest {

	@Test
	public void load() throws ParsingException, IOException {
//		InputStream is = this.getClass().getResourceAsStream("/schematics/schematica/9383.schematic");
		InputStream is = this.getClass().getResourceAsStream("/schematics/schematica/12727.schematic");
		NamedTag rootTag = NBTUtil.Reader.read().from(is);

//		schematic = SchematicUtil.parse(rootTag);
		final Schematic schematic = new SchematicaParser().parse(rootTag);

//		assertNotNull(schematic);
	}

}
