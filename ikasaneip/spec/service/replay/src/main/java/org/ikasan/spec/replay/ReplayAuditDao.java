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
package org.ikasan.spec.replay;

import java.util.Date;
import java.util.List;

/**
 * Data Access interface for <code>ReplayEvent</code> instances
 *
 * @author Ikasan Development Team
 *
 */
public interface ReplayAuditDao<AUDIT, AUDIT_EVENT>
{

	/**
	 * Method to save or update a ReplayAudit.
	 *
	 * @param replayAudit
	 */
	public void saveOrUpdateAudit(AUDIT replayAudit);

	/**
	 * Method to save or update a ReplayAuditEvent.
	 *
	 * @param replayAuditEvent
	 */
	public void saveOrUpdate(AUDIT_EVENT replayAuditEvent);

    /**
     *  Get a list of ReplayAudit depending upon search criteria.
     *
     * @param moduleNames
     * @param flowNames
     * @param eventId
     * @param user
     * @param startDate
     * @param endDate
     * @return
     */
    public List<AUDIT> getReplayAudits(List<String> moduleNames, List<String> flowNames,
                                       String eventId, String user, Date startDate, Date endDate);

    /**
     * Get a ReplayAudit by its id.
     *
     * @param id
     * @return
     */
    public AUDIT getReplayAuditById(Long id);

    /**
     * Get a ReplayAuditEvents by their audit id.
     *
     * @param id
     * @return
     */
    public List<AUDIT_EVENT> getReplayAuditEventsByAuditId(Long id);

    /**
     * Get the number of ReplayAuditEvents by their audit id.
     *
     * @param id
     * @return
     */
    public Long getNumberReplayAuditEventsByAuditId(Long id);

}
