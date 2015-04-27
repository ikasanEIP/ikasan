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

import org.ikasan.exclusion.dao.ExclusionServiceDao;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Default implementation of the ExclusionService.
 *
 * @author Ikasan Development Team
 */
public class ExclusionServiceDefaultImpl implements ExclusionService<FlowEvent<String,?>>
{
    /** module name */
    String moduleName;

    /** flowName */
    String flowName;

    /** handle to the underlying DAO */
    ExclusionServiceDao<String,ExclusionEvent> exclusionServiceDao;

    /** allow override of timeToLive */
    Long timeToLive = ExclusionService.DEFAULT_TIME_TO_LIVE;

    /**
     * Constructor
     * @param exclusionServiceDao
     */
    public ExclusionServiceDefaultImpl(String moduleName, String flowName, ExclusionServiceDao exclusionServiceDao)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }

        this.flowName = flowName;
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null'");
        }

        this.exclusionServiceDao = exclusionServiceDao;
        if(exclusionServiceDao == null)
        {
            throw new IllegalArgumentException("exclusionServiceDao cannot be 'null'");
        }
    }

    @Override
    public boolean isBlackListed(FlowEvent<String,?> event)
    {
        return this.exclusionServiceDao.contains(this.moduleName, this.flowName, event.getIdentifier());
    }

    @Override
    public void addBlacklisted(FlowEvent<String,?> event)
    {
        ExclusionEvent exclusionEvent = new ExclusionEvent(this.moduleName, this.flowName, event.getIdentifier(), timeToLive.longValue());
        this.exclusionServiceDao.add(exclusionEvent);
    }

    @Override
    public void removeBlacklisted(FlowEvent<String,?> event)
    {
        this.exclusionServiceDao.remove(this.moduleName, this.flowName, event.getIdentifier());
    }

    @Override
    public void setTimeToLive(Long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    @Override
    public void housekeep()
    {
        this.exclusionServiceDao.deleteExpired();
    }

}