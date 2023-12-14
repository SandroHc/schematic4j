package net.sandrohc.schematic4j;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.sandrohc.schematic4j.nbt.tag.CompoundTag;

import static net.sandrohc.schematic4j.parser.TestUtils.nbtFromResource;
import static org.assertj.core.api.Assertions.assertThat;

public class SchematicFormatTest {

	@ParameterizedTest
	@MethodSource("guessFormatData")
	void guessFormat(SchematicFormat expected, String file) {
		final CompoundTag nbt = nbtFromResource(file);
		final SchematicFormat actual = SchematicFormat.guessFormat(nbt);
		assertThat(actual).isEqualTo(expected);
	}

	private static Stream<Arguments> guessFormatData() {
		return Stream.of(
				Arguments.of(SchematicFormat.SPONGE_V1, "/schematics/sponge/v1/sponge-v1.schem"),
				Arguments.of(SchematicFormat.SPONGE_V2, "/schematics/sponge/v2/issue-1.schem"),
				Arguments.of(SchematicFormat.SPONGE_V2, "/schematics/sponge/v2/green-cottage.schem"),
				Arguments.of(SchematicFormat.SPONGE_V2, "/schematics/sponge/v2/interieur-exterieur-chunk-project.schem"),
				Arguments.of(SchematicFormat.SPONGE_V3, "/schematics/sponge/v3/sponge-v3.schem"),
				Arguments.of(SchematicFormat.SCHEMATICA, "/schematics/schematica/9383.schematic"),
				Arguments.of(SchematicFormat.SCHEMATICA, "/schematics/schematica/12727.schematic")
		);
	}
}
