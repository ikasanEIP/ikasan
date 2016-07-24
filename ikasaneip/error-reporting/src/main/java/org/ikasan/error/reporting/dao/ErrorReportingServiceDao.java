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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

/**
 * Error Reporting Service Data Access Contract.
 * @author Ikasan Development Team
 */
public interface ErrorReportingServiceDao<EVENT, IDENTIFIER>
{
    /**
     * Find an error reporting event instance from the incoming uri.
     * @param uri
     * @return EVENT
     */
    public EVENT find(IDENTIFIER uri);

    /**
     * Find a map of error reporting event instances from the incoming uris.
     *
     * @param uris
     * @return EVENT
     */
    public Map<IDENTIFIER, EVENT> find(List<IDENTIFIER> uris);

    /**
     * Find an error reporting events based on a list of moduleName, flowName and flowElementName
     * as well as a date range.
     * 
     * @param moduleName
     * @param flowName
     * @param flowElementname
     * @param startDate
     * @param endDate
     * 
     * @return
     */
    public List<EVENT> find(List<String> moduleName, List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate, int size);
    
    /**
     * Find an error reporting events based on a list of moduleName, flowName and flowElementName
     * as well as a date range.
     * 
     * @param moduleName
     * @param flowName
     * @param flowElementname
     * @param startDate
     * @param endDate
     * 
     * @return
     */
    public List<EVENT> find(List<String> moduleName, List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate, String action, String exceptionClass, int size);
    
    /**
     * Helper method to return the row count based on the criteria.
     * 
     * @param moduleName
     * @param flowName
     * @param flowElementname
     * @param startDate
     * @param endDate
     * @return
     */
    public Long rowCount(List<String> moduleName, List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate);

    /**
     * Save the incoming EVENT.
     * @param event
     */
    public void save(EVENT event);

    /**
     * Support delete of expired error reporting events.
     */
    public void deleteExpired();
}
