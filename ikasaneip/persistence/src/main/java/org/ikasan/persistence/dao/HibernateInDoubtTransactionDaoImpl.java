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

import java.util.List;

public class HibernateInDoubtTransactionDaoImpl implements InDoubtTransactionDao {

    @PersistenceContext(unitName = "persistence")
    private EntityManager entityManager;

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
        if(this.getInDoubtTransaction(transactionName) == null) {
            throw new RuntimeException(String.format("An in doubt transaction with name[%s] does not exist in the database!" +
                " Unable to commit the in doubt transaction!", transactionName));
        }
        entityManager.createNativeQuery("COMMIT TRANSACTION " + transactionName).executeUpdate();
    }

    @Override
    public void rollbackInDoubtTransaction(String transactionName) {
        if(this.getInDoubtTransaction(transactionName) == null) {
            throw new RuntimeException(String.format("An in doubt transaction with name[%s] does not exist in the database!" +
                " Unable to rollback the in doubt transaction!", transactionName));
        }
        entityManager.createNativeQuery("ROLLBACK TRANSACTION " + transactionName).executeUpdate();
    }
}
