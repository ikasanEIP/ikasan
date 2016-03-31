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

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateReplayDao extends HibernateDaoSupport implements ReplayDao
{
	/* (non-Javadoc)
	 * @see org.ikasan.replay.dao.ReplayDao#getReplayAudits(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ReplayAudit> getReplayAudits(List<String> moduleNames, List<String> flowNames,
			String eventId, String user, Date startDate, Date endDate) 
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ReplayAudit.class);
		
		if(user != null && user.length() > 0)
		{
			criteria.add(Restrictions.eq("user", user));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}
				
		if((moduleNames != null && moduleNames.size() > 0) ||
				(flowNames != null && flowNames.size() > 0) ||
				(eventId != null && eventId.length() > 0))
		{
			DetachedCriteria nestedCriteria = criteria.createCriteria("replayAuditEvents").createCriteria("replayEvent");
			
			if(moduleNames != null && moduleNames.size() > 0)
			{
				nestedCriteria
					.add(Restrictions.in("moduleName", moduleNames));
			}
			
			if(flowNames != null && flowNames.size() > 0)
			{
				nestedCriteria
					.add(Restrictions.in("flowName", flowNames));
			}
			
			if (eventId != null && eventId.length() > 0)
		    {
				nestedCriteria
					.add(Restrictions.eq("eventId", eventId));
		    }
		}	
		
		criteria.addOrder(Order.desc("timestamp"));	
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return (List<ReplayAudit>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.replay.dao.ReplayDao#saveOrUpdate(org.ikasan.replay.model.ReplayEvent)
	 */
	@Override
	public void saveOrUpdate(ReplayEvent replayEvent) 
	{
		this.getHibernateTemplate().saveOrUpdate(replayEvent);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.replay.dao.ReplayDao#getReplayEvents(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ReplayEvent> getReplayEvents(String moduleName,
			String flowName, Date startDate, Date endDate) 
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ReplayEvent.class);
		
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
		
		return (List<ReplayEvent>)this.getHibernateTemplate().findByCriteria(criteria);
	}
	
	

	/* (non-Javadoc)
	 * @see org.ikasan.replay.dao.ReplayDao#getReplayEvents(java.util.List, java.util.List, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ReplayEvent> getReplayEvents(List<String> moduleNames,
			List<String> flowNames, String eventId,
			Date fromDate, Date toDate) 
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ReplayEvent.class);
		
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
		 
		
		criteria.addOrder(Order.desc("timestamp"));	
		
		return (List<ReplayEvent>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.replay.dao.ReplayDao#saveOrUpdate(org.ikasan.replay.model.ReplayAudit)
	 */
	@Override
	public void saveOrUpdate(ReplayAudit replayAudit) 
	{
		this.getHibernateTemplate().saveOrUpdate(replayAudit);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.replay.dao.ReplayDao#saveOrUpdate(org.ikasan.replay.model.ReplayAuditEvent)
	 */
	@Override
	public void saveOrUpdate(ReplayAuditEvent replayAuditEvent) 
	{
		this.getHibernateTemplate().saveOrUpdate(replayAuditEvent);
	}

	
}
