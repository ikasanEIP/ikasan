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
package org.ikasan.nonfunctional.test.util;

import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wiretap test utility methods.
 *
 * @author Ikasan Development Team
 */
public class WiretapTestUtil
{
    private WiretapService<WiretapEvent,PagedSearchResult, Long> wiretapService;
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    /**
     * Constructor
     * @param wiretapService
     * @param jobAwareFlowEventListener
     */
    public WiretapTestUtil(WiretapService<WiretapEvent,PagedSearchResult, Long> wiretapService, JobAwareFlowEventListener jobAwareFlowEventListener)
    {
        this.wiretapService = wiretapService;
        if(wiretapService == null)
        {
            throw new IllegalArgumentException("wiretapService cannot be 'null'");
        }

        this.jobAwareFlowEventListener = jobAwareFlowEventListener;
        if(wiretapService == null)
        {
            throw new IllegalArgumentException("jobAwareFlowEventListener cannot be 'null'");
        }
    }

    public PagedSearchResult<WiretapEvent> getWiretaps(String moduleName, String flowName, TriggerRelationship relationship, String componentName, int expectedResultSize)
    {
        Set<String> moduleNames = new HashSet<String>();
        moduleNames.add(moduleName);
        String location = relationship.name().toLowerCase() + " " + componentName;
        int pageSize = expectedResultSize;
        if(pageSize == 0)
        {
            pageSize++;
        }

        return wiretapService.findWiretapEvents(0, pageSize, null,
                true, moduleNames, flowName, location, null, null,
                null,null, null );
    }

    /**
     * Add wiretap listeners with default time to live of 10,0000 minutes.
     * @param moduleName
     * @param flowName
     * @param relationship
     * @param componentName
     */
    public void addWiretapTrigger(String moduleName, String flowName, TriggerRelationship relationship, String componentName)
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("timeToLive", "100000");
        addWiretapTrigger(moduleName, flowName, relationship, componentName, params);
    }

    /**
     * Add wiretap listeners
     * @param moduleName
     * @param flowName
     * @param relationship
     * @param componentName
     * @param params
     */
    public void addWiretapTrigger(String moduleName, String flowName, TriggerRelationship relationship, String componentName, Map<String,String> params)
    {
        jobAwareFlowEventListener.addDynamicTrigger( new TriggerImpl(moduleName, flowName, relationship.name(), "wiretapJob", componentName, params));
    }
}
