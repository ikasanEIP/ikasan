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
package org.ikasan.history.model;

import org.ikasan.spec.history.ComponentHistoryEvent;
import org.ikasan.spec.history.MessageHistoryEvent;

import java.util.List;

/**
 * Implementation of a MessageHistoryEvent based on a String lifeIdentifier from a Flow
 *
 * @author Ikasan Development Team
 */
public class MessageHistoryFlowEvent implements MessageHistoryEvent<String>
{
    String moduleName, flowName, lifeIdentifier, relatedLifeIdentifier;
    List<? extends ComponentHistoryEvent> componentHistoryEvents;
    long startTimeMillis, endTimeMillis, expiry, id;

    /** Required by the ORM... */
    protected MessageHistoryFlowEvent()
    {
    }

    public MessageHistoryFlowEvent(String moduleName, String flowName, String lifeIdentifier, String relatedLifeIdentifier,
            List<? extends ComponentHistoryEvent> componentHistoryEvents,
            long startTimeMillis, long endTimeMillis, long expiry)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.lifeIdentifier = lifeIdentifier;
        this.relatedLifeIdentifier = relatedLifeIdentifier;
        this.componentHistoryEvents = componentHistoryEvents;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.expiry = expiry;
    }

    @Override
    public String getModuleName()
    {
        return moduleName;
    }

    @Override
    public String getFlowName()
    {
        return flowName;
    }

    @Override
    public String getLifeIdentifier()
    {
        return lifeIdentifier;
    }

    @Override
    public String getRelatedLifeIdentifier()
    {
        return relatedLifeIdentifier;
    }

    @Override
    public List<? extends ComponentHistoryEvent> getComponentHistoryEvents()
    {
        return componentHistoryEvents;
    }

    @Override
    public long getStartTimeMillis()
    {
        return startTimeMillis;
    }

    @Override
    public long getEndTimeMillis()
    {
        return endTimeMillis;
    }

    @Override
    public long getExpiry()
    {
        return expiry;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public void setLifeIdentifier(String lifeIdentifier)
    {
        this.lifeIdentifier = lifeIdentifier;
    }

    public void setRelatedLifeIdentifier(String relatedLifeIdentifier)
    {
        this.relatedLifeIdentifier = relatedLifeIdentifier;
    }

    public void setComponentHistoryEvents(List<ComponentHistoryEvent> componentHistoryEvents)
    {
        this.componentHistoryEvents = componentHistoryEvents;
    }

    public void setStartTimeMillis(long startTimeMillis)
    {
        this.startTimeMillis = startTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis)
    {
        this.endTimeMillis = endTimeMillis;
    }

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }
}
