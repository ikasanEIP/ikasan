package org.ikasan.sample.ftp.tx;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class MockedTransactionManager implements PlatformTransactionManager {

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {

        TransactionStatus ts = new DefaultTransactionStatus(null,true, true,
        false, true, null);

        return ts;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {

    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {

    }
}