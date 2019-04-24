package com.muchbetter.codetest.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.Balance;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory.DataSourceType;
import com.muchbetter.codetest.datamodel.db.IDataSource;
import com.muchbetter.codetest.datamodel.db.User;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InvalidUserException;

import ratpack.handling.Context;

public class BalanceService implements IService<Balance> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BalanceService.class);
	private static BalanceService INSTANCE = new BalanceService();
	private IDataSource dataSource;

	private BalanceService() {
		this.dataSource = DataSourceFactory.getDataSource(DataSourceType.REDIS);
	}

	public static BalanceService getInstance() {
		return INSTANCE;
	}

	public Balance getBalance(Context ctx)
			throws DBAccessException, InvalidUserException, DataBaseException, Exception {
		UUID uuid = ctx.get(UUID.class);
		User user = dataSource.getUser(uuid.toString());
		Balance balance = new Balance(user.getBalance(), user.getCurrency());
		return balance;
	}

	@Override
	public Balance perform(Context ctx) throws DBAccessException, InvalidUserException, DataBaseException, Exception {
		LOGGER.info("Inside BalanceService's perform method");
		return getBalance(ctx);
	}

}
