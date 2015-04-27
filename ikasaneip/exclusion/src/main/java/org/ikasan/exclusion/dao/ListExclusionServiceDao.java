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
package org.ikasan.exclusion.dao;

import org.apache.log4j.Logger;
import org.ikasan.exclusion.model.ExclusionEvent;

import java.util.*;

/**
 * List implementation of the ExclusionServiceDao.
 * @author Ikasan Development Team
 */
public class ListExclusionServiceDao
        implements ExclusionServiceDao<String,ExclusionEvent>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(ListExclusionServiceDao.class);

    /** actual exclusionEvent instances */
    LinkedHashMap<String,ExclusionEvent> blackList;

    /**
     * Constructor
     * @param blackList
     */
    public ListExclusionServiceDao(LinkedHashMap<String,ExclusionEvent> blackList)
    {
        this.blackList = blackList;
        if(blackList == null)
        {
            throw new IllegalArgumentException("backlist implementation cannot be 'null'");
        }
    }

    @Override
    public void add(ExclusionEvent exclusionEvent)
    {
        this.blackList.put(exclusionEvent.getIdentifier(), exclusionEvent);
    }

    @Override
    public void remove(String moduleName, String flowName, String identifier)
    {
        this.blackList.remove(identifier);
    }

    @Override
    public boolean contains(String moduleName, String flowName, String identifier)
    {
        return this.blackList.containsKey(identifier);
    }

    @Override
    public void deleteExpired()
    {
        List<String> expiredIdentifiers = new ArrayList<String>();

        long expiryTime = System.currentTimeMillis();
        for(Map.Entry<String,ExclusionEvent> entry:blackList.entrySet())
        {
            if(entry.getValue().getExpiry().longValue() < expiryTime)
            {
                expiredIdentifiers.add(entry.getKey());
            }
        }

        for(String expiredIdentifier:expiredIdentifiers)
        {
            blackList.remove(expiredIdentifier);
        }

        if(logger.isDebugEnabled())
        {
            logger.info("Deleted expired blacklist events for identifiers[" + expiredIdentifiers + "]");
        }
    }
}
