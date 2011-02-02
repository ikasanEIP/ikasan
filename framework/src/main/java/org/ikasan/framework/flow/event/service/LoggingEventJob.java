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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Simple <code>FlowEventJob</code> implementation that simply logs the current
 * <code>FlowEvent</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class LoggingEventJob implements FlowEventJob
{
    /**
     * logger instance
     */
    private static final Logger logger = Logger.getLogger(LoggingEventJob.class);

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
        logger.info("FlowEvent module=[" + moduleName + "] flow=[" + flowName + "] location=[" + location + "] event=[" + event + "]");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.event.service.FlowEventJob#getParameters()
     */
    public List<String> getParameters()
    {
        return new ArrayList<String>();
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
        return null;
    }
}
