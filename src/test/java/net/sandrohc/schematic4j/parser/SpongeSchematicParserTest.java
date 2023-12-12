package net.sandrohc.schematic4j.parser;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import net.sandrohc.schematic4j.SchematicFormat;
import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSponge;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpongeSchematicParserTest {

	@Test
	public void load() throws ParsingException, IOException {
		final InputStream is = this.getClass().getResourceAsStream("/schematics/sponge/issue-1.schem");
		final Schematic schem = SchematicLoader.load(is);
		assertNotNull(schem);
		assertInstanceOf(SchematicSponge.class, schem);

		final SchematicSponge spongeSchem = (SchematicSponge) schem;
		assertEquals(SchematicFormat.SPONGE_V2, spongeSchem.format());
		assertEquals(2860, spongeSchem.dataVersion());
		assertNotNull(spongeSchem.metadata());
		assertNull(spongeSchem.name());
		assertNull(spongeSchem.author());
		assertNull(spongeSchem.date());
		assertEquals(1, spongeSchem.width());
		assertEquals(41, spongeSchem.height());
		assertEquals(9, spongeSchem.length());
		assertArrayEquals(new int[]{22, -60, 13}, spongeSchem.offset());
		assertTrue(spongeSchem.blocks().hasNext());
		assertTrue(spongeSchem.blockEntities().isEmpty());
		assertTrue(spongeSchem.entities().isEmpty());
		assertFalse(spongeSchem.biomes().hasNext());
	}

	@Test
	public void blockApi() throws ParsingException, IOException {
		final InputStream is = this.getClass().getResourceAsStream("/schematics/sponge/issue-1.schem");
		final Schematic schem = SchematicLoader.load(is);

		assertEquals("minecraft:stone", schem.block(0, 0, 0).block);
//		assertEquals("minecraft:granite", schem.block(0, 1, 0).block);
//		assertEquals("minecraft:polished_granite", schem.block(0, 2, 0).block);
//		assertEquals("minecraft:diorite", schem.block(0, 3, 0).block);
//		assertEquals("minecraft:polished_diorite", schem.block(0, 4, 0).block);
//		assertEquals("minecraft:andesite", schem.block(0, 5, 0).block);
//		assertEquals("minecraft:polished_andesite", schem.block(0, 6, 0).block);
//		assertEquals("minecraft:deepslate", schem.block(0, 7, 0).block);
//		assertEquals("minecraft:cobbled_deepslate", schem.block(0, 8, 0).block);
	}
}
