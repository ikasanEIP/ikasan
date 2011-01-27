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
package org.ikasan.framework.flow.event.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.ikasan.spec.flow.event.FlowEvent;

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
     * Wiretapped FlowEvents to be kept
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
     * org.ikasan.spec.flow.event.FlowEvent, java.util.Map)
     */
    public void execute(String location, String moduleName, String flowName, FlowEvent event, Map<String, String> params)
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
