package com.muchbetter.codetest.exception;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6552528068231641670L;

	public AuthenticationException() {
		super("Authentication has failed!!!");
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
