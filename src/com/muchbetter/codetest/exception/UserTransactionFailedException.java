package com.muchbetter.codetest.exception;

public class UserTransactionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4852756306548644288L;

	public UserTransactionFailedException() {
		super("Exception occurred during performing transaction on a particular user account!!!");
	}

	public UserTransactionFailedException(String message) {
		super(message);
	}

	public UserTransactionFailedException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
