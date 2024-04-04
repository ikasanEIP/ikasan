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

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hibernate implementation of <code>SystemFlowEventDao</code>
 * <p>
 * Note that can be configured to house keep either simply, or in batches.
 *
 * @author Ikasan Development Team
 */
public class HibernateSystemEventDao implements SystemEventDao<SystemEvent>
{
    /**
     * logger instance
     */
    private static final Logger logger = LoggerFactory.getLogger(HibernateSystemEventDao.class);

    public static final String EXPIRY = "expiry";

    public static final String EVENT_IDS = "eventIds";

    public static final String NOW = "now";

    /**
     * Query used for housekeeping expired system events
     */
    private static final String HOUSEKEEP_QUERY = "delete SystemEventImpl w where w.expiry <= :" + EXPIRY;

    private static final String HARVESTED_HOUSEKEEP_QUERY = "delete SystemEventImpl w where w.harvested = true";

    public static final String SYSTEM_EVENTS_TO_DELETE_QUERY =
        "select id from SystemEventImpl se where se.expiry < :" + NOW;

    public static final String HARVESTED_SYSTEM_EVENTS_TO_DELETE_QUERY =
        "select id from SystemEventImpl se where se.harvested = true order by se.timestamp";

    public static final String SYSTEM_EVENTS_DELETE_QUERY =
        "delete SystemEventImpl se " + " where se.id in(:" + EVENT_IDS + ")";

    public static final String UPDATE_HARVESTED_QUERY =
        "update SystemEventImpl se set se.harvestedDateTime = :" + NOW + ", se.harvested = true" + " where se.id in (:"
            + EVENT_IDS + ")";

    @PersistenceContext(unitName = "system-event")
    private EntityManager entityManager;

    private Boolean orderHarvestQuery = false;

    /**
     * Use batch housekeeping mode?
     */
    private boolean batchHousekeepDelete = false;

    /**
     * Batch size used when in batching housekeep
     */
    private Integer housekeepingBatchSize = 100;

    /**
     * Batch size used when in a single transaction
     */
    private Integer transactionBatchSize = 1000;

    private boolean deleteOnceHarvested;

    /**
     * Constructor
     *
     * @param batchHousekeepDelete  - pass true if you want to use batch deleting
     * @param housekeepingBatchSize - batch size, only respected if set to use batching
     * @param transactionBatchSize - the over all size of the batch to delete
     * @param deleteOnceHarvested - flag to indicate if we should delete entities once harvested or wait until expiry
     */
    public HibernateSystemEventDao(boolean batchHousekeepDelete, Integer housekeepingBatchSize,
                                   Integer transactionBatchSize, boolean deleteOnceHarvested) {
        this();
        this.batchHousekeepDelete = batchHousekeepDelete;
        this.housekeepingBatchSize = housekeepingBatchSize;
        this.transactionBatchSize = transactionBatchSize;
        this.deleteOnceHarvested = deleteOnceHarvested;
    }

    /**
     * Constructor
     */
    public HibernateSystemEventDao() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.ikasan.framework.systemevent.dao.SystemFlowEventDao#save(org.ikasan
     * .framework.systemevent.window.SystemFlowEvent)
     */
    public void save(SystemEvent systemEvent) {
        entityManager.persist(systemEvent);
    }

    public ArrayListPagedSearchResult find(final int pageNo, final int pageSize, final String orderBy,
                                final boolean orderAscending, final String subject, final String action,
                                final Date timestampFrom, final Date timestampTo, final String actor) throws HibernateException {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SystemEvent> criteriaQuery = builder.createQuery(SystemEvent.class);
        Root<SystemEventImpl> root = criteriaQuery.from(SystemEventImpl.class);
        List<Predicate> predicates = getCriteria(builder, root, pageNo, pageSize, orderBy,
            orderAscending, subject, action, timestampFrom, timestampTo, actor);

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]))
            .orderBy(builder.asc(root.get("id")));

        TypedQuery<SystemEvent> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(pageSize);
        int firstResult = pageNo * pageSize;
        query.setFirstResult(firstResult);
        List<SystemEvent> results = query.getResultList();

        Long rowCount = rowCount(pageNo, pageSize, orderBy,
            orderAscending, subject, action, timestampFrom, timestampTo, actor);

        return new ArrayListPagedSearchResult(results, firstResult, rowCount);
    }

    private Long rowCount(final int pageNo, final int pageSize, final String orderBy,
                          final boolean orderAscending, final String subject, final String action,
                          final Date timestampFrom, final Date timestampTo, final String actor) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> metaDataCriteriaQuery = builder.createQuery(Long.class);
        Root<SystemEventImpl> root = metaDataCriteriaQuery.from(SystemEventImpl.class);
        List<Predicate> predicates = getCriteria(builder, root, pageNo, pageSize, orderBy,
            orderAscending, subject, action, timestampFrom, timestampTo, actor);

        metaDataCriteriaQuery.select(builder.count(root))
            .where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Long> metaDataQuery = entityManager.createQuery(metaDataCriteriaQuery);

        List<Long> rowCountList = metaDataQuery.getResultList();
        if ( !rowCountList.isEmpty() )
        {
            return rowCountList.get(0);
        }
        return Long.valueOf(0);
    }

    /**
     * Create a criteria instance for each invocation of data or metadata queries.
     * @param builder
     * @param root
     * @return
     */
    private List<Predicate> getCriteria(CriteriaBuilder builder, Root<SystemEventImpl> root, final int pageNo, final int pageSize, final String orderBy,
                                        final boolean orderAscending, final String subject, final String action,
                                        final Date timestampFrom, final Date timestampTo, final String actor) {
        List<Predicate> predicates = new ArrayList<>();

        if ( restrictionExists(subject) )
        {
            predicates.add(builder.equal(root.get("subject"), subject));
        }
        if ( restrictionExists(action) )
        {
            predicates.add(builder.equal(root.get("action"), action));
        }
        if ( restrictionExists(actor) )
        {
            predicates.add(builder.equal(root.get("actor"), actor));
        }
        if ( restrictionExists(timestampFrom) )
        {
            predicates.add(builder.greaterThan(root.get("timestamp"), timestampFrom));
        }
        if ( restrictionExists(timestampTo) )
        {
            predicates.add(builder.lessThan(root.get("timestamp"), timestampTo));
        }

        return predicates;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.systemevent.SystemEventDao#list(java.lang.String, java.lang.String, java.util.Date, java
     * .util.Date)
     */
    @Override
    public List<SystemEvent> list(final List<String> subjects, final String actor, final Date timestampFrom,
                                  final Date timestampTo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SystemEvent> criteriaQuery = builder.createQuery(SystemEvent.class);
        Root<SystemEventImpl> root = criteriaQuery.from(SystemEventImpl.class);
        List<Predicate> predicates = getCriteria(builder, root, subjects, actor, timestampFrom, timestampTo);

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]))
                     .orderBy(builder.asc(root.get("id")));

        TypedQuery<SystemEvent> query = entityManager.createQuery(criteriaQuery);
        List<SystemEvent> results = query.getResultList();
        return results;
    }

    /**
     * Create a consistent criteria instance for both result and metadata
     * @param builder
     * @param root
     * @return
     */
    private List<Predicate> getCriteria(CriteriaBuilder builder, Root<SystemEventImpl> root,
                                        final List<String> subjects, final String actor, final Date timestampFrom,
                                        final Date timestampTo) {
        List<Predicate> predicates = new ArrayList<>();

        if ( restrictionExists(subjects) )
        {
            predicates.add(root.get("subject").in(subjects));
        }
        if ( restrictionExists(actor) )
        {
            predicates.add(builder.equal(root.get("actor"), actor));
        }
        if ( restrictionExists(timestampFrom) )
        {
            predicates.add(builder.greaterThan(root.get("timestamp"), timestampFrom));
        }
        if ( restrictionExists(timestampTo) )
        {
            predicates.add(builder.lessThan(root.get("timestamp"), timestampTo));
        }

        return predicates;
    }

    /**
     * Check to see if the restriction exists
     *
     * @param restrictionValue - The value to check
     * @return - true if the restriction exists for that value, else false
     */
    static boolean restrictionExists(Object restrictionValue) {
        // If the value passed in is not null and not an empty string then it
        // can have a restriction applied
        if ( restrictionValue != null && !"".equals(restrictionValue) ) {
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
    public void deleteExpired() {
        if ( !batchHousekeepDelete ) {
            Query query = entityManager.createQuery(this.deleteOnceHarvested ? HARVESTED_HOUSEKEEP_QUERY : HOUSEKEEP_QUERY);
            if(!this.deleteOnceHarvested)query.setParameter(EXPIRY, new Date());
            query.executeUpdate();
        }
        else {
            batchHousekeepDelete();
        }
    }

    /**
     * Housekeep using batching.
     * <p>
     * Loops, checking for housekeepable items. If they exist, it identifies a
     * batch and attempts to delete that batch
     */
    private void batchHousekeepDelete() {
        logger.debug("SystemEvent called batchHousekeepDelete");

        int numberDeleted = 0;

        while (housekeepablesExist() && numberDeleted < this.transactionBatchSize) {
            numberDeleted += this.housekeepingBatchSize;

            Query query = entityManager.createQuery(this.deleteOnceHarvested ? HARVESTED_SYSTEM_EVENTS_TO_DELETE_QUERY
                : SYSTEM_EVENTS_TO_DELETE_QUERY);
            if(!this.deleteOnceHarvested)query.setParameter(NOW, new Date());
            query.setMaxResults(housekeepingBatchSize);

            List<Long> wiretapEventIds = (List<Long>) query.getResultList();

            if ( wiretapEventIds.size() > 0 )
            {
                query = entityManager.createQuery(SYSTEM_EVENTS_DELETE_QUERY);
                query.setParameter(EVENT_IDS, wiretapEventIds);
                query.executeUpdate();
            }
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
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<SystemEventImpl> root = criteriaQuery.from(SystemEventImpl.class);

        if(this.deleteOnceHarvested) {
            criteriaQuery.select(builder.count(root)).where(builder.equal(root.get("harvested"), true));
        }
        else {
            criteriaQuery.select(builder.count(root)).where(builder.lessThan(root.get("expiry"), new Date()));
        }

        Query query = entityManager.createQuery(criteriaQuery);
        List<Long> rowCountList = query.getResultList();
        Long rowCount = Long.valueOf(0);
        if ( !rowCountList.isEmpty() ) {
            rowCount = rowCountList.get(0);
        }

        logger.debug(rowCount + ", SystemEvent housekeepables exist");
        return Boolean.valueOf(rowCount > 0);
    }

    @Override
    public boolean isBatchHousekeepDelete() {
        return batchHousekeepDelete;
    }

    @Override
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete) {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }

    @Override
    public Integer getHousekeepingBatchSize() {
        return housekeepingBatchSize;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize) {
        this.housekeepingBatchSize = housekeepingBatchSize;
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize) {
        this.transactionBatchSize = transactionBatchSize;
    }

    @Override
    public List<SystemEvent> getHarvestableRecords(final int harvestingBatchSize) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SystemEvent> criteriaQuery = builder.createQuery(SystemEvent.class);
        Root<SystemEventImpl> root = criteriaQuery.from(SystemEventImpl.class);

        criteriaQuery.select(root).where(builder.lessThanOrEqualTo(root.get("harvestedDateTime"), 0));

        if(this.orderHarvestQuery) {
            criteriaQuery.orderBy(builder.asc(root.get("timestamp")));
        }

        Query query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(harvestingBatchSize);
        return query.getResultList();
    }

    @Override
    public void updateAsHarvested(List<SystemEvent> events) {
        List<Long> extractedEventIds = events.stream().map(s -> s.getId()).collect(Collectors.toList());

        List<List<Long>> partitionedIds = Lists.partition(extractedEventIds, 300);

        for (List<Long> eventIds : partitionedIds) {
            Query query = entityManager.createQuery(UPDATE_HARVESTED_QUERY);
            query.setParameter(NOW, System.currentTimeMillis());
            query.setParameter(EVENT_IDS, eventIds);
            query.executeUpdate();
        }
    }

    @Override
    public Boolean getOrderHarvestQuery() {
        return this.orderHarvestQuery;
    }

    @Override
    public void setOrderHarvestQuery(Boolean orderHarvestQuery) {
        this.orderHarvestQuery = orderHarvestQuery;
    }
}