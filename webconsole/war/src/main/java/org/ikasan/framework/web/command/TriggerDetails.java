/*
 * $Id: TriggerDetails.java 16798 2009-04-24 14:12:09Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/java/org/ikasan/framework/web/command/TriggerDetails.java $
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
package org.ikasan.framework.web.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ikasan.framework.flow.event.model.Trigger;

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
