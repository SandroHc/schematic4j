package net.sandrohc.schematic4j;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.SchematicSponge;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SpongeSchematicParserTest {

	@Test
	public void load() throws ParsingException, IOException {
		final InputStream is = this.getClass().getResourceAsStream("/schematics/sponge/issue-1.schem");
		final Schematic schem = SchematicUtil.load(is);
		assertNotNull(schem);
		assertInstanceOf(SchematicSponge.class, schem);

		final SchematicSponge spongeSchem = (SchematicSponge) schem;
		assertEquals(SchematicFormat.SPONGE_V2, spongeSchem.getFormat());
		assertEquals(2860, spongeSchem.getDataVersion());
		assertNotNull(spongeSchem.getMetadata());
		assertNull(spongeSchem.getName());
		assertNull(spongeSchem.getAuthor());
		assertNull(spongeSchem.date());
		assertEquals(1, spongeSchem.getWidth());
		assertEquals(41, spongeSchem.getHeight());
		assertEquals(9, spongeSchem.getLength());
		assertArrayEquals(new int[]{22, -60, 13}, spongeSchem.getOffset());
		assertTrue(spongeSchem.getBlocks().hasNext());
		assertTrue(spongeSchem.getBlockEntities().isEmpty());
		assertTrue(spongeSchem.getEntities().isEmpty());
		assertFalse(spongeSchem.getBiomes().hasNext());
	}

	@Test
	public void blockApi() throws ParsingException, IOException {
		final InputStream is = this.getClass().getResourceAsStream("/schematics/sponge/issue-1.schem");
		final Schematic schem = SchematicUtil.load(is);

		assertEquals("minecraft:stone", schem.getBlock(0, 0, 0).block);
//		assertEquals("minecraft:granite", schem.getBlock(0, 1, 0).block);
//		assertEquals("minecraft:polished_granite", schem.getBlock(0, 2, 0).block);
//		assertEquals("minecraft:diorite", schem.getBlock(0, 3, 0).block);
//		assertEquals("minecraft:polished_diorite", schem.getBlock(0, 4, 0).block);
//		assertEquals("minecraft:andesite", schem.getBlock(0, 5, 0).block);
//		assertEquals("minecraft:polished_andesite", schem.getBlock(0, 6, 0).block);
//		assertEquals("minecraft:deepslate", schem.getBlock(0, 7, 0).block);
//		assertEquals("minecraft:cobbled_deepslate", schem.getBlock(0, 8, 0).block);
	}
}
