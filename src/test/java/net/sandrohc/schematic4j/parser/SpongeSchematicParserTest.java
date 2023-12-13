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
import net.sandrohc.schematic4j.nbt.io.NamedTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSponge;

import static net.sandrohc.schematic4j.parser.TestUtils.nbtFromResource;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SnapshotExtension.class)
public class SpongeSchematicParserTest {

	private Expect expect;

	@Test
	public void parses() throws ParsingException {
		final NamedTag nbt = nbtFromResource("/schematics/sponge/issue-1.schem");
		final Schematic schem = new SpongeSchematicParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(SchematicSponge.class);

		final SoftAssertions softly = new SoftAssertions();
		softly.assertThat(schem.format()).isEqualTo(SchematicFormat.SPONGE_V2);
		softly.assertThat(schem.name()).isNull();
		softly.assertThat(schem.author()).isNull();
		softly.assertThat(schem.date()).isNull();
		softly.assertThat(schem.width()).isEqualTo(1);
		softly.assertThat(schem.height()).isEqualTo(41);
		softly.assertThat(schem.length()).isEqualTo(9);
		softly.assertThat(schem.offset()).containsExactly(22, -60, 13);
		softly.assertThat(schem.block(0, 0, 0).block).isEqualTo("minecraft:stone");
		softly.assertThat(schem.blocks()).hasNext();
		softly.assertThat(schem.blockEntities()).isEmpty();
		softly.assertThat(schem.entities()).isEmpty();
		softly.assertThat(schem.biomes()).isExhausted();
		softly.assertThat(((SchematicSponge) schem).dataVersion()).isEqualTo(2860);
		softly.assertThat(((SchematicSponge) schem).metadata()).isNotNull();
		softly.assertAll();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/schematics/sponge/issue-1.schem",
	})
	public void snapshot(String file) throws ParsingException {
		final NamedTag nbt = nbtFromResource(file);
		final Schematic schem = new SpongeSchematicParser().parse(nbt);
		expect.toMatchSnapshot(schem);
	}
}
