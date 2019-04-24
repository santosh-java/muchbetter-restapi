package com.muchbetter.codetest.exception;

public class InvalidUserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1407395771201648766L;

	public InvalidUserException() {
		super("Invalid User!!!");
	}

	public InvalidUserException(String message) {
		super(message);
	}

	public InvalidUserException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
