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

import java.util.Date;
import java.util.List;

import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;


/**
 * Data Access interface for <code>ExclusionEventAction</code> instances
 * 
 * @author Ikasan Development Team
 *
 */
public interface HospitalDao
{
	/**
	 * Method to save or update an ExclusionEventAction.
	 * 
	 * @param exclusionEventAction
	 */
	public void saveOrUpdate(ExclusionEventAction exclusionEventAction);

	/**
	 * A method to return and ExclusionEventAction based on the event uri.
	 * @param errorUri
	 * @return
	 */
	public ExclusionEventAction getExclusionEventActionByErrorUri(String errorUri);
	
	/**
	 * Get actioned exclusions
	 * 
	 * @param moduleName
	 * @param flowName
	 * @param startDate
	 * @param endDate
	 * @param size
	 * @return
	 */
	public List<ExclusionEventAction> getActionedExclusions(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, int size);
	
	/**
	 * Helper method to return the row count based on the criteria.
	 *  
	 * @param moduleName
	 * @param flowName
	 * @param startDate
	 * @param endDate
	 * @return
	 */
    public Long actionedExclusionsRowCount(List<String> moduleName, List<String> flowName, Date startDate, Date endDate);
	
    /**
     * 
     * @param moduleName
     * @return
     */
    public Long getNumberOfModuleActionedExclusions(String moduleName, Date startDate, Date endDate);
  
}
