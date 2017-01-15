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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.history.model.CustomMetric;
import org.ikasan.history.model.MetricEvent;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.ArrayListPagedSearchResult;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.*;

/**
 * Hibernate implementation of the <code>MessageHistoryDao</code>
 *
 * @author Ikasan Development Team
 *
 */
public class HibernateMessageHistoryDao extends HibernateDaoSupport implements MessageHistoryDao
{
	/** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = false;

    /** Batch size used when in batching housekeep */
    private Integer housekeepingBatchSize = 400;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 2000;


    @Override
    public void save(MessageHistoryEvent messageHistoryEvent)
    {
        getHibernateTemplate().saveOrUpdate(messageHistoryEvent);
    }
    
	@Override
	public void save(WiretapEvent wiretapEvent) 
	{
		getHibernateTemplate().saveOrUpdate(wiretapEvent);
	}

    @Override
    public PagedSearchResult<MessageHistoryEvent> findMessageHistoryEvents(final int pageNo, final int pageSize, final String orderBy,
                                                                           final boolean orderAscending, final Set<String> moduleNames,
                                                                           final String flowName, final String componentName,
                                                                           final String eventId, final String relatedEventId,
                                                                           final Date fromDate, final Date toDate)
    {
        return getHibernateTemplate().execute(new HibernateCallback<PagedSearchResult<MessageHistoryEvent>>()
        {
            public PagedSearchResult<MessageHistoryEvent> doInHibernate(Session session) throws HibernateException
            {
                Criteria dataCriteria = getCriteria(session);
                dataCriteria.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                dataCriteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        dataCriteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        dataCriteria.addOrder(Order.desc(orderBy));
                    }
                }
                List<MessageHistoryEvent> messageHistoryResults = dataCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return new ArrayListPagedSearchResult<>(messageHistoryResults, firstResult, rowCount);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param session the hibernate Session
             * @return a Criteria based on the provided search parameters
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);

                if (restrictionExists(moduleNames))
                {
                    criteria.add(Restrictions.in("moduleName", moduleNames));
                }
                if (restrictionExists(flowName))
                {
                    criteria.add(Restrictions.eq("flowName", flowName));
                }
                if (restrictionExists(componentName))
                {
                    criteria.add(Restrictions.eq("componentName", componentName));
                }
                if (restrictionExists(eventId))
                {
                    criteria.add(Restrictions.or(
                                    Restrictions.eq("beforeEventIdentifier", eventId),
                                    Restrictions.eq("afterEventIdentifier", eventId) ));
                }
                if (restrictionExists(relatedEventId))
                {
                    criteria.add(Restrictions.or(
                                    Restrictions.eq("beforeRelatedEventIdentifier", relatedEventId),
                                    Restrictions.eq("afterRelatedEventIdentifier", relatedEventId)));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.ge("startTime", fromDate.getTime()));
                }
                if (restrictionExists(toDate))
                {
                    criteria.add(Restrictions.le("endTime", toDate.getTime()));
                }
                return criteria;
            }
        });
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> getMessageHistoryEvent(final int pageNo, final int pageSize, final String orderBy,
            final boolean orderAscending, final String eventId, final String relatedEventId)
    {
        return getHibernateTemplate().execute(new HibernateCallback<PagedSearchResult<MessageHistoryEvent>>()
        {
            public PagedSearchResult<MessageHistoryEvent> doInHibernate(Session session) throws HibernateException
            {
                Criteria dataCriteria = getCriteria(session);
                dataCriteria.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                dataCriteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        dataCriteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        dataCriteria.addOrder(Order.desc(orderBy));
                    }
                }
                List<MessageHistoryEvent> messageHistoryResults = dataCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return new ArrayListPagedSearchResult<>(messageHistoryResults, firstResult, rowCount);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param session the hibernate Session
             * @return a Criteria based on the provided search parameters
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);

                if (restrictionExists(eventId) && !restrictionExists(relatedEventId))
                {
                    criteria.add(Restrictions.eq("beforeEventIdentifier", eventId));
                }
                if (restrictionExists(relatedEventId))
                {
                    criteria.add(Restrictions.or(
                            Restrictions.eq("beforeEventIdentifier", eventId),
                            Restrictions.eq("beforeRelatedEventIdentifier", relatedEventId)));
                }
                return criteria;
            }
        });
    }

    @Override
    public void deleteAllExpired()
    {        
        if (batchHousekeepDelete)
        {
        	 batchHousekeepDelete();
        }
        else
        {
            getHibernateTemplate().execute(new HibernateCallback<Object>()
	        {
	            public Object doInHibernate(Session session) throws HibernateException
	            {
	            	String deleteMetrics = "DELETE FROM Metric WHERE MessageHistoryId in " +
	            			"(SELECT Id FROM MessageHistory WHERE Expiry <= " + System.currentTimeMillis() +
                            " AND Harvested = 1)";
	            	session.createSQLQuery(deleteMetrics).executeUpdate();
	            	 
	                String delete = "DELETE FROM MessageHistory WHERE Expiry <= " + System.currentTimeMillis() + " AND Harvested = 1";
	                session.createSQLQuery(delete).executeUpdate();
	                return null;
	            }
	        });
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
        logger.info("Message History batched housekeeper called");

        int numberDeleted = 0;

        while(housekeepablesExist() && numberDeleted < this.transactionBatchSize)
        {

            numberDeleted += this.housekeepingBatchSize;

            List<MessageHistoryEvent> events = this.getHarvestedRecords(this.housekeepingBatchSize);

            this.deleteHarvestableRecords(events);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean housekeepablesExist()
    {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);
                criteria.add(Restrictions.le("expiry", System.currentTimeMillis()));
                criteria.add(Restrictions.eq("harvested", true));
                criteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                logger.info(rowCount+", MessageHistory housekeepables exist");
                return rowCount > 0;
            }
        });
    }

    @Override
    public boolean harvestableRecordsExist()
    {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);
                criteria.add(Restrictions.eq("harvested", false));
                criteria.setProjection(Projections.rowCount());

                Long rowCount = 0L;
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                logger.info(rowCount+", MessageHistory harvestable records exist");
                return rowCount > 0;
            }
        });
    }

    @Override
    public List<MessageHistoryEvent> getHarvestableRecords(final int housekeepingBatchSize)
    {
        return this.getHarvestableRecords(housekeepingBatchSize, false);
    }

    public List<MessageHistoryEvent> getHarvestedRecords(final int housekeepingBatchSize)
    {
        return this.getHarvestableRecords(housekeepingBatchSize, true);
    }


    public List<MessageHistoryEvent> getHarvestableRecords(final int housekeepingBatchSize, final Boolean harvested)
    {
        return (List<MessageHistoryEvent>) this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);
                criteria.add(Restrictions.eq("harvested", harvested));
                criteria.setMaxResults(housekeepingBatchSize);
                criteria.addOrder(Order.asc("startTimeMillis"));

                List<MessageHistoryEvent> messageHistoryEvents = criteria.list();
                ArrayList<String> eventIds = new ArrayList<String>();

                List<List<MessageHistoryEvent>> smallerLists = Lists.partition(messageHistoryEvents, 200);

                Map<String, MetricEvent> eventsMap = new HashMap<String, MetricEvent>();

                for(List<MessageHistoryEvent> list: smallerLists)
                {
                    for (MessageHistoryEvent event: list)
                    {
                        eventIds.add((String)event.getBeforeEventIdentifier());
                    }

                    eventsMap.putAll(getWiretapFlowEvents(eventIds));

                    eventIds = new ArrayList<String>();
                }

                for(MessageHistoryEvent<String, CustomMetric, MetricEvent> messageHistoryEvent: messageHistoryEvents)
                {
                    MetricEvent event = eventsMap.get(messageHistoryEvent.getBeforeEventIdentifier());
                    if(event != null)
                    {
                        if(event.getComponentName().equals(messageHistoryEvent.getComponentName())
                                && event.getFlowName().equals(messageHistoryEvent.getFlowName())
                                && event.getModuleName().equals(messageHistoryEvent.getModuleName()))
                        {
                            messageHistoryEvent.setWiretapFlowEvent(event);
                        }
                    }
                }

                return messageHistoryEvents;
            }
        });
    }

    protected Map<String, MetricEvent> getWiretapFlowEvents(final List<String> eventIds)
    {
        return (Map<String, MetricEvent>) this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(MetricEvent.class);
                criteria.add(Restrictions.in("eventId", eventIds));


                List<MetricEvent> wiretapEvents = criteria.list();

                HashMap<String, MetricEvent> results = new HashMap<String, MetricEvent>();

                for(MetricEvent event: wiretapEvents)
                {
                    results.put(event.getEventId(), event);
                }

                return results;
            }
        });
    }

    @Override
    public void deleteHarvestableRecords(List<MessageHistoryEvent> events)
    {
        for(MessageHistoryEvent event: events)
        {
            if(event.getWiretapFlowEvent() != null)
            {
                getHibernateTemplate().delete(event.getWiretapFlowEvent());
            }

            for(CustomMetric metric: (Set<CustomMetric>)event.getMetrics())
            {
                getHibernateTemplate().delete(metric);
            }

            getHibernateTemplate().delete(event);
        }
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
        return restrictionValue != null && !"".equals(restrictionValue);
    }

	/**
	 * @return the batchHousekeepDelete
	 */
	public boolean isBatchHousekeepDelete() 
	{
		return batchHousekeepDelete;
	}

	/**
	 * @param batchHousekeepDelete the batchHousekeepDelete to set
	 */
	public void setBatchHousekeepDelete(boolean batchHousekeepDelete) 
	{
		this.batchHousekeepDelete = batchHousekeepDelete;
	}

	/**
	 * @return the housekeepingBatchSize
	 */
	public Integer getHousekeepingBatchSize() 
	{
		return housekeepingBatchSize;
	}

	/**
	 * @param housekeepingBatchSize the housekeepingBatchSize to set
	 */
	public void setHousekeepingBatchSize(Integer housekeepingBatchSize) 
	{
		this.housekeepingBatchSize = housekeepingBatchSize;
	}

	/**
	 * @return the housekeepingBatchSize
	 */
	public Integer getTransactionBatchSize() 
	{
		return transactionBatchSize;
	}

	/**
	 * @param transactionBatchSize the housekeepingBatchSize to set
	 */
	public void setTransactionBatchSize(Integer transactionBatchSize)
	{
		this.transactionBatchSize = transactionBatchSize;
	}	

}
