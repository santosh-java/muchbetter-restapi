package com.muchbetter.codetest.datamodel.db;

import java.io.Serializable;
import java.util.Currency;

public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6869394605414397226L;
	private static final String ACCOUNT_ID_PREFIX = "ACCOUNT_";
	private static long accountsCount = 1L;
	private String accountId;
	private double balance;
	private Currency currency;

	public Account() {
		this.accountId = ACCOUNT_ID_PREFIX + accountsCount++;
	}

	public Account(double balance, Currency currency) {
		this.accountId = "Account" + accountsCount++;
		this.balance = balance;
		this.currency = currency;
	}

	public String getAccountId() {
		return accountId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
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
		Account other = (Account) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", balance=" + balance + ", currency=" + currency + "]";
	}
}
