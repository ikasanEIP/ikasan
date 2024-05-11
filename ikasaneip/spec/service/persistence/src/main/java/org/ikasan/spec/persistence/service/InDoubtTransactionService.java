package org.ikasan.spec.persistence.service;

import org.ikasan.spec.persistence.model.InDoubtTransaction;

import java.util.List;

/**
 * The InDoubtTransactionService interface provides methods for retrieving, committing, and rolling back in-doubt transactions.
 */
public interface InDoubtTransactionService {

    /**
     * Retrieves a list of in-doubt transactions.
     *
     * @return a list of InDoubtTransaction objects representing the in-doubt transactions
     */
    List<InDoubtTransaction> getInDoubtTransactions();

    /**
     * Retrieves the InDoubtTransaction with the specified transaction name.
     *
     * @param transactionName the name of the transaction to retrieve
     * @return the InDoubtTransaction object representing the in-doubt transaction,
     *         or null if no transaction with the specified name is found
     */
    InDoubtTransaction getInDoubtTransaction(String transactionName);

    /**
     * Commits an in-doubt transaction with the specified transaction name.
     *
     * @param transactionName the name of the in-doubt transaction to commit
     */
    void commitInDoubtTransaction(String transactionName);

    /**
     * Rolls back an in-doubt transaction with the specified transaction name.
     *
     * @param transactionName the name of the in-doubt transaction to rollback
     */
    void rollbackInDoubtTransaction(String transactionName);

    /**
     * Commits all in-doubt transactions.
     *
     * This method initiates the commit process for all in-doubt transactions. An in-doubt transaction
     * is a transaction that has not yet been committed or rolled back and is in an uncertain state.
     * By calling this method, all in-doubt transactions will be committed and their state will be
     * updated accordingly.
     */
    void commitAllInDoubtTransactions();

    /**
     * Rolls back all in-doubt transactions.
     *
     * This method initiates the rollback process for all in-doubt transactions. An in-doubt transaction
     * is a transaction that has not yet been committed or rolled back and is in an uncertain state.
     * By calling this method, all in-doubt transactions will be rolled back and their state will be
     * updated accordingly.
     */
    void rollbackAllInDoubtTransactions();
}
