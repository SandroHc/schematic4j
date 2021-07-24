package net.sandrohc.schematic4j;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;

import static org.junit.jupiter.api.Assertions.*;

public class SpongeSchematicParserTest {

	@Test
	public void load() throws ParsingException, IOException {
//		InputStream is = this.getClass().getResourceAsStream("/schematics/schematica/9383.schematic");
		InputStream is = this.getClass().getResourceAsStream("/schematics/schematica/12727.schematic");
		Schematic schematic = SchematicUtil.load(is);

		assertNotNull(schematic);
	}

}
