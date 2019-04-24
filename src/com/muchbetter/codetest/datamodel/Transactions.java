package com.muchbetter.codetest.datamodel;

import java.io.Serializable;
import java.util.List;

import com.muchbetter.codetest.datamodel.db.Transaction;

public class Transactions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6188913610295681755L;
	private List<Transaction> transactions;

	public Transactions() {
	}

	public Transactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public String toString() {
		return "Transactions [transactions=" + transactions + "]";
	}

}
