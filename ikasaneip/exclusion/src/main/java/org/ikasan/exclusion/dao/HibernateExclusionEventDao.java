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
package org.ikasan.exclusion.dao;

import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.exclusion.model.ExclusionEventImpl;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of the ExclusionEventDao.
 * @author Ikasan Development Team
 */
public class HibernateExclusionEventDao extends HibernateDaoSupport
        implements ExclusionEventDao<String, ExclusionEvent>
{
    public static final String EVENT_IDS = "eventIds";

    /** batch delete statement */
    private static final String DELETE_QUERY = "delete ExclusionEventImpl s where s.moduleName = :moduleName and s.flowName = :flowName and s.identifier = :identifier";
    private static final String DELETE_QUERY_BY_ERROR_URI = "delete ExclusionEventImpl s where s.errorUri = :errorUri";

    public static final String UPDATE_HARVESTED_QUERY = "update ExclusionEventImpl w set w.harvested = 1 " +
        " where w.id in(:" + EVENT_IDS + ")";


    @Override
    public void save(ExclusionEvent exclusionEvent)
    {
        this.getHibernateTemplate().saveOrUpdate(exclusionEvent);
    }

    @Override
    public void delete(final String moduleName, final String flowName, final String identifier)
    {
        getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                Query query = session.createQuery(DELETE_QUERY);
                query.setParameter("moduleName", moduleName);
                query.setParameter("flowName", flowName);
                query.setParameter("identifier", identifier);
                query.executeUpdate();
                return null;
        }
        });
    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String identifier)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventImpl.class);
        criteria.add(Restrictions.eq("moduleName", moduleName));
        criteria.add(Restrictions.eq("flowName", flowName));
        criteria.add(Restrictions.eq("identifier", identifier));

        List<ExclusionEvent> results = (List<ExclusionEvent>)this.getHibernateTemplate().findByCriteria(criteria);
        if(results == null || results.size() == 0)
        {
            return null;
        }

        return results.get(0);
    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#findAll()
	 */
	@Override
	public List<ExclusionEvent> findAll()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventImpl.class);
		criteria.addOrder(Order.desc("timestamp"));		
		
        return (List<ExclusionEvent>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#delete(java.lang.String)
	 */
	@Override
	public void delete(final String errorUri)
	{
		getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                Query query = session.createQuery(DELETE_QUERY_BY_ERROR_URI);
                query.setParameter("errorUri", errorUri);
                query.executeUpdate();
                return null;
        }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#find(java.util.List, java.util.List, java.util.Date, java.util.Date, java.lang.Object)
	 */
	@Override
	public List<ExclusionEvent> find(List<String> moduleName,
			List<String> flowName, Date startDate, Date endDate,
			String identifier, int size)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventImpl.class);
		
		if(moduleName != null && moduleName.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleName));
		}
		
		if(flowName != null && flowName.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowName));
		}
		
		if(identifier != null && identifier.length() > 0)
		{
			criteria.add(Restrictions.eq("identifier", identifier));
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

		if(size > 0)
        {
            return (List<ExclusionEvent>) this.getHibernateTemplate().findByCriteria(criteria, 0, size);
        }
        else
        {
            return (List<ExclusionEvent>) this.getHibernateTemplate().findByCriteria(criteria);
        }
	}

    /* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorReportingServiceDao#rowCount(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, int)
	 */
    @SuppressWarnings("unchecked")
    @Override
    public Long rowCount(final List<String> moduleName,
                         final List<String> flowName, final Date startDate, final Date endDate,
                         final String identifier)
    {
        return (Long) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                Criteria criteria = session.createCriteria(ExclusionEventImpl.class);

                if(moduleName != null && moduleName.size() > 0)
                {
                    criteria.add(Restrictions.in("moduleName", moduleName));
                }

                if(flowName != null && flowName.size() > 0)
                {
                    criteria.add(Restrictions.in("flowName", flowName));
                }

                if(identifier != null && identifier.length() > 0)
                {
                    criteria.add(Restrictions.eq("identifier", identifier));
                }

                if(startDate != null)
                {
                    criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
                }

                if(endDate != null)
                {
                    criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
                }
                
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

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#find(java.lang.String)
	 */
	@Override
	public ExclusionEvent find(String errorUri)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventImpl.class);
        criteria.add(Restrictions.eq("errorUri", errorUri));

        return (ExclusionEvent)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

    public List<ExclusionEvent> getHarvestableRecords(final int housekeepingBatchSize)
    {
        return (List<ExclusionEvent>) this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(ExclusionEventImpl.class);
                criteria.add(Restrictions.eq("harvested", false));
                criteria.setMaxResults(housekeepingBatchSize);
                criteria.addOrder(Order.asc("timestamp"));

                List<ExclusionEvent> exclusionEvents = criteria.list();

                return exclusionEvents;
            }
        });
    }

    @Override
    public void deleteAllExpired()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsHarvested(List<ExclusionEvent> events)
    {
        getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                List<Long> exclusionEventIds = new ArrayList<Long>();

                for(ExclusionEvent event: events)
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
