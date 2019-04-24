/**
 * 
 */
package com.muchbetter.codetest.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author blues
 *
 */
/*
 * We are currently using Base64 encode/decode for generating the token This
 * class shall be modified in production so that we can use a better encoding
 * and decoding mechanism such as use a private key/secret to encode and decode
 * the userId we pass to the client.
 */
public class EncodeDecodeUtil {
	public static String getTokenFromUID(String userId) {
		return Base64.getEncoder().encodeToString(userId.getBytes(StandardCharsets.ISO_8859_1));
	}

	public static String getUIDFromToken(String token) {
		return new String(Base64.getDecoder().decode(token), StandardCharsets.ISO_8859_1);
	}
}
