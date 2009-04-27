/*
 * $Id: FlowEventJob.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/flow/event/service/FlowEventJob.java $
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

import java.util.List;
import java.util.Map;

import org.ikasan.framework.component.Event;

/**
 * Interface for objects that respond to Flow events
 * 
 * @author Ikasan Development Team
 */
public interface FlowEventJob
{
    /**
     * Execute the Flow Event Job
     * 
     * @param location - The location of the FlowEvent
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param event - The Event
     * @param params - The parameters for the Job
     */
    public void execute(String location, String moduleName, String flowName, Event event, Map<String, String> params);

    /**
     * Returns a List of all specifiable parameters that will be respected when
     * passed in on execute
     * 
     * @return List of parameter names
     */
    public List<String> getParameters();

    /**
     * Validates a map of parameters that may be later used as arguments to the
     * the execute method
     * 
     * @param params - The parameters to validate
     * @return Map of validation errors, error messages mapped by parameterNames
     *         may be null or empty if validation passes
     */
    public Map<String, String> validateParameters(Map<String, String> params);
}
