package com.muchbetter.codetest.exception;

public class InsufficientBalanceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2048955952444263420L;

	public InsufficientBalanceException() {
		// TODO Auto-generated constructor stub
	}

	public InsufficientBalanceException(String message) {
		super(message);
	}

	public InsufficientBalanceException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
