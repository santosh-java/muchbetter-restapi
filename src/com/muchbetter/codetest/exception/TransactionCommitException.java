package com.muchbetter.codetest.exception;

public class TransactionCommitException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8481010261573105323L;

	public TransactionCommitException() {
		super("Commit of transaction has failed!!!");
	}

	public TransactionCommitException(String message) {
		super(message);
	}

	public TransactionCommitException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
