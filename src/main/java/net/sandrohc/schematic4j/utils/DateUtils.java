package net.sandrohc.schematic4j.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Collection of utility functions to work with dates.
 */
public class DateUtils {

	/**
	 * Convert an epoch time into a {@linkplain LocalDateTime}.
	 *
	 * @param epoch The epoch time, in milliseconds
	 * @return The {@linkplain LocalDateTime}
	 */
	public static LocalDateTime epochToDate(long epoch) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.of("UTC"));
	}
}
