package org.ikasan.persistence.service;

import org.ikasan.spec.persistence.dao.InDoubtTransactionDao;
import org.ikasan.spec.persistence.model.InDoubtTransaction;
import org.ikasan.spec.persistence.service.InDoubtTransactionService;

import java.util.List;


/**
 * The InDoubtTransactionServiceImpl class is an implementation of the InDoubtTransactionService interface.
 * It provides methods for retrieving, committing, and rolling back in-doubt transactions.
 */
public class InDoubtTransactionServiceImpl implements InDoubtTransactionService {

    private InDoubtTransactionDao inDoubtTransactionDao;

    /**
     * Constructor.
     *
     * InDoubtTransactionServiceImpl class is an implementation of the InDoubtTransactionService interface.
     *
     * @param inDoubtTransactionDao the implementation of InDoubtTransactionDao used for data access
     */
    public InDoubtTransactionServiceImpl(InDoubtTransactionDao inDoubtTransactionDao) {
        this.inDoubtTransactionDao = inDoubtTransactionDao;
        if(this.inDoubtTransactionDao == null) {
            throw new IllegalArgumentException("inDoubtTransactionDao cannot be null!!");
        }
    }

    @Override
    public List<InDoubtTransaction> getInDoubtTransactions() {
        return this.inDoubtTransactionDao.getInDoubtTransactions();
    }

    @Override
    public InDoubtTransaction getInDoubtTransaction(String transactionName) {
        return this.inDoubtTransactionDao.getInDoubtTransaction(transactionName);
    }

    @Override
    public void commitInDoubtTransaction(String transactionName) {
        this.inDoubtTransactionDao.commitInDoubtTransaction(transactionName);
    }

    @Override
    public void rollbackInDoubtTransaction(String transactionName) {
        this.inDoubtTransactionDao.rollbackInDoubtTransaction(transactionName);
    }

    @Override
    public void commitAllInDoubtTransactions() {
        this.inDoubtTransactionDao.getInDoubtTransactions()
            .forEach(inDoubtTransaction -> this.commitInDoubtTransaction(inDoubtTransaction.getTransactionName()));
    }

    @Override
    public void rollbackAllInDoubtTransactions() {
        this.inDoubtTransactionDao.getInDoubtTransactions()
            .forEach(inDoubtTransaction -> this.rollbackInDoubtTransaction(inDoubtTransaction.getTransactionName()));
    }
}
