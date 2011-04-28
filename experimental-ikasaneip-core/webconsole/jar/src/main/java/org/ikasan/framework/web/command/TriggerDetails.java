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
package org.ikasan.framework.web.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ikasan.trigger.model.Trigger;

/**
 * Class representing the details for a trigger
 * 
 * @author Ikasan Development Team
 */
public class TriggerDetails implements Serializable
{
    /** Serial UID */
    private static final long serialVersionUID = -6834247440035517997L;

    /** Name of the flow element */
    private String flowElementName;

    /** Name of the flow */
    private String flowName;

    /** Name of the job */
    private String jobName;

    /** Name of the module */
    private String moduleName;

    /** Map of parameters */
    private Map<String, String> params = new HashMap<String, String>();

    /** Trigger relationship */
    private String relationship;

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     */
    public TriggerDetails(String moduleName, String flowName)
    {
        super();
        this.moduleName = moduleName;
        this.flowName = flowName;
    }

    /**
     * Get the flow element name
     * 
     * @return flow element name
     */
    public String getFlowElementName()
    {
        return flowElementName;
    }

    /**
     * Get the flow name
     * 
     * @return flow name
     */
    public String getFlowName()
    {
        return flowName;
    }

    /**
     * Get the job name
     * 
     * @return job name
     */
    public String getJobName()
    {
        return jobName;
    }

    /**
     * Get the module name
     * 
     * @return the module name
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Get the map of parameters
     * 
     * @return map of parameters
     */
    public Map<String, String> getParams()
    {
        return params;
    }

    /**
     * Get the trigger relationship
     * 
     * @return relationship
     */
    public String getRelationship()
    {
        return relationship;
    }

    /**
     * Set the flow element name
     * 
     * @param flowElementName - flow element name
     */
    public void setFlowElementName(String flowElementName)
    {
        this.flowElementName = flowElementName;
    }

    /**
     * Set the job name
     * 
     * @param jobName - The job name to set
     */
    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    /**
     * Set the map of parameters
     * 
     * @param params - map of parameters to set
     */
    public void setParams(Map<String, String> params)
    {
        this.params = params;
    }

    /**
     * Set the trigger relationship (before/after)
     * 
     * @param relationship the trigger relationship to set
     */
    public void setRelationship(String relationship)
    {
        this.relationship = relationship;
    }

    /**
     * Create a trigger from the details
     * 
     * @return Trigger
     */
    public Trigger createTrigger()
    {
        Trigger trigger = new Trigger(moduleName, flowName, relationship, jobName, flowElementName, params);
        return trigger;
    }
}
