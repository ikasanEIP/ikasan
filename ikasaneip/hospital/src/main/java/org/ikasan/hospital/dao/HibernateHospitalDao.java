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
package org.ikasan.hospital.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ikasan.hospital.model.ExclusionEventActionImpl;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateHospitalDao extends HibernateDaoSupport implements HospitalDao
{
	public static final Long THIRTY_DAYS = 30 * 24 * 60 *  60 * 1000L;

	public static final String EVENT_IDS = "eventIds";
	public static final String NOW = "now";

	public static final String EXCLUSION_EVENT_ACTIONS_TO_DELETE_QUERY = "select errorUri from ExclusionEventActionImpl eo " +
			" where eo.timestamp < :" + NOW;

	public static final String EXCLUSION_EVENT_ACTIONS_DELETE_QUERY = "delete ExclusionEventActionImpl eo " +
			" where eo.errorUri in(:" + EVENT_IDS + ")";

	private Integer transactionBatchSize;
	private Integer housekeepingBatchSize;

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#saveOrUpdate(org.ikasan.hospital.window.ExclusionEventAction)
	 */
	@Override
	public void saveOrUpdate(ExclusionEventAction exclusionEventAction)
	{
		getHibernateTemplate().save(exclusionEventAction);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#getExclusionEventActionByErrorUri(java.lang.String)
	 */
	@Override
	public ExclusionEventAction getExclusionEventActionByErrorUri(
			String errorUri)
	{
		return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<ExclusionEventAction> criteriaQuery = builder.createQuery(ExclusionEventAction.class);

            Root<ExclusionEventActionImpl> root = criteriaQuery.from(ExclusionEventActionImpl.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("errorUri"),errorUri));

            org.hibernate.query.Query<ExclusionEventAction> query = session.createQuery(criteriaQuery);
            List<ExclusionEventAction> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }

            return results.get(0);

        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#getActionedExclusions(java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExclusionEventAction> getActionedExclusions(
			List<String> moduleName, List<String> flowName, Date startDate,
			Date endDate, int size)
	{
		return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<ExclusionEventAction> criteriaQuery = builder.createQuery(ExclusionEventAction.class);

            Root<ExclusionEventActionImpl> root = criteriaQuery.from(ExclusionEventActionImpl.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null && moduleName.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleName));
            }

            if(flowName != null && flowName.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowName));
            }

            if(startDate != null)
            {
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(builder.desc(root.get("timestamp")));

            org.hibernate.query.Query<ExclusionEventAction> query = session.createQuery(criteriaQuery);
            query.setFirstResult(0);
            query.setMaxResults(size);
            return query.getResultList();

        });
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.hospital.dao.HospitalDao#actionedExclusionsRowCount(java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public Long actionedExclusionsRowCount(List<String> moduleName, List<String> flowName, Date startDate, Date endDate)
	{

		return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

            Root<ExclusionEventActionImpl> root = criteriaQuery.from(ExclusionEventActionImpl.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null && moduleName.size() > 0)
            {
                predicates.add(root.get("moduleName").in(moduleName));
            }

            if(flowName != null && flowName.size() > 0)
            {
                predicates.add(root.get("flowName").in(flowName));
            }

            if(startDate != null)
            {
                predicates.add( builder.greaterThan(root.get("timestamp"),startDate.getTime()));
            }

            if(endDate != null)
            {
                predicates.add( builder.lessThan(root.get("timestamp"),endDate.getTime()));
            }

            criteriaQuery.select(builder.count(root))
                .where(predicates.toArray(new Predicate[predicates.size()]));

            org.hibernate.query.Query<Long> query = session.createQuery(criteriaQuery);

            List<Long> rowCountList = query.getResultList();
            if (!rowCountList.isEmpty())
            {
                return rowCountList.get(0);
            }
            return Long.valueOf(0);

        });

	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getNumberOfModuleActionedExclusions(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getNumberOfModuleActionedExclusions(String moduleName,
			Date startDate, Date endDate)
	{
        return actionedExclusionsRowCount(Arrays.asList(moduleName),null,startDate,endDate);
	}

	@Override
	public void housekeep()
	{
		getHibernateTemplate().execute(new HibernateCallback<Object>()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				int numHousekept = 0;

				while(numHousekept < transactionBatchSize)
				{
					numHousekept += housekeepingBatchSize;
					
					Query query = session.createQuery(EXCLUSION_EVENT_ACTIONS_TO_DELETE_QUERY);
					query.setLong(NOW, System.currentTimeMillis() - THIRTY_DAYS);
					query.setMaxResults(housekeepingBatchSize);

					List<Long> errorUris = (List<Long>) query.list();

					if (errorUris.size() > 0) {
						query = session.createQuery(EXCLUSION_EVENT_ACTIONS_DELETE_QUERY);
						query.setParameterList(EVENT_IDS, errorUris);
						query.executeUpdate();
					}
				}

				return null;
			}
		});
	}

	@Override
	public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
	{
		this.housekeepingBatchSize = housekeepingBatchSize;
	}

	@Override
	public void setTransactionBatchSize(Integer transactionBatchSize)
	{
		this.transactionBatchSize = transactionBatchSize;
	}

}
