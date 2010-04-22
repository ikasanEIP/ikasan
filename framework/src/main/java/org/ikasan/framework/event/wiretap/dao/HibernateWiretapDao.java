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
package org.ikasan.framework.event.wiretap.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.model.WiretapEventHeader;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>WiretapDao</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class HibernateWiretapDao extends HibernateDaoSupport implements WiretapDao
{

	/** Query used for housekeeping expired wiretap events */
    private static final String HOUSEKEEP_QUERY = "delete WiretapEvent w where w.expiry <= ?";

    /** Query for finding all wiretap events with the same payloadId */
    private static final String WIRETAP_EVENT_IDS_FOR_PAYLOAD_ID = "select w.id from WiretapEvent w where w.payloadId = ?";

    /** Batch delete statement */
    private static final String BATCHED_HOUSEKEEP_QUERY = "delete WiretapEvent s where s.id in (:event_ids)";
    
    /** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = false;
    
    /** Batch size used when in batching housekeep */    
	private Integer housekeepingBatchSize = null;
    
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
			Integer housekeepingBatchSize) {
		this();
		this.batchHousekeepDelete = batchHousekeepDelete;
		this.housekeepingBatchSize = housekeepingBatchSize;
	}
	
	/**
     * Save the wiretapEvent
     *  
     * @see
     * org.ikasan.framework.event.wiretap.dao.WiretapDao#save(
     * org.ikasan.framework.event.wiretap.model.WiretapEvent)
     */
    public void save(WiretapEvent wiretapEvent)
    {
        getHibernateTemplate().save(wiretapEvent);
    }

    /**
     * Find the Wiretap by its Id
     * 
     * @see
     * org.ikasan.framework.event.wiretap.dao.WiretapDao#findById(java.lang.
     * Long)
     */
    @SuppressWarnings("unchecked")
    public WiretapEvent findById(Long id)
    {
        // get the WiretapEvent
        WiretapEvent wiretapEvent = (WiretapEvent) getHibernateTemplate().get(WiretapEvent.class, id);
        // find any next or previous by payloadId
        List<Long> relatedIds = getHibernateTemplate().find(WIRETAP_EVENT_IDS_FOR_PAYLOAD_ID, wiretapEvent.getPayloadId());
        Collections.sort(relatedIds);
        int thisWiretapsIndex = relatedIds.indexOf(wiretapEvent.getId());
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
        wiretapEvent.setNextByPayload(nextEvent);
        wiretapEvent.setPreviousByPayload(previousEvent);
        return wiretapEvent;
    }

    /**
     * Find paging list of wiretaps
     * 
     * @param moduleNames - The list of module names
     * @param moduleFlow - The name of Flow internal to the Module
     * @param componentName - The component name
     * @param eventId - The event id
     * @param payloadId - The payload id
     * @param fromDate - The from date
     * @param untilDate - The to date
     * @param payloadContent - The payload content
     * @param maxResults - Max Results to bring back
     * @param firstResult - The first result
     * @return PagedWiretapSearchResult
     * @throws DataAccessException - Exception if we can't get the data
     * @deprecated - Use findWiretapEvents instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public PagedWiretapSearchResult findPaging(final Set<String> moduleNames, final String moduleFlow, final String componentName, final String eventId, final String payloadId,
            final Date fromDate, final Date untilDate, final String payloadContent, final int maxResults, final int firstResult) throws DataAccessException
    {
        return (PagedWiretapSearchResult) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(WiretapEvent.class, "event");
                criteria.add(Restrictions.in("moduleName", moduleNames));
                if (restrictionExists(moduleFlow))
                {
                    criteria.add(Restrictions.eq("flowName", moduleFlow));
                }
                if (restrictionExists(componentName))
                {
                    criteria.add(Restrictions.eq("componentName", componentName));
                }
                if (restrictionExists(eventId))
                {
                    criteria.add(Restrictions.eq("eventId", eventId));
                }
                if (restrictionExists(payloadId))
                {
                    criteria.add(Restrictions.eq("payloadId", payloadId));
                }
                if (restrictionExists(payloadContent))
                {
                    criteria.add(Restrictions.like("payloadContent", payloadContent, MatchMode.ANYWHERE));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.gt("created", fromDate));
                }
                if (restrictionExists(untilDate))
                {
                    criteria.add(Restrictions.lt("created", untilDate));
                }
                criteria.setMaxResults(maxResults);
                criteria.setFirstResult(firstResult);
                criteria.addOrder(Order.desc("id"));
                List<WiretapEventHeader> wiretapResults = criteria.list();
                criteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new PagedWiretapSearchResult(wiretapResults, rowCount, firstResult);
            }
        });
    }

    /**
     * Perform a paged search for <code>WiretapEvent</code>s
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
    @SuppressWarnings("unchecked")
    public PagedSearchResult<WiretapEvent> findWiretapEvents(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
            final Set<String> moduleNames, final String moduleFlow, final String componentName, final String eventId, final String payloadId, final Date fromDate, final Date untilDate,
            final String payloadContent)
    {
        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(WiretapEvent.class);
                criteria.setMaxResults(pageSize);
                int firstResult = (pageNo * pageSize);
                criteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        criteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        criteria.addOrder(Order.desc(orderBy));
                    }
                }
                if (restrictionExists(moduleNames))
                {
                    criteria.add(Restrictions.in("moduleName", moduleNames));
                }
                if (restrictionExists(moduleFlow))
                {
                    criteria.add(Restrictions.eq("flowName", moduleFlow));
                }
                if (restrictionExists(componentName))
                {
                    criteria.add(Restrictions.eq("componentName", componentName));
                }
                if (restrictionExists(eventId))
                {
                    criteria.add(Restrictions.eq("eventId", eventId));
                }
                if (restrictionExists(payloadId))
                {
                    criteria.add(Restrictions.eq("payloadId", payloadId));
                }
                if (restrictionExists(payloadContent))
                {
                    criteria.add(Restrictions.like("payloadContent", payloadContent, MatchMode.ANYWHERE));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.gt("created", fromDate));
                }
                if (restrictionExists(untilDate))
                {
                    criteria.add(Restrictions.lt("created", untilDate));
                }
                List<WiretapEvent> wiretapResults = criteria.list();
                criteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new ArrayListPagedSearchResult<WiretapEvent>(wiretapResults, firstResult, rowCount);
            }
        });
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
        if (restrictionValue != null && !"".equals(restrictionValue))
        {
            return true;
        }
        return false;
    }

    /**
     * Delete all of the expired wiretaps
     */
    public void deleteAllExpired()
    {
    	if (!batchHousekeepDelete){
    		getHibernateTemplate().bulkUpdate(HOUSEKEEP_QUERY, new Date());
    	} else {
			batchHousekeepDelete();
		}
    }
    
	/**
	 * Housekeep using batching.
	 * 
	 *  Loops, checking for housekeepable items. If they exist, it identifies a batch
	 *  and attempts to delete that batch
	 */
	private void batchHousekeepDelete() {
		logger.info("called");
		while(housekeepablesExist()){
			final List<Long> housekeepableBatch = getHousekeepableBatch();
			
			getHibernateTemplate().execute(new HibernateCallback()
	        {
	            public Object doInHibernate(Session session) throws HibernateException
	            {
	            	
	                Query query = session.createQuery(BATCHED_HOUSEKEEP_QUERY);
	                query.setParameterList("event_ids", housekeepableBatch);
	            	query.executeUpdate();
	                return null;
	            }
	        });
		}
		
	}

	/**
	 * Identifies a batch (List of Ids) of housekeepable items
	 * 
	 * @return List of ids for WiretapEvents
	 */
	@SuppressWarnings("unchecked")
	private List<Long> getHousekeepableBatch() {
		return (List<Long>) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
            	List<Long> ids = new ArrayList<Long>();
            	
	            Criteria criteria = session.createCriteria(WiretapEvent.class);
	            criteria.add(Restrictions.lt("expiry", new Date()));
	            criteria.setMaxResults(housekeepingBatchSize);
	            
	            for (Object wiretapEventObj : criteria.list()){
	            	WiretapEvent wiretapEvent = (WiretapEvent)wiretapEventObj;
	            	ids.add(wiretapEvent.getId());
	            }
	           
	            return ids;
            
            }
        });
	}

	/**
	 * Checks if there are housekeepable items in existance, ie expired WiretapEvents
	 * 
	 * @return true if there is at least 1 expired WiretapEvent 
	 */
	private boolean housekeepablesExist() {
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
            Criteria criteria = session.createCriteria(WiretapEvent.class);
            criteria.add(Restrictions.lt("expiry", new Date()));
            criteria.setProjection(Projections.rowCount());
            Long rowCount = new Long(0);
            List<Long> rowCountList = criteria.list();
            if (!rowCountList.isEmpty())
            {
                rowCount = rowCountList.get(0);
            }
            logger.info(rowCount+", housekeepables exist");
            return new Boolean(rowCount>0);
            
            }
        });
            
            

	}
}
