/*
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
package org.ikasan.framework.event.exclusion.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
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

	/* (non-Javadoc)
	 * @see org.ItemDao#save(org.Item)
	 */
	public void save(ExcludedEvent excldudedEvent) {
		getHibernateTemplate().save(excldudedEvent);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.framework.event.exclusion.dao.ExcludedEventDao#load(java.lang.Long)
	 */
	public ExcludedEvent load(Long excludedEventId) {

		return (ExcludedEvent) getHibernateTemplate().get(ExcludedEvent.class, excludedEventId);
	}


	@SuppressWarnings("unchecked")
	public PagedSearchResult<ExcludedEvent> findExcludedEvents(final int pageNo, final int pageSize) {
		return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(ExcludedEvent.class);

 
                criteria.setMaxResults(pageSize);
                int firstResult = (pageNo*pageSize);
				criteria.setFirstResult(firstResult);
                //criteria.addOrder(Order.desc("id"));
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

}
