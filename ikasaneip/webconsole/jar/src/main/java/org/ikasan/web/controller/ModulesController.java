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
package org.ikasan.web.controller;

import java.util.List;

import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.trigger.model.Trigger;
import org.ikasan.trigger.model.TriggerRelationship;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for the web-console dealing with modules
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/modules")
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

    /** The flow startupType parameter name */
    private static final String STARTUP_TYPE_PARAMETER_NAME = "startupType";
    
    /** The flow startup comment parameter name */
    private static final String STARTUP_COMMENT_PARAMETER_NAME = "startupComment";

    /** Service facade for module functions */
    private ModuleService moduleService;

    /** JobAwareFlowEventListener */
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    /**
     * Constructor
     * 
     * @param moduleService - The module service
     * @param monitor - A moitor of module's flows
     */
    @Autowired
    public ModulesController(final ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }

    /**
     * List the modules given a window (map)
     * 
     * @param model - The window (map)
     * @return "modules/modules"
     */
    @RequestMapping("list.htm")
    public String listModules(ModelMap model)
    {
        model.addAttribute("modules", this.moduleService.getModules());
        return "modules/modules";
    }

    /**
     * View the module
     * 
     * @param moduleName - The name of the module to view
     * @param model - The window
     * @return - "modules/viewModule"
     */
    @RequestMapping("view.htm")
    public String viewModule(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName, ModelMap model)
    {
        Module<Flow> module = this.moduleService.getModule(moduleName);
        model.addAttribute("module", module);
        model.addAttribute("flows", module.getFlows());
        // For the navigation bar
        setupNavigationAttributes(moduleName, null, null, model);
        return "modules/viewModule";
    }

    /**
     * View the flow
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param model - The window
     * @return "modules/viewFlow"
     */
    @RequestMapping("viewFlow.htm")
    public String viewFlow(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName, ModelMap model)
    {
        Module<Flow> module = this.moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        model.addAttribute("flowElements", flow.getFlowElements());
        model.addAttribute("flow", flow);
        model.addAttribute("startupControl", this.moduleService.getStartupControl(moduleName, flowName) );

        // For the navigation bar
        setupNavigationAttributes(moduleName, flowName, null, model);
        return "modules/viewFlow";
    }

    /**
     * Control the initiator (stop, start etc)
     * 
     * @param moduleName The name of the module
     * @param initiatorName - The name of the initiator
     * @param initiatorAction - The controlling action for the initiator
     * @return "modules/viewModule"
     * @throws Exception - Exception if we cannot control the initiator
     */
    @RequestMapping(value = "flowStartupControl.htm", method = RequestMethod.POST)
    public String controlInitiator(
            @RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName,
            @RequestParam(value=STARTUP_TYPE_PARAMETER_NAME,required=false) String startupType,
            @RequestParam(value=STARTUP_COMMENT_PARAMETER_NAME, required=false) String startupComment)
    {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (startupType!=null)
        {
            if (startupType.equalsIgnoreCase("manual")||startupType.equalsIgnoreCase("automatic")||startupType.equalsIgnoreCase("disabled"))
            {           
                //crude check to ensure comment is supplied when disabling
                if (startupType.equalsIgnoreCase("disabled"))
                {
                    if (startupComment==null ||"".equals(startupComment.trim()))
                    {
                        throw new IllegalArgumentException("must supply comment when disabling Initiator");
                    }
                }
                
                moduleService.setStartupType(moduleName, flowName, StartupType.valueOf(startupType), startupComment, currentUser);
            }
            else
            {
                throw new RuntimeException("Unknown startupType:" + startupType);
            }           
        }
        
        return "redirect:view.htm?moduleName=" + moduleName;
    }
    
    /**
     * @param moduleName
     * @param flowName
     * @param action 
     * @return
     */
    @RequestMapping(value = "flow.htm", method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('ALL','WriteBlueConsole')")
    public String controlFlow(@RequestParam("moduleName")String moduleName,
                            @RequestParam("flowName") String flowName,
                            @RequestParam("action") String action)
    {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (action.equalsIgnoreCase("start"))
        {
            this.moduleService.startFlow(moduleName, flowName, currentUser);
        }
        else if (action.equalsIgnoreCase("startPause"))
        {
            this.moduleService.startPauseFlow(moduleName, flowName, currentUser);
        }
        else if (action.equalsIgnoreCase("pause"))
        {
            this.moduleService.pauseFlow(moduleName, flowName, currentUser);
        }
        else if (action.equalsIgnoreCase("resume"))
        {
            this.moduleService.resumeFlow(moduleName, flowName, currentUser);
        }
        else if (action.equalsIgnoreCase("stop"))
        {
            this.moduleService.stopFlow(moduleName, flowName, currentUser);
        }
        else
        {
            throw new RuntimeException("Unknown flow action [" + action + "].");
        }
        return "redirect:view.htm?moduleName=" + moduleName;
    }

    /**
     * View the flow element
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param flowElementName - The name of the flow element
     * @param model - The window
     * @return "modules/viewFlowElement"
     */
    @RequestMapping("viewFlowElement.htm")
    public String viewFlowElement(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName,
            @RequestParam(FLOW_ELEMENT_NAME_PARAMETER_NAME) String flowElementName, ModelMap model)
    {
        Module<Flow> module = this.moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        FlowElement<?> flowElement = resolveFlowElement(flowElementName, flow);
        List<Trigger> beforeElementTriggers = null;
        List<Trigger> afterElementTriggers = null;
        if (this.jobAwareFlowEventListener != null)
        {
            beforeElementTriggers = this.jobAwareFlowEventListener.getTriggers(module.getName(), flowName,
                TriggerRelationship.BEFORE, flowElementName);
            afterElementTriggers = this.jobAwareFlowEventListener.getTriggers(module.getName(), flowName,
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
     * Sets the window attributes required for the window sub-navigation
     * 
     * @param moduleName - The name of the module
     * @param flowName - The name of the flow
     * @param flowElementName - The name of the flow element
     * @param model - The window
     */
    private void setupNavigationAttributes(String moduleName, String flowName, String flowElementName, ModelMap model)
    {
        setNotNullAttribute(model, MODULE_NAME_PARAMETER_NAME, moduleName);
        setNotNullAttribute(model, FLOW_NAME_PARAMETER_NAME, flowName);
        setNotNullAttribute(model, FLOW_ELEMENT_NAME_PARAMETER_NAME, flowElementName);
    }

    /**
     * Sets an attribute in the window if it is not null
     * 
     * @param model - The window
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
     * @param model - The window
     * @return "modules/viewFlowElement"
     * @throws Exception - Exception if we fail to delete the trigger
     */
    @RequestMapping("deleteTrigger.htm")
    public String deleteTrigger(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(FLOW_NAME_PARAMETER_NAME) String flowName,
            @RequestParam(FLOW_ELEMENT_NAME_PARAMETER_NAME) String flowElementName,
            @RequestParam(TRIGGER_ID_PARAMETER_NAME) String triggerId, ModelMap model) throws Exception
    {
        this.jobAwareFlowEventListener.deleteDynamicTrigger(new Long(triggerId));
        return viewFlowElement(moduleName, flowName, flowElementName, model);
    }

    /**
     * resolves a named FlowElement from a VisitingInvokerFlow
     * 
     * @param flowElementName - The name of the flow element
     * @param flow - The flow
     * @return The resolve FlowElement
     */
    private FlowElement<?> resolveFlowElement(String flowElementName, Flow flow)
    {
        FlowElement<?> flowElement = null;
        for (FlowElement<?> thisFlowElement : flow.getFlowElements())
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
