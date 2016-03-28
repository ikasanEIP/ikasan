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
package org.ikasan.replay.service;

import java.util.Date;
import java.util.List;

import org.ikasan.replay.dao.ReplayDao;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.replay.ReplayManagementService;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayManagementServiceImpl implements ReplayManagementService<ReplayEvent, ReplayAudit>
{
	/** the underlying dao **/
	private ReplayDao replayDao;
	
	
	/**
	 * Constructor
	 * 
	 * @param replayDao
	 */
	public ReplayManagementServiceImpl(ReplayDao replayDao) 
	{
		super();
		this.replayDao = replayDao;
		if(this.replayDao == null)
		{
			throw new IllegalArgumentException("repalyDao cannot be null!");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayManagementService#getReplayEvents(java.util.List, java.util.List, java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
	 */
	@Override
	public List<ReplayEvent> getReplayEvents(List<String> moduleNames,
			List<String> flowNames, String eventId,
			Date fromDate, Date toDate) 
	{
		return this.replayDao.getReplayEvents(moduleNames, flowNames, eventId, fromDate, toDate);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayManagementService#getReplayAudits(java.lang.String, java.sql.Date, java.sql.Date)
	 */
	@Override
	public List<ReplayAudit> getReplayAudits(List<String> moduleNames, List<String> flowNames,
			String eventId, String user, Date startDate, Date endDate) 
	{
		return this.replayDao.getReplayAudits(moduleNames, flowNames, eventId, user, startDate, endDate);
	}

	

}
