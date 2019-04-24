package com.muchbetter.codetest.handlers;

import static ratpack.jackson.Jackson.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.ApplicationError;
import com.muchbetter.codetest.datamodel.AuthStatus;
import com.muchbetter.codetest.exception.AuthenticationException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.service.AuthService;
import com.muchbetter.codetest.service.IService;
import com.muchbetter.codetest.utils.StackTraceUtil;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;
import ratpack.registry.Registry;

public class AuthHandler implements Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger(AuthHandler.class);

	IService<AuthStatus> authService;

	public AuthHandler() {
		this.authService = AuthService.getInstance();
	}

	@Override
	public void handle(Context ctx) throws Exception {
		ApplicationError error = new ApplicationError();
		AuthStatus authStatus = new AuthStatus();
		try {
			authStatus = this.authService.perform(ctx);
		} catch (AuthenticationException ae) {
			error.setResponseStatus(Status.UNAUTHORIZED.getCode());
			error.setResponseText("User authentication has failed with: " + ae.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(ae));
		} catch (InvalidUserException iue) {
			error.setResponseStatus(Status.BAD_REQUEST.getCode());
			error.setResponseText("Invalid user details passed. " + iue.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(iue));
		} catch (Exception e) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText("Unknown exception occurred during operation. " + e.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(e));
		}

		if (authStatus.isAuthSuccess()) {
			ctx.next(Registry.single(authStatus.getUserId()));
		} else {
			try {
				LOGGER.error("Authorization failed!!!" + error);
				ctx.getResponse().status(error.getResponseStatus());
				ctx.render(json(error));
			} catch (Exception e) {
				LOGGER.error("Exception occurred while sending response to client ::" + e.getLocalizedMessage());
				LOGGER.debug(StackTraceUtil.getStackTraceAsString(e));
				throw e;
			}
		}
	}

}
