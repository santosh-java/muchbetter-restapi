package com.muchbetter.codetest.datamodel.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7519899114328576023L;
	private static final double DEFAULT_BALANCE = 100;
	public static final String DEFAULT_CURRENCY = Currency.getInstance(Locale.US).getCurrencyCode();
	private UUID userId;
	private double balance;
	private String currency;
	private List<Transaction> userTransactions;

	public User() {
		this.userId = UUID.randomUUID();
		this.balance = DEFAULT_BALANCE;
		this.currency = DEFAULT_CURRENCY;
		this.userTransactions = new ArrayList<Transaction>();
		Transaction defaultTrans = new Transaction(new Date(), "Account opening offer amount", DEFAULT_BALANCE,
				Transaction.DEFAULT_CURRENCY, TransactionType.DEPOSIT);
		this.userTransactions.add(defaultTrans);
	}

	public List<Transaction> getUserTransactions() {
		return userTransactions;
	}

	public void setUserTransactions(List<Transaction> userTransactions) {
		this.userTransactions = userTransactions;
	}

	public UUID getUserId() {
		return userId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", balance=" + balance + ", currency=" + currency + ", userTransactions="
				+ userTransactions + "]";
	}

}
