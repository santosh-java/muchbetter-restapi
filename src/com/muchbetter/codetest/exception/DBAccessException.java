package com.muchbetter.codetest.exception;

public class DBAccessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3106708020107922220L;

	public DBAccessException(String message) {
		super(message);
	}

	public DBAccessException() {
		super("Exception occurred during DB access!!!");
	}

	public DBAccessException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
