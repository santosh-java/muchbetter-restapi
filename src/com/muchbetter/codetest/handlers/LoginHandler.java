/**
 * 
 */
package com.muchbetter.codetest.handlers;

import static ratpack.jackson.Jackson.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.ApplicationError;
import com.muchbetter.codetest.datamodel.Token;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.service.IService;
import com.muchbetter.codetest.service.LoginService;
import com.muchbetter.codetest.utils.StackTraceUtil;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

/**
 * @author blues
 *
 */
public class LoginHandler implements Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);
	private IService<Token> loginService;

	public LoginHandler() {
		this.loginService = LoginService.getInstance();
	}

	@Override
	public void handle(Context ctx) throws Exception {
		LOGGER.info("####Inside LoginHandler's handle method####");
		boolean isSuccess = false;
		Token token = null;
		ApplicationError error = new ApplicationError();
		try {
			token = loginService.perform(ctx);
			isSuccess = true;
		} catch (DBAccessException dbae) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText("Database access has failed causing the failure." + dbae.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(dbae));
		} catch (InvalidUserException iue) {
			error.setResponseStatus(Status.BAD_REQUEST.getCode());
			error.setResponseText("Invalid user details passed. " + iue.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(iue));
		} catch (DataBaseException dbe) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText("Observed failure with Database. " + dbe.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(dbe));
		} catch (Exception e) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText("Unknown exception occurred during operation. " + e.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(e));
		}

		if (isSuccess) {
			LOGGER.info("Login successful and token returned for the new user is: " + token);
			ctx.getResponse().status(Status.CREATED);
			ctx.render(json(token));
		} else {
			try {
				LOGGER.error("Failed Login !!!" + error);
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
