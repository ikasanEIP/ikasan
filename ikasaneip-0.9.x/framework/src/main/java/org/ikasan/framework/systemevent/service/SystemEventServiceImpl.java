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
package org.ikasan.framework.systemevent.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.systemevent.dao.SystemEventDao;
import org.ikasan.framework.systemevent.model.SystemEvent;



/**
 * SystemFlowEvent service implementation
 * 
 * @author Ikasan Development Team
 *
 */
public class SystemEventServiceImpl implements SystemEventService
{
	private Logger logger = Logger.getLogger(SystemEventServiceImpl.class);
	
	/**
	 * Underlying data access object
	 */
	private SystemEventDao systemEventDao;
	
	/**
	 * no of minutes for this event to be kept until eligible for housekeep
	 * If null, then no expiry
	 */
	private Long eventExpiryMinutes;
	
	/**
	 * Constructor
	 * @param systemFlowEventDao
	 * @param eventExpiryMinutes - no of minutes for this event to be kept until eligible for housekeep
	 */
	public SystemEventServiceImpl(SystemEventDao systemEventDao, Long eventExpiryMinutes) {
		this.systemEventDao = systemEventDao;
		this.eventExpiryMinutes = eventExpiryMinutes;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.service.SystemFlowEventService#logSystemFlowEvent(java.lang.String, java.lang.String, java.util.Date, java.lang.String)
	 */
	public void logSystemEvent(String subject, String action, String actor){
		Date now = new Date();
		Date expiry = null;
		if (eventExpiryMinutes!=null){
			expiry = new Date(now.getTime()+(60000*eventExpiryMinutes));	
		}
		systemEventDao.save(new SystemEvent(subject, action, now, actor,expiry));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.service.SystemFlowEventService#listSystemFlowEvents(java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	public PagedSearchResult<SystemEvent> listSystemEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,String subject, String action, Date timestampFrom, Date timestampTo, String actor) {

		return systemEventDao.find(pageNo, pageSize, orderBy, orderAscending, subject,action, timestampFrom, timestampTo, actor);

	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.service.SystemFlowEventService#housekeep()
	 */
	public void housekeep() {
		long before = System.currentTimeMillis();
		systemEventDao.deleteExpired();
		long after = System.currentTimeMillis();
		logger.info("housekeep completed in ["+(after-before)+"]ms");
		
	}
}
