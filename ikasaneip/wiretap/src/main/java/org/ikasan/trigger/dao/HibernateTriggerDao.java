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
package org.ikasan.trigger.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.trigger.model.TriggerImpl;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate implementation for the <code>TriggerDao</code> interface
 * 
 * @author Ikasan Development Team
 */
public class HibernateTriggerDao extends HibernateDaoSupport implements TriggerDao
{
    /* (non-Javadoc)
     * @see org.ikasan.trigger.dao.TriggerDao#delete(org.ikasan.trigger.window.Trigger)
     */
    public void delete(Trigger trigger)
    {
        getHibernateTemplate().delete(trigger);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.event.dao.TriggerDao#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<Trigger> findAll()
    {
        return getHibernateTemplate().execute(session -> session.createQuery("from TriggerImpl").getResultList());
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.event.dao.TriggerDao#findById(java.lang.Long)
     */
    public Trigger findById(Long triggerId)
    {
        return (Trigger) getHibernateTemplate().get(TriggerImpl.class, triggerId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.trigger.dao.TriggerDao#save(org.ikasan.trigger.window.Trigger)
     */
    public void save(Trigger trigger)
    {
        getHibernateTemplate().saveOrUpdate(trigger);
    }

	/* (non-Javadoc)
	 * @see org.ikasan.trigger.dao.TriggerDao#findTriggers(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Trigger> findTriggers(String moduleName, String flowName,
			String flowElementName)
	{
		return getHibernateTemplate().execute((Session session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Trigger> criteriaQuery = builder.createQuery(Trigger.class);

            Root<TriggerImpl> root = criteriaQuery.from(TriggerImpl.class);
            List<Predicate> predicates = new ArrayList<>();

            if(moduleName != null && moduleName.length() > 0)
            {
                predicates.add(builder.equal(root.get("moduleName"),moduleName));
            }

            if(flowName != null && flowName.length() > 0)
            {
                predicates.add(builder.equal(root.get("flowName"),flowName));
            }

            if(flowElementName != null && flowElementName.length() > 0)
            {
                predicates.add(builder.equal(root.get("flowElementName"),flowElementName));
            }

            criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[predicates.size()]));

            Query<Trigger> query = session.createQuery(criteriaQuery);
            List<Trigger> rowList = query.getResultList();

            return rowList;

        });

	}
}
