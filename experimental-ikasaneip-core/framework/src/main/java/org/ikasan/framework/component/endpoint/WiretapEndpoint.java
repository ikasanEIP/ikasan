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
package org.ikasan.framework.component.endpoint;

import org.ikasan.core.component.endpoint.Endpoint;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.service.WiretapService;

/**
 * Wiretap endpoint component.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEndpoint implements Endpoint
{
    /** One week in milliseconds constant */
    private static final long ONE_WEEK_IN_MILLISECONDS = 1000 * 3600 * 24 * 7;

    /** TODO - source module name from elsewhere */
    private String moduleName;

    /** TODO - source flow name from elsewhere */
    private String flowName;

    /** TODO - source component name from elsewhere */
    private String componentName;

    /** WiretapService */
    private WiretapService wiretapService;

    /**
     * How long (in milliseconds) should wiretapped events be allowed to live
     */
    private long wiretapEventTimeToLive = ONE_WEEK_IN_MILLISECONDS;

    /**
     * Constructor
     * 
     * @param moduleName The name of the module we are wiretapping
     * @param flowName The name of the flow we are wiretapping
     * @param componentName The name of the component we are wiretapping
     * @param wiretapService The wiretap service to use
     */
    public WiretapEndpoint(String moduleName, String flowName, String componentName, WiretapService wiretapService)
    {
        this.moduleName = moduleName;
        if (this.moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'.");
        }
        this.flowName = flowName;
        if (this.flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null'.");
        }
        this.componentName = componentName;
        if (this.componentName == null)
        {
            throw new IllegalArgumentException("componentName cannot be 'null'.");
        }
        this.wiretapService = wiretapService;
        if (this.wiretapService == null)
        {
            throw new IllegalArgumentException("wiretapService cannot be 'null'.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.endpoint.Endpoint#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event)
    {
        wiretapService.tapEvent(event, componentName, moduleName, flowName, wiretapEventTimeToLive);
    }

    /**
     * Set how long the wire tap has to live
     * 
     * @param wiretapEventTimeToLive The time in milliseconds the wiretap has to live
     */
    public void setWiretapEventTimeToLive(long wiretapEventTimeToLive)
    {
        if (wiretapEventTimeToLive <= 0)
        {
            throw new IllegalArgumentException("wiretapEventTimeToLive must be > 0");
        }
        this.wiretapEventTimeToLive = wiretapEventTimeToLive;
    }
}
