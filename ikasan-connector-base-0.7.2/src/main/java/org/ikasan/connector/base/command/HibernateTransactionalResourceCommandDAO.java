/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
