package com.muchbetter.codetest.datamodel.db;

import java.util.List;
import java.util.UUID;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.exception.AuthenticationException;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InsufficientBalanceException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.exception.TransactionCommitException;
import com.muchbetter.codetest.exception.TransactionRollbackException;
import com.muchbetter.codetest.exception.UserTransactionFailedException;
import com.muchbetter.codetest.utils.EncodeDecodeUtil;
import com.muchbetter.codetest.utils.StackTraceUtil;

public class RedisDataSource implements IDataSource {
	public static final Logger LOGGER = LoggerFactory.getLogger(RedisDataSource.class);
	private RedissonClient redisDS;
	public static final String USERS_STORE_NAME = "USERS_STORE";
	public static final String USER_TRANSACTIONS_STORE_NAME = "USER_TRANSACTION_STORE";
	public static final String TOKENS_STORE_NAME = "TOKENS_STORE";

	public RedisDataSource(Config config) {
		init(config);
	}

	private void init(Config config) {
		// We can read the server address and other config properties as needed from
		// either a properties file or from a database table and use here
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");

		redisDS = Redisson.create(config);
	}

	@Override
	public RTransaction createDBTransaction() {
		return redisDS.createTransaction(getTransactionOptions());
	}

	@Override
	public User addUser(User user) throws DBAccessException, DataBaseException, InvalidUserException, Exception {
		// Log here that since there is no RTransaction provided by the user, we will
		// create a transaction and use it for adding the new User to DB.
		RTransaction rTransaction = createDBTransaction();

		try {
			// Here we may want to put some validations on the User and only when those are
			// passed, we will actually put into the DB. Otherwise we will return null
			// indicating that the User is not added to the DB.
			// Throw InvalidUserException if the user is invalid based on his balance and
			// other stuff etc.

			RMap<UUID, User> usersStore = getUsersStore(rTransaction);
			RMap<UUID, List<Transaction>> userTransactionsStore = getUserTransactionStore(rTransaction);
			RMap<String, String> tokensStore = getTokensStore(rTransaction);
			usersStore.put(user.getUserId(), user);
			userTransactionsStore.put(user.getUserId(), user.getUserTransactions());
			tokensStore.put(EncodeDecodeUtil.getTokenFromUID(user.getUserId().toString()), user.getUserId().toString());
			commitTransaction(rTransaction);
			return user;
		} catch (TransactionCommitException tce) {
			handleException(rTransaction, tce, new DataBaseException(tce.getLocalizedMessage(), tce));
		} catch (Exception e) {
			handleException(rTransaction, e, e);
		}
		return null;
	}

	@Override
	public User addUser(RTransaction rTransaction, User user) throws DBAccessException, InvalidUserException {
		if (rTransaction == null) {
			// Log here that since there is no RTransaction provided by the user, we will
			// create a transaction and use it for adding the new User to DB.
			throw new IllegalArgumentException("DB Transaction instance cannot be passed in as null!!!");
		}

		// Here we may want to put some validations on the User and only when those are
		// passed, we will actually put into the DB. Otherwise we will return null
		// indicating that the User is not added to the DB.

		RMap<UUID, User> usersStore = getUsersStore(rTransaction);
		RMap<UUID, List<Transaction>> userTransactionsStore = getUserTransactionStore(rTransaction);
		RMap<String, String> tokensStore = getTokensStore(rTransaction);
		usersStore.put(user.getUserId(), user);
		userTransactionsStore.put(user.getUserId(), user.getUserTransactions());
		tokensStore.put(EncodeDecodeUtil.getTokenFromUID(user.getUserId().toString()), user.getUserId().toString());
		return user;
	}

	@Override
	public User getUser(String userId) throws DBAccessException, InvalidUserException {
		if (userId == null || userId.length() <= 0) {
			LOGGER.info("UserID cannot be null or empty!!!");
			throw new InvalidUserException("UserID cannot be null or empty!!!");
		}
		try {
			UUID uuid = UUID.fromString(userId);
			RMap<UUID, User> usersStore = getUsersStore(null);
			User user = usersStore.get(uuid);
			return user;
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Invalid user details passed: \n" + StackTraceUtil.getStackTraceAsString(iae));
			throw new InvalidUserException(iae.getLocalizedMessage(), iae);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public User getUser(RTransaction rTransaction, String userId) throws DBAccessException, InvalidUserException {
		if (rTransaction == null) {
			LOGGER.info("DB Transaction instance cannot be passed in as null!!!");
			throw new IllegalArgumentException("DB Transaction instance cannot be passed in as null!!!");
		}

		if (userId == null || userId.length() <= 0) {
			LOGGER.info("UserID cannot be null or empty!!!");
			throw new InvalidUserException("UserID cannot be null or empty!!!");
		}

		try {
			RMap<UUID, User> usersStore = getUsersStore(rTransaction);
			UUID uuid = UUID.fromString(userId);
			User user = usersStore.get(uuid);
			return user;
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Invalid user details passed: \n" + StackTraceUtil.getStackTraceAsString(iae));
			throw new InvalidUserException(iae.getLocalizedMessage(), iae);
		} catch (Exception e) {
			LOGGER.error(StackTraceUtil.getStackTraceAsString(e));
			throw e;
		}
	}

	@Override
	public Transaction performUserTransaction(String userId, Transaction transaction)
			throws UserTransactionFailedException, InvalidUserException, DataBaseException,
			InsufficientBalanceException, Exception {

		if (userId == null || userId.length() <= 0) {
			LOGGER.info("UserID cannot be null or empty!!!");
			throw new InvalidUserException("UserID cannot be null or empty!!!");
		}
		RTransaction rTransaction = createDBTransaction();

		try {
			RMap<UUID, User> usersStore = getUsersStore(rTransaction);
			RMap<UUID, List<Transaction>> userTransactionsStore = getUserTransactionStore(rTransaction);

			UUID uuid = UUID.fromString(userId);
			User user = usersStore.get(uuid);
			if (checkIfUserBalanceSufficientForTransaction(user, transaction)) {
				LOGGER.info("Balance check successful for this transactoin amount of ["
						+ transaction.getTransactionAmount() + "]");
				List<Transaction> userTransactions = user.getUserTransactions();
				userTransactions.add(transaction);
				user.setUserTransactions(userTransactions);
				user.setBalance(user.getBalance() - transaction.getTransactionAmount());
				usersStore.put(uuid, user);
				userTransactionsStore.put(uuid, userTransactions);
				commitTransaction(rTransaction);
				LOGGER.info("Transaction successfully applied to the account!!!" + transaction);
				return transaction;
			} else {
				LOGGER.info("Transaction amount for spending [" + transaction.getTransactionAmount()
						+ "] exceeds the current balance. This transaction cannot be allowed!!!");
				throw new InsufficientBalanceException(
						"Transaction amount for spending [" + transaction.getTransactionAmount()
								+ "] exceeds the current balance. This transaction cannot be allowed!!!");
			}
		} catch (TransactionCommitException tce) {
			handleException(rTransaction, tce, new DataBaseException(tce.getLocalizedMessage(), tce));
		} catch (IllegalArgumentException iae) {
			handleException(rTransaction, iae, new InvalidUserException(iae.getLocalizedMessage(), iae));
		} catch (Exception e) {
			handleException(rTransaction, e, e);
		}
		return null;
	}

	@Override
	public Transaction performUserTransaction(RTransaction rTransaction, String userId, Transaction transaction)
			throws UserTransactionFailedException, InvalidUserException, DataBaseException {
		if (rTransaction == null) {
			throw new IllegalArgumentException("DB Transaction instance cannot be passed in as null!!!");
		}

		if (userId == null || userId.length() <= 0) {
			// Log here that the userId passed is null or empty
			throw new InvalidUserException("UserID cannot be null or empty!!!");
		}

		try {
			UUID uuid = UUID.fromString(userId);
			RMap<UUID, User> usersStore = getUsersStore(rTransaction);
			RMap<UUID, List<Transaction>> userTransactionsStore = getUserTransactionStore(rTransaction);
			User user = usersStore.get(uuid);
			if (checkIfUserBalanceSufficientForTransaction(user, transaction)) {
				List<Transaction> userTransactions = user.getUserTransactions();
				userTransactions.add(transaction);
				user.setUserTransactions(userTransactions);
				usersStore.put(uuid, user);
				userTransactionsStore.put(uuid, userTransactions);
				return transaction;
			}
			return null;
		} catch (IllegalArgumentException iae) {
			LOGGER.error(iae.getLocalizedMessage());
			throw new InvalidUserException(iae.getLocalizedMessage(), iae);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@Override
	public List<Transaction> getUserTransactions(String userId)
			throws DBAccessException, InvalidUserException, DataBaseException, Exception {
		if (userId == null || userId.length() <= 0) {
			LOGGER.info("UserID cannot be null or empty!!!");
			throw new InvalidUserException("UserID cannot be null or empty!!!");
		}

		RTransaction rTransaction = createDBTransaction();

		RMap<UUID, List<Transaction>> userTransactionsStore = getUserTransactionStore(rTransaction);

		try {
			UUID uuid = UUID.fromString(userId);
			List<Transaction> userTransactions = userTransactionsStore.get(uuid);
			commitTransaction(rTransaction);
			return userTransactions;
		} catch (IllegalArgumentException iae) {
			handleException(rTransaction, iae, new InvalidUserException(iae.getLocalizedMessage(), iae));
		} catch (TransactionCommitException tce) {
			handleException(rTransaction, tce, new DataBaseException(tce.getLocalizedMessage(), tce));
		} catch (Exception e) {
			handleException(rTransaction, e, e);
		}
		return null;
	}

	@Override
	public List<Transaction> getUserTransactions(RTransaction rTransaction, String userId)
			throws DBAccessException, InvalidUserException {
		if (rTransaction == null) {
			LOGGER.info("DB Transaction instance cannot be passed in as null!!!");
			throw new IllegalArgumentException("DB Transaction instance cannot be passed in as null!!!");
		}

		if (userId == null || userId.length() <= 0) {
			LOGGER.info("UserID cannot be null or empty!!!");
			throw new InvalidUserException("UserID cannot be null or empty!!!");
		}

		RMap<UUID, List<Transaction>> userTransactionsStore = getUserTransactionStore(rTransaction);

		try {
			UUID uuid = UUID.fromString(userId);
			List<Transaction> userTransactions = userTransactionsStore.get(uuid);
			return userTransactions;
		} catch (IllegalArgumentException iae) {
			LOGGER.error(iae.getLocalizedMessage());
			throw new InvalidUserException(iae.getLocalizedMessage(), iae);
		}
	}

	@Override
	public UUID validateToken(String authToken) throws AuthenticationException, InvalidUserException {
		if (authToken == null || authToken.length() <= 0) {
			throw new AuthenticationException("Invalid token passed for authentication!!!");
		}
		LOGGER.info("AuthToken obtained from caller is: " + authToken);
		RMap<String, String> tokensStore = getTokensStore(null);
		try {
			String uuidFromDBStr = tokensStore.get(authToken);
			LOGGER.info("UUID as string from DB obtained is: " + uuidFromDBStr);
			String uidFromToken = EncodeDecodeUtil.getUIDFromToken(authToken);
			LOGGER.info("UUID as string from request header is: " + uidFromToken);
			UUID uuid = UUID.fromString(uidFromToken);
			if (uuidFromDBStr.equals(uidFromToken)) {
				return uuid;
			} else {
				throw new InvalidUserException("Invalid user details passed!!!");
			}
		} catch (IllegalArgumentException iae) {
			LOGGER.info("Invalid token passed for authentication!!!" + iae.getLocalizedMessage());
			throw new AuthenticationException("Invalid token passed for authentication!!!", iae);
		}
	}

	@Override
	public void commitTransaction(RTransaction rTransaction) throws TransactionCommitException {
		try {
			rTransaction.commit();
		} catch (Exception e) {
			LOGGER.error("DB Transaction commit has failed :: " + e.getLocalizedMessage());
			throw new TransactionCommitException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void rollbackTransaction(RTransaction rTransaction) throws TransactionRollbackException {
		try {
			rTransaction.rollback();
		} catch (Exception e) {
			LOGGER.error("DB Transaction rollback has failed :: " + e.getLocalizedMessage());
			throw new TransactionRollbackException(e.getLocalizedMessage(), e);
		}
	}

	private RMap<UUID, User> getUsersStore(RTransaction rTransaction) {
		if (rTransaction == null) {
			return redisDS.getMap(USERS_STORE_NAME, new JsonJacksonCodec());
		} else {
			return rTransaction.getMap(USERS_STORE_NAME, new JsonJacksonCodec());
		}
	}

	private RMap<UUID, List<Transaction>> getUserTransactionStore(RTransaction rTransaction) {
		if (rTransaction == null) {
			return redisDS.getMap(USER_TRANSACTIONS_STORE_NAME, new JsonJacksonCodec());
		} else {
			return rTransaction.getMap(USER_TRANSACTIONS_STORE_NAME, new JsonJacksonCodec());
		}
	}

	private RMap<String, String> getTokensStore(RTransaction rTransaction) {
		if (rTransaction == null) {
			return redisDS.getMap(TOKENS_STORE_NAME, new JsonJacksonCodec());
		} else {
			return rTransaction.getMap(TOKENS_STORE_NAME, new JsonJacksonCodec());
		}
	}

	private boolean checkIfUserBalanceSufficientForTransaction(User user, Transaction transaction) {
		boolean isTransactionAllowed = false;
		double balance = user.getBalance();
		double transactionAmount = transaction.getTransactionAmount();
		TransactionType transactionType = transaction.getTransactionType();
		switch (transactionType) {
		case DEPOSIT:
			isTransactionAllowed = (balance + transactionAmount) >= 0;
			break;
		case WITHDRAW:
			isTransactionAllowed = (balance - transactionAmount) >= 0;
			break;
		default:
			isTransactionAllowed = false;
		}
		return isTransactionAllowed;
	}

	private TransactionOptions getTransactionOptions() {
		// Here we can construct the transaction options basing on our requirements.
		// These settings can be read from a properties file or we can have them in a
		// different DB table and read them from it whenever we create the transaction
		// options.
		// For now, I am using the defaults for the transaction options.
		return TransactionOptions.defaults();
	}

	private void handleException(RTransaction rTransaction, Exception rootException, Exception exceptionToBeThrown)
			throws Exception {
		try {
			LOGGER.error("Exceptoin occurred:: " + rootException
					+ ". Trying to rollback any changes done to DB until now!!");
			rollbackTransaction(rTransaction);
		} catch (TransactionRollbackException tre) {
			LOGGER.error("Rollback of the transaction was tried as following exception occurred during transaction :"
					+ rootException.getLocalizedMessage()
					+ " but the rollback of the transaction has also failed with: " + tre.getLocalizedMessage());
			throw new DataBaseException(
					"Rollback of the transaction was tried as following exception occurred during transaction :"
							+ rootException.getLocalizedMessage()
							+ " but the rollback of the transaction has also failed with: " + tre.getLocalizedMessage(),
					tre);
		}
		throw exceptionToBeThrown;
	}
}
