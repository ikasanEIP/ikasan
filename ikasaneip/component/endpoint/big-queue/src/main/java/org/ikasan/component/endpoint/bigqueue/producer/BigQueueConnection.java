package org.ikasan.component.endpoint.bigqueue.producer;

import com.arjuna.ats.jta.resources.LastResourceCommitOptimisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.io.IOException;

public class BigQueueConnection implements LastResourceCommitOptimisation {
    private static Logger logger = LoggerFactory.getLogger(BigQueueConnection.class);

    private BigQueueConnectionCallback bigQueueConnectionCallback;

    public BigQueueConnection(BigQueueConnectionCallback bigQueueConnectionCallback) {
        this.bigQueueConnectionCallback = bigQueueConnectionCallback;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        try {
            logger.info(xid + " commit");
            this.bigQueueConnectionCallback.execute();
        } catch (IOException e) {
            throw new XAException();
        }
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        logger.info(xid + " end");
    }

    @Override
    public void forget(Xid xid) throws XAException {
        logger.info(xid + " forget");
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
        logger.info(xid + " prepare");
        return 0;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        logger.info(xid + " rollback");
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        logger.info(xid + " start");
    }
}
