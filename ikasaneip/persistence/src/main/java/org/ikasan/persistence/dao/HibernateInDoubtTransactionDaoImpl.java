package org.ikasan.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.ikasan.persistence.model.InDoubtTransactionImpl;
import org.ikasan.spec.persistence.dao.InDoubtTransactionDao;
import org.ikasan.spec.persistence.model.InDoubtTransaction;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.regex.Pattern;

public class HibernateInDoubtTransactionDaoImpl implements InDoubtTransactionDao {

    @PersistenceContext(unitName = "persistence")
    private EntityManager entityManager;

    private static final Pattern SAFE_TRANSACTION_NAME = Pattern.compile("^[A-Za-z0-9_]+$");

    @Override
    public List<InDoubtTransaction> getInDoubtTransactions() {
        return entityManager.createNativeQuery(
            "SELECT * FROM INFORMATION_SCHEMA.IN_DOUBT", InDoubtTransactionImpl.class)
            .getResultList();
    }

    @Override
    public InDoubtTransaction getInDoubtTransaction(String transactionName) {
        List<InDoubtTransaction> inDoubtTransactions = this.getInDoubtTransactions();

        for (InDoubtTransaction inDoubtTransaction: inDoubtTransactions) {
            if(inDoubtTransaction.getTransactionName().equalsIgnoreCase(transactionName)) {
                return inDoubtTransaction;
            }
        }

        return null;
    }

    @Override
    public void commitInDoubtTransaction(String transactionName) {
        String validatedTransactionName = validateTransactionName(transactionName);

        if(this.getInDoubtTransaction(validatedTransactionName) == null) {
            throw new RuntimeException(String.format("An in doubt transaction with name[%s] does not exist in the database!" +
                " Unable to commit the in doubt transaction!", validatedTransactionName));
        }
        entityManager.createNativeQuery("COMMIT TRANSACTION " + validatedTransactionName).executeUpdate();
    }

    @Override
    public void rollbackInDoubtTransaction(String transactionName) {
        String validatedTransactionName = validateTransactionName(transactionName);

        if(this.getInDoubtTransaction(validatedTransactionName) == null) {
            throw new RuntimeException(String.format("An in doubt transaction with name[%s] does not exist in the database!" +
                " Unable to rollback the in doubt transaction!", transactionName));
        }

        entityManager.createNativeQuery("ROLLBACK TRANSACTION " + validatedTransactionName).executeUpdate();
    }

    /**
     * Validates the provided transaction name to ensure it is non-null, not blank,
     * and matches a predefined pattern of safe characters. Throws an
     * IllegalArgumentException if the validation fails.
     *
     * @param transactionName the transaction name to be validated
     * @return the validated transaction name if it passes all validation checks
     * @throws IllegalArgumentException if the transaction name is null, blank, or does not match the safe pattern
     */
    private String validateTransactionName(String transactionName) {
        if (transactionName == null || transactionName.isBlank() || !SAFE_TRANSACTION_NAME.matcher(transactionName).matches()) {
            throw new IllegalArgumentException("Invalid transaction name supplied.");
        }
        return transactionName;
    }
}
