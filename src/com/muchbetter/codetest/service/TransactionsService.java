package com.muchbetter.codetest.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.db.DataSourceFactory;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory.DataSourceType;
import com.muchbetter.codetest.datamodel.db.IDataSource;
import com.muchbetter.codetest.datamodel.db.Transaction;

import ratpack.handling.Context;

public class TransactionsService implements IService<List<Transaction>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsService.class);
	private static TransactionsService INSTANCE = new TransactionsService();
	private IDataSource dataSource;

	private TransactionsService() {
		dataSource = DataSourceFactory.getDataSource(DataSourceType.REDIS);
	}

	public static TransactionsService getInstance() {
		return INSTANCE;
	}

	@Override
	public List<Transaction> perform(Context ctx) throws Exception {
		LOGGER.info("Inside TransactionsService's perform method");
		return getTransactionsOfUser(ctx);
	}

	private List<Transaction> getTransactionsOfUser(Context ctx) throws Exception {
		LOGGER.info("Inside TransactionsService's getTransactionsOfUser method");
		UUID uuid = ctx.get(UUID.class);
		return this.dataSource.getUserTransactions(uuid.toString());
	}

}
