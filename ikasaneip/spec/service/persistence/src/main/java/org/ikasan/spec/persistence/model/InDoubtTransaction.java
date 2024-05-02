package org.ikasan.spec.persistence.model;

/**
 * The InDoubtTransaction interface represents a transaction in an
 * in-doubt state.
 */
public interface InDoubtTransaction {

    /**
     * Returns the name of the transaction.
     *
     * @return the name of the transaction as a String
     */
    String getTransactionName();

    /**
     * Sets the name of the transaction.
     *
     * @param transactionName the name of the transaction
     */
    void setTransactionName(String transactionName);

    /**
     * Retrieves the state of the transaction.
     *
     * @return the state of the transaction as a String
     */
    String getTransactionState();

    /**
     * Sets the state of the transaction.
     *
     * @param transactionState the state of the transaction as a String
     */
    void setTransactionState(String transactionState);
}
