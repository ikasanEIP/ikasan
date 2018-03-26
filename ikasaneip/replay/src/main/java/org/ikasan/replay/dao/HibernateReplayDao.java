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
package org.ikasan.replay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.ikasan.replay.model.HibernateReplayAudit;
import org.ikasan.replay.model.HibernateReplayAuditEvent;
import org.ikasan.replay.model.HibernateReplayEvent;
import com.google.common.collect.Lists;
import org.ikasan.spec.replay.ReplayDao;
import org.ikasan.spec.replay.ReplayAuditDao;
import org.ikasan.spec.replay.ReplayEvent;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateReplayDao extends HibernateDaoSupport implements ReplayDao,ReplayAuditDao<HibernateReplayAudit,HibernateReplayAuditEvent>
{
	public static final String MODULE_NAME = "moduleName";
	public static final String FLOW_NAME = "flowame";
	public static final String USER = "user";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String EVENT_ID = "eventId";
	public static final String EVENT_IDS = "eventIds";
	public static final String NOW = "now";
	
	public static final String REPLAY_AUDIT_QUERY = "select DISTINCT ra from HibernateReplayAudit ra, HibernateReplayAuditEvent rae "
			+ "where "
			+ "ra.id = rae.id.replayAuditId";
	
	public static final String MODULE_NAME_PREDICATE =  " and rae.moduleName IN :" + MODULE_NAME;
	public static final String FLOW_NAME_PREDICATE =  " and rae.flowName IN :" + FLOW_NAME;
	public static final String USER_PREDICATE =  " and ra.user = :" + USER;
	public static final String START_DATE_PREDICATE =  " and ra.timestamp > :" + START_DATE;
	public static final String END_DATE_PREDICATE =  " and ra.timestamp < :" + END_DATE;
	public static final String EVENT_ID_PREDICATE =  " and rae.eventId = :" + EVENT_ID;
	public static final String ORDER_BY =  " order by ra.timestamp desc";

	public static final String REPLAY_EVENTS_TO_DELETE_QUERY = "select id from HibernateReplayEvent re " +
			" where re.expiry < :" + NOW;

	public static final String REPLAY_EVENTS_DELETE_QUERY = "delete HibernateReplayEvent re " +
			" where re.id in(:" + EVENT_IDS + ")";

	public static final String REPLAY_AUDIT_EVENTS_TO_DELETE_QUERY = "select distinct rae.id.replayAuditId from HibernateReplayAuditEvent rae " +
			" where rae.id.replayEventId in (:" + EVENT_IDS + ")";

	public static final String REPLAY_AUDIT_DELETE_QUERY = "delete HibernateReplayAudit re " +
			" where re.id not in(select distinct rae.id.replayAuditId from HibernateReplayAuditEvent rae)";

	public static final String REPLAY_AUDIT_EVENT_DELETE_QUERY = "delete HibernateReplayAuditEvent re " +
			" where re.id.replayEventId in(:" + EVENT_IDS + ")";

    public static final String UPDATE_HARVESTED_QUERY = "update HibernateReplayEvent w set w.harvested = 1 " +
        " where w.id in(:" + EVENT_IDS + ")";
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayAudits(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HibernateReplayAudit> getReplayAudits(final List<String> moduleNames, final List<String> flowNames,
													  final String eventId, final String user, final Date startDate, final Date endDate)
	{
		return (List<HibernateReplayAudit>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
            	StringBuffer queryStringBuffer = new StringBuffer(REPLAY_AUDIT_QUERY); 
            	if(user != null && user.length() > 0)
        		{
            		queryStringBuffer.append(USER_PREDICATE);
        		}
        		
        		if(startDate != null)
        		{
        			queryStringBuffer.append(START_DATE_PREDICATE);
        		}
        		
        		if(endDate != null)
        		{
        			queryStringBuffer.append(END_DATE_PREDICATE);
        		}
        		
        		if(moduleNames != null && moduleNames.size() > 0)
    			{
        			queryStringBuffer.append(MODULE_NAME_PREDICATE);
    			}
    			
    			if(flowNames != null && flowNames.size() > 0)
    			{
    				queryStringBuffer.append(FLOW_NAME_PREDICATE);
    			}
    			
    			if (eventId != null && eventId.length() > 0)
    		    {
    				queryStringBuffer.append(EVENT_ID_PREDICATE);
    		    }
    			
    			queryStringBuffer.append(ORDER_BY);
        				        		
                Query query = session.createQuery(queryStringBuffer.toString());
                
                if(user != null && user.length() > 0)
        		{
            		query.setParameter(USER, user);
        		}
        		
        		if(startDate != null)
        		{
        			query.setParameter(START_DATE, startDate.getTime());
        		}
        		
        		if(endDate != null)
        		{
        			query.setParameter(END_DATE, endDate.getTime());
        		}
        		
        		if(moduleNames != null && moduleNames.size() > 0)
    			{
        			query.setParameterList(MODULE_NAME, moduleNames);
    			}
    			
    			if(flowNames != null && flowNames.size() > 0)
    			{
    				query.setParameterList(FLOW_NAME, flowNames);
    			}
    			
    			if (eventId != null && eventId.length() > 0)
    		    {
    				query.setParameter(EVENT_ID, eventId);
    		    }

                return (List<HibernateReplayAudit>)query.list();
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#saveOrUpdate(org.ikasan.replay.window.ReplayEvent)
	 */
	@Override
	public void saveOrUpdate(ReplayEvent replayEvent)
	{
		this.getHibernateTemplate().saveOrUpdate(replayEvent);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayEvents(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ReplayEvent> getReplayEvents(String moduleName,
                                                      String flowName, Date startDate, Date endDate, int resultSize)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(HibernateReplayEvent.class);

		if(moduleName != null && moduleName.length() > 0)
		{
			criteria.add(Restrictions.eq("moduleName", moduleName));
		}

		if(flowName != null && flowName.length() > 0)
		{
			criteria.add(Restrictions.eq("flowName", flowName));
		}

		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}

		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}

		criteria.addOrder(Order.desc("timestamp"));

		return (List<ReplayEvent>)this.getHibernateTemplate().findByCriteria(criteria, 0, resultSize);
	}
	
	

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayEvents(java.util.List, java.util.List, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ReplayEvent> getReplayEvents(List<String> moduleNames,
                                                      List<String> flowNames, String eventId,
                                                      String payloadContent, Date fromDate, Date toDate, int resultSize)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(HibernateReplayEvent.class);
		
		if(moduleNames != null && moduleNames.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleNames));
		}
		
		if(flowNames != null && flowNames.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowNames));
		}
		
		if(fromDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", fromDate.getTime()));
		}
		
		if(toDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", toDate.getTime()));
		}
		
		if (eventId != null && eventId.length() > 0)
	    {
	       criteria.add(Restrictions.eq("eventId", eventId));
	    }

		if (payloadContent != null && payloadContent.length() > 0)
		{
			criteria.add(Restrictions.like("eventAsString", payloadContent, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.desc("timestamp"));	
		
		return (List<ReplayEvent>)this.getHibernateTemplate().findByCriteria(criteria, 0, resultSize);
	}

	@Override
	public ReplayEvent getReplayEventById(Long id)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(HibernateReplayEvent.class);

		criteria.add(Restrictions.eq("id", id));

		return (ReplayEvent)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	/* (non-Javadoc)
         * @see org.ikasan.spec.replay.ReplayDao#saveOrUpdate(org.ikasan.replay.window.ReplayAudit)
         */
	@Override
	public void saveOrUpdateAudit(HibernateReplayAudit replayAudit)
	{
		this.getHibernateTemplate().saveOrUpdate(replayAudit);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#saveOrUpdate(org.ikasan.replay.window.ReplayAuditEvent)
	 */
	@Override
	public void saveOrUpdate(HibernateReplayAuditEvent replayAuditEvent)
	{
		this.getHibernateTemplate().saveOrUpdate(replayAuditEvent);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayAuditById(java.lang.Long)
	 */
	@Override
	public HibernateReplayAudit getReplayAuditById(Long id)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(HibernateReplayAudit.class);
		
		criteria.add(Restrictions.eq("id", id));
		
		return (HibernateReplayAudit)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayAuditEventsByAuditId(java.lang.Long)
	 */
	@Override
	public List<HibernateReplayAuditEvent> getReplayAuditEventsByAuditId(Long id)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(HibernateReplayAuditEvent.class);

		criteria.add(Restrictions.eq("id.replayAuditId", id));
		
		
		return (List<HibernateReplayAuditEvent>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getNumberReplayAuditEventsByAuditId(java.lang.Long)
	 */
	@Override
	public Long getNumberReplayAuditEventsByAuditId(final Long id) 
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(HibernateReplayAuditEvent.class);
                criteria.add(Restrictions.eq("id.replayAuditId", id));
                criteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = criteria.list();

                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return rowCount;
            }
        });
	}

	@Override
	public void housekeep(final Integer numToHousekeep)
	{
		this.getHibernateTemplate().execute(new HibernateCallback()
		{
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException
			{
				Query query = session.createQuery(REPLAY_EVENTS_TO_DELETE_QUERY);
				query.setLong(NOW, System.currentTimeMillis());
				query.setMaxResults(numToHousekeep);

				List<Long> replayEventIds = (List<Long>)query.list();

				List<Long> replayAuditEventIds = new ArrayList<Long>();

				if(replayEventIds.size() > 0)
				{

					query = session.createQuery(REPLAY_AUDIT_EVENTS_TO_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, replayEventIds);

					replayAuditEventIds = (List<Long>) query.list();
				}

				if(replayEventIds.size() > 0)
				{
					query = session.createQuery(REPLAY_AUDIT_EVENT_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, replayEventIds);
					query.executeUpdate();
				}

				if(replayAuditEventIds.size() > 0)
				{
					query = session.createQuery(REPLAY_AUDIT_DELETE_QUERY);
					query.executeUpdate();
				}

				if(replayEventIds.size() > 0)
				{
					query = session.createQuery(REPLAY_EVENTS_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, replayEventIds);
					query.executeUpdate();
				}

				return null;
			}
		});
	}

	public List<ReplayEvent> getHarvestableRecords(final int housekeepingBatchSize)
	{
		return (List<ReplayEvent>) this.getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				Criteria criteria = session.createCriteria(HibernateReplayEvent.class);
				criteria.add(Restrictions.eq("harvested", false));
				criteria.setMaxResults(housekeepingBatchSize);
				criteria.addOrder(Order.asc("timestamp"));

				List<HibernateReplayEvent> flowInvocationMetrics = criteria.list();

				return flowInvocationMetrics;
			}
		});
	}

    @Override
    public void updateAsHarvested(List<ReplayEvent> events)
    {
        getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                List<Long> exclusionEventIds = new ArrayList<Long>();

                for(ReplayEvent event: events)
                {
                    exclusionEventIds.add(event.getId());
                }

                List<List<Long>> partitionedIds = Lists.partition(exclusionEventIds, 300);

                for(List<Long> eventIds: partitionedIds)
                {
                    Query query = session.createQuery(UPDATE_HARVESTED_QUERY);
                    query.setParameterList(EVENT_IDS, eventIds);
                    query.executeUpdate();
                }

                return null;
            }
        });
    }


}
