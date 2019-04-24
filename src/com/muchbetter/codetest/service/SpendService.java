package com.muchbetter.codetest.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory.DataSourceType;
import com.muchbetter.codetest.datamodel.db.IDataSource;
import com.muchbetter.codetest.datamodel.db.Transaction;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InsufficientBalanceException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.exception.UserTransactionFailedException;

import ratpack.handling.Context;

public class SpendService implements IService<Transaction> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpendService.class);
	private static SpendService INSTANCE = new SpendService();
	private IDataSource dataSource;
	ObjectMapper mapper = new ObjectMapper();

	private SpendService() {
		dataSource = DataSourceFactory.getDataSource(DataSourceType.REDIS);
	}

	public static IService<Transaction> getInstance() {
		return INSTANCE;
	}

	@Override
	public Transaction perform(Context ctx) throws UserTransactionFailedException, InvalidUserException,
			DataBaseException, InsufficientBalanceException, Exception {
		LOGGER.info("Inside SpendService's peform method");
		Transaction transactionFromRequest = ctx.get(Transaction.class);
		LOGGER.info("Transaction obtained from the POST spend request (context registry0" + transactionFromRequest);
		UUID uuid = ctx.get(UUID.class);
		if (uuid != null) {
			Transaction transactionToReturn = this.dataSource.performUserTransaction(uuid.toString(),
					transactionFromRequest);
			LOGGER.info("Transaction returned after request is processed : " + transactionToReturn);
			return transactionToReturn;
		} else {
			throw new InvalidUserException("User data provided is invalid!!!");
		}
	}
}
