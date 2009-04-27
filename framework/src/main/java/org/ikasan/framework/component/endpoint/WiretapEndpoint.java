/* 
 * $Id: WiretapEndpoint.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/endpoint/WiretapEndpoint.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.component.endpoint;

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
