package com.muchbetter.codetest.datamodel.db;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Transaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1451691797181703191L;
	public static final String DEFAULT_CURRENCY = Currency.getInstance(Locale.US).getCurrencyCode();
	private UUID transactionId;
	private Date transactionDate = new Date();
	private String transactionDescription = "Default transaction description";
	private double transactionAmount = 0;
	private String transactionCurrency = DEFAULT_CURRENCY;
	private TransactionType transactionType = TransactionType.DEPOSIT;

	public Transaction() {
		this.transactionId = UUID.randomUUID();
	}

	public Transaction(Date transactionDate, String transactionDescription, double transactionAmount,
			String transactionCurrency, TransactionType transactionType) {
		this();
		// If any of the values passed in are null or invalid, we will set the default
		// values
		this.transactionDate = transactionDate;
		this.transactionDescription = transactionDescription;
		this.transactionAmount = transactionAmount;
		this.transactionCurrency = transactionCurrency;
		this.transactionType = transactionType;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionCurrency() {
		return transactionCurrency;
	}

	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(transactionAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
		result = prime * result + ((transactionDescription == null) ? 0 : transactionDescription.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
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
		Transaction other = (Transaction) obj;
		if (Double.doubleToLongBits(transactionAmount) != Double.doubleToLongBits(other.transactionAmount))
			return false;
		if (transactionDate == null) {
			if (other.transactionDate != null)
				return false;
		} else if (!transactionDate.equals(other.transactionDate))
			return false;
		if (transactionDescription == null) {
			if (other.transactionDescription != null)
				return false;
		} else if (!transactionDescription.equals(other.transactionDescription))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", transactionDate=" + transactionDate
				+ ", transactionDescription=" + transactionDescription + ", transactionAmount=" + transactionAmount
				+ ", transactionCurrency=" + transactionCurrency + "]";
	}
}
