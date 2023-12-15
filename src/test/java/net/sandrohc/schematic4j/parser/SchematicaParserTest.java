package net.sandrohc.schematic4j.parser;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicaSchematic;

import static net.sandrohc.schematic4j.parser.TestUtils.assertSchematic;
import static net.sandrohc.schematic4j.parser.TestUtils.assertSchematicBlockIterator;
import static net.sandrohc.schematic4j.parser.TestUtils.nbtFromResource;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SnapshotExtension.class)
public class SchematicaParserTest {

	private Expect expect;

	@Test
	public void parser() throws ParsingException {
		final CompoundTag nbt = nbtFromResource("/schematics/schematica/12727.schematic");
		final Schematic schem = new SchematicaParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(SchematicaSchematic.class);

		final SoftAssertions softly = new SoftAssertions();
		softly.assertThat(schem.format()).isEqualTo(SchematicFormat.SCHEMATICA);
		softly.assertThat(schem.width()).isEqualTo(86);
		softly.assertThat(schem.height()).isEqualTo(82);
		softly.assertThat(schem.length()).isEqualTo(101);
		softly.assertThat(((SchematicaSchematic) schem).materials()).isEqualTo(SchematicaSchematic.MATERIAL_ALPHA);
		softly.assertAll();
	}

	@Test
	public void blockIterator() throws ParsingException {
		assertSchematicBlockIterator(expect, "/schematics/schematica/9383.schematic", new SchematicaParser());
	}

	/**
	 * Check if the schematics too big for snapshot still parse without errors.
	 */
	@ParameterizedTest
	@ValueSource(strings = {"/schematics/schematica/12727.schematic"})
	public void parsesNoSnapshot(String file) throws ParsingException {
		final CompoundTag nbt = nbtFromResource(file);
		final Schematic schem = new SchematicaParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(SchematicaSchematic.class);
	}

	@Test
	public void snapshot1() throws ParsingException {
		assertSchematic(expect, "/schematics/schematica/9383.schematic", new SchematicaParser());
	}
}
