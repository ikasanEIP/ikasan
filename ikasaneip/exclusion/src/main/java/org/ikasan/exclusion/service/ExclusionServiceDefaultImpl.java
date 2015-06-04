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

import org.ikasan.exclusion.dao.BlackListDao;
import org.ikasan.exclusion.dao.ExclusionEventDao;
import org.ikasan.exclusion.model.BlackListEvent;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.serialiser.Serialiser;

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
    BlackListDao<String,BlackListEvent> blackListDao;

    /** handle to the underlying DAO */
    ExclusionEventDao<String,ExclusionEvent> exclusionEventDao;

    /** need a serialiser to serialise the incoming event payload of T */
    Serialiser<Object,byte[]> serialiser;

    /** time to live in the blacklist */
    long timeToLive = ExclusionService.DEFAULT_TIME_TO_LIVE;

    /**
     * Constructor
     * @param blackListDao
     */
    public ExclusionServiceDefaultImpl(String moduleName, String flowName, BlackListDao blackListDao, ExclusionEventDao exclusionEventDao, Serialiser<Object,byte[]> serialiser)
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

        this.blackListDao = blackListDao;
        if(blackListDao == null)
        {
            throw new IllegalArgumentException("exclusionServiceDao cannot be 'null'");
        }

        this.exclusionEventDao = exclusionEventDao;
        if(exclusionEventDao == null)
        {
            throw new IllegalArgumentException("exclusionEventDao cannot be 'null'");
        }

        this.serialiser = serialiser;
        if(serialiser == null)
        {
            throw new IllegalArgumentException("serialiser cannot be 'null'");
        }
    }

    @Override
    public boolean isBlackListed(FlowEvent<String,?> event)
    {
        return this.blackListDao.contains(this.moduleName, this.flowName, event.getIdentifier());
    }

    @Override
    public void park(FlowEvent<String,?> event)
    {
        String lifeIdentifier = event.getIdentifier();
        BlackListEvent blacklistEvent = this.blackListDao.find(this.moduleName, this.flowName, lifeIdentifier);
        byte[] bytes = serialiser.serialise(event.getPayload());
        String uri = blacklistEvent.getErrorUri();
        ExclusionEvent exclusionEvent = newExclusionEvent(lifeIdentifier, bytes, uri);
        this.exclusionEventDao.save(exclusionEvent);
    }

    @Override
    public void addBlacklisted(FlowEvent<String,?> event, String errorUri)
    {
        BlackListEvent blackListEvent = new BlackListEvent(this.moduleName, this.flowName, event.getIdentifier(), errorUri, this.timeToLive);
        this.blackListDao.insert(blackListEvent);
    }

    @Override
    public void removeBlacklisted(FlowEvent<String,?> event)
    {
        this.blackListDao.delete(this.moduleName, this.flowName, event.getIdentifier());
    }

    @Override
    public void setTimeToLive(Long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    @Override
    public void housekeep()
    {
        this.blackListDao.deleteExpired();
    }

    /**
     * Factory method for creating new ExclusionEvent instances.
     * @param identifier
     * @param eventBytes
     * @param errorUri
     * @return
     */
    protected ExclusionEvent newExclusionEvent(String identifier, byte[] eventBytes, String errorUri)
    {
        return new ExclusionEvent(moduleName, flowName, identifier, eventBytes, errorUri);

    }
}