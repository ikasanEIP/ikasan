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
package org.ikasan.exclusion.service;

import java.util.Date;
import java.util.List;

import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.management.HousekeeperService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ExclusionManagementServiceImpl implements ExclusionManagementService<ExclusionEvent, String>, HarvestService<ExclusionEvent>, HousekeeperService
{
	private ExclusionEventDao<String,ExclusionEvent> exclusionEventDao;

	public ExclusionManagementServiceImpl(ExclusionEventDao<String,ExclusionEvent> exclusionEventDao)
	{
		this.exclusionEventDao = exclusionEventDao;
		if(this.exclusionEventDao == null)
		{
			throw new IllegalArgumentException("exclusionEventDao cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionManagementService#find(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ExclusionEvent find(String moduleName, String flowName,
			String identifier)
	{
		return this.exclusionEventDao.find(moduleName, flowName, identifier);
	}

	@Override
	public long count(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String identifier)
	{
		return this.exclusionEventDao.rowCount(moduleName,flowName, startDate, endDate, identifier);
	}

	@Override
	public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String identifier, int size)
	{
		return this.exclusionEventDao.find(moduleName, flowName, startDate, endDate, identifier, size);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionManagementService#findAll()
	 */
	@Override
	public List<ExclusionEvent> findAll()
	{
		return this.exclusionEventDao.findAll();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionManagementService#delete(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void delete(String moduleName, String flowName, String identifier)
	{
		this.exclusionEventDao.delete(moduleName, flowName, identifier);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionManagementService#delete(java.lang.String)
	 */
	@Override
	public void delete(String errorUri)
	{
		this.exclusionEventDao.delete(errorUri);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionManagementService#find(java.util.List, java.util.List, java.util.Date, java.util.Date, java.lang.Object)
	 */
	@Override
	public List<ExclusionEvent> find(List<String> moduleName,
			List<String> flowName, Date startDate, Date endDate,
			String identifier)
	{
		return this.exclusionEventDao.find(moduleName, flowName, startDate, endDate, identifier, -1);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.exclusion.ExclusionManagementService#find(java.lang.String)
	 */
	@Override
	public ExclusionEvent find(String errorUri)
	{
		return this.exclusionEventDao.find(errorUri);
	}

	@Override
	public List<ExclusionEvent> harvest(int transactionBatchSize)
	{
		return this.exclusionEventDao.getHarvestableRecords(transactionBatchSize);
	}

	@Override
	public boolean harvestableRecordsExist()
	{
		return true;
	}

	@Override
	public void saveHarvestedRecord(ExclusionEvent harvestedRecord)
	{
		this.exclusionEventDao.save(harvestedRecord);
	}

	@Override
	public void housekeep()
	{
		this.exclusionEventDao.deleteAllExpired();
	}

	@Override
	public boolean housekeepablesExist()
	{
		return false;
	}

    @Override
    public void updateAsHarvested(List<ExclusionEvent> events)
    {
        this.exclusionEventDao.updateAsHarvested(events);
    }
}
 