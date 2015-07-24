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

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.error.reporting.dao.constants.ErrorCategorisationDaoConstants;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;
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

	private Logger logger = Logger.getLogger(HibernateErrorCategorisationDao.class);
	
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
	 * @see org.ikasan.error.reporting.dao.ErrorCategorisationDao#findCategorisedErrorOccurences(java.util.List, java.util.List, java.util.List, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<CategorisedErrorOccurrence> findCategorisedErrorOccurences( final List<String> moduleNames, final List<String> flowNames,
			final List<String> flowElementNames, final String action, final String exceptionClass, final String errorCategory, final Date startDate, final Date endDate)
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
                else
                {
                	queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_MODULE_NAMES_EMPTY_OR_NULL);
                }
                
                
                if(flowNames != null && flowNames.size() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_FLOW_NAMES);
                }
                else
                {
                	queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_FLOW_NAMES_EMPTY_OR_NULL);
                }
                
                if(flowElementNames != null && flowElementNames.size() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_FLOW_ELEMENT_NAMES);
                }
                else
                {
                	queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_FLOW_ELEMENT_NAMES_EMPTY_OR_NULL);
                }
                
                
                if(action != null && action.length() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_ACTION);
                }
                
                if(exceptionClass != null && exceptionClass.length() > 0)
                {
                    queryString.append(ErrorCategorisationDaoConstants.NARROW_BY_EXCEPTION_CLASS);
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
                
                queryString.append(ErrorCategorisationDaoConstants.ORDER_BY);
                

                Query query = session.createQuery(queryString.toString());
                
                if(moduleNames != null && moduleNames.size() > 0)
                {
                	query.setParameterList(ErrorCategorisationDaoConstants.MODULE_NAMES, moduleNames);
                }
                else
                {
                	query.setParameter(ErrorCategorisationDaoConstants.MODULE_NAMES, "");
                }
                
                if(flowNames != null && flowNames.size() > 0)
                {
                	query.setParameterList(ErrorCategorisationDaoConstants.FLOW_NAMES, flowNames);
                }
                else
                {
                	query.setParameter(ErrorCategorisationDaoConstants.FLOW_NAMES, "");
                }
                
                if(flowElementNames != null && flowElementNames.size() > 0)
                {
                	query.setParameterList(ErrorCategorisationDaoConstants.FLOW_ELEMENT_NAMES, flowElementNames);
                }
                else
                {
                	query.setParameter(ErrorCategorisationDaoConstants.FLOW_ELEMENT_NAMES, "");
                }
                
                if(action != null && action.length() > 0)
                {
                	query.setParameter(ErrorCategorisationDaoConstants.ACTION, action);
                }
                
                if(exceptionClass != null && exceptionClass.length() > 0)
                {
                	query.setParameter(ErrorCategorisationDaoConstants.EXCEPTION_CLASS, exceptionClass);
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
                
                logger.info(query);
                return (List<ErrorOccurrence>)query.list();
            }
        });
		
		Map<CategorisedErrorKey, ErrorCategorisation> categorisedErrorMap = this.getErrorCategorisationMap();
		
		ArrayList<CategorisedErrorOccurrence> categorisedErrorOccurrences = new ArrayList<CategorisedErrorOccurrence>();
		
		logger.info("query results: " + errorOccurrences.size());
		
		for(ErrorOccurrence errorOccurrence: errorOccurrences)
		{
//			logger.info("trying to get link for error occurence: " + errorOccurrences);
			
			logger.info("Action: " + this.getAction(errorOccurrence));
			
			ErrorCategorisation errorCategorisation = categorisedErrorMap.get
					(new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
							, errorOccurrence.getFlowElementName(), this.getAction(errorOccurrence)));
			
			
			logger.info("errorCategorisation: " + errorCategorisation);
			
			if(errorCategorisation == null)
			{
				errorCategorisation = categorisedErrorMap.get
						(new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
								, "", this.getAction(errorOccurrence)));
			}
			
			if(errorCategorisation == null)
			{
				errorCategorisation = categorisedErrorMap.get
						(new CategorisedErrorKey(errorOccurrence.getModuleName(), ""
								, "", this.getAction(errorOccurrence)));
			}
			
			if(errorCategorisation == null)
			{
				errorCategorisation = categorisedErrorMap.get
						(new CategorisedErrorKey("", "" , "", this.getAction(errorOccurrence)));
			}
			
			if(errorCategorisation != null)
			{
				categorisedErrorOccurrences.add(new CategorisedErrorOccurrence(errorOccurrence, errorCategorisation));
			}
		}
		
		logger.info("returning: " + categorisedErrorOccurrences.size());
		return categorisedErrorOccurrences;
	}
	
	protected Map<CategorisedErrorKey, ErrorCategorisation> getErrorCategorisationMap()
	{
		List<ErrorCategorisationLink> errorCategorisations = this.findAllErrorCategorisationLinks();
		
		HashMap<CategorisedErrorKey, ErrorCategorisation> map = new HashMap<CategorisedErrorKey, ErrorCategorisation>();
		
		for(ErrorCategorisationLink errorCategorisationLink: errorCategorisations)
		{
			logger.info("Addin key " + new CategorisedErrorKey(errorCategorisationLink.getModuleName()
					, errorCategorisationLink.getFlowName(), errorCategorisationLink.getFlowElementName()
					, errorCategorisationLink.getAction()));
			logger.info("Addin value " + errorCategorisationLink.getErrorCategorisation());
			
			map.put(new CategorisedErrorKey(errorCategorisationLink.getModuleName()
					, errorCategorisationLink.getFlowName(), errorCategorisationLink.getFlowElementName()
					, errorCategorisationLink.getAction())
					, errorCategorisationLink.getErrorCategorisation());	
		}
		
		return map;
	}
	
	protected String getAction(ErrorOccurrence errorOccurrence)
	{
		if(errorOccurrence.getAction() == null)
		{
			return null;
		}
		else if(errorOccurrence.getAction().startsWith(ErrorCategorisationLink.EXCLUDE_EVENT_ACTION))
		{
			return ErrorCategorisationLink.EXCLUDE_EVENT_ACTION;
		}
		else if(errorOccurrence.getAction().startsWith(ErrorCategorisationLink.STOP_ACTION))
		{
			return ErrorCategorisationLink.STOP_ACTION;
		}
		else if(errorOccurrence.getAction().startsWith(ErrorCategorisationLink.RETRY_ACTION))
		{
			return ErrorCategorisationLink.RETRY_ACTION;
		}
		
		return "";
	}
	
	
	private class CategorisedErrorKey 
	{
		String moduleName;
		String flowName;
		String flowElementName;
		String action;
		
		/**
		 * @param moduleName
		 * @param flowName
		 * @param flowElementName
		 */
		public CategorisedErrorKey(String moduleName, String flowName,
				String flowElementName, String action)
		{
			super();
			this.moduleName = moduleName;
			this.flowName = flowName;
			this.flowElementName = flowElementName;
			this.action = action;
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
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
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
			if (action == null)
			{
				if (other.action != null)
					return false;
			} else if (!action.equals(other.action))
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
