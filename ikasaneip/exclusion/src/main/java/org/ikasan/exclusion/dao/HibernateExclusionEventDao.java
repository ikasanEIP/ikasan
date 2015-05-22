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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.exclusion.model.BlackListEvent;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate implementation of the ExclusionEventeDao.
 * @author Ikasan Development Team
 */
public class HibernateExclusionEventDao extends HibernateDaoSupport
        implements ExclusionEventDao<String,ExclusionEvent>
{
    /** default batch size */
    private static Integer housekeepingBatchSize = Integer.valueOf(100);

    /** batch delete statement */
    private static final String BATCHED_HOUSEKEEP_QUERY = "delete ExclusionEvent s where s.identifier in (:event_ids)";

    /** batch delete statement */
    private static final String DELETE_QUERY = "delete ExclusionEvent s where s.moduleName = :moduleName and s.flowName = :flowName and s.identifier = :identifier";


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
        return null; //TODO FIXME
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
                    query.setParameterList("event_ids", housekeepableBatch);
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

                Criteria criteria = session.createCriteria(BlackListEvent.class);
                criteria.add(Restrictions.lt("expiry", System.currentTimeMillis()));
                criteria.setMaxResults(housekeepingBatchSize);

                for (Object exclusionEventObj : criteria.list())
                {
                    BlackListEvent blackListEvent = (BlackListEvent)exclusionEventObj;
                    ids.add(blackListEvent.getIdentifier());
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
                Criteria criteria = session.createCriteria(BlackListEvent.class);
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
}
