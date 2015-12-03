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
package org.ikasan.hospital.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ikasan.hospital.dao.HospitalDao;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class HospitalManagementServiceImpl implements
		HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount>
{

	private HospitalDao hospitalDao;

	/**
	 * Constructor
	 * 
	 * @param moduleContainer
	 */
	public HospitalManagementServiceImpl(HospitalDao hospitalDao)
	{
		super();
		this.hospitalDao = hospitalDao;
		if(this.hospitalDao == null)
		{
			throw new IllegalArgumentException("hospitalDao cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.service.HospitalManagementService#getExclusionEventActionByErrorUri(java.lang.String)
	 */
	@Override
	public ExclusionEventAction getExclusionEventActionByErrorUri(String errorUri)
	{
		return hospitalDao.getExclusionEventActionByErrorUri(errorUri);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.hospital.service.HospitalManagementService#getActionedExclusions(java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ExclusionEventAction> getActionedExclusions(List<String> moduleName, List<String> flowName, Date startDate, Date endDate)
	{
		return this.hospitalDao.getActionedExclusions(moduleName, flowName, startDate, endDate);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.error.reporting.ErrorReportingManagementService#getModuleErrorCount(java.util.List)
	 */
	@Override
	public List<ModuleActionedExclusionCount> getModuleActionedExclusionCount(List<String> moduleNames, Date startDate, Date endDate)
	{
		ArrayList<ModuleActionedExclusionCount> errorCounts = new ArrayList<ModuleActionedExclusionCount>();
		
		for(String moduleName: moduleNames)
		{
			ModuleActionedExclusionCount errorCount = new ModuleActionedExclusionCount(moduleName,
					this.hospitalDao.getNumberOfModuleActionedExclusions(moduleName, startDate, endDate));
			
			errorCounts.add(errorCount);
		}
		
		return errorCounts;
	}

}
