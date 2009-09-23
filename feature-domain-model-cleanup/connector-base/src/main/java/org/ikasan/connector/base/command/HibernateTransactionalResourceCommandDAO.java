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
package org.ikasan.connector.base.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.transaction.xa.Xid;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.ikasan.connector.ConnectorContext;
import org.ikasan.connector.ResourceLoader;
import org.ikasan.connector.base.command.state.State;

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
    private static Logger logger = Logger.getLogger(HibernateTransactionalResourceCommandDAO.class);

    /** Session Factory for a local transactional datasource */
    private SessionFactory sessionFactory;

    /** Connector context is hidden behind an interface */
    protected ConnectorContext context = ResourceLoader.getInstance().newContext();

    /**
     * Constructor
     * 
     * @deprecated - use alternate constructor avoiding JNDI lookup internally
     * 
     * @param sessionFactoryServicePath - The session factory service path
     * @throws ResourceException - Exception if construction fails
     */
    @Deprecated
    public HibernateTransactionalResourceCommandDAO(String sessionFactoryServicePath) throws ResourceException
    {
        try
        {
            this.sessionFactory = (SessionFactory) context.lookup(sessionFactoryServicePath);
            if (this.sessionFactory == null)
            {
                throw new ResourceException("SessionFactory lookup of [" //$NON-NLS-1$
                        + sessionFactoryServicePath + "] returned null"); //$NON-NLS-1$
            }
        }
        catch (NamingException e)
        {
            throw new ResourceException("Failed to get SessionFactory from [" //$NON-NLS-1$
                    + sessionFactoryServicePath + "].", e); //$NON-NLS-1$
        }
    }

    /**
     * Constructor
     * 
     * @param sessionFactory - The session factory to use
     */
    public HibernateTransactionalResourceCommandDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public void save(TransactionalResourceCommand command) throws TransactionalResourceCommandPersistenceException
    {
        logger.debug("save called with command [" + command + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        Session session = null;
        try
        {
            session = startSession();
            session.saveOrUpdate(command);
            session.flush();
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            if (session != null)
            {
                session.getTransaction().rollback();
            }
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                session.close();
            }
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
        Session session = null;
        try
        {
            session = startSession();
            String xidParam = "xidParam";
            Query query = session.createQuery("from AbstractTransactionalResourceCommand c where c.xid.id = :"
                    + xidParam);
            query.setParameter(xidParam, xidImpl.getId());
            result = query.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            logger.error(e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen()) session.close();
        }
        return result;
    }

    /**
     * Initiates a new session within a hibernate transaction
     * 
     * @return Session
     */
    private Session startSession()
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    public List<TransactionalResourceCommand> findCommandsByState(State state)
            throws TransactionalResourceCommandPersistenceException
    {
        List<TransactionalResourceCommand> result = new ArrayList<TransactionalResourceCommand>();
        Session session = null;
        try
        {
            session = startSession();
            Query query = session.createQuery(FIND_COMMANDS_BY_STATE);
            query.setParameter(STATE_PARAMETER, state.getName());
            result = query.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            logger.error(e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen()) session.close();
        }
        return result;
    }

    public XidImpl find(Xid xid) throws TransactionalResourceCommandPersistenceException
    {
        long startTime = System.currentTimeMillis();
        logger.debug("looking for Xid matching [" + xid + "]"); //$NON-NLS-1$//$NON-NLS-2$
        XidImpl result = null;
        Session session = null;
        try
        {
            session = startSession();
            Query query = session.createQuery(FIND_XID_BY_XID);
            query.setParameter(GLOBAL_TRANSACTION_ID_PARAMETER, new String(xid.getGlobalTransactionId()));
            query.setParameter(BRANCH_TRANSACTION_ID_PARAMETER, new String(xid.getBranchQualifier()));
            List<XidImpl> xids = query.list();
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
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            logger.error(e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen()) session.close();
        }
        long endTime = System.currentTimeMillis();
        logger.debug("found [" + xid + "] in [" + (endTime - startTime) + "] ms"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        return result;
    }

    public void save(XidImpl xid) throws TransactionalResourceCommandPersistenceException
    {
        logger.debug("save called with xid [" + xid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        Session session = null;
        try
        {
            session = startSession();
            session.saveOrUpdate(xid);
            session.flush();
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            if (session != null)
            {
                session.getTransaction().rollback();
            }
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                session.close();
            }
        }
    }

    public List<XidImpl> findXidbyState(String state) throws TransactionalResourceCommandPersistenceException
    {
        List<XidImpl> result = new ArrayList<XidImpl>();
        Session session = null;
        try
        {
            session = startSession();
            Query query = session.createQuery(FIND_XID_BY_STATE);
            query.setParameter(STATE_PARAMETER, state);
            result = query.list();
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            logger.error(e);
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen()) session.close();
        }
        return result;
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
        Session session = null;
        try
        {
            session = startSession();
            session.delete(obj);
            session.flush();
            session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            if (session != null)
            {
                session.getTransaction().rollback();
            }
            throw new TransactionalResourceCommandPersistenceException(e);
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                session.close();
            }
        }
    }
}
