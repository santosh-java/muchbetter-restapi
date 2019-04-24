package com.muchbetter.codetest.datamodel;

import java.io.Serializable;

public class Token implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6273105811600648942L;
	private String token;

	public Token() {

	}

	public Token(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "Token [token=" + token + "]";
	}
}
