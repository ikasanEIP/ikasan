/*
 * $Id:$
 * $URL:$
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
package org.ikasan.connector.base.command;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.ikasan.connector.base.command.state.State;
import org.ikasan.connector.util.HexConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hibernate Implementation of the <Code>TransactionalResourceCommandDAO</code>
 * 
 * @author Ikasan Development Team
 */
public class HibernateTransactionalResourceCommandDAO implements TransactionalResourceCommandDAO
{
    /** Hibernate globalTransactionId parameter */
    private static final String GLOBAL_TRANSACTION_ID_PARAMETER = "globalTransactionIdParam";

    /** Hibernate branchTransactionId parameter */
    private static final String BRANCH_TRANSACTION_ID_PARAMETER = "branchTransactionIdParam";

    /** Hibernate state parameter */
    private static final String STATE_PARAMETER = "stateParam";

    /** Hibernate query for finding persisted Xid from a given xid. */
    private static final String FIND_XID_BY_XID = " from XidImpl c where c.globalTransactionIdString = :"
            + GLOBAL_TRANSACTION_ID_PARAMETER + " and c.branchQualifierString = :" + BRANCH_TRANSACTION_ID_PARAMETER;

    /** Hibernate query for finding persisted Xid by state */
    private static final String FIND_XID_BY_STATE = " from XidImpl c where c.state = :" + STATE_PARAMETER;

    /** Hibernate query for finding all commands in a given state */
    private static final String FIND_COMMANDS_BY_STATE = " from AbstractTransactionalResourceCommand c where c.state = :"
            + STATE_PARAMETER;

    /** The logger instance. */
    private static Logger logger = LoggerFactory.getLogger(HibernateTransactionalResourceCommandDAO.class);

    @PersistenceContext(unitName = "file-transfer")
    private EntityManager entityManager;

    public void save(TransactionalResourceCommand command) throws TransactionalResourceCommandPersistenceException
    {
        logger.debug("save called with command [" + command + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
            this.entityManager.persist(command);
        }
        catch (HibernateException e)
        {
            throw new TransactionalResourceCommandPersistenceException(e);
        }

    }

    @SuppressWarnings("unchecked")
    public List<TransactionalResourceCommand> findCommandsByTransaction(Xid xid)
            throws TransactionalResourceCommandPersistenceException
    {
        List<TransactionalResourceCommand> result = new ArrayList<TransactionalResourceCommand>();
        XidImpl xidImpl = find(xid);
        if (xidImpl == null)
        {
            throw new TransactionalResourceCommandPersistenceException(
                "Could not find commands for Transaction because Xid cannot be found!!"); //$NON-NLS-1$
        }
        logger.debug("found xidImpl [" + xidImpl + "], now looking for associated commands..."); //$NON-NLS-1$//$NON-NLS-2$
        try
        {
            String xidParam = "xidParam";
            Query query = this.entityManager.createQuery("from AbstractTransactionalResourceCommand c where c.xid.id = :"
                + xidParam + " order by c.xid.id");
            logger.debug("Executing from AbstractTransactionalResourceCommand c where c.xid.id = :" + xidImpl.getId() + " order by c.xid.id");
            query.setParameter(xidParam, xidImpl.getId());
            result = query.getResultList();
        }
        catch (HibernateException e)
        {
            logger.error(e.getMessage(),e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        logger.debug("Number of results [" + result.size() + "]");
        return result;
    }



    public List<TransactionalResourceCommand> findCommandsByState(State state)
            throws TransactionalResourceCommandPersistenceException
    {
        try
        {
            Query query = this.entityManager.createQuery(FIND_COMMANDS_BY_STATE);
            query.setParameter(STATE_PARAMETER, state.getName());
            return query.getResultList();
        }
        catch (HibernateException e)
        {
            logger.error(e.getMessage(),e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }

    }

    public XidImpl find(Xid xid) throws TransactionalResourceCommandPersistenceException
    {
        long startTime = System.currentTimeMillis();
        logger.debug("looking for Xid matching [" + xid + "]"); //$NON-NLS-1$//$NON-NLS-2$
        XidImpl result = null;
        try
        {
            Query query = this.entityManager.createQuery(FIND_XID_BY_XID);
            String globalTransactionId = HexConverter.byteArrayToHex(xid.getGlobalTransactionId());
            String branchQualifier = HexConverter.byteArrayToHex(xid.getBranchQualifier());
            query.setParameter(GLOBAL_TRANSACTION_ID_PARAMETER, globalTransactionId);
            query.setParameter(BRANCH_TRANSACTION_ID_PARAMETER, branchQualifier);
            List<XidImpl> xids = query.getResultList();

            if (xids.size() > 1)
            {
                Iterator<XidImpl> xidIterator = xids.iterator();
                while (xidIterator.hasNext())
                {
                    XidImpl xidImpl = xidIterator.next();
                    logger.debug("found non unique XidImpl [" + xidImpl + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                throw new NonUniqueResultException(xids.size());
            }
            else if (xids.size() == 1)
            {
                result = xids.get(0);
            }
        }
        catch (HibernateException e)
        {
            logger.error(e.getMessage(),e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }

        long endTime = System.currentTimeMillis();
        logger.debug("found [" + xid + "] in [" + (endTime - startTime) + "] ms"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        return result;
    }

    public void save(XidImpl xid) throws TransactionalResourceCommandPersistenceException
    {
        logger.debug("save called with xid [" + xid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
            this.entityManager.persist(xid);
        }
        catch (HibernateException e)
        {
            throw new TransactionalResourceCommandPersistenceException(e);
        }
    }

    public List<XidImpl> findXidbyState(String state) throws TransactionalResourceCommandPersistenceException
    {
        try
        {
            Query query = this.entityManager.createQuery(FIND_XID_BY_STATE);
            query.setParameter(STATE_PARAMETER, state);
            return query.getResultList();
        }
        catch (HibernateException e)
        {
            logger.error(e.getMessage(),e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }

    }

    public void deleteCommand(TransactionalResourceCommand command)
            throws TransactionalResourceCommandPersistenceException
    {
        delete(command);
    }

    public void deleteXid(XidImpl xid) throws TransactionalResourceCommandPersistenceException
    {
        logger.debug("delete called with Xid [" + xid + "]"); //$NON-NLS-1$//$NON-NLS-2$
        delete(xid);
    }

    /**
     * Delete an object
     * 
     * @param obj - Object to delete
     * @throws TransactionalResourceCommandPersistenceException - Exception if delete fails
     */
    private void delete(Object obj) throws TransactionalResourceCommandPersistenceException
    {
        try
        {
            this.entityManager.remove(obj);
        }
        catch (HibernateException e)
        {
            throw new TransactionalResourceCommandPersistenceException(e);
        }

    }
}
