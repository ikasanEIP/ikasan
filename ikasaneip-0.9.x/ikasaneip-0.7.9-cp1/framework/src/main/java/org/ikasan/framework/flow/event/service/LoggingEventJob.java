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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;

/**
 * Simple <code>FlowEventJob</code> implementation that simply logs the current
 * <code>Event</code>
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
     * org.ikasan.framework.component.Event, java.util.Map)
     */
    public void execute(String location, String moduleName, String flowName, Event event, Map<String, String> params)
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
