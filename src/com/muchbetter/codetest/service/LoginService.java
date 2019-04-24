package com.muchbetter.codetest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.Token;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory.DataSourceType;
import com.muchbetter.codetest.datamodel.db.IDataSource;
import com.muchbetter.codetest.datamodel.db.User;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.utils.EncodeDecodeUtil;

import ratpack.handling.Context;

public class LoginService implements IService<Token> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
	private static LoginService INSTANCE = new LoginService();
	private final IDataSource dataSource;

	private LoginService() {
		// TODO: Need to get datasource from Factory. Currently it will be
		// RedisDataSource.
		// It can be modified in future to any other datasource.
		this.dataSource = DataSourceFactory.getDataSource(DataSourceType.REDIS);
	}

	public static LoginService getInstance() {
		return INSTANCE;
	}

	public Token performLogin(Context ctx)
			throws DBAccessException, InvalidUserException, DataBaseException, Exception {
		// TODO: We need to create User object, get the UUID of the user and convert to
		// token. We need to store the newly created User object, to the DB using the
		// datasource helper
		LOGGER.info("Inside LoginService's performLogin method");
		User user = new User();
		dataSource.addUser(user);
		Token token = new Token(EncodeDecodeUtil.getTokenFromUID(user.getUserId().toString()));
		return token;
	}

	@Override
	public Token perform(Context ctx) throws DBAccessException, InvalidUserException, DataBaseException, Exception {
		LOGGER.info("Inside LoginService's perform method");
		return performLogin(ctx);
	}

}
