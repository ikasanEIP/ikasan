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
package org.ikasan.connector.basefiletransfer.outbound.persistence;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;

/**
 * Implementation for Hibernate interactions involving File Transfer connectors
 * 
 * TODO Could change this so that all business methods use HibernateTemplate as we 
 * (extend from HibernateDaoSupport)
 * 
 * @author Ikasan Development Team
 */
public class HibernateBaseFileTransferDaoImpl implements BaseFileTransferDao
{
    /** Client ID parameter */
    private final static String CLIENT_ID = "clientId";
    
    /** Criteria parameter */
    private final static String CRITERIA = "criteria";
    
    /** Last Modified parameter */
    private final static String LAST_MODIFIED = "lastModified";
    
    /** Size parameter */
    private final static String SIZE = "size";

    /** Age of Files parameter */
    private final static String AGE_OF_FILES = "ageOfFiles";
    
    /** Hibernate session factory */
    protected SessionFactory sessionFactory;
    
    /** Hibernate name of the mapped object */
    protected static final String filterTableName = "FileFilter";

    /** Logger */
    private static Logger logger = Logger.getLogger(HibernateBaseFileTransferDaoImpl.class);
    
    /**
     * Constructor must provide a persistence handle
     * 
     * @param sessionFactory
     */
    public HibernateBaseFileTransferDaoImpl(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @see org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao#isDuplicate(ClientListEntry,
     *      boolean, boolean)
     */
    public boolean isDuplicate(ClientListEntry entry, boolean filterOnFilename, boolean filterOnLastModifiedDate) throws HibernateException
    {
        // Convert to persistence object
        FileFilter qo = entry.toPersistObject();
        
        // Log the parameters
        logIsDuplicateParameters(qo, filterOnFilename, filterOnLastModifiedDate);
        
        // Query using Hibernate Query Language
        StringBuilder hibernateQuery = new StringBuilder(256);
        hibernateQuery.append(" from "); //$NON-NLS-1$
        hibernateQuery.append(filterTableName);
        hibernateQuery.append(" as po"); //$NON-NLS-1$
        hibernateQuery.append(" where po.clientId = :" + CLIENT_ID); //$NON-NLS-1$
        if (filterOnFilename)
        {
            hibernateQuery.append(" and po.criteria = :" + CRITERIA); //$NON-NLS-1$
        }
        if (filterOnLastModifiedDate)
        {
            hibernateQuery.append(" and po.lastModified = :" + LAST_MODIFIED); //$NON-NLS-1$
        }
        hibernateQuery.append(" and po.size = :" + SIZE); //$NON-NLS-1$
        hibernateQuery.trimToSize();
        FileFilter resultObject = null;
        // Open the session
        Session session = this.sessionFactory.openSession();
        try
        {
            Query query = session.createQuery(hibernateQuery.toString());
            query.setParameter(CLIENT_ID, qo.getClientId());
            if (filterOnFilename)
            {
                query.setParameter(CRITERIA, qo.getCriteria());
            }
            if (filterOnLastModifiedDate)
            {
                query.setParameter(LAST_MODIFIED, qo.getLastModified());
            }
            query.setParameter(SIZE, qo.getSize());
            resultObject = (FileFilter) query.uniqueResult();
        }
        catch (HibernateException e)
        {
            StringBuilder sb = new StringBuilder(256);
            sb.append("Query for unique result failed! ["); //$NON-NLS-1$
            sb.append(hibernateQuery.toString());
            sb.append("]."); //$NON-NLS-1$
            logger.error(sb.toString() + " " + e.getMessage(), e); //$NON-NLS-1$
            throw e;
        }
        finally
        {
            // Always close the session
            if (session.isOpen())
            {
                session.close();
            }
        }
        if (resultObject != null)
        {
            logDuplicateFileFound(resultObject);
            return true;
        }
        return false;
    }

    /**
     * @see org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao#persistClientListEntry(org.ikasan.connector.basefiletransfer.net.ClientListEntry)
     */
    public void persistClientListEntry(ClientListEntry entry) throws HibernateException
    {
        logger.debug("Persisting filter entry for [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create the required persist object
        FileFilter o = entry.toPersistObject();
        
        // Build part of a message
        StringBuilder sb = buildPersistMessage(o);

        // Open the session
        Session session = sessionFactory.openSession();
        try
        {
            // Try saving the entry to the database
            session.save(o);
            session.flush();
            sb.append(" successfully added to ["); //$NON-NLS-1$
            sb.append(filterTableName);
            sb.append("]."); //$NON-NLS-1$
            sb.trimToSize();
            if(logger.isDebugEnabled())
            {
                logger.debug(sb.toString());
            }
        }
        catch (ConstraintViolationException cve)
        {
            logger.debug("Tried to insert duplicate which is not allowed, this is OK behaviour if filterDuplicates is false.");
        }
        finally
        {
            // Always close the session
            if (session.isOpen())
            {
                session.close();
            }
        }
    }

    /**
     * Delete entries from the FileFilter table based on their 
     * age of creation and their clientId
     * 
     * TODO  This is _not_ a platform independent solution 
     * as we are using a Sybase specific functions (dateadd() & getdate())
     * 
     * @param clientId 
     * @param ageOfFiles 
     * @param maxRows 
     * @throws HibernateException 
     */
    public void housekeep(String clientId, int ageOfFiles, int maxRows) throws HibernateException
    {
        int historyInDays = ageOfFiles * -1;
        
        StringBuilder hibernateQuery = new StringBuilder(256);
        hibernateQuery.append(" from ");
        hibernateQuery.append(filterTableName);
        hibernateQuery.append(" as ff");
        hibernateQuery.append(" where ff.clientId = :" + CLIENT_ID);
        hibernateQuery.append(" and dateadd(dd, :" + AGE_OF_FILES + ", getdate()) > ff.createdDateTime");
        hibernateQuery.trimToSize();
        
        if(logger.isDebugEnabled())
        {
            logger.debug("About to housekeep by running [" + hibernateQuery 
                + "], where clientId [" + clientId 
                + "] and ageOfFiles parameter is set to (should be a negative number) [" 
                + historyInDays + "]");
        }

        // Result
        List<FileFilter> fileFilters = null;
        
        Session session = this.sessionFactory.openSession();
        try
        {
            Query query = session.createQuery(hibernateQuery.toString());
            query.setParameter(CLIENT_ID, clientId);
            query.setParameter(AGE_OF_FILES, historyInDays);
            query.setMaxResults(maxRows);
            fileFilters = query.list();
            if (fileFilters != null)
            {
                for (FileFilter fileFilter:fileFilters)
                {
                    session.delete(fileFilter);
                    session.flush();
                }
            }
        }
        catch (HibernateException e)
        {
            StringBuilder sb = new StringBuilder(256);
            sb.append("Query for result failed! [");
            sb.append(hibernateQuery.toString());
            sb.append("].");
            logger.error(sb.toString() + " " + e.getMessage(), e);
            throw e;
        }
        finally
        {
            // Always close the session
            if (session.isOpen())
            {
                session.close();
            }
        }
    }

    /* **********************
     * Helper Logging methods
     * **********************/
    
    /**
     * Log the parameters for the isDuplicate method
     * 
     * @param fileFilter
     * @param filterOnFilename
     * @param filterOnLastModifiedDate
     */
    private void logIsDuplicateParameters(FileFilter fileFilter, boolean filterOnFilename, boolean filterOnLastModifiedDate)
    {
        logger.debug("Entry object is: [" + fileFilter.toString() + "]");
        logger.debug("ClientId is: [" + fileFilter.getClientId() + "]");
        logger.debug("Filtering criteria is [" + fileFilter.getCriteria() + "].");
        logger.debug("Filter On Filename is: [" + filterOnFilename + "]");
        logger.debug("Filter On Last Modified Date is: [" + filterOnLastModifiedDate + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Helper method to do some logging
     * @param resultObject
     */
    private void logDuplicateFileFound(FileFilter resultObject)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("Duplicate File. Dropping entry with ClientId ["); //$NON-NLS-1$
        sb.append(resultObject.getClientId());
        sb.append("] Criteria ["); //$NON-NLS-1$
        sb.append(resultObject.getCriteria());
        sb.append("] LastModifiedDate ["); //$NON-NLS-1$
        sb.append(resultObject.getLastModified());
        sb.append("] LastAccessedDate ["); //$NON-NLS-1$
        sb.append(resultObject.getLastAccessed());
        sb.append("] and Size ["); //$NON-NLS-1$
        sb.append(resultObject.getSize());
        sb.append("]."); //$NON-NLS-1$
        sb.trimToSize();
        if(logger.isDebugEnabled())
        {
            logger.debug(sb.toString());
        }
    }
    
    /**
     * Helper method to do some logging
     * 
     * @param fileFilter
     * @return return partial message
     */
    private StringBuilder buildPersistMessage(FileFilter fileFilter)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("ClientListEntry with ClientId ["); //$NON-NLS-1$
        sb.append(fileFilter.getClientId());
        sb.append("] Criteria ["); //$NON-NLS-1$
        sb.append(fileFilter.getCriteria());
        sb.append("] LastModifiedDate ["); //$NON-NLS-1$
        sb.append(fileFilter.getLastModified());
        sb.append("] LastAccessedDate ["); //$NON-NLS-1$
        sb.append(fileFilter.getLastAccessed());
        sb.append("] and Size ["); //$NON-NLS-1$
        sb.append(fileFilter.getSize());
        sb.append(']');
        return sb;
    }
    
}
