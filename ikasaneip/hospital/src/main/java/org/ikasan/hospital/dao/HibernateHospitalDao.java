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
package org.ikasan.hospital.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateHospitalDao extends HibernateDaoSupport implements HospitalDao
{
	public static final Long THIRTY_DAYS = 30 * 24 * 60 * 1000L;

	public static final String EVENT_IDS = "eventIds";
	public static final String NOW = "now";

	public static final String EXCLUSION_EVENT_ACTIONS_TO_DELETE_QUERY = "select errorUri from ExclusionEventAction eo " +
			" where eo.timestamp < :" + NOW;

	public static final String EXCLUSION_EVENT_ACTIONS_DELETE_QUERY = "delete ExclusionEventAction eo " +
			" where eo.errorUri in(:" + EVENT_IDS + ")";

	private Integer transactionBatchSize;
	private Integer housekeepingBatchSize;

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#saveOrUpdate(org.ikasan.hospital.window.ExclusionEventAction)
	 */
	@Override
	public void saveOrUpdate(ExclusionEventAction exclusionEventAction)
	{
		getHibernateTemplate().save(exclusionEventAction);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#getExclusionEventActionByErrorUri(java.lang.String)
	 */
	@Override
	public ExclusionEventAction getExclusionEventActionByErrorUri(
			String errorUri)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventAction.class);
        criteria.add(Restrictions.eq("errorUri", errorUri));
        ExclusionEventAction excludedEventAction = (ExclusionEventAction) DataAccessUtils
        		.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return excludedEventAction;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#getActionedExclusions(java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExclusionEventAction> getActionedExclusions(
			List<String> moduleName, List<String> flowName, Date startDate,
			Date endDate, int size)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventAction.class);
		
		if(moduleName != null && moduleName.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleName));
		}
		
		if(flowName != null && flowName.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowName));
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
		
		return (List<ExclusionEventAction>) this.getHibernateTemplate().findByCriteria(criteria, 0, size);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#actionedExclusionsRowCount(java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public Long actionedExclusionsRowCount(List<String> moduleName, List<String> flowName, Date startDate, Date endDate)
	{

		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventAction.class);
		
		if(moduleName != null && moduleName.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleName));
		}
		
		if(flowName != null && flowName.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowName));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}

		criteria.setProjection(Projections.projectionList()
                .add(Projections.count("moduleName")));

		return (Long) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getNumberOfModuleActionedExclusions(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getNumberOfModuleActionedExclusions(String moduleName,
			Date startDate, Date endDate)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEventAction.class);
		
		if(moduleName != null)
		{
			criteria.add(Restrictions.eq("moduleName", moduleName));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}
		
		criteria.setProjection(Projections.projectionList()
		                    .add(Projections.count("moduleName")));
		
		return (Long) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public void housekeep()
	{
		getHibernateTemplate().execute(new HibernateCallback<Object>()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				int numHousekept = 0;

				while(numHousekept < transactionBatchSize)
				{
					numHousekept += housekeepingBatchSize;
					
					Query query = session.createQuery(EXCLUSION_EVENT_ACTIONS_TO_DELETE_QUERY);
					query.setLong(NOW, System.currentTimeMillis() - THIRTY_DAYS);
					query.setMaxResults(housekeepingBatchSize);

					List<Long> errorUris = (List<Long>) query.list();

					if (errorUris.size() > 0) {
						query = session.createQuery(EXCLUSION_EVENT_ACTIONS_DELETE_QUERY);
						query.setParameterList(EVENT_IDS, errorUris);
						query.executeUpdate();
					}
				}

				return null;
			}
		});
	}

	@Override
	public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
	{
		this.housekeepingBatchSize = housekeepingBatchSize;
	}

	@Override
	public void setTransactionBatchSize(Integer transactionBatchSize)
	{
		this.transactionBatchSize = transactionBatchSize;
	}

}
