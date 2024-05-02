package org.ikasan.spec.persistence.dao;

import org.ikasan.spec.persistence.model.InDoubtTransaction;

import java.util.List;

public interface InDoubtTransactionDao {

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
}
