package org.ikasan.persistence.service;

import org.ikasan.spec.persistence.model.InDoubtTransaction;
import org.ikasan.spec.persistence.service.InDoubtTransactionService;

import java.util.List;

public class InDoubtTransactionServiceImpl implements InDoubtTransactionService {
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
