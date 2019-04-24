/**
 * 
 */
package com.muchbetter.codetest.handlers;

import static ratpack.jackson.Jackson.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchbetter.codetest.datamodel.ApplicationError;
import com.muchbetter.codetest.datamodel.db.Transaction;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InsufficientBalanceException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.exception.UserTransactionFailedException;
import com.muchbetter.codetest.service.IService;
import com.muchbetter.codetest.service.SpendService;
import com.muchbetter.codetest.utils.StackTraceUtil;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;
import ratpack.registry.NotInRegistryException;

/**
 * @author blues
 *
 */
public class SpendHandler implements Handler {
	public static final Logger LOGGER = LoggerFactory.getLogger(SpendHandler.class);
	private IService<Transaction> spendService = SpendService.getInstance();
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void handle(Context ctx) throws Exception {
		ctx.getRequest().getBody().then(requestBody -> {
			Transaction transactionFromRequest = mapper.readValue(requestBody.getInputStream(), Transaction.class);
			ctx.getRequest().add(transactionFromRequest);
			ApplicationError error = new ApplicationError();
			Transaction trans = null;
			try {
				trans = spendService.perform(ctx);
			} catch (NotInRegistryException nire) {
				error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
				error.setResponseText("User details are not processed correctly during Auth causing this failure!!!");
				LOGGER.debug(StackTraceUtil.getStackTraceAsString(nire));
			} catch (UserTransactionFailedException utfe) {
				error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
				error.setResponseText("Transaction has failed with :" + utfe.getLocalizedMessage());
				LOGGER.debug(
						"SpendHandler: Transaction has failed with :" + StackTraceUtil.getStackTraceAsString(utfe));
			} catch (InvalidUserException iue) {
				error.setResponseStatus(Status.BAD_REQUEST.getCode());
				error.setResponseText("Invalid user details passed." + iue.getLocalizedMessage());
				LOGGER.debug("SpendHandler: Invalid user details passed. " + StackTraceUtil.getStackTraceAsString(iue));
			} catch (DataBaseException dbe) {
				error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
				error.setResponseText("Observed failure with Database. " + dbe.getLocalizedMessage());
				LOGGER.debug(
						"SpendHandler: Observed failure with Database. " + StackTraceUtil.getStackTraceAsString(dbe));
			} catch (InsufficientBalanceException ibe) {
				error.setResponseStatus(Status.BAD_REQUEST.getCode());
				error.setResponseText(
						"Insufficient funds in the account. Spend transaction not allowed" + ibe.getLocalizedMessage());
				LOGGER.debug("SpendHandler: Insufficient funds in the account. Spend transaction not allowed \n"
						+ StackTraceUtil.getStackTraceAsString(ibe));
			} catch (Exception e) {
				error.setResponseStatus(Status.INTERNAL_SERVER_ERROR.getCode());
				error.setResponseText("Unknown exception occurred during operation. " + e.getLocalizedMessage());
				LOGGER.debug("SpendHandler: Unknown exception occurred during operation. "
						+ StackTraceUtil.getStackTraceAsString(e));
			}

			if (trans != null) {
				LOGGER.info("Transaction executed!!!");
				ctx.getResponse().status(Status.OK);
				ctx.render(json(trans));
			} else {
				try {
					LOGGER.error("Transaction failed !!! reason is " + error);
					ctx.getResponse().status(error.getResponseStatus());
					ctx.render(json(error));
				} catch (Exception e) {
					LOGGER.error("Exception occurred while sending response to client ::" + e.getLocalizedMessage());
					LOGGER.debug(StackTraceUtil.getStackTraceAsString(e));
					throw e;
				}
			}
		});
	}

}
