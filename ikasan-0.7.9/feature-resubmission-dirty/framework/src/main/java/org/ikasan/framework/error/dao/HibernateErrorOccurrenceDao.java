/*
 * $Id
 * $URL
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
package org.ikasan.framework.error.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Ikasan Development Team
 *
 */
public class HibernateErrorOccurrenceDao extends HibernateDaoSupport implements ErrorOccurrenceDao {

    /** Query used for housekeeping */
    private static final String HOUSEKEEP_QUERY = "delete ErrorOccurrence e where e.expiry <= ?";
    
    /** Query used for finding all ErrorOccurrences for the specifiend event */
    private static final String FOR_EVENT_QUERY = "from ErrorOccurrence e where e.eventId = ?";
	
    private static final Logger logger = Logger.getLogger(HibernateErrorOccurrenceDao.class);
    /* (non-Javadoc)
	 * @see org.ikasan.framework.error.dao.ErrorOccurrenceDao#save(org.ikasan.framework.error.model.ErrorOccurrence)
	 */
	public void save(ErrorOccurrence errorOccurrence) {
		logger.info("saving ["+errorOccurrence+"]");
		getHibernateTemplate().save(errorOccurrence);
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.dao.ErrorOccurrenceDao#getErrorOccurrence(java.lang.Long)
	 */
	public ErrorOccurrence getErrorOccurrence(Long id) {
		return (ErrorOccurrence) getHibernateTemplate().get(ErrorOccurrence.class, id);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.dao.ErrorOccurrenceDao#findErrorOccurrences()
	 */
	@SuppressWarnings("unchecked")
	public PagedSearchResult<ErrorOccurrence> findErrorOccurrences(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,final String moduleName, final String flowName) {
		
        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(ErrorOccurrence.class);

 
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
                List<ErrorOccurrence> wiretapResults = criteria.list();
                criteria.setProjection(Projections.rowCount());
                Integer rowCount = 0;
                List<Integer> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new ArrayListPagedSearchResult<ErrorOccurrence>(wiretapResults, firstResult, rowCount);
            }
        });
	}

    /* (non-Javadoc)
     * @see org.ikasan.framework.error.dao.ErrorOccurrenceDao#deleteAllExpired()
     */
    public void deleteAllExpired()
    {
        getHibernateTemplate().bulkUpdate(HOUSEKEEP_QUERY, new Date());
    }

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.dao.ErrorOccurrenceDao#getErrorOccurrences(java.lang.String)
	 */
	public List<ErrorOccurrence> getErrorOccurrences(String eventId) {
		return getHibernateTemplate().find(FOR_EVENT_QUERY, eventId);
	}


}
