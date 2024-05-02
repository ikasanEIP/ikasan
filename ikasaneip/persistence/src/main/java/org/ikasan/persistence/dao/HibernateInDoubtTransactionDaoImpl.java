package org.ikasan.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.ikasan.spec.persistence.dao.InDoubtTransactionDao;
import org.ikasan.spec.persistence.model.InDoubtTransaction;

import java.util.List;

public class HibernateInDoubtTransactionDaoImpl implements InDoubtTransactionDao {

    @PersistenceContext(unitName = "persistence")
    private EntityManager entityManager;

    @Override
    public List<InDoubtTransaction> getInDoubtTransactions() {
        return null;
    }

    @Override
    public InDoubtTransaction getInDoubtTransaction(String transactionName) {
        return null;
    }

    @Override
    public void commitInDoubtTransaction(String transactionName) {

    }

    @Override
    public void rollbackInDoubtTransaction(String transactionName) {

    }
}
