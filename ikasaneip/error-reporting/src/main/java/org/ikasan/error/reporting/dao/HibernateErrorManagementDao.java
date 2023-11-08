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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.error.reporting.dao.constants.ErrorManagementDaoConstants;
import org.ikasan.error.reporting.model.*;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ikasan Development Team
 */
public class HibernateErrorManagementDao extends HibernateDaoSupport implements ErrorManagementDao
{
    private static Logger logger = LoggerFactory.getLogger(HibernateErrorManagementDao.class);

    public static final String EVENT_IDS = "eventIds";

    public static final String NOW = "now";

    public static final String ERROR_OCCURRENCES_TO_DELETE_QUERY = "select uri from ErrorOccurrenceImpl eo " +
            " where eo.expiry < :" + NOW;

    public static final String ERROR_OCCURRENCE_DELETE_QUERY = "delete ErrorOccurrenceImpl eo " +
            " where eo.uri in(:" + EVENT_IDS + ")";

    public static final String UPDATE_HARVESTED_QUERY = "update ErrorOccurrenceImpl w set w.harvestedDateTime = :" + NOW + ", w.harvested = 1" +
        " where w.id in(:" + EVENT_IDS + ")";

    private boolean isHarvestQueryOrdered = false;


	@Override
	public void saveErrorOccurrence(ErrorOccurrence errorOccurrence)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrence);
	}

    /* (non-Javadoc)
     * @see org.ikasan.error.reporting.dao.ErrorManagementDao#deleteErrorOccurence(org.ikasan.error.reporting.window.ErrorOccurrence)
     */
    @Override
    public void deleteErrorOccurence(ErrorOccurrence errorOccurrence)
    {
        this.getHibernateTemplate().delete(errorOccurrence);
    }

    /* (non-Javadoc)
     * @see org.ikasan.error.reporting.dao.ErrorManagementDao#findErrorOccurrences(java.util.List)
     */
    @Override
    public List<ErrorOccurrence> findErrorOccurrences(List<String> errorUris)
    {
        return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ErrorOccurrence> criteriaQuery = builder.createQuery(ErrorOccurrence.class);
            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            criteriaQuery
                .select(root)
                .where(root.get("uri").in(errorUris))
                .orderBy(builder.desc(root.get("timestamp")));

            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
            query.setFirstResult(0);
            query.setMaxResults(2000);
            return query.getResultList();

        });
    }


    /* (non-Javadoc)
     * @see org.ikasan.error.reporting.dao.ErrorManagementDao#findErrorOccurrenceActions(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
     */
    @Override
    public List<ErrorOccurrence> findActionErrorOccurrences(
            List<String> moduleName, List<String> flowName,
            List<String> flowElementname, Date startDate, Date endDate)
    {
        return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<ErrorOccurrence> criteriaQuery = builder.createQuery(ErrorOccurrence.class);

            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null && moduleName.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleName));
            }

            if(flowName != null && flowName.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowName));
            }

            if(flowElementname != null && flowElementname.size() > 0)
            {
                predicates.add(root.get("flowElementName").in(flowElementname));
            }

            if(startDate != null)
            {
                predicates.add( builder.greaterThan(root.get("userActionTimestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("userActionTimestamp"),endDate.getTime()));
            }

            predicates.add(root.get("userAction").isNotNull());

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(builder.desc(root.get("userActionTimestamp")));


            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
            query.setFirstResult(0);
            query.setMaxResults(2000);
            List<ErrorOccurrence> rowList = query.getResultList();

            return rowList;

        });
    }

    /* (non-Javadoc)
     * @see org.ikasan.error.reporting.dao.ErrorManagementDao#close(java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void close(final List<String> uris, final String user)
    {
        this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(ErrorManagementDaoConstants.CLOSE_ERROR_OCCURRENCE);
                query.setParameterList(ErrorManagementDaoConstants.ERROR_URIS, uris);
                query.setParameter(ErrorManagementDaoConstants.USER, user);
                query.setParameter(ErrorManagementDaoConstants.TIMESTAMP, System.currentTimeMillis());
                logger.debug("Query: " + query);
                query.executeUpdate();
                return null;
            }
        });
    }

    /* (non-Javadoc)
     * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getNumberOfModuleErrors(java.lang.String)
     */
    @Override
    public Long getNumberOfModuleErrors(String moduleName, boolean excluded, boolean actioned, Date startDate, Date endDate)
    {
        return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null)
            {
                predicates.add(builder.equal(root.get("moduleName"),moduleName));
            }
            if(startDate != null)
            {
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }
            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }
            if(excluded)
            {
                predicates.add(builder.equal(root.get("action"),"ExcludeEvent"));
            }
            if (actioned)
            {
                predicates.add(root.get("userAction").isNotNull());
            }
            else{
                predicates.add(root.get("userAction").isNull());
            }

            criteriaQuery.select(builder.count(root))
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(builder.desc(root.get("userActionTimestamp")));

            Query<Long> query = session.createQuery(criteriaQuery);

            List<Long> rowCountList = query.getResultList();
            if (!rowCountList.isEmpty())
            {
                return rowCountList.get(0);
            }
            return Long.valueOf(0);

        });
    }

	@Override
	public void housekeep(final Integer numToHousekeep)
	{
		getHibernateTemplate().execute(new HibernateCallback<Object>()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				Query query = session.createQuery(ERROR_OCCURRENCES_TO_DELETE_QUERY);
				query.setLong(NOW, System.currentTimeMillis());
				query.setMaxResults(numToHousekeep);

				List<Long> errorUris = (List<Long>)query.list();

				if(errorUris.size() > 0)
				{

					query = session.createQuery(ERROR_OCCURRENCE_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, errorUris);
					query.executeUpdate();
				}

				return null;
			}
		});
	}

	@Override
	public List<ErrorOccurrence> getHarvestableRecords(final int harvestingBatchSize)
	{
	    return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ErrorOccurrence> criteriaQuery = builder.createQuery(ErrorOccurrence.class);
            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("harvestedDateTime"),0));

            if(this.isHarvestQueryOrdered) {
                criteriaQuery.orderBy(builder.asc(root.get("timestamp")));
            }

            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
            query.setMaxResults(harvestingBatchSize);
            return query.getResultList();
        });
	}

    public void updateAsHarvested(List<ErrorOccurrence> events)
    {
        getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {

                List<String> wiretapEventIds = new ArrayList<String>();

                for(ErrorOccurrence event: events)
                {
                    wiretapEventIds.add(event.getUri());
                }

                List<List<String>> partitionedIds = Lists.partition(wiretapEventIds, 300);

                for(List<String> eventIds: partitionedIds)
                {
                    Query query = session.createQuery(UPDATE_HARVESTED_QUERY);
                    query.setParameter(NOW, System.currentTimeMillis());
                    query.setParameterList(EVENT_IDS, eventIds);
                    query.executeUpdate();
                }

                return null;
            }
        });
    }


    @Override
    public void setHarvestQueryOrdered(boolean isHarvestQueryOrdered) {
        this.isHarvestQueryOrdered = isHarvestQueryOrdered;
    }
}
