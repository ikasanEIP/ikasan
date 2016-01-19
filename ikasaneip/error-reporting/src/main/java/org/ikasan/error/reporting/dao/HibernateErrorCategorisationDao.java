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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateErrorCategorisationDao extends HibernateDaoSupport implements ErrorCategorisationDao
{
	private Logger logger = Logger.getLogger(HibernateErrorCategorisationDao.class);

	public static final String MODULE_NAMES = "moduleNames";
	public static final String FLOW_NAMES = "flowNames";
	public static final String COMPONENT_NAMES = "componentNames";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	
	public static final String ERROR_CATERORISED_COUNT_SQL = "select distinct count(Uri) from ErrorCategorisationLink l, ErrorOccurrence e " +
			"where (l.ModuleName = e.ModuleName and l.FlowName = e.FlowName and l.FlowElementName = e.FlowElementName) " +
			"or (l.ModuleName = e.ModuleName and l.FlowName = e.FlowName) " +
			"or (l.ModuleName = e.ModuleName) " +
			"and e.ModuleName in(:" + MODULE_NAMES +") " +
			"and e.FlowName in (:" + FLOW_NAMES + ") " +
			"and e.FlowElementName in (:" + COMPONENT_NAMES + ") " +
			"and e.Timestamp <= :" + END_DATE + " " +
			"and e.Timestamp >= :" + START_DATE + " " +
			"and e.UserAction is NULL";
	
	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#save(org.ikasan.error.reporting.model.ErrorCategorisation)
	 */
	@Override
	public void save(ErrorCategorisation errorCategorisation)
	{
		 this.getHibernateTemplate().saveOrUpdate(errorCategorisation);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#save(org.ikasan.error.reporting.model.ErrorCategorisationLink)
	 */
	@Override
	public void save(ErrorCategorisationLink errorCategorisationLink)
	{
		this.getHibernateTemplate().saveOrUpdate(errorCategorisationLink);
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#delete(org.ikasan.error.reporting.model.ErrorCategorisationLink)
	 */
	@Override
	public void delete(ErrorCategorisationLink errorCategorisationLink)
	{
		this.getHibernateTemplate().delete(errorCategorisationLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#find(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ErrorCategorisationLink> find(String moduleName, String flowName,
			String flowElementName)
	{
		if(moduleName == null || flowName == null || flowElementName == null)
		{
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorCategorisationLink.class);
        criteria.add(Restrictions.eq("moduleName", moduleName));
        criteria.add(Restrictions.eq("flowName", flowName));
        criteria.add(Restrictions.eq("flowElementName", flowElementName));

        return (List<ErrorCategorisationLink>)this.getHibernateTemplate().findByCriteria(criteria);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#find(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ErrorCategorisationLink find(String moduleName, String flowName,
			String flowElementName, String action)
	{
		if(moduleName == null || flowName == null || flowElementName == null || action == null)
		{
			return null;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorCategorisationLink.class);
        criteria.add(Restrictions.eq("moduleName", moduleName));
        criteria.add(Restrictions.eq("flowName", flowName));
        criteria.add(Restrictions.eq("flowElementName", flowElementName));
        criteria.add(Restrictions.eq("action", action));
        
        ErrorCategorisationLink errorCategorisationLink = (ErrorCategorisationLink) DataAccessUtils
        		.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return errorCategorisationLink;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#delete(org.ikasan.error.reporting.model.ErrorCategorisation)
	 */
	@Override
	public void delete(ErrorCategorisation errorCategorisation)
	{
		 this.getHibernateTemplate().delete(errorCategorisation);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#findAll()
	 */
	@Override
	public List<ErrorCategorisation> findAll()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorCategorisation.class);
        
        return (List<ErrorCategorisation>)this.getHibernateTemplate().findByCriteria(criteria);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#findAll()
	 */
	@Override
	public List<ErrorCategorisationLink> findAllErrorCategorisationLinks()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorCategorisationLink.class);
        
        return (List<ErrorCategorisationLink>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#rowCount(java.util.List, java.util.List, java.util.List, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Long rowCount(final List<String> moduleNames, final List<String> flowNames,
			final List<String> flowElementNames, final Date startDate,
			final Date endDate)
	{
		return (Long) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {            	
            	SQLQuery query = session.createSQLQuery(ERROR_CATERORISED_COUNT_SQL);
            	
            	query.setParameterList(MODULE_NAMES, moduleNames);
            	query.setParameterList(FLOW_NAMES, flowNames);
            	query.setParameterList(COMPONENT_NAMES, flowElementNames);
            	
            	if(startDate == null)
            	{
            		query.setParameter(START_DATE, 0);
            	}
            	else
            	{
            		query.setParameter(START_DATE, startDate.getTime());
            	}
            	
            	if(endDate == null)
            	{
            		query.setParameter(END_DATE, System.currentTimeMillis());
            	}
            	else
            	{
            		query.setParameter(END_DATE, endDate.getTime());
            	}

            	List rowCountList = query.list();
            	Long rowCount = new Long(0);
            	
            	if (!rowCountList.isEmpty())
			    {
            		if(rowCountList.get(0) instanceof BigInteger)
            		{
            			rowCount = ((BigInteger)rowCountList.get(0)).longValue();
            		}
            		else if(rowCountList.get(0) instanceof Integer)
            		{
            			rowCount = ((Integer)rowCountList.get(0)).longValue();
            		}
			    }
			    
			    return rowCount;		                
            }
        });
	}

	
		
}
