package com.ikasan.sample.spring.boot.builderpattern;

import com.arjuna.ats.jta.resources.LastResourceCommitOptimisation;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.jta.JtaTransactionManager;

import jakarta.transaction.xa.XAException;
import jakarta.transaction.xa.XAResource;
import jakarta.transaction.xa.Xid;

public class SampleMessageProvider implements MessageProvider<String>, LastResourceCommitOptimisation {

    private JtaTransactionManager transactionManager;

    private String data;

    @Override
    public String invoke(JobExecutionContext context) {
        try {
            transactionManager.getTransactionManager().getTransaction().enlistResource(this);
            this.data = FakeDataProvider.get();

            return this.data;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        FakeDataProvider.remove(data);
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {

    }

    @Override
    public void forget(Xid xid) throws XAException {

    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        return false;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return 0;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) throws XAException {

    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {

    }

    public void setTransactionManager(JtaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
