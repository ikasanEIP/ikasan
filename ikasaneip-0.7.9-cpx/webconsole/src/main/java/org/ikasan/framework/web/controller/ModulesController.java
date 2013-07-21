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
package org.ikasan.framework.web.controller;

import java.util.List;

import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.VisitingInvokerFlow;
import org.ikasan.framework.flow.event.listener.JobAwareFlowEventListener;
import org.ikasan.framework.flow.event.model.Trigger;
import org.ikasan.framework.flow.event.model.TriggerRelationship;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for the web-console dealing with modules
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/modules/*.htm")
public class ModulesController
{
    /** The module name parameter name */
    private static final String MODULE_NAME_PARAMETER_NAME = "moduleName";

    /** The flow name parameter name */
    private static final String FLOW_NAME_PARAMETER_NAME = "flowName";

    /** The flow element name parameter name */
    private static final String FLOW_ELEMENT_NAME_PARAMETER_NAME = "flowElementName";

    /** The trigger id parameter name */
    private static final String TRIGGER_ID_PARAMETER_NAME = "triggerId";

    /** Service facade for module functions */
    private ModuleService moduleService;

    /** JobAwareFlowEventListener */
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    /**
     * Constructor
     * 
     * @param moduleService - The module service
     */
    @Autowired
    public ModulesController(ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }

    /**
     * List the modules given a model (map)
     * 
     * @param model - The model (map)
     * @return "modules/modules"
     */
    @RequestMapping("list.htm")
    public String listModules(ModelMap model)
    {
        model.addAttribute("modules", moduleService.getModules());
        return "modules/modules";
    }

    /**
     * View the module
     * 
     * @param moduleName - The name of the module to view
     * @param model - The model
     * @return - "modules/viewModule"
     */
    @RequestMapping("view.htm")
    public String viewModule(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName, ModelMap model)
    {
        model.addAttribute("module", moduleService.getModule(moduleName));
        // For the navigation bar
        setupNavigationAttributes(moduleName, null, null, model);
        return "modules/viewModule";
    }

    /**
     * View the flow
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param model - The model
     * @return "modules/viewFlow"
     */
    @RequestMapping("viewFlow.htm")
    public String viewFlow(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName, ModelMap model)
    {
        Module module = moduleService.getModule(moduleName);
        Flow flow = module.getFlows().get(flowName);
        if (flow instanceof VisitingInvokerFlow)
        {
            model.addAttribute("flowElements", ((VisitingInvokerFlow) flow).getFlowElements());
        }
        model.addAttribute("flow", flow);
        // For the navigation bar
        setupNavigationAttributes(moduleName, flowName, null, model);
        return "modules/viewFlow";
    }

    /**
     * View the flow element
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param flowElementName - The name of the flow element
     * @param model - The model
     * @return "modules/viewFlowElement"
     */
    @RequestMapping("viewFlowElement.htm")
    public String viewFlowElement(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName,
            @RequestParam(FLOW_ELEMENT_NAME_PARAMETER_NAME) String flowElementName, ModelMap model)
    {
        Module module = moduleService.getModule(moduleName);
        Flow flow = module.getFlows().get(flowName);
        FlowElement flowElement = resolveFlowElement(flowElementName, (VisitingInvokerFlow) flow);
        List<Trigger> beforeElementTriggers = null;
        List<Trigger> afterElementTriggers = null;
        if (jobAwareFlowEventListener != null)
        {
            beforeElementTriggers = jobAwareFlowEventListener.getTriggers(module.getName(), flowName,
                TriggerRelationship.BEFORE, flowElementName);
            afterElementTriggers = jobAwareFlowEventListener.getTriggers(module.getName(), flowName,
                TriggerRelationship.AFTER, flowElementName);
        }
        model.addAttribute("flow", flow);
        model.addAttribute("flowElement", flowElement);
        model.addAttribute("beforeElementTriggers", beforeElementTriggers);
        model.addAttribute("afterElementTriggers", afterElementTriggers);
        // For the navigation bar
        setupNavigationAttributes(moduleName, flowName, flowElementName, model);
        return "modules/viewFlowElement";
    }

    /**
     * Sets the model attributes required for the model sub-navigation
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param flowElementName - The name of the flow element
     * @param model - The model
     */
    private void setupNavigationAttributes(String moduleName, String flowName, String flowElementName, ModelMap model)
    {
        setNotNullAttribute(model, MODULE_NAME_PARAMETER_NAME, moduleName);
        setNotNullAttribute(model, FLOW_NAME_PARAMETER_NAME, flowName);
        setNotNullAttribute(model, FLOW_ELEMENT_NAME_PARAMETER_NAME, flowElementName);
    }

    /**
     * Sets an attribute in the model if it is not null
     * 
     * @param model - The model
     * @param parameterName - The parameter name
     * @param parameterValue - The parameter value
     */
    private void setNotNullAttribute(ModelMap model, String parameterName, String parameterValue)
    {
        if (parameterValue != null)
        {
            model.addAttribute(parameterName, parameterValue);
        }
    }

    /**
     * Delete a wiretap trigger
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param flowElementName - The name of the flow element
     * @param triggerId - The id of the trigger to delete
     * @param model - The model
     * @return "modules/viewFlowElement"
     * @throws Exception - Exception if we fail to delete the trigger
     */
    @RequestMapping("deleteTrigger.htm")
    public String deleteTrigger(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName,
            @RequestParam(FLOW_ELEMENT_NAME_PARAMETER_NAME) String flowElementName,
            @RequestParam(TRIGGER_ID_PARAMETER_NAME) String triggerId, ModelMap model) throws Exception
    {
        jobAwareFlowEventListener.deleteDynamicTrigger(new Long(triggerId));
        return viewFlowElement(moduleName, flowName, flowElementName, model);
    }

    /**
     * resolves a named FlowElement from a VisitingInvokerFlow
     * 
     * @param flowElementName - The name of the flow element
     * @param flow - The flow
     * @return The resolve FlowElement
     */
    private FlowElement resolveFlowElement(String flowElementName, VisitingInvokerFlow flow)
    {
        FlowElement flowElement = null;
        for (FlowElement thisFlowElement : flow.getFlowElements())
        {
            if (thisFlowElement.getComponentName().equals(flowElementName))
            {
                flowElement = thisFlowElement;
                break;
            }
        }
        return flowElement;
    }

    /**
     * Set the job aware flow event listener
     * 
     * @param jobAwareFlowEventListener - The job aware flow event listener to set
     */
    @Autowired
    public void setJobAwareFlowEventListener(JobAwareFlowEventListener jobAwareFlowEventListener)
    {
        this.jobAwareFlowEventListener = jobAwareFlowEventListener;
    }
}
