package com.muchbetter.codetest.exception;

public class TransactionRollbackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8789091416624173718L;

	public TransactionRollbackException() {
		super("Rollback of transaction has failed!!!");
	}

	public TransactionRollbackException(String message) {
		super(message);
	}

	public TransactionRollbackException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
