/*
 * $Id$
 * $URL$
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
package org.ikasan.framework.flow.event.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.service.WiretapService;

/**
 * <code>FlowEventJob</code> for invoking the WiretapService
 * 
 * requires the parameter 'timeToLive' to be passed on execution
 * 
 * @author Ikasan Development Team
 * 
 */
public class WiretapEventJob implements FlowEventJob
{  
    /**
     * underlying service
     */
    private WiretapService wiretapService;

    /**
     * Time To Live parameter specifies length of time in minutes for
     * Wiretapped Events to be kept
     */
    public static final String TIME_TO_LIVE_PARAM = "timeToLive";

    /**
     * Default Time To Live
     */
    private static final Long ONE_WEEK = 60 * 24 * 7l;

    /**
     * logger instance
     */
    private static final Logger logger = Logger.getLogger(WiretapEventJob.class);

    /**
     * List of names of parameters supported by this job
     */
    private static final List<String> parameterNames = new ArrayList<String>();
    static
    {
        parameterNames.add(TIME_TO_LIVE_PARAM);
    }

    /**
     * Constructor
     * 
     * @param wiretapService The wiretap service to use
     */
    public WiretapEventJob(WiretapService wiretapService)
    {
        super();
        this.wiretapService = wiretapService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.service.FlowEventJob#execute(java.lang
     * .String, java.lang.String, java.lang.String,
     * org.ikasan.framework.component.Event, java.util.Map)
     */
    public void execute(String location, String moduleName, String flowName, Event event, Map<String, String> params)
    {
        Long timeToLive = ONE_WEEK;
        String timeToLiveString = params.get(TIME_TO_LIVE_PARAM);
        if (timeToLiveString != null)
        {
            try
            {
                timeToLive = Long.parseLong(timeToLiveString);
            }
            catch (NumberFormatException nfe)
            {
                logger.error("timeToLive could not be parsed [" + timeToLiveString + "]");
            }
        }
        logger.info("about to wiretap with timeToLive=[" + timeToLive + "]");
        wiretapService.tapEvent(event, location, moduleName, flowName, timeToLive);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.event.service.FlowEventJob#getParameters()
     */
    public List<String> getParameters()
    {
        return new ArrayList<String>(parameterNames);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.service.FlowEventJob#validateParameters
     * (java.util.Map)
     */
    public Map<String, String> validateParameters(Map<String, String> params)
    {
        Map<String, String> result = new HashMap<String, String>();
        if (!params.containsKey(TIME_TO_LIVE_PARAM))
        {
            result.put(TIME_TO_LIVE_PARAM, TIME_TO_LIVE_PARAM + " is mandatory");
        }
        else
        {
            String timeToLiveString = params.get(TIME_TO_LIVE_PARAM);
            try
            {
                Long timeToLive = Long.parseLong(timeToLiveString);
                if (timeToLive < 0)
                {
                    result.put(TIME_TO_LIVE_PARAM, TIME_TO_LIVE_PARAM + " must be greater than 0");
                }
            }
            catch (NumberFormatException nfe)
            {
                result.put(TIME_TO_LIVE_PARAM, TIME_TO_LIVE_PARAM + " is not a valid number");
            }
        }
        return result;
    }
}
