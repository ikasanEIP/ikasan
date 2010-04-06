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
