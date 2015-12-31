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

import java.util.Date;
import java.util.List;

import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public interface ErrorCategorisationService
{
	/**
	 * Method to save an error categorisation.
	 * 
	 * @param errorCategorisation
	 */
	public void save(ErrorCategorisation errorCategorisation);
	
	/**
	 * Method to save an error categorisation link.
	 * 
	 * @param errorCategorisationLink
	 */
	public void save(ErrorCategorisationLink errorCategorisationLink);

	/**
	 * Method to find an error categorisation.
	 *  
	 * @param moduleName
	 * @param flowName
	 * @param flowElementName
	 * @return
	 */	
	public List<ErrorCategorisationLink> find(String moduleName, String flowName, String flowElementName);
	
	/**
	 * Method to find an error categorisation link.
	 * 
	 * @param moduleName
	 * @param flowName
	 * @param flowElementName
	 * @param action
	 * @return
	 */
	public ErrorCategorisationLink find(String moduleName, String flowName,
			String flowElementName, String action);
	
	/**
	 * Method to delete an error categorisation.
	 * 
	 * @param errorCategorisation
	 */
	public void delete(ErrorCategorisation errorCategorisation);
	
	/**
	 * Method to delete an error categorisation link.
	 * 
	 * @param errorCategorisation
	 */
	public void delete(ErrorCategorisationLink errorCategorisationLink);
	
	/**
	 * Method to find categorised error occurrences based on filter criteria.
	 * 
	 * @param moduleNames
	 * @param flowNames
	 * @param flowElementNames
	 * @param errorCategory
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<CategorisedErrorOccurrence> findCategorisedErrorOccurences(List<String> moduleNames, List<String> flowNames, List<String> flowElementNames
			, String action, String exceptionClass, String errorCategory, Date startDate, Date endDate, int size);
}
