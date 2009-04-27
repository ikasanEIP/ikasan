/*
 * $Id: Trigger.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/flow/event/model/Trigger.java $
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
package org.ikasan.framework.flow.event.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class the represent s a trigger for a Job
 * 
 * @author Ikasan Development Team
 */
public class Trigger
{
    /**
     * Name of the <code>FlowElement</code>, if any, to which this
     * <code>Trigger</code> applies If null, this trigger is deemed to apply to
     * the <code>Flow</code> itself
     */
    private String flowElementName;

    /** Name of the <code>Flow</code> to which this <code>Trigger</code> applies */
    private String flowName;

    /** Unique identifier */
    private Long id;

    /** Name of the <code>Job</code> to which this <code>Trigger</code> refers */
    private String jobName;

    /**
     * Name of the <code>Module</code> to which this <code>Trigger</code>
     * applies
     */
    private String moduleName;

    /** Additional parameters to be used when invoking jobs */
    private Map<String, String> params = new HashMap<String, String>();

    /** Either before or after */
    private TriggerRelationship relationship;

    /** (Hibernate) Constructor */
    @SuppressWarnings("unused")
    private Trigger()
    {
        // Constructor used by Hibernate
    }

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param relationshipDescription - The relationship description
     * @param jobName - The name of the Job to trigger
     */
    public Trigger(String moduleName, String flowName, String relationshipDescription, String jobName)
    {
        super();
        this.jobName = jobName;
        this.moduleName = moduleName;
        setRelationshipDescription(relationshipDescription);
        this.flowName = flowName;
    }

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param relationshipDescription - The relationship description
     * @param jobName - The name of the Job to trigger
     * @param params - The parameters for the trigger
     */
    public Trigger(String moduleName, String flowName, String relationshipDescription, String jobName, Map<String, String> params)
    {
        this(moduleName, flowName, relationshipDescription, jobName);
        this.params.putAll(params);
    }

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param relationshipDescription - The relationship description
     * @param jobName - The name of the Job to trigger
     * @param flowElementName - The name of the Flow Element
     */
    public Trigger(String moduleName, String flowName, String relationshipDescription, String jobName, String flowElementName)
    {
        this(moduleName, flowName, relationshipDescription, jobName);
        this.flowElementName = flowElementName;
    }

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param relationshipDescription - The relationship description
     * @param jobName - The name of the Job to trigger
     * @param flowElementName - The name of the Flow Element
     * @param params - The parameters for the trigger
     */
    public Trigger(String moduleName, String flowName, String relationshipDescription, String jobName, String flowElementName, Map<String, String> params)
    {
        this(moduleName, flowName, relationshipDescription, jobName, flowElementName);
        this.params.putAll(params);
    }

    /**
     * Returns true if this is to apply to a <code>FlowElement</code>, otherwise
     * false
     * 
     * @return true if this is to apply to a <code>FlowElement</code>, otherwise
     * false
     */
    public boolean appliesToFlowElement()
    {
        return flowElementName != null;
    }

    /**
     * Accessor for flowElementName
     * 
     * @return The flow element name
     */
    public String getFlowElementName()
    {
        return flowElementName;
    }

    /**
     * Accessor for flowName
     * 
     * @return flowName
     */
    public String getFlowName()
    {
        return flowName;
    }

    /**
     * Accessor for id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Accessor for jobName
     * 
     * @return jobName
     */
    public String getJobName()
    {
        return jobName;
    }

    /**
     * Accessor for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Accessor for params
     * 
     * @return params
     */
    public Map<String, String> getParams()
    {
        return params;
    }

    /**
     * Accessor for relationship
     * 
     * @return relationship
     */
    public TriggerRelationship getRelationship()
    {
        return relationship;
    }

    /**
     * (Hibernate) Accessor for relationship
     * 
     * @return The relationship description
     */
    @SuppressWarnings("unused")
    private String getRelationshipDescription()
    {
        return relationship.getDescription();
    }

    /**
     * (Hibernate) Setter for Id
     * 
     * @param id - The trigger id to set
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * (Hibernate) Setter for jobName
     * 
     * @param jobName - The name of the job to set
     */
    @SuppressWarnings("unused")
    private void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    /**
     * (Hibernate) Setter for moduleName
     * 
     * @param moduleName - The name of the module to set
     */
    @SuppressWarnings("unused")
    private void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * (Hibernate) Setter for flowName
     * 
     * @param flowName - The name of the flow to set
     */
    @SuppressWarnings("unused")
    private void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * (Hibernate) Setter for flowElementName
     * 
     * @param flowElementName - The name of the flow element to set
     */
    @SuppressWarnings("unused")
    private void setFlowElementName(String flowElementName)
    {
        this.flowElementName = flowElementName;
    }

    /**
     * (Hibernate) Setter for parameters
     * 
     * @param params - The parameters to set
     */
    @SuppressWarnings("unused")
    private void setParams(Map<String, String> params)
    {
        this.params = params;
    }

    /**
     * (Hibernate) Setter for relationship
     * 
     * @param relationshipDescription - The relationship description to set
     */
    private void setRelationshipDescription(String relationshipDescription)
    {
        TriggerRelationship thisRelationship = TriggerRelationship.get(relationshipDescription);
        if (thisRelationship == null)
        {
            throw new IllegalArgumentException("No such relationship:" + relationship);
        }
        this.relationship = thisRelationship;
    }

    /**
     * String representation
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(getClass().getName() + "[");
        sb.append("id=");
        sb.append(id);
        sb.append(",");
        sb.append("moduleName=");
        sb.append(moduleName);
        sb.append(",");
        sb.append("flowName=");
        sb.append(flowName);
        sb.append(",");
        sb.append("flowElementName=");
        sb.append(flowElementName);
        sb.append(",");
        sb.append("params=");
        sb.append(params);
        sb.append(",");
        sb.append("jobName=");
        sb.append(jobName);
        sb.append(",");
        sb.append("relationship=");
        sb.append(relationship);
        sb.append("]");
        return sb.toString();
    }
}
