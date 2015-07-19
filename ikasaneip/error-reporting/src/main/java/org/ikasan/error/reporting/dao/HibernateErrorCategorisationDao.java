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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.error.reporting.dao.constants.ErrorCategorisationDaoConstants;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorOccurrence;
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

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#save(org.ikasan.error.reporting.model.ErrorCategorisation)
	 */
	@Override
	public void save(ErrorCategorisation errorCategorisation)
	{
		 this.getHibernateTemplate().saveOrUpdate(errorCategorisation);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#find(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ErrorCategorisation find(String moduleName, String flowName,
			String flowElementName)
	{
		if(moduleName == null || flowName == null || flowElementName == null)
		{
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorCategorisation.class);
        criteria.add(Restrictions.eq("moduleName", moduleName));
        criteria.add(Restrictions.eq("flowName", flowName));
        criteria.add(Restrictions.eq("flowElementName", flowElementName));
        
        ErrorCategorisation excludedEventAction = (ErrorCategorisation) DataAccessUtils
        		.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return excludedEventAction;
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
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#findCategorisedErrorOccurences(java.util.List, java.util.List, java.util.List, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<CategorisedErrorOccurrence> findCategorisedErrorOccurences( final List<String> moduleNames, final List<String> flowNames,
			final List<String> flowElementNames, final String errorCategory, final Date startDate, final Date endDate)
	{
		List<ErrorOccurrence> errorOccurrences =  (List<ErrorOccurrence>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                StringBuffer queryString = new StringBuffer(ErrorCategorisationDaoConstants.CATEGORISED_ERROR_OCCURRENCE_QUERY);

                if(moduleNames != null && moduleNames.size() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_MODULE_NAMES);
                }
                
                if(flowNames != null && flowNames.size() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_FLOW_NAMES);
                }
                
                if(flowElementNames != null && flowElementNames.size() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_FLOW_ELEMENT_NAMES);
                }
                
                if(errorCategory != null && errorCategory.length() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_ERROR_CATEGORY);
                }
                
                if(startDate != null)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_START_DATE);
                }
                
                if(endDate != null)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_END_DATE);
                }
                

                Query query = session.createQuery(queryString.toString());
                
                if(moduleNames != null && moduleNames.size() > 0)
                {
                	query.setParameterList(ErrorCategorisationDaoConstants.MODULE_NAMES, moduleNames);
                }
                
                if(flowNames != null && flowNames.size() > 0)
                {
                	query.setParameterList(ErrorCategorisationDaoConstants.FLOW_NAMES, flowNames);
                }
                
                if(flowElementNames != null && flowElementNames.size() > 0)
                {
                	query.setParameterList(ErrorCategorisationDaoConstants.FLOW_ELEMENT_NAMES, flowElementNames);
                }
                
                if(errorCategory != null && errorCategory.length() > 0)
                {
                	query.setParameter(ErrorCategorisationDaoConstants.ERROR_CATEGORY, errorCategory);
                }
                
                if(startDate != null)
                {
                	query.setParameter(ErrorCategorisationDaoConstants.START_DATE, startDate.getTime());
                }
                
                if(endDate != null)
                {
                	 query.setParameter(ErrorCategorisationDaoConstants.END_DATE, endDate.getTime());
                }
                
                return (List<ErrorOccurrence>)query.list();
            }
        });
		
		Map<CategorisedErrorKey, ErrorCategorisation> categorisedErrorMap = this.getErrorCategorisationMap();
		
		ArrayList<CategorisedErrorOccurrence> categorisedErrorOccurrences = new ArrayList<CategorisedErrorOccurrence>();
		
		for(ErrorOccurrence errorOccurrence: errorOccurrences)
		{
			ErrorCategorisation errorCategorisation = categorisedErrorMap.get
					(new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
							, errorOccurrence.getFlowElementName()));
			
			categorisedErrorOccurrences.add(new CategorisedErrorOccurrence(errorOccurrence, errorCategorisation)); 
		}
		
		return categorisedErrorOccurrences;
	}
	
	protected Map<CategorisedErrorKey, ErrorCategorisation> getErrorCategorisationMap()
	{
		List<ErrorCategorisation> errorCategorisations = this.findAll();
		
		HashMap<CategorisedErrorKey, ErrorCategorisation> map = new HashMap<CategorisedErrorKey, ErrorCategorisation>();
		
		for(ErrorCategorisation errorCategorisation: errorCategorisations)
		{
			map.put(new CategorisedErrorKey(errorCategorisation.getModuleName()
					, errorCategorisation.getFlowName(), errorCategorisation.getFlowElementName())
					, errorCategorisation);	
		}
		
		return map;
	}
	
	
	private class CategorisedErrorKey 
	{
		String moduleName;
		String flowName;
		String flowElementName;
		
		/**
		 * @param moduleName
		 * @param flowName
		 * @param flowElementName
		 */
		public CategorisedErrorKey(String moduleName, String flowName,
				String flowElementName)
		{
			super();
			this.moduleName = moduleName;
			this.flowName = flowName;
			this.flowElementName = flowElementName;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((flowElementName == null) ? 0 : flowElementName
							.hashCode());
			result = prime * result
					+ ((flowName == null) ? 0 : flowName.hashCode());
			result = prime * result
					+ ((moduleName == null) ? 0 : moduleName.hashCode());
			return result;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CategorisedErrorKey other = (CategorisedErrorKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (flowElementName == null)
			{
				if (other.flowElementName != null)
					return false;
			} else if (!flowElementName.equals(other.flowElementName))
				return false;
			if (flowName == null)
			{
				if (other.flowName != null)
					return false;
			} else if (!flowName.equals(other.flowName))
				return false;
			if (moduleName == null)
			{
				if (other.moduleName != null)
					return false;
			} else if (!moduleName.equals(other.moduleName))
				return false;
			return true;
		}
		private HibernateErrorCategorisationDao getOuterType()
		{
			return HibernateErrorCategorisationDao.this;
		}
	}

}
