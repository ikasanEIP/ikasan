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
package org.ikasan.error.reporting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.error.reporting.dao.ErrorCategorisationDao;
import org.ikasan.error.reporting.dao.ErrorReportingServiceDao;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;
import org.ikasan.error.reporting.model.ErrorOccurrence;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorCategorisationServiceImpl implements
		ErrorCategorisationService
{
	private Logger logger = Logger.getLogger(ErrorCategorisationServiceImpl.class);
	
	private ErrorCategorisationDao errorCategorisationDao;
	private ErrorReportingServiceDao errorReportingService;

	/**
	 * @param errorCategorisationDao
	 */
	public ErrorCategorisationServiceImpl(
			ErrorCategorisationDao errorCategorisationDao,
			ErrorReportingServiceDao errorReportingService)
	{
		super();
		this.errorCategorisationDao = errorCategorisationDao;
		if(this.errorCategorisationDao == null)
		{
			throw new IllegalArgumentException("errorCategorisationDao cannot be null!!");
		}
		this.errorReportingService = errorReportingService;
		if(this.errorReportingService == null)
		{
			throw new IllegalArgumentException("errorReportingService cannot be null!!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#save(org.ikasan.error.reporting.model.ErrorCategorisation)
	 */
	@Override
	public void save(ErrorCategorisation errorCategorisation)
	{
		this.errorCategorisationDao.save(errorCategorisation);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#find(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ErrorCategorisationLink> find(String moduleName, String flowName,
			String flowElementName)
	{
		return this.errorCategorisationDao.find(moduleName, flowName, flowElementName);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#find(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ErrorCategorisationLink find(String moduleName, String flowName,
			String flowElementName, String action)
	{
		return this.errorCategorisationDao.find(moduleName, flowName, flowElementName, action);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#delete(org.ikasan.error.reporting.model.ErrorCategorisation)
	 */
	@Override
	public void delete(ErrorCategorisation errorCategorisation)
	{
		this.errorCategorisationDao.delete(errorCategorisation);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#save(org.ikasan.error.reporting.model.ErrorCategorisationLink)
	 */
	@Override
	public void save(ErrorCategorisationLink errorCategorisationLink)
	{
		this.errorCategorisationDao.save(errorCategorisationLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#delete(org.ikasan.error.reporting.model.ErrorCategorisationLink)
	 */
	@Override
	public void delete(ErrorCategorisationLink errorCategorisationLink)
	{
		this.errorCategorisationDao.delete(errorCategorisationLink);
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#findCategorisedErrorOccurences(java.util.List, java.util.List, java.util.List, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<CategorisedErrorOccurrence> findCategorisedErrorOccurences(List<String> moduleNames, List<String> flowNames,
			List<String> flowElementNames, String action, String exceptionClass, String errorCategory, Date startDate, Date endDate,
			int size)
	{
		List<ErrorOccurrence> errorOccurrences = this.errorReportingService.find(moduleNames, flowNames, flowElementNames, startDate, endDate, size);
		
		Map<CategorisedErrorKey, ErrorCategorisation> categorisedErrorMap = this.getErrorCategorisationMap();
		
		ArrayList<CategorisedErrorOccurrence> categorisedErrorOccurrences = new ArrayList<CategorisedErrorOccurrence>();
		
		
		for(ErrorOccurrence errorOccurrence: errorOccurrences)
		{			
			logger.debug("Action: " + this.getAction(errorOccurrence));
			
			if(errorOccurrence.getExceptionClass() == null)
			{
				errorOccurrence.setExceptionClass("");
			}
			
			CategorisedErrorKey key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
					, errorOccurrence.getFlowElementName(), this.getAction(errorOccurrence), errorOccurrence.getExceptionClass().trim());
			
			logger.debug("Using key " + key);
			
			// Casacade down the configured error occurrences to get the most focused 
			// error categorisation associated with the error occurrence.
			ErrorCategorisation errorCategorisation = categorisedErrorMap.get(key);
			
			
			logger.debug("errorCategorisation: " + errorCategorisation);
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
						, errorOccurrence.getFlowElementName(), this.getAction(errorOccurrence), "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
						, errorOccurrence.getFlowElementName(), "", "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
						, "", this.getAction(errorOccurrence), errorOccurrence.getExceptionClass().trim());
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
						, "", "", errorOccurrence.getExceptionClass().trim());
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
						, "", this.getAction(errorOccurrence), "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
						, "", "", "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), ""
						, "", this.getAction(errorOccurrence), errorOccurrence.getExceptionClass().trim());
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), ""
						, "", "", errorOccurrence.getExceptionClass().trim());
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), ""
						, "", this.getAction(errorOccurrence), "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey(errorOccurrence.getModuleName(), ""
						, "", "", "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey("", "" , "", this.getAction(errorOccurrence), errorOccurrence.getExceptionClass().trim());
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{
				key = new CategorisedErrorKey("", "" , "", "", errorOccurrence.getExceptionClass().trim());
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);
			}
			
			if(errorCategorisation == null)
			{				
				key = new CategorisedErrorKey("", "" , "", this.getAction(errorOccurrence), "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);	
			}
			
			if(errorCategorisation == null)
			{				
				key = new CategorisedErrorKey("", "" , "", "", "");
				
				logger.debug("Using key " + key);
				
				errorCategorisation = categorisedErrorMap.get(key);
				
				logger.debug("errorCategorisation: " + errorCategorisation);	
			}
			
			if(errorCategorisation != null && (errorCategory == null || errorCategory.equals(errorCategorisation.getErrorCategory())))
			{
				categorisedErrorOccurrences.add(new CategorisedErrorOccurrence(errorOccurrence, errorCategorisation));
			}
		}
		
		logger.debug("returning: " + categorisedErrorOccurrences.size());
		return categorisedErrorOccurrences;
	}
	
	protected Map<CategorisedErrorKey, ErrorCategorisation> getErrorCategorisationMap()
	{
		List<ErrorCategorisationLink> errorCategorisations = this.errorCategorisationDao.findAllErrorCategorisationLinks();
		
		HashMap<CategorisedErrorKey, ErrorCategorisation> map = new HashMap<CategorisedErrorKey, ErrorCategorisation>();
		
		for(ErrorCategorisationLink errorCategorisationLink: errorCategorisations)
		{
			CategorisedErrorKey key = new CategorisedErrorKey(errorCategorisationLink.getModuleName().trim()
					, errorCategorisationLink.getFlowName().trim(), errorCategorisationLink.getFlowElementName().trim()
					, errorCategorisationLink.getAction().trim(), errorCategorisationLink.getExceptionClass().trim());
			
			logger.debug("Adding key " + key);
			logger.debug("Adding value " + errorCategorisationLink.getErrorCategorisation());
			
			map.put(key, errorCategorisationLink.getErrorCategorisation());	
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
		String exceptionClass;
		
		/**
		 * @param moduleName
		 * @param flowName
		 * @param flowElementName
		 */
		public CategorisedErrorKey(String moduleName, String flowName,
				String flowElementName, String action, String exceptionClass)
		{
			super();
			this.moduleName = moduleName;
			this.flowName = flowName;
			this.flowElementName = flowElementName;
			this.action = action;
			this.exceptionClass = exceptionClass;
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
					+ ((exceptionClass == null) ? 0 : exceptionClass.hashCode());
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
			if (exceptionClass == null)
			{
				if (other.exceptionClass != null)
					return false;
			} else if (!exceptionClass.equals(other.exceptionClass))
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

		private ErrorCategorisationServiceImpl getOuterType()
		{
			return ErrorCategorisationServiceImpl.this;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "CategorisedErrorKey [moduleName=" + moduleName
					+ ", flowName=" + flowName + ", flowElementName="
					+ flowElementName + ", action=" + action
					+ ", exceptionClass=" + exceptionClass + "]";
		}	
	}


	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.service.ErrorCategorisationService#rowCount(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public Long rowCount(List<String> moduleName, List<String> flowName,
			List<String> flowElementname, Date startDate, Date endDate)
	{
		return this.errorReportingService.rowCount(moduleName, flowName, flowElementname, startDate, endDate);
	}

}
