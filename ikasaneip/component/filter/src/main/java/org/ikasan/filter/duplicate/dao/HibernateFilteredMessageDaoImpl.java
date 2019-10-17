/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */

package org.ikasan.filter.duplicate.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.search.PagedSearchResult;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of {@link FilteredMessageDao}
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateFilteredMessageDaoImpl extends HibernateDaoSupport implements FilteredMessageDao
{
    public static final String EXPIRY = "expiry";
    public static final String EVENT_IDS = "eventIds";
    public static final String NOW = "now";
    public static final String FROM_DATE = "fromDate";
    public static final String UNTIL_DATE = "untilDate";

    /** Query used for housekeeping expired filtered messages */
    private static final String HOUSEKEEP_QUERY = "delete DefaultFilterEntry m where m.expiry <= :" + EXPIRY;

    private static final String MESSAGE_FILTER_TO_DELETE_QUERY = "delete DefaultFilterEntry se where "
        + " se.clientId = :" + FilterEntry.CLIENT_ID_PROP_KEY + " and se.criteria = :" +FilterEntry.CRITERIA_PROP_KEY;

    public static final String MESSAGE_FILTER_ENTRIES_TO_DELETE_QUERY = "select id from DefaultFilterEntry se " +
            " where se.expiry < :" + NOW;

    public static final String MESSAGE_FILTER_ENTRIES_TO_SELECT_ONE_QUERY = "select se from DefaultFilterEntry se " +
        " where se.clientId = :" + FilterEntry.CLIENT_ID_PROP_KEY + " and se.criteria = :" +FilterEntry.CRITERIA_PROP_KEY;

    public static final String MESSAGE_FILTER_ENTRIES_TO_SELECT_BY_CLIENTID_QUERY = "select se from DefaultFilterEntry se " +
        " where se.clientId = :" + FilterEntry.CLIENT_ID_PROP_KEY ;


    public static final String MESSAGE_FILTER_ENTRIES_DELETE_QUERY = "delete DefaultFilterEntry se " +
            " where se.id in(:" + EVENT_IDS + ")";


    public static final String SEARCH_FILTER_ENTRIES_FROM = "select se from DefaultFilterEntry se " +
        " where ";

    public static final String COUNT_FILTER_ENTRIES_FROM = "select count(se) from DefaultFilterEntry se " +
        " where ";

    public static final String SEARCH_FILTER_ENTRIES_CLIENT_PREDICATE = " se.id.clientId = :" + FilterEntry.CLIENT_ID_PROP_KEY;

    public static final String SEARCH_FILTER_ENTRIES_CRITERIA_PREDICATE = " se.criteria = :" + FilterEntry.CRITERIA_PROP_KEY;

    public static final String SEARCH_FILTER_ENTRIES_UNTIL_DATE_PREDICATE = " se.createdDateTime < :" + UNTIL_DATE;

    public static final String SEARCH_FILTER_ENTRIES_FROM_DATE_PREDICATE = " se.createdDateTime > :" + FROM_DATE;


    /** Flag for batch housekeeping option. Defaults to true */
    private boolean batchHousekeepDelete = true;

    /** The batch size used when {@link #batchHousekeepDelete} option is set. Default to 100*/
    private int housekeepingBatchSize = 100;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 1000;


    @Override
    public List<FilterEntry> findMessages(String clientId)
    {
        return getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(MESSAGE_FILTER_ENTRIES_TO_SELECT_BY_CLIENTID_QUERY);
            query.setParameter(FilterEntry.CLIENT_ID_PROP_KEY,  clientId);

            List<FilterEntry> result = query.getResultList();
            if (!result.isEmpty())
            {
                return result;
            }else{
                return null;
            }

        });

    }

    @Override
    public PagedSearchResult<FilterEntry> findMessagesByPage(final int pageNo, final int pageSize,
        Integer criteria, String clientId ,final Date fromDate, final Date untilDate)
    {


        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                Query query = getQueryWithParam(session.createQuery(buildQuery(false)));

                query.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                query.setFirstResult(firstResult);

                List<FilterEntry> results = query.getResultList();

                Long rowCount = rowCount(session);

                return new ArrayListPagedSearchResult(results, firstResult, rowCount);
            }

            private Long rowCount(Session session){

                Query metaDataQuery = getQueryWithParam(session.createQuery(buildQuery(true)));

                List<Long> rowCountList = metaDataQuery.getResultList();
                if (!rowCountList.isEmpty())
                {
                    return rowCountList.get(0);
                }
                return new Long(0);
            }

            /**
             * Enrich query with provided parameters.
             * @param query
             * @return
             */
            private Query getQueryWithParam(Query query)
            {


                if (restrictionExists(clientId))
                {
                    query.setParameter(FilterEntry.CLIENT_ID_PROP_KEY,  clientId);
                }

                if (restrictionExists(criteria))
                {
                    query.setParameter(FilterEntry.CRITERIA_PROP_KEY,  criteria);
                }

                if (restrictionExists(fromDate))
                {
                    query.setParameter(FROM_DATE,  fromDate.getTime());
                }
                if (restrictionExists(untilDate))
                {
                    query.setParameter(UNTIL_DATE,  untilDate.getTime());
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
                if (shouldCount)
                {
                    query.append(COUNT_FILTER_ENTRIES_FROM);
                }
                else
                {
                    query.append(SEARCH_FILTER_ENTRIES_FROM);
                }
                query.append(String.join(" AND ",predicate()));

                return query.toString();

            }

            private List<String> predicate()
            {
                List<String> predicates = new ArrayList<>();
                if (restrictionExists(clientId))
                {
                    predicates.add(SEARCH_FILTER_ENTRIES_CLIENT_PREDICATE);
                }

                if (restrictionExists(criteria))
                {
                    predicates.add(SEARCH_FILTER_ENTRIES_CRITERIA_PREDICATE);
                }

                if (restrictionExists(fromDate))
                {
                    predicates.add(SEARCH_FILTER_ENTRIES_FROM_DATE_PREDICATE);
                }
                if (restrictionExists(untilDate))
                {
                    predicates.add(SEARCH_FILTER_ENTRIES_UNTIL_DATE_PREDICATE);
                }

                return predicates;
            }
        });

    }

    /*
         * (non-Javadoc)
         * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#findMessageById(org.ikasan.filter.duplicate.window.FilterEntry)
         */
    @SuppressWarnings("unchecked")
    public FilterEntry findMessage(FilterEntry message)
    {
        return getHibernateTemplate().execute((session) -> {

            Query query = session.createQuery(MESSAGE_FILTER_ENTRIES_TO_SELECT_ONE_QUERY);
            query.setParameter(FilterEntry.CRITERIA_PROP_KEY,  message.getCriteria());
            query.setParameter(FilterEntry.CLIENT_ID_PROP_KEY,  message.getClientId());

            List<FilterEntry> result = query.getResultList();
            if (!result.isEmpty())
            {
                return result.get(0);
            }else{
                return null;
            }

        });

    }

    @Override
    public void saveOrUpdate(FilterEntry message)
    {
        this.getHibernateTemplate().saveOrUpdate(message);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#save(org.ikasan.filter.duplicate.window.FilterEntry)
     */
    public void save(FilterEntry message)
    {
        this.getHibernateTemplate().save(message);
    }

    public void delete(FilterEntry message)
    {
        getHibernateTemplate().execute((session) -> {

            Query query = session.createQuery(MESSAGE_FILTER_TO_DELETE_QUERY);
            query.setParameter(FilterEntry.CRITERIA_PROP_KEY,  message.getCriteria());
            query.setParameter(FilterEntry.CLIENT_ID_PROP_KEY,  message.getClientId());

            return query.executeUpdate();

        });

    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.FilteredMessageDao#deleteAllExpired()
     */
    public void deleteAllExpired()
    {
        if (!this.batchHousekeepDelete)
        {
            getHibernateTemplate().execute(session -> {
                Query query = session.createQuery(HOUSEKEEP_QUERY);
                query.setParameter(EXPIRY, System.currentTimeMillis());
                query.executeUpdate();
                return null;
            });
        }
        else
        {
            this.batchDeleteAllExpired();
        }
    }

    /**
     * Delete expired messages 100 at a time until non is left
     */
    protected void batchDeleteAllExpired()
    {
        logger.debug("MessageFilter batch delete.");

        int numberDeleted = 0;

        while(housekeepablesExist() && numberDeleted < this.transactionBatchSize)
        {
            numberDeleted += this.housekeepingBatchSize;
            getHibernateTemplate().execute(session -> {
                Query query = session.createQuery(MESSAGE_FILTER_ENTRIES_TO_DELETE_QUERY);
                query.setParameter(NOW, System.currentTimeMillis());
                query.setMaxResults(housekeepingBatchSize);

                List<Long> filterIds = (List<Long>) query.list();
                if (filterIds.size() > 0)
                {
                    query = session.createQuery(MESSAGE_FILTER_ENTRIES_DELETE_QUERY);
                    query.setParameterList(EVENT_IDS, filterIds);
                    query.executeUpdate();
                }

                return null;
            });
        }
    }

    /**
     * Find expired entries
     * @return List of max 100 expired filter entries
     */
    @SuppressWarnings("unchecked")
    public List<FilterEntry> findExpiredMessages()
    {
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<FilterEntry> criteriaQuery = builder.createQuery(FilterEntry.class);
            Root<DefaultFilterEntry> root = criteriaQuery.from(DefaultFilterEntry.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.lessThan(root.get(FilterEntry.EXPRIY_PROP_KEY), System.currentTimeMillis()));
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
            Query<FilterEntry> query = session.createQuery(criteriaQuery);
            query.setMaxResults(housekeepingBatchSize);
            List<FilterEntry> result = query.getResultList();
            if (!result.isEmpty())
            {
                return result;
            }else{
                return null;
            }

        });
    }

    /**
     * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
     *
     * @return true if there is at least 1 expired WiretapFlowEvent
     */
    public boolean housekeepablesExist()
    {
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<DefaultFilterEntry> root = criteriaQuery.from(DefaultFilterEntry.class);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.lessThan(root.get(FilterEntry.EXPRIY_PROP_KEY), System.currentTimeMillis()));
            criteriaQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));
            org.hibernate.query.Query<Long> query = session.createQuery(criteriaQuery);
            List<Long> rowCountList = query.getResultList();
            Long rowCount = new Long(0);
            if (!rowCountList.isEmpty())
            {
                rowCount = rowCountList.get(0);
            }
            logger.debug(rowCount+", MessageFilter housekeepables exist");
            return new Boolean(rowCount > 0);
        });
    }

    /* (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.FilteredMessageDao#setTransactionBatchSize(int)
     */
    @Override
    public void setTransactionBatchSize(int transactionBatchSize)
    {
        this.transactionBatchSize = transactionBatchSize;
    }

    /**
     * Setter for {@link #batchHousekeepDelete} flag for overriding default
     * value
     * @param batchHousekeepDelete
     */
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }

    /**
     * Setter for {@link #housekeepingBatchSize} for overriding default value
     * @param housekeepingBatchSize
     */
    public void setHousekeepingBatchSize(int housekeepingBatchSize)
    {
        this.housekeepingBatchSize = housekeepingBatchSize;
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
        if (restrictionValue != null )
        {
            if(restrictionValue instanceof Collection){
                if (!((Collection)restrictionValue).isEmpty())
                    return true;
            }else{

                if( !"".equals(restrictionValue))
                    return true;
            }

        }
        return false;
    }
}