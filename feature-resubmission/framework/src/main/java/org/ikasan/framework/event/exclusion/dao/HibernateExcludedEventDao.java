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
package org.ikasan.framework.event.exclusion.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author The Ikasan Development Team
 *
 */
public class HibernateExcludedEventDao extends HibernateDaoSupport implements ExcludedEventDao{

	
	private Logger logger = Logger.getLogger(HibernateExcludedEventDao.class);
	
	/* (non-Javadoc)
	 * @see org.ItemDao#save(org.Item)
	 */
	public void save(ExcludedEvent excludedEvent) {
		logger.info("saving ["+excludedEvent+"]");
		getHibernateTemplate().save(excludedEvent);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.framework.event.exclusion.dao.ExcludedEventDao#load(java.lang.Long)
	 */
	public ExcludedEvent load(Long excludedEventId) {

		return (ExcludedEvent) getHibernateTemplate().get(ExcludedEvent.class, excludedEventId);
	}


	@SuppressWarnings("unchecked")
	public PagedSearchResult<ExcludedEvent> findExcludedEvents(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,final String moduleName, final String flowName) {
		return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(ExcludedEvent.class);

 
                criteria.setMaxResults(pageSize);
                int firstResult = (pageNo*pageSize);
				criteria.setFirstResult(firstResult);
				if (orderBy!=null){
					if(orderAscending){
						criteria.addOrder(Order.asc(orderBy));
					} else{
						 criteria.addOrder(Order.desc(orderBy));
					}
				}
				if (moduleName!=null){
					criteria.add(Restrictions.eq("moduleName", moduleName));
				}
				if (flowName!=null){
					criteria.add(Restrictions.eq("flowName", flowName));
				}
				
				
                List<ExcludedEvent> results = criteria.list();
                criteria.setProjection(Projections.rowCount());
                Integer rowCount = 0;
                List<Integer> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new ArrayListPagedSearchResult<ExcludedEvent>(results, firstResult, rowCount);
            }
        });
	}


	public ExcludedEvent getExcludedEvent(long excludedEventId) {
		
		return (ExcludedEvent) getHibernateTemplate().get(ExcludedEvent.class, excludedEventId);
	}


	public void delete(ExcludedEvent excludedEvent) {
		getHibernateTemplate().delete(excludedEvent);
	}


	public ExcludedEvent getExcludedEvent(String eventId, boolean mutable) {
		ExcludedEvent result = null;
		List excludedEvents = getHibernateTemplate().find("from ExcludedEvent e where e.event.id = ?", eventId);
		
		if (excludedEvents.size()>0){
			result = (ExcludedEvent) excludedEvents.get(0);
		}
		if (!mutable){
			getHibernateTemplate().evict(result);
			
			//also clear persistenceIds from payloads
			for (Payload payload : result.getEvent().getPayloads()){
				((DefaultPayload)payload).setPersistenceId(null);
			}
		}
		return result;
	}

}
