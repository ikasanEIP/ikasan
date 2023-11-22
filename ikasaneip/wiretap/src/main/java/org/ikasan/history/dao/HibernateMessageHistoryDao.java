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
package org.ikasan.history.dao;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.ikasan.history.model.ComponentInvocationMetricImpl;
import org.ikasan.history.model.CustomMetric;
import org.ikasan.history.model.FlowInvocationMetricImpl;
import org.ikasan.history.model.MetricEvent;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.history.ComponentInvocationMetric;
import org.ikasan.spec.history.FlowInvocationMetric;
import org.ikasan.spec.search.PagedSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hibernate implementation of the <code>MessageHistoryDao</code>
 *
 * @author Ikasan Development Team
 *
 */
@Transactional
public class HibernateMessageHistoryDao implements MessageHistoryDao
{
    /**
     * logger instance
     */
    private static final Logger logger = LoggerFactory.getLogger(HibernateMessageHistoryDao.class);

    public static final String EVENT_IDS = "eventIds";
    public static final String NOW = "now";

    /** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = true;

    /** Batch size used when in batching housekeep */
    private Integer housekeepingBatchSize = 400;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 2000;

    public static final String UPDATE_HARVESTED_QUERY = "update FlowInvocationMetricImpl w set w.harvestedDateTime = :" + NOW + ", w.harvested = true" +
        " where w.id in(:" + EVENT_IDS + ")";

    private boolean isHarvestQueryOrdered = false;

    @PersistenceContext(unitName = "wiretap")
    private EntityManager entityManager;

    @Override
    public void save(ComponentInvocationMetric messageHistoryEvent) {
        entityManager.persist(entityManager.contains(messageHistoryEvent)
            ? messageHistoryEvent : entityManager.merge(messageHistoryEvent));
    }

    @Override
    public void save(FlowInvocationMetric flowInvocationMetric) {
        entityManager.persist(entityManager.contains(flowInvocationMetric)
            ? flowInvocationMetric : entityManager.merge(flowInvocationMetric));
    }
    
	@Override
	public void save(MetricEvent metricEvent) {
        entityManager.persist(entityManager.contains(metricEvent)
            ? metricEvent : entityManager.merge(metricEvent));
	}

    @Override
    public PagedSearchResult<ComponentInvocationMetric> findMessageHistoryEvents(final int pageNo, final int pageSize, final String orderBy,
                                                                                 final boolean orderAscending, final Set<String> moduleNames,
                                                                                 final String flowName, final String componentName,
                                                                                 final String eventId, final String relatedEventId,
                                                                                 final Date fromDate, final Date toDate) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ComponentInvocationMetric> criteriaQuery = builder.createQuery(ComponentInvocationMetric.class);
        Root<ComponentInvocationMetricImpl> root = criteriaQuery.from(ComponentInvocationMetricImpl.class);
        List<Predicate> predicates = getCriteria(builder, root, pageNo, pageSize, orderBy,
            orderAscending, moduleNames, flowName, componentName, eventId, relatedEventId, fromDate, toDate);

        criteriaQuery.select(root)
            .where(predicates.toArray(new Predicate[predicates.size()]));

        if (orderBy != null) {
            if (orderAscending) {
                criteriaQuery.orderBy(builder.asc(root.get(orderBy)));
            }
            else {
                criteriaQuery.orderBy(builder.desc(root.get(orderBy)));
            }
        }

        TypedQuery<ComponentInvocationMetric> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(pageSize);
        int firstResult = pageNo * pageSize;
        query.setFirstResult(firstResult);
        List<ComponentInvocationMetric> results = query.getResultList();

        Long rowCount = rowCount(pageNo, pageSize, orderBy, orderAscending, moduleNames, flowName
            , componentName, eventId, relatedEventId, fromDate, toDate);

        return new ArrayListPagedSearchResult(results, firstResult, rowCount);
    }

    private Long rowCount(final int pageNo, final int pageSize, final String orderBy,
                          final boolean orderAscending, final Set<String> moduleNames,
                          final String flowName, final String componentName,
                          final String eventId, final String relatedEventId,
                          final Date fromDate, final Date toDate) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> metaDataCriteriaQuery = builder.createQuery(Long.class);
        Root<ComponentInvocationMetricImpl> root = metaDataCriteriaQuery.from(ComponentInvocationMetricImpl.class);
        List<Predicate> predicates = getCriteria(builder, root, pageNo, pageSize, orderBy,
            orderAscending, moduleNames, flowName, componentName, eventId, relatedEventId, fromDate, toDate);

        metaDataCriteriaQuery.select(builder.count(root))
        .where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Long> metaDataQuery = entityManager.createQuery(metaDataCriteriaQuery);

        List<Long> rowCountList = metaDataQuery.getResultList();
        if (!rowCountList.isEmpty()) {
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
    private List<Predicate> getCriteria(CriteriaBuilder builder, Root<ComponentInvocationMetricImpl> root,
                                        final int pageNo, final int pageSize, final String orderBy,
                                        final boolean orderAscending, final Set<String> moduleNames,
                                        final String flowName, final String componentName,
                                        final String eventId, final String relatedEventId,
                                        final Date fromDate, final Date toDate) {

        List<Predicate> predicates = new ArrayList<>();

        if (restrictionExists(componentName)) {
            predicates.add(builder.equal(root.get("componentName"), componentName));
        }
        if (restrictionExists(eventId)) {
            predicates.add(builder.or(
                builder.equal(root.get("beforeEventIdentifier"), eventId),
                builder.equal(root.get("afterEventIdentifier"), eventId)
            ));

        }
        if (restrictionExists(relatedEventId)) {
            predicates.add(builder.or(
                builder.equal(root.get("beforeRelatedEventIdentifier"), relatedEventId),
                builder.equal(root.get("afterRelatedEventIdentifier"), relatedEventId)
            ));

        }
        if (restrictionExists(fromDate)) {
            predicates.add(builder.greaterThan(root.get("startTime"), fromDate.getTime()));
        }
        if (restrictionExists(toDate)) {
            predicates.add(builder.lessThan(root.get("endTime"), toDate.getTime()));
        }

        return predicates;
    }

    @Override
    public PagedSearchResult<ComponentInvocationMetric> getMessageHistoryEvent(final int pageNo, final int pageSize, final String orderBy,
                                                                               final boolean orderAscending, final String eventId,
                                                                               final String relatedEventId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ComponentInvocationMetric> criteriaQuery = builder.createQuery(ComponentInvocationMetric.class);
        Root<ComponentInvocationMetricImpl> root = criteriaQuery.from(ComponentInvocationMetricImpl.class);
        List<Predicate> predicates = getCriteria(builder,root, pageNo, pageSize, orderBy,
            orderAscending, null, null, null, eventId,
            relatedEventId, null, null);

        criteriaQuery.select(root)
            .where(predicates.toArray(new Predicate[predicates.size()]));

        if (orderBy != null)
        {
            if (orderAscending)
            {
                criteriaQuery.orderBy(builder.asc(root.get(orderBy)));
            }
            else
            {
                criteriaQuery.orderBy(builder.desc(root.get(orderBy)));
            }
        }

        TypedQuery<ComponentInvocationMetric> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(pageSize);
        int firstResult = pageNo * pageSize;
        query.setFirstResult(firstResult);
        List<ComponentInvocationMetric> results = query.getResultList();

        Long rowCount = rowCount(pageNo, pageSize, orderBy,
            orderAscending, null, null, null, eventId,
            relatedEventId, null, null);

        return new ArrayListPagedSearchResult(results, firstResult, rowCount);
    }

    @Override
    public void deleteAllExpired() {
        if (batchHousekeepDelete) {
            batchHousekeepDelete();
        } else {
            String deleteMetrics = "DELETE FROM CustomMetric WHERE CompInvocationMetricId in " +
                "(SELECT Id FROM ComponentInvocationMetric WHERE FlowInvocationMetricId in (SELECT Id FROM FlowInvocationMetric WHERE Expiry <= " + System.currentTimeMillis() +
                " AND HarvestedDateTime > 0))";
            entityManager.createNativeQuery(deleteMetrics).executeUpdate();

            String deleteMessageHistory = "DELETE FROM ComponentInvocationMetric WHERE FlowInvocationMetricId in (SELECT Id FROM FlowInvocationMetric WHERE Expiry <= " + System.currentTimeMillis() +
                " AND HarvestedDateTime > 0)";
            entityManager.createNativeQuery(deleteMessageHistory).executeUpdate();

            String deleteFlowInvocation = "DELETE FROM FlowInvocationMetric WHERE Expiry <= " + System.currentTimeMillis() +
                " AND HarvestedDateTime > 0";
            entityManager.createNativeQuery(deleteFlowInvocation).executeUpdate();
        }
    }

    /**
     * Housekeep using batching.
     *
     *  Loops, checking for housekeepable items. If they exist, it identifies a batch
     *  and attempts to delete that batch
     */
    private void batchHousekeepDelete() {
        logger.debug("Metrics batched housekeeper called");

        int numberDeleted = 0;

        while(housekeepablesExist() && numberDeleted < this.transactionBatchSize) {

            numberDeleted += this.housekeepingBatchSize;

            List<FlowInvocationMetric> events = this.getHarvestedRecords(this.housekeepingBatchSize);

            this.deleteHarvestableRecords(events);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean housekeepablesExist() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<FlowInvocationMetricImpl> root = criteriaQuery.from(FlowInvocationMetricImpl.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.lessThan(root.get("expiry"),System.currentTimeMillis()));
        predicates.add(builder.greaterThan(root.get("harvestedDateTime"),0));

        criteriaQuery.select(builder.count(root))
            .where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        List<Long> rowCountList = query.getResultList();
        Long rowCount = Long.valueOf(0);
        if (!rowCountList.isEmpty())
        {
            rowCount = rowCountList.get(0);
        }

        logger.debug(rowCount+", FlowInvocation housekeepables exist");
        return Boolean.valueOf(rowCount > 0);
    }


    @Override
    public boolean harvestableRecordsExist() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<FlowInvocationMetricImpl> root = criteriaQuery.from(FlowInvocationMetricImpl.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("harvestedDateTime"),0));

        criteriaQuery.select(builder.count(root))
            .where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        List<Long> rowCountList = query.getResultList();
        Long rowCount = Long.valueOf(0);
        if (!rowCountList.isEmpty())
        {
            rowCount = rowCountList.get(0);
        }

        logger.debug(rowCount+", FlowInvocation harvestable records exist");
        return rowCount>0;
    }

    @Override
    public List<FlowInvocationMetric> getHarvestableRecords(final int housekeepingBatchSize) {
        return this.getHarvestableRecords(housekeepingBatchSize, false);
    }

    public List<FlowInvocationMetric> getHarvestedRecords(final int housekeepingBatchSize) {
        return this.getHarvestableRecords(housekeepingBatchSize, true);
    }


    public List<FlowInvocationMetric> getHarvestableRecords(final int housekeepingBatchSize, final Boolean harvested) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FlowInvocationMetric> criteriaQuery = builder.createQuery(FlowInvocationMetric.class);
        Root<FlowInvocationMetricImpl> root = criteriaQuery.from(FlowInvocationMetricImpl.class);

        if(harvested) {
            criteriaQuery.select(root)
                .where(builder.greaterThan(root.get("harvestedDateTime"), 0));

            if(isHarvestQueryOrdered) {
                criteriaQuery.orderBy(builder.asc(root.get("invocationStartTime")));
            }
        }
        else {
            criteriaQuery.select(root)
                .where(builder.equal(root.get("harvestedDateTime"), 0));

            if(isHarvestQueryOrdered) {
                criteriaQuery.orderBy(builder.asc(root.get("invocationStartTime")));
            }
        }

        TypedQuery<FlowInvocationMetric> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(housekeepingBatchSize);
        List<FlowInvocationMetric> flowInvocationMetrics = query.getResultList();

        ArrayList<String> eventIds = new ArrayList<String>();

        Set<ComponentInvocationMetric> messageHistoryEvents = new HashSet<ComponentInvocationMetric>();
        Map<String, MetricEvent> eventsMap = new HashMap<String, MetricEvent>();

        for(FlowInvocationMetric<ComponentInvocationMetric> flowInvocationMetric : flowInvocationMetrics) {
            messageHistoryEvents.addAll(flowInvocationMetric.getFlowInvocationEvents());
        }

        List<List<ComponentInvocationMetric>> smallerLists = Lists.partition(new ArrayList<ComponentInvocationMetric>(messageHistoryEvents), 200);

        for(List<ComponentInvocationMetric> list: smallerLists) {
            for (ComponentInvocationMetric event: list) {
                eventIds.add((String)event.getBeforeEventIdentifier());
            }

            eventsMap.putAll(getWiretapFlowEvents(eventIds));

            eventIds = new ArrayList<>();
        }

        for(FlowInvocationMetric<ComponentInvocationMetric> flowInvocationMetric : flowInvocationMetrics) {
            for (ComponentInvocationMetric<String, CustomMetric, MetricEvent> messageHistoryEvent : flowInvocationMetric.getFlowInvocationEvents()) {
                MetricEvent event = eventsMap.get(messageHistoryEvent.getBeforeEventIdentifier()
                        + flowInvocationMetric.getModuleName() + flowInvocationMetric.getFlowName() + messageHistoryEvent.getComponentName());

                if (event != null) {
                    if (event.getComponentName().equals(messageHistoryEvent.getComponentName())
                            && event.getFlowName().equals(flowInvocationMetric.getFlowName())
                            && event.getModuleName().equals(flowInvocationMetric.getModuleName())) {
                        messageHistoryEvent.setWiretapFlowEvent(event);
                    }
                }
            }
        }

        return flowInvocationMetrics;
    }

    protected Map<String, MetricEvent> getWiretapFlowEvents(final List<String> eventIds) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MetricEvent> criteriaQuery = builder.createQuery(MetricEvent.class);
        Root<MetricEvent> root = criteriaQuery.from(MetricEvent.class);

        criteriaQuery.select(root)
            .where(root.get("eventId").in(eventIds));

        TypedQuery<MetricEvent> query = entityManager.createQuery(criteriaQuery);

        Map<String, MetricEvent> results = query.getResultStream()
            .collect(Collectors.toMap(
            event -> event.getEventId() + event.getModuleName() + event.getFlowName() + event.getComponentName(),
            event -> event, (eventold,eventnew) -> eventnew)
            );
        return results;
    }

    @Override
    public void deleteHarvestableRecords(List<FlowInvocationMetric> flowInvocationMetrics) {
        for(FlowInvocationMetric flowInvocationMetric : flowInvocationMetrics) {
            Set<ComponentInvocationMetric> events = flowInvocationMetric.getFlowInvocationEvents();

            for (ComponentInvocationMetric event : events) {
                if (event.getWiretapFlowEvent() != null) {
                    entityManager.remove(entityManager.contains(event.getWiretapFlowEvent())
                        ? event.getWiretapFlowEvent() : entityManager.merge(event.getWiretapFlowEvent()));
                }

                for (CustomMetric metric : (Set<CustomMetric>) event.getMetrics()) {
                    entityManager.remove(entityManager.contains(metric)
                        ? metric : entityManager.merge(metric));
                }

                entityManager.remove(entityManager.contains(event)
                    ? event : entityManager.merge(event));
            }

            entityManager.remove(entityManager.contains(flowInvocationMetric)
                ? flowInvocationMetric : entityManager.merge(flowInvocationMetric));
        }
    }

    @Override
    public void updateAsHarvested(List<FlowInvocationMetric> events) {
            List<Long> flowInvocationMetricIds = new ArrayList<Long>();

            for(FlowInvocationMetric event: events) {
                flowInvocationMetricIds.add(((FlowInvocationMetricImpl)event).getId());
            }

            List<List<Long>> partitionedIds = Lists.partition(flowInvocationMetricIds, 300);

            for(List<Long> eventIds: partitionedIds) {
                Query query = entityManager.createQuery(UPDATE_HARVESTED_QUERY);
                query.setParameter(NOW, System.currentTimeMillis());
                query.setParameter(EVENT_IDS, eventIds);
                query.executeUpdate();
            }
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
        return restrictionValue != null && !"".equals(restrictionValue);
    }

	/**
	 * @return the batchHousekeepDelete
	 */
	public boolean isBatchHousekeepDelete() {
		return batchHousekeepDelete;
	}

	/**
	 * @param batchHousekeepDelete the batchHousekeepDelete to set
	 */
	public void setBatchHousekeepDelete(boolean batchHousekeepDelete) {
		this.batchHousekeepDelete = batchHousekeepDelete;
	}

	/**
	 * @return the housekeepingBatchSize
	 */
	public Integer getHousekeepingBatchSize() {
		return housekeepingBatchSize;
	}

	/**
	 * @param housekeepingBatchSize the housekeepingBatchSize to set
	 */
	public void setHousekeepingBatchSize(Integer housekeepingBatchSize) {
		this.housekeepingBatchSize = housekeepingBatchSize;
	}

	/**
	 * @return the housekeepingBatchSize
	 */
	public Integer getTransactionBatchSize() {
		return transactionBatchSize;
	}

	/**
	 * @param transactionBatchSize the housekeepingBatchSize to set
	 */
	public void setTransactionBatchSize(Integer transactionBatchSize) {
		this.transactionBatchSize = transactionBatchSize;
	}

    @Override
    public void setHarvestQueryOrdered(boolean isHarvestQueryOrdered) {
        this.isHarvestQueryOrdered = isHarvestQueryOrdered;
    }
}
