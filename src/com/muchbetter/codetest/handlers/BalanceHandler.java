/**
 * 
 */
package com.muchbetter.codetest.handlers;

import static ratpack.jackson.Jackson.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.ApplicationError;
import com.muchbetter.codetest.datamodel.Balance;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.service.BalanceService;
import com.muchbetter.codetest.service.IService;
import com.muchbetter.codetest.utils.StackTraceUtil;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

/**
 * @author blues
 *
 */
public class BalanceHandler implements Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger(BalanceHandler.class);
	private IService<Balance> balanceService;

	public BalanceHandler() {
		balanceService = BalanceService.getInstance();
	}

	@Override
	public void handle(Context ctx) throws Exception {
		System.out.println("Inside BalanceHandler's handle method");
		boolean isSuccess = false;
		Balance balance = null;
		ApplicationError error = new ApplicationError();
		try {
			balance = balanceService.perform(ctx);
			isSuccess = true;
		} catch (DBAccessException dbae) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText(
					"BalanceHandler: Database access has failed causing the failure." + dbae.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(dbae));
		} catch (InvalidUserException iue) {
			error.setResponseStatus(Status.BAD_REQUEST.getCode());
			error.setResponseText("LoginHandler: Invalid user details passed. " + iue.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(iue));
		} catch (DataBaseException dbe) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText("LoginHandler: Observed failure with Database. " + dbe.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(dbe));
		} catch (Exception e) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText(
					"LoginHandler: Unknown exception occurred during operation. " + e.getLocalizedMessage());
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(e));
		}

		if (isSuccess) {
			LOGGER.info("Balance check successful and balance details returned for the user is: " + balance);
			ctx.getResponse().status(Status.OK);
			ctx.render(json(balance));
		} else {
			try {
				LOGGER.error("Balance check failed !!! : " + error);
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
