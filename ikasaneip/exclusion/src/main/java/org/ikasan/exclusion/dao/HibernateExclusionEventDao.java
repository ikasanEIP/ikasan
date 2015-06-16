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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.List;

/**
 * Hibernate implementation of the ExclusionEventDao.
 * @author Ikasan Development Team
 */
public class HibernateExclusionEventDao extends HibernateDaoSupport
        implements ExclusionEventDao<String,ExclusionEvent>
{
    /** batch delete statement */
    private static final String DELETE_QUERY = "delete ExclusionEvent s where s.moduleName = :moduleName and s.flowName = :flowName and s.identifier = :identifier";
    private static final String DELETE_QUERY_BY_ERROR_URI = "delete ExclusionEvent s where s.errorUri = :errorUri";


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
        DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEvent.class);
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
	 * @see org.ikasan.exclusion.dao.ExclusionEventDao#findAll()
	 */
	@Override
	public List<ExclusionEvent> findAll()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ExclusionEvent.class);
		criteria.addOrder(Order.desc("timestamp"));		
		
        return (List<ExclusionEvent>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.exclusion.dao.ExclusionEventDao#delete(java.lang.String)
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
}
