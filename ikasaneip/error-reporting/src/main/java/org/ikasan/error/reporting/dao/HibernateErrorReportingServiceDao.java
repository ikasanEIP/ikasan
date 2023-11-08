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
import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.model.ArrayListPagedSearchResult;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.search.PagedSearchResult;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hibernate specific implementation of the ErrorReportingServiceDao.
 * @author Ikasan Development Team
 */
public class HibernateErrorReportingServiceDao extends HibernateDaoSupport
        implements ErrorReportingServiceDao<ErrorOccurrence, String>
{
    /** default batch size */
    private static Integer housekeepingBatchSize = Integer.valueOf(100);

    /** batch delete statement */
    private static final String BATCHED_HOUSEKEEP_QUERY = "delete ErrorOccurrenceImpl s where s.uri in (:event_uris)";

    public HibernateErrorReportingServiceDao()
    {
    }

    @Override
    public ErrorOccurrence find(String uri)
    {
        return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<ErrorOccurrence> criteriaQuery = builder.createQuery(ErrorOccurrence.class);

            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("uri"),uri));


            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
            List<ErrorOccurrence> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }

            return results.get(0);

        });

    }

	@Override
	public Map<String, ErrorOccurrence> find(List<String> uris)
	{
		Map<String, ErrorOccurrence> results = new HashMap<String, ErrorOccurrence>();

		List<List<String>> partitions = Lists.partition(uris, 300);

		for(List<String> partition: partitions)
		{

			 Map<String,ErrorOccurrence> r = getUri(partition).collect( Collectors.toMap(e -> e.getUri(), e-> e));
 		     results.putAll(r);
		}

		return results;



	}

    private Stream<ErrorOccurrence> getUri(List<String> partition) {
        return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ErrorOccurrence> criteriaQuery = builder.createQuery(ErrorOccurrence.class);
            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            criteriaQuery
                .select(root)
                .where(root.get("uri").in(partition));

            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);

            return query.getResultStream();

        });
    }
	/* (non-Javadoc)
     * @see org.ikasan.spec.error.reporting.ErrorReportingServiceDao#find(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
	public List<ErrorOccurrence> find(List<String> moduleName, List<String> flowName, List<String> flowElementname,
                                                  Date startDate, Date endDate, int size)
	{

        return getHibernateTemplate().execute((session) -> {

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
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            predicates.add(root.get("userAction").isNull());

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(builder.desc(root.get("timestamp")));


            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
            query.setMaxResults(size);
            return query.getResultList();

        });

    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.error.reporting.ErrorReportingServiceDao#find(int, int, java.lang.String, boolean, java.lang.String, java.lang.String, java.lang.String, java.util.Data, java.util.Data)
     */
    @Override
    public PagedSearchResult<ErrorOccurrence> find(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
        final String moduleName, final String flowName, final String componentName,  final Date fromDate, final Date untilDate)
    {
        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                CriteriaBuilder builder = session.getCriteriaBuilder();

                CriteriaQuery<ErrorOccurrence> criteriaQuery = builder.createQuery(ErrorOccurrence.class);
                Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
                List<Predicate> predicates = getCriteria(builder,root);

                criteriaQuery.select(root)
                    .where(predicates.toArray(new Predicate[predicates.size()]));

                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        criteriaQuery.orderBy(builder.asc(root.get(orderBy)));
                    }
                    else
                    {
                        criteriaQuery.orderBy(builder.desc(root.get(orderBy)));

                    }
                } else {
                    criteriaQuery.orderBy(builder.desc(root.get("timestamp")));
                }


                Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
                query.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                query.setFirstResult(firstResult);
                List<ErrorOccurrence> results = query.getResultList();

                Long rowCount = rowCount(session);

                return new ArrayListPagedSearchResult<ErrorOccurrence>(results, firstResult, rowCount);
            }

            private Long rowCount(Session session){


                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Long> metaDataCriteriaQuery = builder.createQuery(Long.class);
                Root<ErrorOccurrenceImpl> root = metaDataCriteriaQuery.from(ErrorOccurrenceImpl.class);
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
            private List<Predicate>  getCriteria(CriteriaBuilder builder,Root<ErrorOccurrenceImpl> root)
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

                if(fromDate != null)
                {
                    predicates.add( builder.greaterThan(root.get("timestamp"),fromDate.getTime()));
                }

                if(untilDate != null)
                {
                    predicates.add( builder.lessThan(root.get("timestamp"),untilDate.getTime()));
                }

                predicates.add(root.get("userAction").isNull());
                return predicates;

            }
        });
    }
    
    @Override
    public void save(ErrorOccurrence errorOccurrence)
    {
        this.getHibernateTemplate().saveOrUpdate(errorOccurrence);
    }

    @Override
    public void save(List<ErrorOccurrence> errorOccurrences)
    {
        errorOccurrences.forEach(errorOccurrence -> this.save(errorOccurrence));
    }

    @Override
    public void deleteExpired()
    {
        while(housekeepablesExist()){
            final List<String> housekeepableBatch = getHousekeepableBatch();
            getHibernateTemplate().execute((session) -> {
                Query query = session.createQuery(BATCHED_HOUSEKEEP_QUERY);
                query.setParameterList("event_uris", housekeepableBatch);
                query.executeUpdate();
                return null;
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
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            criteriaQuery.select(root.get("uri"))
                .where(builder.lessThan(root.get("expiry"), System.currentTimeMillis()));
            Query<String> query = session.createQuery(criteriaQuery);
            query.setMaxResults(housekeepingBatchSize);
            return query.getResultList();
        });
    }

    private boolean housekeepablesExist() {
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.lessThan(root.get("expiry"), System.currentTimeMillis()));
            criteriaQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));
            Query<Long> query = session.createQuery(criteriaQuery);
            List<Long> rowCountList = query.getResultList();
            Long rowCount = Long.valueOf(0);
            if (!rowCountList.isEmpty())
            {
                rowCount = rowCountList.get(0);
            }
            return Boolean.valueOf(rowCount > 0);
        });
    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingServiceDao#rowCount(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long rowCount(final List<String> moduleName, final List<String> flowName,
			final List<String> flowElementname, final Date startDate, final Date endDate)
	{
        return getHibernateTemplate().execute((Session session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<ErrorOccurrenceImpl> root = criteriaQuery.from(ErrorOccurrenceImpl.class);
            List<Predicate> predicates = new ArrayList<>();
            if (moduleName != null && moduleName.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleName));
            }
            if (flowName != null && flowName.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowName));
            }
            if (flowElementname != null && flowElementname.size() > 0)
            {
                predicates.add(root.get("flowElementName").in(flowElementname));
            }
            if (startDate != null)
            {
                predicates.add(builder.greaterThan(root.get("timestamp"), startDate.getTime()));
            }
            if (endDate != null)
            {
                predicates.add(builder.lessThan(root.get("timestamp"), endDate.getTime()));
            }
            predicates.add(root.get("userAction").isNull());
            criteriaQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));
            Query<Long> query = session.createQuery(criteriaQuery);
            List<Long> rowCountList = query.getResultList();
            Long rowCount = Long.valueOf(0);
            if (!rowCountList.isEmpty())
            {
                rowCount = rowCountList.get(0);
            }
            return rowCount;
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingServiceDao#find(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<ErrorOccurrence> find(List<String> moduleName,
                                                  List<String> flowName, List<String> flowElementname,
                                                  Date startDate, Date endDate, String action, String exceptionClass,
                                                  int size)
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
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            if(exceptionClass != null)
            {
                predicates.add( builder.equal(root.get("exceptionClass"),exceptionClass));
            }

            if(action != null)
            {
                predicates.add( builder.equal(root.get("action"),action));
            }

            predicates.add(root.get("userAction").isNull());

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(builder.desc(root.get("timestamp")));


            Query<ErrorOccurrence> query = session.createQuery(criteriaQuery);
            query.setMaxResults(size);
            List<ErrorOccurrence> rowList = query.getResultList();

            return rowList;

        });

	}

}
