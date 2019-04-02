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
package org.ikasan.systemevent.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.systemevent.model.SystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of <code>SystemFlowEventDao</code>
 * 
 * Note that can be configured to housekeep either simply, or in batches.
 * 
 * @author Ikasan Development Team
 * 
 */
public class HibernateSystemEventDao extends HibernateDaoSupport implements SystemEventDao
{
    /** logger instance */
    private static final Logger logger = LoggerFactory.getLogger(HibernateSystemEventDao.class);

    public static final String EXPIRY = "expiry";
    public static final String EVENT_IDS = "eventIds";
    public static final String NOW = "now";

    /** Query used for housekeeping expired system events */
    private static final String HOUSEKEEP_QUERY = "delete SystemEvent w where w.expiry <= :" + EXPIRY;

    public static final String SYSTEM_EVENTS_TO_DELETE_QUERY = "select id from SystemEvent se " +
            " where se.expiry < :" + NOW;

    public static final String SYSTEM_EVENTS_DELETE_QUERY = "delete SystemEvent se " +
            " where se.id in(:" + EVENT_IDS + ")";

    /** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = false;

    /** Batch size used when in batching housekeep */
    private Integer housekeepingBatchSize = 100;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 1000;

    private String housekeepQuery;

    /**
     * Constructor
     *
     * @param batchHousekeepDelete - pass true if you want to use batch deleting
     * @param housekeepingBatchSize - batch size, only respected if set to use
     *            batching
     */
    public HibernateSystemEventDao(boolean batchHousekeepDelete, Integer housekeepingBatchSize,
            Integer transactionBatchSize)
    {
        this();
        this.batchHousekeepDelete = batchHousekeepDelete;
        this.housekeepingBatchSize = housekeepingBatchSize;
        this.transactionBatchSize = transactionBatchSize;
    }

    /**
     * Constructor
     */
    public HibernateSystemEventDao()
    {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.ikasan.framework.systemevent.dao.SystemFlowEventDao#save(org.ikasan
     * .framework.systemevent.window.SystemFlowEvent)
     */
    public void save(SystemEvent systemEvent)
    {
        getHibernateTemplate().save(systemEvent);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.systemevent.dao.SystemFlowEventDao#find(int,
     * int, java.lang.String, boolean, java.lang.String, java.lang.String,
     * java.util.Date, java.util.Date, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public PagedSearchResult<SystemEvent> find(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending, final String subject, final String action,
            final Date timestampFrom, final Date timestampTo, final String actor)
    {

        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<SystemEvent> criteriaQuery = builder.createQuery(SystemEvent.class);
                Root<SystemEvent> root = criteriaQuery.from(SystemEvent.class);
                List<Predicate> predicates = getCriteria(builder,root);

                criteriaQuery.select(root)
                    .where(predicates.toArray(new Predicate[predicates.size()]))
                    .orderBy(builder.asc(root.get("id")));

                Query<SystemEvent> query = session.createQuery(criteriaQuery);
                query.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                query.setFirstResult(firstResult);
                List<SystemEvent> results = query.getResultList();

                Long rowCount = rowCount(session);

                return new ArrayListPagedSearchResult(results, firstResult, rowCount);
            }

            private Long rowCount(Session session){

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Long> metaDataCriteriaQuery = builder.createQuery(Long.class);
                Root<SystemEvent> root = metaDataCriteriaQuery.from(SystemEvent.class);
                List<Predicate> predicates = getCriteria(builder,root);

                metaDataCriteriaQuery.select(builder.count(root))
                    .where(predicates.toArray(new Predicate[predicates.size()]));

                org.hibernate.query.Query<Long> metaDataQuery = session.createQuery(metaDataCriteriaQuery);

                List<Long> rowCountList = metaDataQuery.getResultList();
                if (!rowCountList.isEmpty())
                {
                    return rowCountList.get(0);
                }
                return new Long(0);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param builder
             * @param root
             * @return
             */
            private List<Predicate>  getCriteria(CriteriaBuilder builder,Root<SystemEvent> root)
            {

                List<Predicate> predicates = new ArrayList<>();

                if (restrictionExists(subject))
                {
                    predicates.add(builder.equal(root.get("subject"),subject));
                }
                if (restrictionExists(action))
                {
                    predicates.add(builder.equal(root.get("action"),action));
                }
                if (restrictionExists(actor))
                {
                    predicates.add(builder.equal(root.get("actor"),actor));
                }
                if (restrictionExists(timestampFrom))
                {
                    predicates.add( builder.greaterThan(root.get("timestamp"),timestampFrom));
                }
                if (restrictionExists(timestampTo))
                {
                    predicates.add( builder.lessThan(root.get("timestamp"),timestampTo));
                }

                return predicates;
            }
        });

    }

    /* (non-Javadoc)
	 * @see org.ikasan.systemevent.dao.SystemEventDao#listSystemEvents(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
    @Override
    public List<SystemEvent> listSystemEvents(final List<String> subjects, final String actor,
            final Date timestampFrom, final Date timestampTo)
    {
        return (List<SystemEvent>) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
               CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<SystemEvent> criteriaQuery = builder.createQuery(SystemEvent.class);
                Root<SystemEvent> root = criteriaQuery.from(SystemEvent.class);
                List<Predicate> predicates = getCriteria(builder,root);

                criteriaQuery.select(root)
                    .where(predicates.toArray(new Predicate[predicates.size()]))
                    .orderBy(builder.asc(root.get("id")));

                Query<SystemEvent> query = session.createQuery(criteriaQuery);
                List<SystemEvent> results = query.getResultList();
                return results;

            }


            /**
             * Create a consistent criteria instance for both result and metadata
             * @param builder
             * @param root
             * @return
             */
            private List<Predicate>  getCriteria(CriteriaBuilder builder,Root<SystemEvent> root)
            {

                List<Predicate> predicates = new ArrayList<>();

                if (restrictionExists(subjects))
                {
                    predicates.add(root.get("subject").in(subjects));
                }
                if (restrictionExists(actor))
                {
                    predicates.add(builder.equal(root.get("actor"),actor));
                }
                if (restrictionExists(timestampFrom))
                {
                    predicates.add( builder.greaterThan(root.get("timestamp"),timestampFrom));
                }
                if (restrictionExists(timestampTo))
                {
                    predicates.add( builder.lessThan(root.get("timestamp"),timestampTo));
                }

                return predicates;
            }
        });
    }

    /**
     * Check to see if the restriction exists
     *
     * @param restrictionValue - The value to check
     * @return - true if the restriction exists for that value, else false
     */
    static boolean restrictionExists(Object restrictionValue)
    {
        // If the value passed in is not null and not an empty string then it
        // can have a restriction applied
        if (restrictionValue != null && !"".equals(restrictionValue))
        {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.ikasan.framework.systemevent.dao.SystemFlowEventDao#deleteExpired()
     */
    public void deleteExpired()
    {
        if (!batchHousekeepDelete)
        {
            getHibernateTemplate().execute(new HibernateCallback<Object>()
            {
                public Object doInHibernate(Session session) throws HibernateException
                {

                    Query query = session.createQuery(HOUSEKEEP_QUERY);
                    query.setParameter(EXPIRY, new Date());
                    query.executeUpdate();
                    return null;
                }
            });
        }
        else
        {
            batchHousekeepDelete();
        }

    }

    /**
     * Housekeep using batching.
     *
     * Loops, checking for housekeepable items. If they exist, it identifies a
     * batch and attempts to delete that batch
     */
    private void batchHousekeepDelete()
    {
        logger.info("SystemEvent called batchHousekeepDelete");

        int numberDeleted = 0;

        while(housekeepablesExist() && numberDeleted < this.transactionBatchSize)
        {

            numberDeleted += this.housekeepingBatchSize;

            getHibernateTemplate().execute(new HibernateCallback<Object>()
            {
                public Object doInHibernate(Session session) throws HibernateException
                {

                    Query query = session.createQuery(SYSTEM_EVENTS_TO_DELETE_QUERY);
                    query.setParameter(NOW, new Date());
                    query.setMaxResults(housekeepingBatchSize);

                    List<Long> wiretapEventIds = (List<Long>)query.list();

                    if(wiretapEventIds.size() > 0)
                    {
                        query = session.createQuery(SYSTEM_EVENTS_DELETE_QUERY);
                        query.setParameterList(EVENT_IDS, wiretapEventIds);
                        query.executeUpdate();
                    }

                    return null;
                }
            });
        }
    }

    /**
     * Checks if there are housekeepable items in existance, ie expired
     * SystemFlowEvents
     *
     * @return true if there is at least 1 expired SystemFlowEvent
     */
    public boolean housekeepablesExist()
    {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
                Root<SystemEvent> root = criteriaQuery.from(SystemEvent.class);

                criteriaQuery.select(builder.count(root))
                    .where(builder.lessThan(root.get("expiry"),new Date()));


                Query<Long> query = session.createQuery(criteriaQuery);
                List<Long> rowCountList = query.getResultList();
                Long rowCount = new Long(0);
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                logger.info(rowCount+", SystemEvent housekeepables exist");
                return new Boolean(rowCount>0);

            }
        });

    }

    @Override
    public boolean isBatchHousekeepDelete()
    {
        return batchHousekeepDelete;
    }

    @Override
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }

    @Override
    public Integer getHousekeepingBatchSize()
    {
        return housekeepingBatchSize;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        this.housekeepingBatchSize = housekeepingBatchSize;
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        this.transactionBatchSize = transactionBatchSize;
    }

    @Override public void setHousekeepQuery(String housekeepQuery)
    {
        this.housekeepQuery = housekeepQuery;
    }

}