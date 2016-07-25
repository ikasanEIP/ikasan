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
package org.ikasan.error.reporting.dao;

import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.*;

/**
 * Hibernate specific implementation of the ErrorReportingServiceDao.
 * @author Ikasan Development Team
 */
public class HibernateErrorReportingServiceDao extends HibernateDaoSupport
        implements ErrorReportingServiceDao<ErrorOccurrence<byte[]>, String>
{
    /** default batch size */
    private static Integer housekeepingBatchSize = Integer.valueOf(100);

    /** batch delete statement */
    private static final String BATCHED_HOUSEKEEP_QUERY = "delete ErrorOccurrence s where s.uri in (:event_uris)";

    @Override
    public ErrorOccurrence find(String uri)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
        criteria.add(Restrictions.eq("uri", uri));

        List<ErrorOccurrence> results = (List<ErrorOccurrence>)this.getHibernateTemplate().findByCriteria(criteria);
        if(results == null || results.size() == 0)
        {
            return null;
        }

        return results.get(0);

    }

	@Override
	public Map<String, ErrorOccurrence<byte[]>> find(List<String> uris)
	{
		Map<String, ErrorOccurrence<byte[]>> results = new HashMap<String, ErrorOccurrence<byte[]>>();

		List<List<String>> partitions = Lists.partition(uris, 300);

		for(List<String> partition: partitions)
		{
			DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
			criteria.add(Restrictions.in("uri", partition));

			List<ErrorOccurrence> queryResults = (List<ErrorOccurrence>)this.getHibernateTemplate().findByCriteria(criteria);

			for(ErrorOccurrence errorOccurrence: queryResults)
			{
				results.put(errorOccurrence.getUri(), errorOccurrence);
			}
		}

		return results;
	}

	/* (non-Javadoc)
     * @see org.ikasan.error.reporting.dao.ErrorReportingServiceDao#find(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
	public List<ErrorOccurrence<byte[]>> find(List<String> moduleName, List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate, int size)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
		
		if(moduleName != null && moduleName.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleName));
		}
		
		if(flowName != null && flowName.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowName));
		}
		
		if(flowElementname != null && flowElementname.size() > 0)
		{
			criteria.add(Restrictions.in("flowElementName", flowElementname));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}
		
		criteria.add(Restrictions.isNull("userAction"));
		criteria.addOrder(Order.desc("timestamp"));

        return (List<ErrorOccurrence<byte[]>>)this.getHibernateTemplate().findByCriteria(criteria, 0, size);
	}
    
    @Override
    public void save(ErrorOccurrence errorOccurrence)
    {
        this.getHibernateTemplate().saveOrUpdate(errorOccurrence);
    }

    @Override
    public void deleteExpired()
    {
        while(housekeepablesExist()){
            final List<String> housekeepableBatch = getHousekeepableBatch();

            getHibernateTemplate().execute(new HibernateCallback()
            {
                public Object doInHibernate(Session session) throws HibernateException
                {

                    Query query = session.createQuery(BATCHED_HOUSEKEEP_QUERY);
                    query.setParameterList("event_uris", housekeepableBatch);
                    query.executeUpdate();
                    return null;
                }
            });
        }
    }

    /**
     * Identifies a batch (List of Ids) of housekeepable items
     *
     * @return List of ids
     */
    @SuppressWarnings("unchecked")
    private List<String> getHousekeepableBatch()
    {
        return (List<String>) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                List<String> ids = new ArrayList<String>();

                Criteria criteria = session.createCriteria(ErrorOccurrence.class);
                criteria.add(Restrictions.lt("expiry", System.currentTimeMillis()));
                criteria.setMaxResults(housekeepingBatchSize);

                for (Object errorOccurrenceObj : criteria.list())
                {
                    ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorOccurrenceObj;
                    ids.add(errorOccurrence.getUri());
                }

                return ids;
            }
        });
    }

    private boolean housekeepablesExist() {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(ErrorOccurrence.class);
                criteria.add(Restrictions.lt("expiry", System.currentTimeMillis()));
                criteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new Boolean(rowCount>0);

            }
        });
    }

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorReportingServiceDao#rowCount(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long rowCount(final List<String> moduleName, final List<String> flowName,
			final List<String> flowElementname, final Date startDate, final Date endDate)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
			
				Criteria criteria = session.createCriteria(ErrorOccurrence.class);
				
				if(moduleName != null && moduleName.size() > 0)
				{
					criteria.add(Restrictions.in("moduleName", moduleName));
				}
				
				if(flowName != null && flowName.size() > 0)
				{
					criteria.add(Restrictions.in("flowName", flowName));
				}
				
				if(flowElementname != null && flowElementname.size() > 0)
				{
					criteria.add(Restrictions.in("flowElementName", flowElementname));
				}
				
				if(startDate != null)
				{
					criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
				}
				
				if(endDate != null)
				{
					criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
				}
				
				criteria.add(Restrictions.isNull("userAction"));
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
	 * @see org.ikasan.error.reporting.dao.ErrorReportingServiceDao#find(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<ErrorOccurrence<byte[]>> find(List<String> moduleName,
			List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate, String action, String exceptionClass,
			int size)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
		
		if(moduleName != null && moduleName.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleName));
		}
		
		if(flowName != null && flowName.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowName));
		}
		
		if(flowElementname != null && flowElementname.size() > 0)
		{
			criteria.add(Restrictions.in("flowElementName", flowElementname));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}
		
		if(exceptionClass != null)
		{
			criteria.add(Restrictions.eq("exceptionClass", exceptionClass));
		}
		
		if(exceptionClass != null)
		{
			criteria.add(Restrictions.eq("action", action));
		}
		
		criteria.add(Restrictions.isNull("userAction"));
		criteria.addOrder(Order.desc("timestamp"));

        return (List<ErrorOccurrence<byte[]>>)this.getHibernateTemplate().findByCriteria(criteria, 0, size);
	}
}
