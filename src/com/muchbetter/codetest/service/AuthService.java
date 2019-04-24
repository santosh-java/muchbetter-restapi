package com.muchbetter.codetest.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.AuthStatus;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory;
import com.muchbetter.codetest.datamodel.db.DataSourceFactory.DataSourceType;
import com.muchbetter.codetest.datamodel.db.IDataSource;
import com.muchbetter.codetest.exception.AuthenticationException;
import com.muchbetter.codetest.exception.InvalidUserException;

import ratpack.handling.Context;
import ratpack.http.Headers;

public class AuthService implements IService<AuthStatus> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static AuthService INSTANCE = new AuthService();
	private final IDataSource dataSource;

	private AuthService() {
		// TODO: Need to get datasource from Factory. Currently it will be
		// RedisDataSource.
		// It can be modified in future to any other datasource.
		this.dataSource = DataSourceFactory.getDataSource(DataSourceType.REDIS);
	}

	public static AuthService getInstance() {
		return INSTANCE;
	}

	private AuthStatus performAuthentication(Context ctx) throws AuthenticationException, InvalidUserException {
		AuthStatus authStatus = new AuthStatus();
		Headers headers = ctx.getRequest().getHeaders();
		String authHeader = headers.get(AUTHORIZATION_HEADER);

		if (authHeader == null || authHeader.isEmpty()) {
			authStatus.setAuthSuccess(false);
			authStatus.setFailureCause("Empty Authorization header passed!!!");
			throw new InvalidUserException("Empty Authorization header passed!!!");
		}
		LOGGER.info("Authorization header obtained from the request header is: #" + authHeader + "#");
		if (!authHeader.startsWith(TOKEN_PREFIX)) {
			authStatus.setAuthSuccess(false);
			authStatus.setFailureCause("Token prefix is invalid!!! Authorization header's prefix should be 'Bearer '");
			throw new InvalidUserException(
					"Token prefix is invalid!!! Authorization header's prefix should be 'Bearer '");
		}

		String authToken = authHeader.split(" ")[1];
		UUID uuid = dataSource.validateToken(authToken);
		if (uuid != null) {
			authStatus.setAuthSuccess(true);
			authStatus.setUserId(uuid);
		}

		return authStatus;
	}

	@Override
	public AuthStatus perform(Context ctx) throws AuthenticationException, InvalidUserException {
		return performAuthentication(ctx);
	}

}
