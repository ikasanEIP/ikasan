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

import com.google.common.collect.Lists;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.replay.model.HibernateReplayAudit;
import org.ikasan.replay.model.HibernateReplayAuditEvent;
import org.ikasan.replay.model.HibernateReplayEvent;
import org.ikasan.replay.model.ReplayAuditEventKey;
import org.ikasan.spec.replay.ReplayAuditDao;
import org.ikasan.spec.replay.ReplayDao;
import org.ikasan.spec.replay.ReplayEvent;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateReplayDao extends HibernateDaoSupport implements ReplayDao<Long>,ReplayAuditDao<HibernateReplayAudit,HibernateReplayAuditEvent>
{
	public static final String MODULE_NAME = "moduleName";
	public static final String FLOW_NAME = "flowame";
	public static final String USER = "user";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String EVENT_ID = "eventId";
	public static final String EVENT_IDS = "eventIds";
	public static final String NOW = "now";
	
	public static final String REPLAY_AUDIT_QUERY = """
            select DISTINCT ra from HibernateReplayAudit ra, HibernateReplayAuditEvent rae \
            where \
            ra.id = rae.id.replayAuditId\
            """;
	
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

	public static final String REPLAY_AUDIT_DELETE_QUERY = """
            delete HibernateReplayAudit re \
             where re.id not in(select distinct rae.id.replayAuditId from HibernateReplayAuditEvent rae)\
            """;

	public static final String REPLAY_AUDIT_EVENT_DELETE_QUERY = "delete HibernateReplayAuditEvent re " +
			" where re.id.replayEventId in(:" + EVENT_IDS + ")";

    public static final String UPDATE_HARVESTED_QUERY = "update HibernateReplayEvent w set w.harvestedDateTime = :" + NOW + ", w.harvested = 1" +
        " where w.id in(:" + EVENT_IDS + ")";

    private boolean isHarvestQueryOrdered = false;


    public HibernateReplayDao()
    {

    }

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

    @Override
    public void save(List<ReplayEvent> replayEvents)
    {
        replayEvents.forEach(replayEvent -> this.getHibernateTemplate().save(replayEvent));
    }

    /* (non-Javadoc)
         * @see org.ikasan.spec.replay.ReplayDao#getReplayEvents(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
         */
	@Override
	public List<ReplayEvent> getReplayEvents(String moduleName,
                                                      String flowName, Date startDate, Date endDate, int resultSize)
	{

		return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<ReplayEvent> criteriaQuery = builder.createQuery(ReplayEvent.class);

            Root<HibernateReplayEvent> root = criteriaQuery.from(HibernateReplayEvent.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null)
            {
                predicates.add( builder.equal(root.get("moduleName"),moduleName));
            }

            if(flowName != null)
            {
                predicates.add( builder.equal(root.get("flowName"),flowName));
            }

            if(startDate != null)
            {
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }


            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(
                    builder.desc(root.get("timestamp")));


            Query<ReplayEvent> query = session.createQuery(criteriaQuery);
            query.setFirstResult(0);
            query.setMaxResults(resultSize);
            List<ReplayEvent> rowList = query.getResultList();

            return rowList;
        });
	}
	
	

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayEvents(java.util.List, java.util.List, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ReplayEvent> getReplayEvents(List<String> moduleNames,
                                                      List<String> flowNames, String eventId,
                                                      String payloadContent, Date startDate, Date endDate, int resultSize)
	{
	    return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<ReplayEvent> criteriaQuery = builder.createQuery(ReplayEvent.class);

            Root<HibernateReplayEvent> root = criteriaQuery.from(HibernateReplayEvent.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleNames != null && moduleNames.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleNames));
            }

            if(flowNames != null && flowNames.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowNames));
            }

            if(startDate != null)
            {
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            if (eventId != null && eventId.length() > 0)
            {
                predicates.add( builder.equal(root.get("eventId"),eventId));
            }

            if (payloadContent != null && payloadContent.length() > 0)
            {
                //criteria.add(Restrictions.like("eventAsString", payloadContent, MatchMode.ANYWHERE));
                predicates.add( builder.like(root.get("eventAsString"),payloadContent));
            }

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(
                    builder.desc(root.get("timestamp")));


            Query<ReplayEvent> query = session.createQuery(criteriaQuery);
            query.setFirstResult(0);
            query.setMaxResults(resultSize);
            List<ReplayEvent> rowList = query.getResultList();

            return rowList;
        });
	}

	@Override
	public ReplayEvent getReplayEventById(Long id)
	{
        return this.getHibernateTemplate().get(HibernateReplayEvent.class,id);
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
        return this.getHibernateTemplate().get(HibernateReplayAudit.class,id);

	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getReplayAuditEventsByAuditId(java.lang.Long)
	 */
	@Override
	public List<HibernateReplayAuditEvent> getReplayAuditEventsByAuditId(Long id)
	{
        return getHibernateTemplate().execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<HibernateReplayAuditEvent> criteriaQuery = builder.createQuery(HibernateReplayAuditEvent.class);

            Root<HibernateReplayAuditEvent> root = criteriaQuery.from(HibernateReplayAuditEvent.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("id").get("replayAuditId"),id));

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]));

            Query<HibernateReplayAuditEvent> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayDao#getNumberReplayAuditEventsByAuditId(java.lang.Long)
	 */
	@Override
	public Long getNumberReplayAuditEventsByAuditId(final Long id) 
	{
        return getHibernateTemplate().execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

            Root<HibernateReplayAuditEvent> root = criteriaQuery.from(HibernateReplayAuditEvent.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("id").get("replayAuditId"),id));

            criteriaQuery.select(builder.count(root))
                .where(predicates.toArray(new Predicate[predicates.size()]));

            Query<Long> query = session.createQuery(criteriaQuery);
            List<Long> rowCountList = query.getResultList();
            if (!rowCountList.isEmpty())
            {
                return rowCountList.get(0);
            }
            return Long.valueOf(0);
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
		return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ReplayEvent> criteriaQuery = builder.createQuery(ReplayEvent.class);
            Root<HibernateReplayEvent> root = criteriaQuery.from(HibernateReplayEvent.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("harvestedDateTime"),0));

            if(this.isHarvestQueryOrdered) {
                criteriaQuery.orderBy(
                    builder.asc(root.get("timestamp")));
            }

            Query<ReplayEvent> query = session.createQuery(criteriaQuery);
            query.setMaxResults(housekeepingBatchSize);
            return query.getResultList();
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
                    query.setParameter(NOW, System.currentTimeMillis());
                    query.executeUpdate();
                }

                return null;
            }
        });
    }

    @Override
    public void setHarvestQueryOrdered(boolean isHarvestQueryOrdered) {
        this.isHarvestQueryOrdered = isHarvestQueryOrdered;
    }
}
