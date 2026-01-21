package org.ikasan.component.endpoint.kafka.client.reactive.producer;

import com.arjuna.ats.jta.resources.LastResourceCommitOptimisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class KafkaConnection implements LastResourceCommitOptimisation {
    private static Logger logger = LoggerFactory.getLogger(KafkaConnection.class);

    private org.ikasan.component.endpoint.kafka.producer.KafkaConnectionCallback kafkaConnectionCallback;

    public KafkaConnection(org.ikasan.component.endpoint.kafka.producer.KafkaConnectionCallback kafkaConnectionCallback) {
        this.kafkaConnectionCallback = kafkaConnectionCallback;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        try {
            logger.debug(xid + " commit");
            this.kafkaConnectionCallback.execute();
        } catch (Throwable e) {
            throw new XAException();
        }
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        logger.debug(xid + " end");
    }

    @Override
    public void forget(Xid xid) throws XAException {
        logger.debug(xid + " forget");
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
        logger.debug(xid + " prepare");
        return 0;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        logger.debug(xid + " rollback");
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        logger.debug(xid + " start");
    }
}
