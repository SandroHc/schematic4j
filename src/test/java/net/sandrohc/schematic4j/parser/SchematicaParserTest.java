package net.sandrohc.schematic4j.parser;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import org.junit.jupiter.api.Test;

import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;

import static org.junit.jupiter.api.Assertions.*;

public class SchematicaParserTest {

	@Test
	public void load() throws ParsingException, IOException {
//		InputStream is = this.getClass().getResourceAsStream("/schematics/schematica/9383.schematic");
		InputStream is = this.getClass().getResourceAsStream("/schematics/schematica/12727.schematic");
		NamedTag rootTag = NBTUtil.Reader.read().from(is);

//		schematic = SchematicUtil.parse(rootTag);

		Instant start = Instant.now();
		int ITERATIONS = 1000;


		for (int i = 0; i < ITERATIONS; i++) {
			final Schematic schematic = new SchematicaParser().parse(rootTag);
//			System.out.println(schematic);
		}

		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);

		long opNs = duration.dividedBy(ITERATIONS).getNano();
		long opMs = opNs / 1000000;
		String opTime = opMs + "." + (opNs - (opMs * 1000000));

		System.out.println("Took " + opTime + "ms per operation (total: " + duration + ")");

//		assertNotNull(schematic);
	}

}
