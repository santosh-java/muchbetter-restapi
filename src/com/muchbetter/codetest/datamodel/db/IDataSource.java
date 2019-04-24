package com.muchbetter.codetest.datamodel.db;

import java.util.List;
import java.util.UUID;

import org.redisson.api.RTransaction;

import com.muchbetter.codetest.exception.AuthenticationException;
import com.muchbetter.codetest.exception.DBAccessException;
import com.muchbetter.codetest.exception.DataBaseException;
import com.muchbetter.codetest.exception.InvalidUserException;
import com.muchbetter.codetest.exception.TransactionCommitException;
import com.muchbetter.codetest.exception.TransactionCreationException;
import com.muchbetter.codetest.exception.TransactionRollbackException;
import com.muchbetter.codetest.exception.UserTransactionFailedException;

public interface IDataSource {
	/**
	 * Method that returns a DB transaction object that can be used for performing
	 * the operations in an atomic way by the client.
	 * 
	 * @return DB transaction for performing operations in atomic way
	 * @throws TransactionCreationException
	 */
	public RTransaction createDBTransaction();

	/**
	 * Method that adds a new user to the DB. The client is expected to pass in the
	 * DB Transaction which can be obtained by <code>createDBTransaction()</code> if
	 * he wants to perform many operations on the DB in an atomic way. In this case,
	 * client is expected to call
	 * <code>commitTransaction() or rollbackTransaction() methods appropriately so that the changes are commited to the DB.</code>
	 * 
	 * In case the client does not want to handle the DB transactions himself, he
	 * can make use of <code>addUser(User)</code> method instead which handles the
	 * DB transactions internally
	 * 
	 * This is a convenience method for user
	 * 
	 * @param rTransaction DB transaction instance that is used for performing the
	 *                     specified operation on the DB. This cannot be a
	 *                     <code>null</code>
	 * @param user         New user to be inserted to the DB
	 * @return The user newly added to the DB
	 * @throws DBAccessException
	 * @throws InvalidUserException if the provided <code>userId</code> is invalid
	 * @throws DataBaseException
	 */
	public User addUser(RTransaction rTransaction, User user)
			throws DBAccessException, InvalidUserException, DataBaseException;

	/**
	 * Method that adds a new user to the DB.
	 * 
	 * @param user New user to be inserted in to the DB
	 * @return the user newly added to the DB
	 * @throws DBAccessException
	 * @throws InvalidUserException if the provided <code>userId</code> is invalid
	 * @throws DataBaseException
	 * @throws Exception
	 */
	public User addUser(User user) throws DBAccessException, InvalidUserException, DataBaseException, Exception;

	/**
	 * Method to get a User from the DB. The client is expected to pass in the DB
	 * Transaction which can be obtained by <code>createDBTransaction()</code> if he
	 * wants to perform many operations on the DB in an atomic way. In this case,
	 * client is expected to call
	 * <code>commitTransaction() or rollbackTransaction() methods appropriately so that the changes are commited to the DB.</code>
	 * 
	 * @param rTransaction DB transaction instance that is used for performing the
	 *                     specified operation on the DB. This cannot be a
	 *                     <code>null</code>
	 * @param userId       User account identification that needs to be read from
	 *                     the DB
	 * @return The User that is identified by the provide <code>userId</code> if
	 *         exists. A null otherwise
	 * @throws DBAccessException
	 * @throws InvalidUserException if the provided <code>userId</code> is invalid
	 * @throws DataBaseException
	 */
	public User getUser(RTransaction rTransaction, String userId)
			throws DBAccessException, InvalidUserException, DataBaseException;

	/**
	 * Method to get a User from the DB basing on provided user identification.
	 * 
	 * @param userId User identification that needs to be read from the DB
	 * @return The User that is identified by the provide <code>userId</code> if
	 *         exists. A null otherwise
	 * @throws DBAccessException
	 * @throws InvalidUserException if the provided <code>userId</code> is invalid
	 * @throws DataBaseException
	 */
	public User getUser(String userId) throws DBAccessException, InvalidUserException, DataBaseException;

	/**
	 * Method that performs the provided transaction on the user account and store
	 * the modifications to the DB if it is valid. The client is expected to pass in
	 * the DB Transaction which can be obtained by
	 * <code>createDBTransaction()</code> if he wants to perform many operations on
	 * the DB in an atomic way. In this case, client is expected to call
	 * <code>commitTransaction() or rollbackTransaction() methods appropriately so that the changes are committed to the DB.</code>
	 * 
	 * @param rTransaction DB transaction instance that is used for performing the
	 *                     specified operation on the DB. This cannot be a
	 *                     <code>null</code>
	 * @param userId       User account identification on which the provided
	 *                     <code>transaction</code> needs to be performed
	 * @param transaction  The user transaction that needs to be performed on the
	 *                     user account identified by <code>userId</code>
	 * @return The transaction if it is performed successfully. <code>null</code>
	 *         otherwise
	 * @throws UserTransactionFailedException
	 * @throws InvalidUserException           if the provided <code>userId</code> is
	 *                                        invalid
	 * @throws DataBaseException
	 */
	public Transaction performUserTransaction(RTransaction rTransaction, String userId, Transaction transaction)
			throws UserTransactionFailedException, InvalidUserException, DataBaseException;

	/**
	 * Method that performs the provided transaction on the user account and store
	 * the modifications to the DB if it is valid.
	 * 
	 * @param userId      User account identification on which the provided
	 *                    <code>transaction</code> needs to be performed
	 * @param transaction The user transaction that needs to be performed on the
	 *                    user account identified by <code>userId</code>
	 * @return The transaction if it is performed successfully. <code>null</code>
	 *         otherwise
	 * @throws UserTransactionFailedException
	 * @throws InvalidUserException           if the provided <code>userId</code> is
	 *                                        invalid
	 * @throws DataBaseException
	 * @throws Exception
	 */
	public Transaction performUserTransaction(String userId, Transaction transaction)
			throws UserTransactionFailedException, InvalidUserException, DataBaseException, Exception;

	/**
	 * Method that retrieves all the transactions specific to a user account. The
	 * client is expected to pass in the DB Transaction which can be obtained by
	 * <code>createDBTransaction()</code> if he wants to perform many operations on
	 * the DB in an atomic way. In this case, client is expected to call
	 * <code>commitTransaction() or rollbackTransaction() methods appropriately so that the changes are committed to the DB.</code>
	 * 
	 * @param rTransaction DB transaction instance that is used for performing the
	 *                     specified operation on the DB. This cannot be a
	 *                     <code>null</code>
	 * @param userId       User account identification from which the transaction
	 *                     details are to be fetched
	 * @return The transactions list of a specified <code>userId</code>
	 * @throws DBAccessException
	 * @throws InvalidUserException if the provided <code>userId</code> is invalid
	 * @throws DataBaseException
	 */
	public List<Transaction> getUserTransactions(RTransaction rTransaction, String userId)
			throws DBAccessException, InvalidUserException, DataBaseException;

	/**
	 * Method that retrieves all the transactions specific to a user account.
	 * 
	 * @param userId User account identification from which the transaction details
	 *               are to be fetched
	 * @return The transactions list of a specified <code>userId</code>
	 * @throws DBAccessException
	 * @throws InvalidUserException if the provided <code>userId</code> is invalid
	 * @throws DataBaseException
	 * @throws Exception
	 */
	public List<Transaction> getUserTransactions(String userId)
			throws DBAccessException, InvalidUserException, DataBaseException, Exception;

	/**
	 * Method that commits the DB transaction
	 * 
	 * @param rTransaction DB transaction that needs to be committed.
	 * @throws TransactionCommitException
	 */
	public void commitTransaction(RTransaction rTransaction) throws TransactionCommitException;

	/**
	 * Method that rolls back the DB transaction
	 * 
	 * @param rTransaction DB transaction that needs to be rolled back
	 */
	public void rollbackTransaction(RTransaction rTransaction) throws TransactionRollbackException;

	/**
	 * Method that validates the provided authToken
	 * 
	 * @param authToken
	 * @return true if the token presented is valid, false otherwise
	 * @throws InvalidUserException 
	 */
	public UUID validateToken(String authToken) throws AuthenticationException, InvalidUserException;
}
