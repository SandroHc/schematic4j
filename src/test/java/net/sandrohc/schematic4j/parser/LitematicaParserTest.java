package net.sandrohc.schematic4j.parser;

import java.time.LocalDateTime;

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
import net.sandrohc.schematic4j.schematic.LitematicaSchematic;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;
import net.sandrohc.schematic4j.schematic.types.SchematicBlockPos;

import static net.sandrohc.schematic4j.parser.TestUtils.assertSchematic;
import static net.sandrohc.schematic4j.parser.TestUtils.nbtFromResource;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SnapshotExtension.class)
public class LitematicaParserTest {

	private Expect expect;

	@Test
	public void parser() throws ParsingException {
		final CompoundTag nbt = nbtFromResource("/schematics/litematica/v6/demo.litematic");
		final Schematic schem = new LitematicaParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(LitematicaSchematic.class);

		SoftAssertions softly = new SoftAssertions();
		softly.assertThat(schem.format()).isEqualTo(SchematicFormat.LITEMATICA);
		softly.assertThat(schem.width()).isEqualTo(4);
		softly.assertThat(schem.height()).isEqualTo(3);
		softly.assertThat(schem.length()).isEqualTo(4);
		softly.assertThat(schem.name()).isEqualTo("Demo Schematic");
		softly.assertThat(schem.author()).isEqualTo("SandroHc");
		softly.assertThat(schem.date()).isEqualTo(LocalDateTime.parse("2023-12-14T22:06:47.465"));
		softly.assertThat(((LitematicaSchematic) schem).regions()).hasSize(1);
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.name).isEqualTo("Demo Schematic");
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.description).isEqualTo("");
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.author).isEqualTo("SandroHc");
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.timeCreated).isEqualTo(LocalDateTime.parse("2023-12-14T22:06:47.465"));
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.timeModified).isEqualTo(LocalDateTime.parse("2023-12-14T22:06:47.465"));
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.enclosingSize).isEqualTo(new SchematicBlockPos(4, 3, 4));
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.regionCount).isEqualTo(1);
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.totalBlocks).isEqualTo(8L);
		softly.assertThat(((LitematicaSchematic) schem).metadata()).extracting(o -> o.totalVolume).isEqualTo(48L);
		softly.assertThat(((LitematicaSchematic) schem).metadata()).satisfies(o -> assertThat(o.extra).isEmpty());
		softly.assertAll();

		final LitematicaSchematic.Region region = ((LitematicaSchematic) schem).regions[0];
		softly = new SoftAssertions();
		softly.assertThat(region.name).isEqualTo("Demo Sub-region 1");
		softly.assertThat(region.position).isEqualTo(new SchematicBlockPos(0, 0, 0));
		softly.assertThat(region.size).isEqualTo(new SchematicBlockPos(4, 3, 4));
		softly.assertThat(region.blockStates).hasSize(48);
		softly.assertThat(region.blockStatePalette).hasSize(6);
		softly.assertThat(region.blockEntities).hasSize(1);
		softly.assertThat(region.entities).hasSize(1);
		softly.assertThat(region.pendingBlockTicks).isEmpty();
		softly.assertThat(region.pendingFluidTicks).isEmpty();
		softly.assertAll();

		softly = new SoftAssertions();
		softly.assertThat(schem.block(0, 0, 0)).isEqualTo(new SchematicBlock("minecraft:white_wool"));
		softly.assertThat(schem.block(1, 0, 1)).isEqualTo(new SchematicBlock("minecraft:chest[facing=south,type=single,waterlogged=false]"));
		// X axis
		softly.assertThat(schem.block(1, 0, 0)).isEqualTo(new SchematicBlock("minecraft:red_wool"));
		softly.assertThat(schem.block(2, 0, 0)).isEqualTo(new SchematicBlock("minecraft:red_wool"));
		softly.assertThat(schem.block(3, 0, 0)).isEqualTo(new SchematicBlock("minecraft:air"));
		// Y axis
		softly.assertThat(schem.block(0, 1, 0)).isEqualTo(new SchematicBlock("minecraft:lime_wool"));
		softly.assertThat(schem.block(0, 2, 0)).isEqualTo(new SchematicBlock("minecraft:lime_wool"));
		softly.assertThat(schem.block(0, 3, 0)).isEqualTo(new SchematicBlock("minecraft:air"));
		// Z axis
		softly.assertThat(schem.block(0, 0, 1)).isEqualTo(new SchematicBlock("minecraft:light_blue_wool"));
		softly.assertThat(schem.block(0, 0, 2)).isEqualTo(new SchematicBlock("minecraft:light_blue_wool"));
		softly.assertThat(schem.block(0, 0, 3)).isEqualTo(new SchematicBlock("minecraft:air"));
		// Outside the boundaries of the schematic
		softly.assertThat(schem.block(-1, 0, 0)).isEqualTo(new SchematicBlock("minecraft:air"));
		softly.assertThat(schem.block(-1, 1, 1)).isEqualTo(new SchematicBlock("minecraft:air"));
		softly.assertThat(schem.block(0, -1, 0)).isEqualTo(new SchematicBlock("minecraft:air"));
		softly.assertThat(schem.block(1, -1, 1)).isEqualTo(new SchematicBlock("minecraft:air"));
		softly.assertThat(schem.block(0, 0, -1)).isEqualTo(new SchematicBlock("minecraft:air"));
		softly.assertThat(schem.block(1, 1, -1)).isEqualTo(new SchematicBlock("minecraft:air"));
		softly.assertAll();
	}

	/**
	 * Check if the schematics too big for snapshot still parse without errors.
	 */
	@ParameterizedTest
	@ValueSource(strings = {"/schematics/litematica/v5/island.litematic"})
	public void parsesNoSnapshot(String file) throws ParsingException {
		final CompoundTag nbt = nbtFromResource(file);
		final Schematic schem = new LitematicaParser().parse(nbt);
		assertThat(schem).isNotNull().isInstanceOf(LitematicaSchematic.class);
	}

	@Test
	public void snapshot1() throws ParsingException {
		assertSchematic(expect, "/schematics/litematica/v6/demo.litematic", new LitematicaParser());
	}

	@Test
	public void snapshot2() throws ParsingException {
		assertSchematic(expect, "/schematics/litematica/v5/simple.litematic", new LitematicaParser());
	}

	@Test
	public void snapshot3() throws ParsingException {
		assertSchematic(expect, "/schematics/litematica/v5/mansion.litematic", new LitematicaParser());
	}

	@Test
	public void snapshot4() throws ParsingException {
		assertSchematic(expect, "/schematics/litematica/v5/tower.litematic", new LitematicaParser());
	}
}
