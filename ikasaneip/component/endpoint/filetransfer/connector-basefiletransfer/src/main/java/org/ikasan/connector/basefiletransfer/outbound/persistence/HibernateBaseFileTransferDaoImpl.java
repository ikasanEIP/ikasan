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
package org.ikasan.connector.basefiletransfer.outbound.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.search.PagedSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import jakarta.persistence.NoResultException;
import java.util.*;

/**
 * Implementation for Hibernate interactions involving File Transfer connectors
 * <p>
 *
 * @author Ikasan Development Team
 */
public class HibernateBaseFileTransferDaoImpl extends HibernateDaoSupport implements BaseFileTransferDao
{
    /**
     * Client ID parameter
     */
    private final static String CLIENT_ID = "clientId";

    /**
     * Criteria parameter
     */
    private final static String CRITERIA = "criteria";

    /**
     * Last Modified parameter
     */
    private final static String LAST_MODIFIED = "lastModified";

    /**
     * Size parameter
     */
    private final static String SIZE = "size";

    /**
     * Created Date parameter
     **/
    private final static String CREATED_DATE_TIME = "createdDateTime";

    public static final String FILE_FILTER_TO_SELECT_ONE_QUERY =
        """
        select ff from FileFilter ff \
         where ff.id = :id \
        """;

    public static final String HOUSEKEEP_FILE_FILTER_FROM =
        """
        select ff from FileFilter ff \
         where ff.clientId=:clientId and createdDateTime < :createdDateTime\
        """;

    public static final String COUNT_FILE_FILTER_FROM = "select count(ff) from FileFilter ff " + " where ";

    public static final String FILE_FILTER_FROM = "select ff from FileFilter ff where ";

    public static final String FILE_FILTER_CLIENT_EQUALS_PREDICATE = " ff.fileFilterId.clientId = :" + CLIENT_ID;

    public static final String FILE_FILTER_CRITERIA_EQUALS_PREDICATE = " ff.fileFilterId.criteria = :" + CRITERIA;

    public static final String FILE_FILTER_SIZE_EQUALS_PREDICATE = " ff.fileFilterId.size = :" + SIZE;

    public static final String FILE_FILTER_LAST_MODIFIED_EQUALS_PREDICATE =
        " ff.fileFilterId.lastModified = :" + LAST_MODIFIED;

    public static final String FILE_FILTER_CLIENT_LIKE_PREDICATE = " ff.fileFilterId.clientId like :" + CLIENT_ID;

    public static final String FILE_FILTER_CRITERIA_LIKE_PREDICATE = " ff.fileFilterId.criteria like :" + CRITERIA;

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(HibernateBaseFileTransferDaoImpl.class);

    /**
     * @see org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao#isDuplicate(ClientListEntry,
     * boolean, boolean)
     */
    public boolean isDuplicate(ClientListEntry entry, boolean filterOnFilename, boolean filterOnLastModifiedDate)
        throws HibernateException
    {
        // Convert to persistence object
        FileFilter qo = entry.toPersistObject();

        // Log the parameters
        logIsDuplicateParameters(qo, filterOnFilename, filterOnLastModifiedDate);
        try
        {
            FileFilter resultObject = (FileFilter) getHibernateTemplate().execute(new HibernateCallback<Object>()
            {
                public Object doInHibernate(Session session) throws HibernateException
                {
                    Query query = getQueryWithParam(session.createQuery(buildQuery()));

                    return query.getSingleResult();
                }

                /**
                 * Enrich query with provided parameters.
                 *
                 * @param query
                 * @return
                 */
                private Query getQueryWithParam(Query query)
                {

                    query.setParameter(CLIENT_ID, qo.getClientId());
                    query.setParameter(SIZE, qo.getSize());
                    if ( filterOnFilename )
                    {
                        query.setParameter(CRITERIA, qo.getCriteria());
                    }

                    if ( filterOnLastModifiedDate )
                    {
                        query.setParameter(LAST_MODIFIED, qo.getLastModified());
                    }

                    return query;
                }

                /**
                 * Create query with provided parameters.
                 *
                 * @return
                 */
                private String buildQuery()
                {
                    StringBuilder query = new StringBuilder();
                    query.append(FILE_FILTER_FROM);
                    query.append(String.join(" AND ", predicate()));

                    return query.toString();

                }

                private List<String> predicate()
                {
                    List<String> predicates = new ArrayList<>();

                    predicates.add(FILE_FILTER_CLIENT_EQUALS_PREDICATE);
                    predicates.add(FILE_FILTER_SIZE_EQUALS_PREDICATE);

                    if ( filterOnFilename )
                    {
                        predicates.add(FILE_FILTER_CRITERIA_EQUALS_PREDICATE);
                    }

                    if ( filterOnLastModifiedDate )
                    {
                        predicates.add(FILE_FILTER_LAST_MODIFIED_EQUALS_PREDICATE);
                    }

                    return predicates;
                }
            });

            if ( resultObject != null )
            {
                logDuplicateFileFound(resultObject);
                return true;
            }
            return false;
        }
        catch (NoResultException noResultException)
        {
            return false;
        }

    }

    /**
     * @see org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao#persistClientListEntry(org.ikasan.connector.basefiletransfer.net.ClientListEntry)
     */
    public void persistClientListEntry(ClientListEntry entry) throws HibernateException
    {
        logger.debug("Persisting filter entry for [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        // Create the required persist object
        FileFilter o = entry.toPersistObject();

        try
        {
            this.getHibernateTemplate().save(o);
        }
        catch (ConstraintViolationException cve)
        {
            logger.debug(
                "Tried to insert duplicate which is not allowed, this is OK behaviour if filterDuplicates is false.");
        }

    }

    /**
     * Delete entries from the FileFilter table based on their
     * age of creation and their clientId
     * <p>
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
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, historyInDays);

        if ( logger.isDebugEnabled() )
        {
            logger.debug(
                "About to housekeep by running  where clientId [" + clientId + "], createdDateTime [" + cal.getTime()
                                                                                                           .getTime()
                    + "] and ageOfFiles parameter is set to (should be a negative number) [" + historyInDays + "]");
        }

        getHibernateTemplate().execute((session) -> {

            Query query = session.createQuery(HOUSEKEEP_FILE_FILTER_FROM);
            query.setParameter(CLIENT_ID, clientId);
            query.setParameter(CREATED_DATE_TIME, cal.getTime().getTime());

            query.setMaxResults(maxRows);

            query.list().stream().forEach(fileFilter -> session.delete(fileFilter));
            return null;
        });

    }

    public FileFilter findById(int id)
    {
        return getHibernateTemplate().execute((session) -> {

            Query query = session.createQuery(FILE_FILTER_TO_SELECT_ONE_QUERY);
            query.setParameter("id", id);

            List<FileFilter> result = query.getResultList();
            if ( !result.isEmpty() )
            {
                return result.get(0);
            }
            else
            {
                return null;
            }

        });
    }

    public void delete(FileFilter fileFilter)
    {
        getHibernateTemplate().delete(fileFilter);
    }

    public FileFilter save(FileFilter fileFilter)
    {
        getHibernateTemplate().save(fileFilter);
        return fileFilter;
    }

    @Override
    public PagedSearchResult<FileFilter> find(int pageNo, int pageSize, String criteria, String clientId)
    {

        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                Query query = getQueryWithParam(session.createQuery(buildQuery(false)));

                query.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                query.setFirstResult(firstResult);

                List<FileFilter> results = query.getResultList();

                Long rowCount = rowCount(session);

                return new ArrayListPagedSearchResult(results, firstResult, rowCount);
            }

            private Long rowCount(Session session)
            {

                Query metaDataQuery = getQueryWithParam(session.createQuery(buildQuery(true)));

                List<Long> rowCountList = metaDataQuery.getResultList();
                if ( !rowCountList.isEmpty() )
                {
                    return rowCountList.get(0);
                }
                return Long.valueOf(0);
            }

            /**
             * Enrich query with provided parameters.
             * @param query
             * @return
             */
            private Query getQueryWithParam(Query query)
            {

                if ( restrictionExists(clientId) )
                {
                    query.setParameter(CLIENT_ID, clientId);
                }

                if ( restrictionExists(criteria) )
                {
                    query.setParameter(CRITERIA, criteria);
                }

                return query;
            }

            /**
             * Create query with provided parameters.
             * @param shouldCount
             * @return
             */
            private String buildQuery(boolean shouldCount)
            {
                StringBuilder query = new StringBuilder();
                if ( shouldCount )
                {
                    query.append(COUNT_FILE_FILTER_FROM);
                }
                else
                {
                    query.append(FILE_FILTER_FROM);
                }
                query.append(String.join(" AND ", predicate()));

                return query.toString();

            }

            private List<String> predicate()
            {
                List<String> predicates = new ArrayList<>();
                if ( restrictionExists(clientId) )
                {
                    predicates.add(FILE_FILTER_CLIENT_LIKE_PREDICATE);
                }

                if ( restrictionExists(criteria) )
                {
                    predicates.add(FILE_FILTER_CRITERIA_LIKE_PREDICATE);
                }

                return predicates;
            }
        });

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
    private void logIsDuplicateParameters(FileFilter fileFilter, boolean filterOnFilename,
                                          boolean filterOnLastModifiedDate)
    {
        logger.debug("Entry object is: [" + fileFilter.toString() + "]");
        logger.debug("ClientId is: [" + fileFilter.getClientId() + "]");
        logger.debug("Filtering criteria is [" + fileFilter.getCriteria() + "].");
        logger.debug("Filter On Filename is: [" + filterOnFilename + "]");
        logger.debug("Filter On Last Modified Date is: [" + filterOnLastModifiedDate + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Helper method to do some logging
     *
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
        if ( logger.isDebugEnabled() )
        {
            logger.debug(sb.toString());
        }
    }

    /**
     * Check to see if the restriction exists
     *
     * @param restrictionValue - The value to check
     * @return - true if the restriction exists for that value, else false
     */
    static final boolean restrictionExists(Object restrictionValue)
    {
        // If the value passed in is not null and not an empty string then it
        // can have a restriction applied
        if ( restrictionValue != null )
        {
            if ( restrictionValue instanceof Collection collection )
            {
                if ( !collection.isEmpty() )
                { return true; }
            }
            else
            {

                if ( !"".equals(restrictionValue) )
                { return true; }
            }

        }
        return false;
    }

}
