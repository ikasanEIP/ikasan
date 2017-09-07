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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.exclusion.model.BlackListEvent;

import java.util.*;

/**
 * Map implementation of the BlackListDao.
 * @author Ikasan Development Team
 */
public class MapBlackListDao implements BlackListDao<String,BlackListEvent>
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(MapBlackListDao.class);

    /** blacklist instances */
    LinkedHashMap<String,BlackListEvent> blackList;

    /**
     * Constructor
     * @param blackList
     */
    public MapBlackListDao(LinkedHashMap<String, BlackListEvent> blackList)
    {
        this.blackList = blackList;
        if(blackList == null)
        {
            throw new IllegalArgumentException("backlist implementation cannot be 'null'");
        }
    }

    @Override
    public void insert(BlackListEvent blackListEvent)
    {
        this.blackList.put(blackListEvent.getIdentifier(), blackListEvent);
    }

    @Override
    public void delete(String moduleName, String flowName, String identifier)
    {
        this.blackList.remove(identifier);
    }

    @Override
    public boolean contains(String moduleName, String flowName, String identifier)
    {
        return this.blackList.containsKey(identifier);
    }

    @Override
    public BlackListEvent find(String moduleName, String flowName, String identifier)
    {
        return this.blackList.get(identifier);
    }

    @Override
    public void deleteExpired()
    {
        List<String> expiredIdentifiers = new ArrayList<>();

        long expiryTime = System.currentTimeMillis();
        for(Map.Entry<String,BlackListEvent> entry:blackList.entrySet())
        {
            if(entry.getValue().getExpiry() < expiryTime)
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

    @Override
    public int count() {
        return blackList.size();
    }
}
