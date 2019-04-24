package com.muchbetter.codetest.exception;

public class TransactionCreationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7176966592536654298L;

	public TransactionCreationException() {
		super("Exception occurred during creation of a transaction instance on DB!!!");
	}

	public TransactionCreationException(String message) {
		super(message);
	}

	public TransactionCreationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
