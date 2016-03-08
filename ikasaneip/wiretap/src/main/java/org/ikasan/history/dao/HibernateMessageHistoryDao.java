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
package org.ikasan.history.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.wiretap.model.ArrayListPagedSearchResult;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Hibernate implementation of the <code>MessageHistoryDao</code>
 *
 * @author Ikasan Development Team
 *
 */
public class HibernateMessageHistoryDao extends HibernateDaoSupport implements MessageHistoryDao
{

    private static final String EXPIRY = "expiry";

    /** Hibernate query used for housekeeping expired wiretap events, note the Object name, not the table name since thats mapped by HBM config */
    private static final String HOUSEKEEP_DELETE_QUERY = "delete MessageHistoryFlowEvent w where w.expiry <= :" + EXPIRY;

    @Override
    public void save(MessageHistoryEvent messageHistoryEvent)
    {
        getHibernateTemplate().save(messageHistoryEvent);
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> findMessageHistoryEvents(final int pageNo, final int pageSize, final String orderBy,
                                                                           final boolean orderAscending, final Set<String> moduleNames,
                                                                           final String flowName,
                                                                           final String lifeId, final String relatedLifeId,
                                                                           final Date fromDate, final Date toDate)
    {
        return getHibernateTemplate().execute(new HibernateCallback<PagedSearchResult<MessageHistoryEvent>>()
        {
            public PagedSearchResult<MessageHistoryEvent> doInHibernate(Session session) throws HibernateException
            {
                Criteria dataCriteria = getCriteria(session);
                dataCriteria.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                dataCriteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        dataCriteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        dataCriteria.addOrder(Order.desc(orderBy));
                    }
                }
                List<MessageHistoryEvent> messageHistoryResults = dataCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return new ArrayListPagedSearchResult<>(messageHistoryResults, firstResult, rowCount);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param session the hibernate Session
             * @return a Criteria based on the provided search parameters
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);

                if (restrictionExists(moduleNames))
                {
                    criteria.add(Restrictions.in("moduleName", moduleNames));
                }
                if (restrictionExists(flowName))
                {
                    criteria.add(Restrictions.eq("flowName", flowName));
                }
                if (restrictionExists(lifeId))
                {
                    criteria.add(Restrictions.eq("lifeIdentifier", lifeId));
                }
                if (restrictionExists(relatedLifeId))
                {
                    criteria.add(Restrictions.eq("relatedLifeIdentifier", relatedLifeId));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.ge("startTime", fromDate.getTime()));
                }
                if (restrictionExists(toDate))
                {
                    criteria.add(Restrictions.le("endTime", toDate.getTime()));
                }
                return criteria;
            }
        });
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> getMessageHistoryEvent(final int pageNo, final int pageSize, final String orderBy,
            final boolean orderAscending, final String lifeId, final String relatedLifeId)
    {
        return getHibernateTemplate().execute(new HibernateCallback<PagedSearchResult<MessageHistoryEvent>>()
        {
            public PagedSearchResult<MessageHistoryEvent> doInHibernate(Session session) throws HibernateException
            {
                Criteria dataCriteria = getCriteria(session);
                dataCriteria.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                dataCriteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        dataCriteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        dataCriteria.addOrder(Order.desc(orderBy));
                    }
                }
                List<MessageHistoryEvent> messageHistoryResults = dataCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return new ArrayListPagedSearchResult<>(messageHistoryResults, firstResult, rowCount);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param session the hibernate Session
             * @return a Criteria based on the provided search parameters
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);

                if (restrictionExists(lifeId) && !restrictionExists(relatedLifeId))
                {
                    criteria.add(Restrictions.eq("lifeIdentifier", lifeId));
                }
                if (restrictionExists(relatedLifeId))
                {
                    criteria.add(Restrictions.or(
                            Restrictions.eq("lifeIdentifier", lifeId),
                            Restrictions.eq("relatedLifeIdentifier", relatedLifeId)));
                }
                return criteria;
            }
        });
    }

    @Override
    public void deleteAllExpired()
    {
        getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                // rely on the DELETE CASCADE option on the FK to remove from the child tables as well
                String delete = "DELETE FROM MessageHistory WHERE Expiry <= " + System.currentTimeMillis();
                session.createSQLQuery(delete).executeUpdate();
                return null;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean housekeepablesExist()
    {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(MessageHistoryEvent.class);
                criteria.add(Restrictions.le("expiry", System.currentTimeMillis()));
                criteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                logger.info(rowCount+", MessageHistory housekeepables exist");
                return rowCount > 0;
            }
        });
    }

    /**
     * Check to see if the restriction exists
     *
     * @param restrictionValue - The value to check
     * @return - true if the restriction exists for that value, else false
     */
    static boolean restrictionExists(Object restrictionValue)
    {
        // If the value passed in is not null and not an empty string then it
        // can have a restriction applied
        return restrictionValue != null && !"".equals(restrictionValue);
    }
}
