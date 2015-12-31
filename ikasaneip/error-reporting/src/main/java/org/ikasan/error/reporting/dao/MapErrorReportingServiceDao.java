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

import org.apache.log4j.Logger;
import org.ikasan.error.reporting.model.ErrorOccurrence;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Map implementation of the ErrorReportingServiceDao.
 * @author Ikasan Development Team
 */
public class MapErrorReportingServiceDao<T>
        implements ErrorReportingServiceDao<ErrorOccurrence>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(MapErrorReportingServiceDao.class);

    /** actual errorOccurrence instances */
    LinkedHashMap<String,ErrorOccurrence> errorOccurrences;

    /**
     * Constructor
     * @param errorOccurrences
     */
    public MapErrorReportingServiceDao(LinkedHashMap<String, ErrorOccurrence> errorOccurrences)
    {
        this.errorOccurrences = errorOccurrences;
        if(errorOccurrences == null)
        {
            throw new IllegalArgumentException("errorOccurrences implementation cannot be 'null'");
        }
    }

    @Override
    public ErrorOccurrence find(String uri)
    {
        return this.errorOccurrences.get(uri);
    }

    @Override
    public void save(ErrorOccurrence errorOccurrence)
    {
        this.errorOccurrences.put(errorOccurrence.getUri(), errorOccurrence);
    }

    @Override
    public void deleteExpired()
    {
        List<String> expiredIdentifiers = new ArrayList<String>();

        long expiryTime = System.currentTimeMillis();
        for(Map.Entry<String,ErrorOccurrence> entry:errorOccurrences.entrySet())
        {
            if(entry.getValue().getExpiry() < expiryTime)
            {
                expiredIdentifiers.add(entry.getKey());
            }
        }

        for(String expiredIdentifier:expiredIdentifiers)
        {
            errorOccurrences.remove(expiredIdentifier);
        }

        if(logger.isDebugEnabled())
        {
            logger.info("Deleted expired errorOccurrences events for identifiers[" + expiredIdentifiers + "]");
        }
    }

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorReportingServiceDao#find(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ErrorOccurrence> find(List<String> moduleName,
			List<String> flowName, List<String> flowElementname,
			Date startDate, Date endDate, int size)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
