package net.sandrohc.schematic4j.parser;

import java.time.LocalDateTime;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.nbt.tag.CompoundTag;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SpongeSchematic;

import static net.sandrohc.schematic4j.parser.TestUtils.assertSchematic;
import static net.sandrohc.schematic4j.parser.TestUtils.nbtFromResource;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SnapshotExtension.class)
public class SpongeSchematicParserTest {

	private Expect expect;

	@Test
	public void parses() throws ParsingException {
		final CompoundTag nbt = nbtFromResource("/schematics/sponge/v2/issue-1.schem");
		final Schematic schem = new SpongeSchematicParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(SpongeSchematic.class);

		final SoftAssertions softly = new SoftAssertions();
		softly.assertThat(schem.format()).isEqualTo(SchematicFormat.SPONGE_V2);
		softly.assertThat(schem.name()).isEqualTo("example_name");
		softly.assertThat(schem.author()).isEqualTo("example_author");
		softly.assertThat(schem.date()).isEqualTo(LocalDateTime.parse("2023-12-13T12:06:04.631"));
		softly.assertThat(schem.width()).isEqualTo(1);
		softly.assertThat(schem.height()).isEqualTo(41);
		softly.assertThat(schem.length()).isEqualTo(9);
		softly.assertThat(schem.offset()).containsExactly(22, -60, 13);
		softly.assertThat(schem.block(0, 0, 0)).extracting(o -> o.name).isEqualTo("minecraft:stone");
		softly.assertThat(schem.blocks()).hasNext();
		softly.assertThat(schem.blockEntities()).hasNext();
		softly.assertThat(schem.entities()).hasNext();
		softly.assertThat(schem.biomes()).isExhausted();
		softly.assertThat(((SpongeSchematic) schem).dataVersion()).isEqualTo(2860);
		softly.assertThat(((SpongeSchematic) schem).metadata()).isNotNull();
		softly.assertAll();
	}

	@Test
	public void snapshot1() throws ParsingException {
		assertSchematic(expect, "/schematics/sponge/v2/issue-1.schem", new SpongeSchematicParser());
	}

	@Test
	public void snapshot2() throws ParsingException {
		assertSchematic(expect, "/schematics/sponge/v2/green-cottage.schem", new SpongeSchematicParser());
	}

	@Test
	public void snapshot3() throws ParsingException {
		assertSchematic(expect, "/schematics/sponge/v3/sponge-v3.schem", new SpongeSchematicParser());
	}

	@Test
	public void snapshot4() throws ParsingException {
		assertSchematic(expect, "/schematics/sponge/v1/sponge-v1.schem", new SpongeSchematicParser());
	}
}
