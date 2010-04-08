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
package org.ikasan.framework.systemevent.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.systemevent.model.SystemEvent;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>SystemEventDao</code>
 * 
 * Note that can be configured to housekeep either simply, or in batches.
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateSystemEventDao extends HibernateDaoSupport implements SystemEventDao{

    /** Query used for housekeeping expired system events */
    private static final String HOUSEKEEP_QUERY = "delete SystemEvent w where w.expiry <= ?";
    
    /** Batch delete statement */
    private static final String BATCHED_HOUSEKEEP_QUERY = "delete SystemEvent s where s.id in (:event_ids)";
    
    /** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = false;
    
    /** Batch size used when in batching housekeep */    
	private Integer housekeepingBatchSize = null;
    
    /** logger instance */
    private static final Logger logger = Logger.getLogger(HibernateSystemEventDao.class);
    
    /**
     * Constructor
     * 
     * @param batchHousekeepDelete - pass true if you want to use batch deleting
     * @param housekeepingBatchSize - batch size, only respected if set to use batching
     */
    public HibernateSystemEventDao(boolean batchHousekeepDelete,
			Integer housekeepingBatchSize) {
		this();
		this.batchHousekeepDelete = batchHousekeepDelete;
		this.housekeepingBatchSize = housekeepingBatchSize;
	}

	/**
	 * Constructor
	 */
	public HibernateSystemEventDao() {
		super();
	}


	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.dao.SystemEventDao#save(org.ikasan.framework.systemevent.model.SystemEvent)
	 */
	public void save(SystemEvent systemEvent){
		getHibernateTemplate().save(systemEvent);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.dao.SystemEventDao#find(int, int, java.lang.String, boolean, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public PagedSearchResult<SystemEvent> find(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,final String subject, final String action,
			final Date timestampFrom, final Date timestampTo, final String actor){

		return (PagedSearchResult<SystemEvent>) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(SystemEvent.class, "event");
                
                criteria.setMaxResults(pageSize);
                int firstResult = (pageNo * pageSize);
                criteria.setFirstResult(firstResult);
                criteria.addOrder(Order.desc("id"));
                if (restrictionExists(subject))
                {
                    criteria.add(Restrictions.eq("subject", subject));
                }
                if (restrictionExists(action))
                {
                    criteria.add(Restrictions.eq("action", action));
                }
                if (restrictionExists(actor))
                {
                    criteria.add(Restrictions.eq("actor", actor));
                }
                if (restrictionExists(timestampFrom))
                {
                    criteria.add(Restrictions.gt("timestamp", timestampFrom));
                }
                if (restrictionExists(timestampTo))
                {
                    criteria.add(Restrictions.lt("timestamp", timestampTo));
                }
                List<SystemEvent> systemEventResults = criteria.list();
                
                criteria.setProjection(Projections.rowCount());
                Integer rowCount = 0;
                List<Integer> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new ArrayListPagedSearchResult<SystemEvent>(systemEventResults, firstResult, rowCount);
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

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.dao.SystemEventDao#deleteExpired()
	 */
	public void deleteExpired() {
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
	 * @return List of ids for SystemEvents
	 */
	@SuppressWarnings("unchecked")
	private List<Long> getHousekeepableBatch() {
		return (List<Long>) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
            	List<Long> ids = new ArrayList<Long>();
            	
	            Criteria criteria = session.createCriteria(SystemEvent.class);
	            criteria.add(Restrictions.lt("expiry", new Date()));
	            criteria.setMaxResults(housekeepingBatchSize);
	            
	            for (Object systemEventObj : criteria.list()){
	            	SystemEvent systemEvent = (SystemEvent)systemEventObj;
	            	ids.add(systemEvent.getId());
	            }
	           
	            return ids;
            
            }
        });
	}

	/**
	 * Checks if there are housekeepable items in existance, ie expired SystemEvents
	 * 
	 * @return true if there is at least 1 expired SystemEvent 
	 */
	private boolean housekeepablesExist() {
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
            Criteria criteria = session.createCriteria(SystemEvent.class);
            criteria.add(Restrictions.lt("expiry", new Date()));
            criteria.setProjection(Projections.rowCount());
            Integer rowCount = 0;
            List<Integer> rowCountList = criteria.list();
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
