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
