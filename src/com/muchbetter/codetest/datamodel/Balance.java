package com.muchbetter.codetest.datamodel;

import java.io.Serializable;

public class Balance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6165008853274339564L;
	private double balance;
	private String currencyCode;

	public Balance() {
	}

	public Balance(double balance, String currencyCode) {
		this.balance = balance;
		this.currencyCode = currencyCode;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Override
	public String toString() {
		return "Balance [balance=" + balance + ", currencyCode=" + currencyCode + "]";
	}
}
