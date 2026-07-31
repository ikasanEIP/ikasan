/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.pulsar.producer;

import com.arjuna.ats.jta.resources.LastResourceCommitOptimisation;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Pulsar connection implementing Last Resource Commit Optimisation (LRCO).
 * This allows Pulsar producers to participate in XA transactions without full 2PC support.
 *
 * @author Ikasan Development Team
 */
public class PulsarConnection implements LastResourceCommitOptimisation {

    private static Logger logger = LoggerFactory.getLogger(PulsarConnection.class);

    private PulsarConnectionCallback pulsarConnectionCallback;

    /**
     * Constructor
     *
     * @param pulsarConnectionCallback callback to execute on commit
     */
    public PulsarConnection(PulsarConnectionCallback pulsarConnectionCallback) {
        this.pulsarConnectionCallback = pulsarConnectionCallback;
        if (this.pulsarConnectionCallback == null) {
            throw new IllegalArgumentException("pulsarConnectionCallback cannot be null!");
        }
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        logger.debug("commit {}", xid);
        try {
            this.pulsarConnectionCallback.execute();
        } catch (PulsarClientException e) {
            logger.error("Failed to commit Pulsar message", e);
            throw new XAException(e.getMessage());
        }
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        logger.debug("end {}", xid);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        logger.debug("forget {}", xid);
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
        logger.debug("prepare {}", xid);
        return XA_OK;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        logger.debug("rollback {} - Pulsar message will not be sent", xid);
        // On rollback, we simply don't execute the callback
        // The message is effectively discarded
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        logger.debug("start {}", xid);
    }
}
