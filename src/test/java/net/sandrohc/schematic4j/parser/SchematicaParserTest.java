package net.sandrohc.schematic4j.parser;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSchematica;

import static net.sandrohc.schematic4j.parser.TestUtils.assertSchematic;
import static net.sandrohc.schematic4j.parser.TestUtils.nbtFromResource;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SnapshotExtension.class)
public class SchematicaParserTest {

	private Expect expect;

	@Test
	public void parser() throws ParsingException {
		final NamedTag nbt = nbtFromResource("/schematics/schematica/12727.schematic");
		final Schematic schem = new SchematicaParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(SchematicSchematica.class);

		final SoftAssertions softly = new SoftAssertions();
		softly.assertThat(schem.format()).isEqualTo(SchematicFormat.SCHEMATICA);
		softly.assertThat(schem.width()).isEqualTo(86);
		softly.assertThat(schem.height()).isEqualTo(82);
		softly.assertThat(schem.length()).isEqualTo(101);
		softly.assertThat(((SchematicSchematica) schem).materials()).isEqualTo(SchematicSchematica.MATERIAL_ALPHA);
		softly.assertAll();
	}

	@Test
	public void snapshot1() throws ParsingException {
		assertSchematic(expect, "/schematics/schematica/9383.schematic", new SchematicaParser());
	}
}
