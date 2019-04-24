package com.muchbetter.codetest.handlers;

import static ratpack.jackson.Jackson.json;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.datamodel.ApplicationError;
import com.muchbetter.codetest.datamodel.db.Transaction;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.service.IService;
import com.muchbetter.codetest.service.TransactionsService;
import com.muchbetter.codetest.utils.StackTraceUtil;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;
import ratpack.registry.NotInRegistryException;

public class TransactionHandler implements Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger(TransactionHandler.class);
	private IService<List<Transaction>> transactionsService = TransactionsService.getInstance();

	@Override
	public void handle(Context ctx) throws Exception {
		ApplicationError error = new ApplicationError();
		List<Transaction> transactions = null;
		try {
			transactions = transactionsService.perform(ctx);
		} catch (NotInRegistryException nire) {
			error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
			error.setResponseText("User details are not processed correctly during Auth causing this failure!!!");
			LOGGER.debug(StackTraceUtil.getStackTraceAsString(nire));
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

		if (transactions != null) {
			LOGGER.info("TransactionHandler: Transactions of the user are fetched successfully: " + transactions);
			ctx.getResponse().status(Status.OK);
			ctx.render(json(transactions));
		} else {
			try {
				LOGGER.error("TransactionHandler: Failure occurred::" + error);
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
