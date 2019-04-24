package com.muchbetter.codetest.exception;

public class DataBaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3353908438796761481L;

	public DataBaseException() {
		super("A Database exception has occurred!!!");
	}

	public DataBaseException(String message) {
		super(message);
	}

	public DataBaseException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
