package com.muchbetter.codetest.utils;

import java.util.UUID;

public class UUIDUtils {
	public static UUID getUUIDFromString(String uuidAsString) {
		try {
			return UUID.fromString(uuidAsString);
		} catch (IllegalArgumentException iae) {
			throw iae;
		}
	}
}
