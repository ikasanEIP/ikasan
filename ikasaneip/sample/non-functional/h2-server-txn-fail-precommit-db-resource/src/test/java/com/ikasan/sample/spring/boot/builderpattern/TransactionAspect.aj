/*
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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
package com.ikasan.sample.spring.boot.builderpattern;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.ikasan.nonfunctional.test.util.TransactionTestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


@Aspect
public class TransactionAspect
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    int h2_prepare_callCount = 0;
    int h2_commit_callCount = 0;
    int amq_prepare_callCount = 0;
    int amq_commit_callCount = 0;

    Map<String, String> transactions = new HashMap();

    @Before("execution(* org.h2.jdbcx.JdbcXAConnection.prepare(javax.transaction.xa.Xid)) && args(xid)")
    public void beforeH2Prepare(javax.transaction.xa.Xid xid)
    {
        ++h2_prepare_callCount;
        String transactionId = TransactionTestUtil.getTransactionId(xid);
        String branchQualifier = TransactionTestUtil.getTransactionBranchId(xid);

        String msg = ((transactions.containsKey(branchQualifier))
                ? "#### Existing Txn H2  prepare() on call "
                : "#### New      Txn H2  prepare() on call ")
                + h2_prepare_callCount
                + "\t TxnId " + transactionId
                + "\t Branch " + branchQualifier;

        logger.info(msg);
        transactions.put(branchQualifier, transactionId);
        if(h2_prepare_callCount == 2)
        {
            throw new RuntimeException("Transaction Fail Exception " + msg);
        }
    }

    @Before("execution(* org.h2.jdbcx.JdbcXAConnection.commit(javax.transaction.xa.Xid, boolean)) && args(xid, var1)")
    public void beforeH2Commit(javax.transaction.xa.Xid xid, boolean var1)
    {
        ++h2_commit_callCount;
        String transactionId = TransactionTestUtil.getTransactionId(xid);
        String branchQualifier = TransactionTestUtil.getTransactionBranchId(xid);

        String msg = ((transactions.containsKey(branchQualifier))
                ? "#### Existing Txn H2  commit() on call "
                : "#### New 1PC  Txn H2  commit() on call ")
                + h2_commit_callCount
                + "\t TxnId " + transactionId
                + "\t Branch " + branchQualifier;

        logger.info(msg);
    }

    @Before("execution(* org.apache.activemq.TransactionContext.prepare(javax.transaction.xa.Xid)) && args(xid)")
    public void beforeAmqPrepare(javax.transaction.xa.Xid xid)
    {
        ++amq_prepare_callCount;
        String transactionId = TransactionTestUtil.getTransactionId(xid);
        String branchQualifier = TransactionTestUtil.getTransactionBranchId(xid);
        String msg = ((transactions.containsKey(branchQualifier))
                ? "#### Existing Txn AMQ prepare() on call "
                : "#### New      Txn AMQ prepare() on call ")
                + amq_prepare_callCount
                + "\t TxnId " + transactionId
                + "\t Branch " + branchQualifier;

        logger.info(msg);
        transactions.put(branchQualifier, transactionId);
    }

    @Before("execution(* org.apache.activemq.TransactionContext.commit(javax.transaction.xa.Xid, boolean)) && args(xid, onePhase)")
    public void beforeAmqCommit(javax.transaction.xa.Xid xid, boolean onePhase)
    {
        ++amq_commit_callCount;
        String transactionId = TransactionTestUtil.getTransactionId(xid);
        String branchQualifier = TransactionTestUtil.getTransactionBranchId(xid);
        String msg = ((transactions.containsKey(branchQualifier))
                ? "#### Existing Txn AMQ commit() on call "
                : "#### New 1PC  Txn AMQ commit() on call ")
                + amq_commit_callCount
                + "\t TxnId " + transactionId
                + "\t Branch " + branchQualifier;

        logger.info(msg);
    }

}
