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

import com.google.common.collect.Lists;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.exclusion.model.ExclusionEventImpl;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.search.PagedSearchResult;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hibernate implementation of the ExclusionEventDao.
 * @author Ikasan Development Team
 */
public class HibernateExclusionEventDao extends HibernateDaoSupport
        implements ExclusionEventDao<String, ExclusionEvent>
{
    public static final String EVENT_IDS = "eventIds";

    public static final String NOW = "now";

    /** batch delete statement */
    private static final String DELETE_QUERY = "delete ExclusionEventImpl s where s.moduleName = :moduleName and s.flowName = :flowName and s.identifier = :identifier";
    private static final String DELETE_QUERY_BY_ERROR_URI = "delete ExclusionEventImpl s where s.errorUri = :errorUri";

    public static final String UPDATE_HARVESTED_QUERY = "update ExclusionEventImpl w set w.harvestedDateTime = :" + NOW + ", w.harvested = 1" +
        " where w.id in(:" + EVENT_IDS + ")";

    private boolean isHarvestQueryOrdered = false;

    public HibernateExclusionEventDao()
    {
    }

    @Override
    public void save(ExclusionEvent exclusionEvent)
    {
        this.getHibernateTemplate().saveOrUpdate(exclusionEvent);
    }

    @Override
    public void save(List<ExclusionEvent> exclusionEvents)
    {
        exclusionEvents.forEach(exclusionEvent -> this.save(exclusionEvent));
    }

    @Override
    public void delete(final String moduleName, final String flowName, final String identifier)
    {
        getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(DELETE_QUERY);
            query.setParameter("moduleName", moduleName);
            query.setParameter("flowName", flowName);
            query.setParameter("identifier", identifier);
            query.executeUpdate();
            return null;
        });
    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String identifier)
    {

        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ExclusionEvent> criteriaQuery = builder.createQuery(ExclusionEvent.class);
            Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("moduleName"),moduleName));
            predicates.add(builder.equal(root.get("flowName"),flowName));
            predicates.add(builder.equal(root.get("identifier"),identifier));

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
               ;

            Query<ExclusionEvent> query = session.createQuery(criteriaQuery);
            List<ExclusionEvent> result = query.getResultList();
            if(!result.isEmpty()){
                return result.get(0);
            }else {
                return null;
            }
        });

    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#findAll()
	 */
	@Override
	public List<ExclusionEvent> findAll()
	{
		return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ExclusionEvent> criteriaQuery = builder.createQuery(ExclusionEvent.class);
            Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);

            criteriaQuery.select(root)
                .orderBy(
                    builder.desc(root.get("timestamp")));

            Query<ExclusionEvent> query = session.createQuery(criteriaQuery);

            return query.getResultList();
        });
	}

    /* (non-Javadoc)
     * @see org.ikasan.spec.exclusion.ExclusionEventDao#find(int, int, java.lang.String, boolean, java.lang.String, java.lang.String, java.lang.String, java.util.Data, java.util.Data)
     */
    @Override
    public PagedSearchResult<ExclusionEvent> find(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
        final String moduleName, final String flowName, final String componentName, final String identifier, final Date fromDate, final Date untilDate)
    {
        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                CriteriaBuilder builder = session.getCriteriaBuilder();

                CriteriaQuery<ExclusionEvent> criteriaQuery = builder.createQuery(ExclusionEvent.class);
                Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);
                List<Predicate> predicates = getCriteria(builder,root);

                criteriaQuery.select(root)
                    .where(predicates.toArray(new Predicate[predicates.size()]));

                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        criteriaQuery.orderBy(
                            builder.asc(root.get(orderBy)));
                    }
                    else
                    {
                        criteriaQuery.orderBy(
                            builder.desc(root.get(orderBy)));

                    }
                } else {
                    criteriaQuery.orderBy(
                        builder.desc(root.get("timestamp")));
                }


                Query<ExclusionEvent> query = session.createQuery(criteriaQuery);
                query.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                query.setFirstResult(firstResult);
                List<ExclusionEvent> results = query.getResultList();

                Long rowCount = rowCount(session);

                return new ArrayListPagedSearchResult(results, firstResult, rowCount);
            }

            private Long rowCount(Session session){

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Long> metaDataCriteriaQuery = builder.createQuery(Long.class);

                Root<ExclusionEventImpl> root = metaDataCriteriaQuery.from(ExclusionEventImpl.class);
                List<Predicate> predicates = getCriteria(builder,root);

                metaDataCriteriaQuery.select(builder.count(root))
                    .where(predicates.toArray(new Predicate[predicates.size()]));

                Query<Long> metaDataQuery = session.createQuery(metaDataCriteriaQuery);

                List<Long> rowCountList = metaDataQuery.getResultList();
                if (!rowCountList.isEmpty())
                {
                    return rowCountList.get(0);
                }
                return Long.valueOf(0);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param builder
             * @param root
             * @return
             */
            private List<Predicate>  getCriteria(CriteriaBuilder builder,Root<ExclusionEventImpl> root)
            {

                List<Predicate> predicates = new ArrayList<>();

                if(moduleName != null)
                {
                    predicates.add(builder.equal(root.get("moduleName"),moduleName));
                }

                if(flowName != null )
                {
                    predicates.add(builder.equal(root.get("flowName"),flowName));
                }

                if(componentName != null )
                {
                    predicates.add(builder.equal(root.get("flowElementName"),componentName));
                }

                if(identifier != null )
                {
                    predicates.add(builder.equal(root.get("identifier"),identifier));
                }

                if(fromDate != null)
                {
                    predicates.add( builder.greaterThan(root.get("timestamp"),fromDate.getTime()));
                }

                if(untilDate != null)
                {
                    predicates.add( builder.lessThan(root.get("timestamp"),untilDate.getTime()));
                }

                return predicates;
            }
        });
    }

    /* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#delete(java.lang.String)
	 */
	@Override
	public void delete(final String errorUri)
	{
        getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(DELETE_QUERY_BY_ERROR_URI);
            query.setParameter("errorUri", errorUri);
            query.executeUpdate();
            return null;
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#find(java.util.List, java.util.List, java.util.Date, java.util.Date, java.lang.Object)
	 */
	@Override
	public List<ExclusionEvent> find(List<String> moduleName,
			List<String> flowName, Date startDate, Date endDate,
			String identifier, int size)
	{
	    return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ExclusionEvent> criteriaQuery = builder.createQuery(ExclusionEvent.class);
            Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);

            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null && moduleName.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleName));
            }

            if(flowName != null && flowName.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowName));
            }
            if(identifier != null && identifier.length() > 0)
            {
                predicates.add(builder.equal(root.get("identifier"),identifier));
            }
            if(startDate != null)
            {
                predicates.add(builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add(builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(
                    builder.desc(root.get("timestamp")));

            Query<ExclusionEvent> query = session.createQuery(criteriaQuery);
            if(size > 0)
            {
                query.setFirstResult(0);
                query.setMaxResults(size);

            }
            return query.getResultList();
        });
	}

    /* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorReportingServiceDao#rowCount(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, int)
	 */
    @SuppressWarnings("unchecked")
    @Override
    public Long rowCount(final List<String> moduleName,
                         final List<String> flowName, final Date startDate, final Date endDate,
                         final String identifier)
    {
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);

            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null && moduleName.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleName));
            }

            if(flowName != null && flowName.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowName));
            }
            if(identifier != null && identifier.length() > 0)
            {
                predicates.add(builder.equal(root.get("identifier"),identifier));
            }
            if(startDate != null)
            {
                predicates.add(builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add(builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            criteriaQuery.select(builder.count(root))
                .where(predicates.toArray(new Predicate[predicates.size()]));

            Query<Long> query = session.createQuery(criteriaQuery);

            List<Long> rowCountList = query.getResultList();
            if (!rowCountList.isEmpty())
            {
                return rowCountList.get(0);
            }else{
                return Long.valueOf(0);
            }


        });

    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionEventDao#find(java.lang.String)
	 */
	@Override
	public ExclusionEvent find(String errorUri)
	{
	    return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ExclusionEvent> criteriaQuery = builder.createQuery(ExclusionEvent.class);
            Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("errorUri"),errorUri));

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
            ;

            Query<ExclusionEvent> query = session.createQuery(criteriaQuery);
            List<ExclusionEvent> result = query.getResultList();
            if(!result.isEmpty()){
                return result.get(0);
            }else {
                return null;
            }
        });
	}

    public List<ExclusionEvent> getHarvestableRecords(final int housekeepingBatchSize)
    {
        return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ExclusionEvent> criteriaQuery = builder.createQuery(ExclusionEvent.class);
            Root<ExclusionEventImpl> root = criteriaQuery.from(ExclusionEventImpl.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("harvestedDateTime"),0));

            if(this.isHarvestQueryOrdered) {
                criteriaQuery.orderBy(
                    builder.asc(root.get("timestamp")));
            }

            Query<ExclusionEvent> query = session.createQuery(criteriaQuery);
            query.setMaxResults(housekeepingBatchSize);
            return query.getResultList();
        });
    }

    @Override
    public void deleteAllExpired()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsHarvested(List<ExclusionEvent> events)
    {
        getHibernateTemplate().execute((session) -> {
            List<Long> exclusionEventIds = events.stream()
                .map(e -> (Long)e.getId())
                .collect(Collectors.toList());

            List<List<Long>> partitionedIds = Lists.partition(exclusionEventIds, 300);
            for (List<Long> eventIds : partitionedIds)
            {
                Query query = session.createQuery(UPDATE_HARVESTED_QUERY);
                query.setParameter(NOW, System.currentTimeMillis());
                query.setParameterList(EVENT_IDS, eventIds);
                query.executeUpdate();
            }
            return null;
        });
    }

    @Override
    public void setHarvestQueryOrdered(boolean isHarvestQueryOrdered) {
        this.isHarvestQueryOrdered = isHarvestQueryOrdered;
    }
}
