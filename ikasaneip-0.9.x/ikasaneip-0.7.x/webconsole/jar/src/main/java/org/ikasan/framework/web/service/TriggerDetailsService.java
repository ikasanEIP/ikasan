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
package org.ikasan.framework.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.VisitingInvokerFlow;
import org.ikasan.framework.flow.event.listener.JobAwareFlowEventListener;
import org.ikasan.framework.flow.event.model.Trigger;
import org.ikasan.framework.flow.event.model.TriggerRelationship;
import org.ikasan.framework.flow.event.service.FlowEventJob;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.ikasan.framework.web.command.TriggerDetails;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

/**
 * The service layer for the trigger details
 * 
 * @author Ikasan Development Team
 */
public class TriggerDetailsService
{
    /** The job aware flow event listener */
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    /** The container for the modules */
    private ModuleService moduleService;

    /**
     * Constructor
     * 
     * @param jobAwareFlowEventListener - The job aware flow event listener
     * @param moduleService - The provider service for the modules
     */
    public TriggerDetailsService(JobAwareFlowEventListener jobAwareFlowEventListener, ModuleService moduleService)
    {
        super();
        this.jobAwareFlowEventListener = jobAwareFlowEventListener;
        this.moduleService = moduleService;
    }

    /**
     * Create a new Trigger details
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param flowElementName - The name of the flow element
     * @param relationship - The relationship
     * @return A new TriggerDetails
     */
    public TriggerDetails createTriggerDetails(String moduleName, String flowName, String flowElementName,
            String relationship)
    {
        TriggerDetails triggerDetails = new TriggerDetails(moduleName, flowName);
        if (flowElementName != null)
        {
            triggerDetails.setFlowElementName(flowElementName);
        }
        if (relationship != null)
        {
            triggerDetails.setRelationship(relationship);
        }
        return triggerDetails;
    }

    /**
     * Get a list of the job names
     * 
     * @return list of job names
     */
    public List<String> getJobNames()
    {
        List<String> result = new ArrayList<String>();
        for (String jobName : jobAwareFlowEventListener.getRegisteredJobs().keySet())
        {
            result.add(jobName);
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Get the trigger relationships
     * 
     * @return list of trigger relationships
     */
    public static List<String> getRelationships()
    {
        List<String> result = new ArrayList<String>();
        for (TriggerRelationship relationship : TriggerRelationship.values())
        {
            result.add(relationship.getDescription());
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Get a List of parameter names for a job
     * 
     * @param jobName - The job name to get the parameters for
     * @return list of a parameter names for the job
     */
    public List<String> getParameterNames(String jobName)
    {
        List<String> result = new ArrayList<String>();
        FlowEventJob flowEventJob = jobAwareFlowEventListener.getRegisteredJobs().get(jobName);
        if (flowEventJob != null)
        {
            result.addAll(flowEventJob.getParameters());
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Get a list of the flow element names for a flow in a module
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @return list of flow element names
     */
    public List<String> getFlowElementNames(String moduleName, String flowName)
    {
        List<String> result = new ArrayList<String>();
        Module module = moduleService.getModule(moduleName);
        Flow flow = module.getFlows().get(flowName);
        if (flow instanceof VisitingInvokerFlow)
        {
            List<FlowElement> flowElements = ((VisitingInvokerFlow) flow).getFlowElements();
            for (FlowElement flowElement : flowElements)
            {
                result.add(flowElement.getComponentName());
            }
        }
        return result;
    }

    /**
     * Create a trigger given details and a context
     * 
     * @param triggerDetails - The trigger details to use
     * @param context - The context to create it it
     * @return A new Trigger
     */
    public String createTrigger(TriggerDetails triggerDetails, RequestContext context)
    {
        // Find the job from the jobName
        String jobName = triggerDetails.getJobName();
        FlowEventJob flowEventJob = jobAwareFlowEventListener.getRegisteredJobs().get(jobName);
        MessageContext messageContext = context.getMessageContext();
        // If can't even find the job then thats a big problem
        if (flowEventJob == null)
        {
            messageContext.addMessage(new MessageBuilder().error().source("jobName").defaultText(
                "Unknown job:" + jobName).build());
        }
        else
        {
            // Call the job's validate method with the parameters and see if there are any errors
            Map<String, String> validationErrors = flowEventJob.validateParameters(triggerDetails.getParams());
            if ((validationErrors != null) && (!validationErrors.isEmpty()))
            {
                // Convert errors to front end messages
                for (String parameterName : validationErrors.keySet())
                {
                    messageContext.addMessage(new MessageBuilder().error().source(parameterName).defaultText(
                        validationErrors.get(parameterName)).build());
                }
            }
        }
        if (!messageContext.hasErrorMessages())
        {
            Trigger trigger = triggerDetails.createTrigger();
            jobAwareFlowEventListener.addDynamicTrigger(trigger);
            return "success";
        }
        return "error";
    }
}
