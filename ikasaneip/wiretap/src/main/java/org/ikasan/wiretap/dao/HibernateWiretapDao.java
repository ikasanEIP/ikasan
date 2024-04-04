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
package org.ikasan.wiretap.dao;

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
import org.hibernate.Session;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapDao;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * The HibernateWiretapDao class is an implementation of the WiretapDao interface using Hibernate as the underlying persistence framework.
 *
 * This class provides methods for saving wiretap events, finding wiretap events based on various criteria, deleting expired wiretaps,
 * performing housekeeping operations, and managing configuration properties such as batch housekeeping and transaction batch size.
 *
 * @author Your Name
 */
public class HibernateWiretapDao implements WiretapDao<Long>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HibernateWiretapDao.class);

    @PersistenceContext(unitName = "wiretap")
    private EntityManager entityManager;

    private static final String EXPIRY = "expiry";
    private static final String EVENT_ID = "eventId";
    public static final String EVENT_IDS = "eventIds";
    public static final String CURRENT_DATE_TIME = "currentDateTime";
    public static final String NOW = "now";

    /** Query used for housekeeping expired persistence events */
    private static final String HOUSEKEEP_DELETE_QUERY = "delete WiretapFlowEvent w where w.expiry <= :" + EXPIRY;
    private static final String HARVESTED_HOUSEKEEP_DELETE_QUERY = "delete WiretapFlowEvent w where w.harvested=true";

    /** Query for finding all persistence events with the same payloadId */
    private static final String WIRETAP_IDS_FOR_GROUPED_EVENT_ID = "select w.id from WiretapFlowEvent w where w.eventId = :" + EVENT_ID;


    public static final String WIRETAP_EVENTS_TO_DELETE_QUERY = "select id from WiretapFlowEvent w " +
            " where w.expiry < :" + NOW;

    public static final String HARVESTED_WIRETAP_EVENTS_TO_DELETE_QUERY = "select id from WiretapFlowEvent w " +
        " where w.harvested= true ORDER BY w.timestamp asc";

    public static final String WIRETAP_EVENTS_DELETE_QUERY = "delete WiretapFlowEvent w " +
            " where w.id in(:" + EVENT_IDS + ")";

    public static final String UPDATE_HARVESTED_QUERY = "update WiretapFlowEvent w set w.harvestedDateTime = :" + CURRENT_DATE_TIME + ", w.harvested = true" +
        " where w.id in(:" + EVENT_IDS + ")";


    /** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = false;

    /** Batch size used when in batching housekeep */
    private Integer housekeepingBatchSize = 200;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 2000;

    private String housekeepQuery;

    private boolean isHarvestQueryOrdered = false;
    private boolean deleteOnceHarvested;

    /**
     * Constructor
     */
    public HibernateWiretapDao() {
        super();
    }

    /**
     * Constructor
     *
     * @param batchHousekeepDelete - pass true if you want to use batch deleting
     * @param housekeepingBatchSize - batch size, only respected if set to use batching
     */
    public HibernateWiretapDao(boolean batchHousekeepDelete,
            Integer housekeepingBatchSize, boolean deleteOnceHarvested) {
        this();
        this.batchHousekeepDelete = batchHousekeepDelete;
        this.housekeepingBatchSize = housekeepingBatchSize;
        this.deleteOnceHarvested = deleteOnceHarvested;
    }


    /**
     * Saves a WiretapEvent in the database.
     *
     * @param wiretapEvent the WiretapEvent object to be saved
     */
    public void save(WiretapEvent wiretapEvent)
    {
        this.entityManager.persist(wiretapEvent);
    }

    /**
     * Saves a list of wiretap events.
     *
     * @param wiretapEvents the list of wiretap events to be saved
     */
    @Override
    public void save(List<WiretapEvent> wiretapEvents) {
        wiretapEvents.forEach(wiretapEvent -> this.save(wiretapEvent));
    }


    /**
     * Finds a WiretapEvent object by its identifier.
     *
     * @param identifier the unique identifier of the WiretapEvent
     *
     * @return the WiretapEvent object with the specified identifier,
     *         or null if no WiretapEvent is found
     */
    @Override
    public WiretapEvent findById(final Long identifier) {
        WiretapFlowEvent wiretapEvent = this.entityManager.find(WiretapFlowEvent.class, identifier);

        Query query = this.entityManager.createQuery(WIRETAP_IDS_FOR_GROUPED_EVENT_ID);
        query.setParameter(EVENT_ID, wiretapEvent.getEventId());


        List<Long> relatedIds = (List<Long>)query.getResultList();

        Collections.sort(relatedIds);
        int thisWiretapsIndex = relatedIds.indexOf(wiretapEvent.getIdentifier());
        Long nextEvent = null;
        Long previousEvent = null;
        if (thisWiretapsIndex > 0)
        {
            previousEvent = relatedIds.get(thisWiretapsIndex - 1);
        }
        if (thisWiretapsIndex < relatedIds.size() - 1)
        {
            nextEvent = relatedIds.get(thisWiretapsIndex + 1);
        }
        wiretapEvent.setNextByEventId(nextEvent);
        wiretapEvent.setPreviousByEventId(previousEvent);
        return wiretapEvent;
    }

    /**
     * Perform a paged search for <code>WiretapFlowEvent</code>s
     *
     * @param pageNo - The page number to retrieve
     * @param pageSize - The size of the page
     * @param orderBy - order by field
     * @param orderAscending - ascending flag
     * @param moduleNames - The list of module names
     * @param moduleFlow - The name of Flow internal to the Module
     * @param componentName - The component name
     * @param eventId - The event id
     * @param payloadId - The payload id
     * @param fromDate - The from date
     * @param untilDate - The to date
     * @param payloadContent - The payload content
     *
     * @return PagedSearchResult
     */
    @Override
    public PagedSearchResult<WiretapEvent> findWiretapEvents(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
            final Set<String> moduleNames, final String moduleFlow, final String componentName, final String eventId, final String payloadId, final Date fromDate, final Date untilDate,
            final String payloadContent) {

        Set<String> flowNames = null;
        Set<String> componentNames = null;
        if(restrictionExists(moduleFlow)){
            flowNames = new HashSet<String>(Arrays.asList(moduleFlow));
        }
        if (restrictionExists(componentName)) {

            componentNames = new HashSet<String>(Arrays.asList(componentName));
        }
        return findWiretapEvents(pageNo,pageSize,orderBy,orderAscending,moduleNames,
            flowNames,componentNames,eventId,payloadId,fromDate,untilDate, payloadContent );

    }

    /**
     * Finds wiretap events based on the specified search criteria.
     *
     * @param pageNo         The page number of the results.
     * @param pageSize       The number of results per page.
     * @param orderBy        The attribute to order the results by.
     * @param orderAscending Specifies whether the results should be ordered in ascending order.
     * @param moduleNames    Set of module names to filter the results.
     * @param moduleFlows    Set of module flows to filter the results.
     * @param componentNames Set of component names to filter the results.
     * @param eventId        The event ID to filter the results.
     * @param payloadId      The payload ID to filter the results.
     * @param fromDate       The start date to filter the results.
     * @param untilDate      The end date to filter the results.
     * @param payloadContent The content of the payload to filter the results.
     *
     * @return A PagedSearchResult containing the wiretap events that match the specified criteria.
     */
    @Override
    public PagedSearchResult<WiretapEvent> findWiretapEvents(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
            final Set<String> moduleNames, final Set<String> moduleFlows, final Set<String> componentNames, final String eventId, final String payloadId,
            final Date fromDate, final Date untilDate, final String payloadContent) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<WiretapEvent> criteriaQuery = builder.createQuery(WiretapEvent.class);
        Root<WiretapFlowEvent> root = criteriaQuery.from(WiretapFlowEvent.class);
        List<Predicate> predicates = getCriteria(builder, root, pageNo, pageSize, orderBy, orderAscending,
            moduleNames, moduleFlows, componentNames, eventId, payloadId,
            fromDate, untilDate, payloadContent);

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
        } else {
            criteriaQuery.orderBy(builder.desc(root.get("timestamp")));
        }


        TypedQuery<WiretapEvent> query = this.entityManager.createQuery(criteriaQuery);
        query.setMaxResults(pageSize);
        int firstResult = pageNo * pageSize;
        query.setFirstResult(firstResult);
        List<WiretapEvent> results = query.getResultList();

        Long rowCount = rowCount(pageNo, pageSize, orderBy, orderAscending,
            moduleNames, moduleFlows, componentNames, eventId, payloadId,
            fromDate, untilDate, payloadContent);

        return new ArrayListPagedSearchResult(results, firstResult, rowCount);
    }

    /**
     * Calculates the total number of rows for a paged search based on the specified criteria.
     *
     * @param pageNo           The page number to retrieve.
     * @param pageSize         The size of the page.
     * @param orderBy          The field to order the results by.
     * @param orderAscending   The flag indicating whether to sort in ascending order.
     * @param moduleNames      The set of module names.
     * @param moduleFlows      The set of module flows.
     * @param componentNames   The set of component names.
     * @param eventId          The event ID.
     * @param payloadId        The payload ID.
     * @param fromDate         The starting date.
     * @param untilDate        The ending date.
     * @param payloadContent   The payload content.
     *
     * @return The total number of rows.
     */
    private Long rowCount(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
                          final Set<String> moduleNames, final Set<String> moduleFlows, final Set<String> componentNames, final String eventId, final String payloadId,
                          final Date fromDate, final Date untilDate, final String payloadContent){

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> metaDataCriteriaQuery = builder.createQuery(Long.class);
        Root<WiretapFlowEvent> root = metaDataCriteriaQuery.from(WiretapFlowEvent.class);
        List<Predicate> predicates = getCriteria(builder,root, pageNo, pageSize, orderBy, orderAscending,
            moduleNames, moduleFlows, componentNames, eventId, payloadId,
            fromDate, untilDate, payloadContent);

        metaDataCriteriaQuery.select(builder.count(root))
            .where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Long> metaDataQuery = this.entityManager.createQuery(metaDataCriteriaQuery);

        List<Long> rowCountList = metaDataQuery.getResultList();
        if (!rowCountList.isEmpty())
        {
            return rowCountList.get(0);
        }
        return Long.valueOf(0);
    }

    /**
     * Create a criteria instance for each invocation of data or metadata queries.
     *
     * @param builder
     * @param root
     * @return
     */
    private List<Predicate>  getCriteria(CriteriaBuilder builder,Root<WiretapFlowEvent> root, final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
                                         final Set<String> moduleNames, final Set<String> moduleFlows, final Set<String> componentNames, final String eventId, final String payloadId,
                                         final Date fromDate, final Date untilDate, final String payloadContent)
    {

        List<Predicate> predicates = new ArrayList<>();

        if (restrictionExists(moduleNames))
        {
            predicates.add(root.get("moduleName").in(moduleNames));
        }
        if (restrictionExists(moduleFlows))
        {
            predicates.add(root.get("flowName").in(moduleFlows));
        }
        if (restrictionExists(componentNames))
        {
            predicates.add(root.get("componentName").in(componentNames));
        }
        if (restrictionExists(eventId))
        {
            predicates.add(builder.equal(root.get("eventId"),eventId));
        }
        if (restrictionExists(payloadContent))
        {
            if(payloadContent.startsWith("%")||payloadContent.endsWith("%"))
            {
                predicates.add(builder.like(root.get("event"), payloadContent));
            }else{
                predicates.add(builder.like(root.get("event"), "%" + payloadContent + "%"));
            }
        }
        if (restrictionExists(fromDate))
        {
            predicates.add( builder.greaterThan(root.get("timestamp"),fromDate.getTime()));
        }
        if (restrictionExists(untilDate))
        {
            predicates.add( builder.lessThan(root.get("timestamp"),untilDate.getTime()));
        }

        return predicates;
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
            if(restrictionValue instanceof Collection collection){
                if (!collection.isEmpty())
                    return true;
            }else{

                if( !"".equals(restrictionValue))
                    return true;
            }

        }
        return false;
    }

    /**
     * Delete all of the expired wiretaps
     */
    @Override
    public void deleteAllExpired()
    {
        if (!batchHousekeepDelete) {
            Query query = this.entityManager.createQuery(deleteOnceHarvested? HARVESTED_HOUSEKEEP_DELETE_QUERY : HOUSEKEEP_DELETE_QUERY);
            if(!deleteOnceHarvested)query.setParameter(EXPIRY, System.currentTimeMillis());
            query.executeUpdate();
        }
        else {
            batchHousekeepDelete();
        }
    }

    /**
     * Housekeep using batching.
     *
     *  Loops, checking for housekeepable items. If they exist, it identifies a batch
     *  and attempts to delete that batch
     */
    private void batchHousekeepDelete() 
    {
        logger.debug("Wiretap batched housekeeper called");

        int numberDeleted = 0;

        while(housekeepablesExist() && numberDeleted < this.transactionBatchSize)
        {

            numberDeleted += this.housekeepingBatchSize;

            Query query = this.entityManager
                .createQuery(deleteOnceHarvested ? HARVESTED_WIRETAP_EVENTS_TO_DELETE_QUERY : WIRETAP_EVENTS_TO_DELETE_QUERY);
            if(!deleteOnceHarvested)query.setParameter(NOW, System.currentTimeMillis());
            query.setMaxResults(housekeepingBatchSize);

            List<Long> wiretapEventIds = (List<Long>)query.getResultList();

            if(wiretapEventIds.size() > 0)
            {
                query = this.entityManager.createQuery(WIRETAP_EVENTS_DELETE_QUERY);
                query.setParameter(EVENT_IDS, wiretapEventIds);
                query.executeUpdate();
            }
        }
    }


    /**
     * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
     *
     * @return true if there is at least 1 expired WiretapFlowEvent
     */
    @Override
    public boolean housekeepablesExist() {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<WiretapFlowEvent> root = criteriaQuery.from(WiretapFlowEvent.class);

        if(this.deleteOnceHarvested) {
            criteriaQuery.select(builder.count(root))
                .where(builder.equal(root.get("harvested"), true));
        }
        else {
            criteriaQuery.select(builder.count(root))
                .where(builder.lessThan(root.get("expiry"), System.currentTimeMillis()));
        }


        Query query = this.entityManager.createQuery(criteriaQuery);
        List<Long> rowCountList = query.getResultList();
        Long rowCount = Long.valueOf(0);
        if (!rowCountList.isEmpty())
        {
            rowCount = rowCountList.get(0);
        }

        logger.debug(rowCount+", Wiretap housekeepables exist");
        return Boolean.valueOf(rowCount > 0);
    }

    /**
     * Retrieves a list of harvestable wiretap records.
     *
     * @param housekeepingBatchSize the maximum number of records to retrieve
     * @return a list of harvestable wiretap records
     */
    @Override
    public List<WiretapEvent> getHarvestableRecords(final int housekeepingBatchSize) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<WiretapEvent> criteriaQuery = builder.createQuery(WiretapEvent.class);
        Root<WiretapFlowEvent> root = criteriaQuery.from(WiretapFlowEvent.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("harvestedDateTime"),0));

       if(this.isHarvestQueryOrdered) {
           criteriaQuery.orderBy(
               builder.asc(root.get("timestamp")));
       }

        Query query = this.entityManager.createQuery(criteriaQuery);
        query.setFirstResult(0);
        query.setMaxResults(housekeepingBatchSize);
        return query.getResultList();
    }

    /**
     * Update the given list of WiretapEvents as harvested.
     *
     * @param events The list of WiretapEvents to be updated
     */
    @Override
    public void updateAsHarvested(List<WiretapEvent> events) {
        List<Long> wiretapEventIds = new ArrayList<Long>();

        for(WiretapEvent event: events)
        {
            wiretapEventIds.add(event.getIdentifier());
        }

        List<List<Long>> partitionedIds = Lists.partition(wiretapEventIds, 300);

        for(List<Long> eventIds: partitionedIds)
        {
            Query query = this.entityManager.createQuery(UPDATE_HARVESTED_QUERY);
            query.setParameter(EVENT_IDS, eventIds);
            query.setParameter(CURRENT_DATE_TIME, System.currentTimeMillis());
            query.executeUpdate();
        }
    }


    /**
     * Returns the value of the batchHousekeepDelete flag.
     *
     * @return the value of the batchHousekeepDelete flag
     */
    @Override
    public boolean isBatchHousekeepDelete()
    {
        return batchHousekeepDelete;
    }

    /**
     * Sets the flag for batch housekeeping deletion.
     *
     * @param batchHousekeepDelete true to enable batch deleting, false otherwise
     */
    @Override
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }

    /**
     * Retrieves the batch size for housekeeping.
     *
     * @return The batch size for housekeeping
     */
    @Override
    public Integer getHousekeepingBatchSize()
    {
        return housekeepingBatchSize;
    }


    /**
     * Sets the batch size for housekeeping.
     *
     * @param housekeepingBatchSize the size of the batch for housekeeping
     */
    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        this.housekeepingBatchSize = housekeepingBatchSize;
    }


    /**
     * Retrieves the transaction batch size.
     *
     * @return The transaction batch size
     */
    @Override
    public Integer getTransactionBatchSize()
    {
        return transactionBatchSize;
    }


    /**
     * Sets the transaction batch size.
     *
     * @param transactionBatchSize The size of the transaction batch.
     */
    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        this.transactionBatchSize = transactionBatchSize;
    }

    /**
     * Sets the housekeep query to be used for deleting expired wiretaps.
     *
     * @param housekeepQuery the SQL query for deleting expired wiretaps
     */
    @Override
    public void setHousekeepQuery(String housekeepQuery)
    {
        this.housekeepQuery = housekeepQuery;
    }

    /**
     * Sets whether the harvest query should be ordered.
     *
     * @param isHarvestQueryOrdered true if the harvest query should be ordered, false otherwise
     */
    @Override
    public void setHarvestQueryOrdered(boolean isHarvestQueryOrdered) {
        this.isHarvestQueryOrdered = isHarvestQueryOrdered;
    }
}