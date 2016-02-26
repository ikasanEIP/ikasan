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
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.systemevent.model.ArrayListPagedSearchResult;
import org.ikasan.systemevent.model.SystemEvent;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

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

	private static final String EXPIRY = "expiry";
	
    /** Query used for housekeeping expired system events */
    private static final String HOUSEKEEP_QUERY = "delete SystemEvent w where w.expiry <= :" + EXPIRY;

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
     * @param housekeepingBatchSize - batch size, only respected if set to use
     *            batching
     */
    public HibernateSystemEventDao(boolean batchHousekeepDelete, Integer housekeepingBatchSize)
    {
        this();
        this.batchHousekeepDelete = batchHousekeepDelete;
        this.housekeepingBatchSize = housekeepingBatchSize;
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
     * .framework.systemevent.model.SystemFlowEvent)
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

        return (PagedSearchResult<SystemEvent>) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria resultCriteria = getCriteria(session);
                resultCriteria.setMaxResults(pageSize);
                int firstResult = (pageNo * pageSize);
                resultCriteria.setFirstResult(firstResult);
                resultCriteria.addOrder(Order.desc("id"));
                List<SystemEvent> systemEventResults = resultCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new ArrayListPagedSearchResult<SystemEvent>(systemEventResults, firstResult, rowCount);
            }
            
            /**
             * Create a consistent criteria instance for both result and metadata
             * @param session
             * @return
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(SystemEvent.class, "event");
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
                return criteria;
                
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
			                Criteria resultCriteria = getCriteria(session);
			                resultCriteria.addOrder(Order.desc("id"));
			                
			                List<SystemEvent> systemEventResults = resultCriteria.list();

			                return systemEventResults;
			            }
			            
			            /**
			             * Create a consistent criteria instance for both result and metadata
			             * @param session
			             * @return
			             */
			            private Criteria getCriteria(Session session)
			            {
			                Criteria criteria = session.createCriteria(SystemEvent.class, "event");
			                if (restrictionExists(subjects))
			                {
			                    criteria.add(Restrictions.in("subject", subjects));
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
			                return criteria;
			                
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
        logger.info("called batchHousekeppDelete");
        while (housekeepablesExist())
        {
            final List<Long> housekeepableBatch = getHousekeepableBatch();

            getHibernateTemplate().execute(new HibernateCallback<Object>()
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
     * @return List of ids for SystemFlowEvents
     */
    @SuppressWarnings("unchecked")
    private List<Long> getHousekeepableBatch()
    {
        return (List<Long>) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                List<Long> ids = new ArrayList<Long>();

                Criteria criteria = session.createCriteria(SystemEvent.class);
                criteria.add(Restrictions.lt("expiry", new Date()));
                criteria.setMaxResults(housekeepingBatchSize);

                for (Object systemEventObj : criteria.list())
                {
                    SystemEvent systemFlowEvent = (SystemEvent) systemEventObj;
                    ids.add(systemFlowEvent.getId());
                }

                return ids;

            }
        });
    }

    /**
     * Checks if there are housekeepable items in existance, ie expired
     * SystemFlowEvents
     * 
     * @return true if there is at least 1 expired SystemFlowEvent
     */
    private boolean housekeepablesExist()
    {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(SystemEvent.class);
                criteria.add(Restrictions.lt("expiry", new Date()));
                criteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                logger.info(rowCount + ", housekeepables exist");
                return new Boolean(rowCount > 0);

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

}
