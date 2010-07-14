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
package org.ikasan.framework.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.SimpleInitiator;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiatorImpl;
import org.ikasan.framework.initiator.eventdriven.EventDrivenInitiatorImpl;
import org.ikasan.framework.initiator.scheduled.quartz.QuartzDrivenInitiator;
import org.ikasan.framework.initiator.scheduled.quartz.QuartzStatefulScheduledDrivenInitiator;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.ikasan.framework.web.command.PayloadCommand;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for interaction with the initiators from a client.
 * 
 * @author Ikasan Development Team
 */
@Controller
@SessionAttributes("payloadCommand")
public class InitiatorsController
{
    /** The module name parameter name */
    private static final String MODULE_NAME_PARAMETER_NAME = "moduleName";

    /** The initiator name parameter name */
    private static final String INITIATOR_NAME_PARAMETER_NAME = "initiatorName";

    /** The initiator action parameter name */
    private static final String INITIATOR_ACTION_PARAMETER_NAME = "initiatorAction";

    /** The initiator startupType parameter name */
    private static final String STARTUP_TYPE_PARAMETER_NAME = "startupType";
    
    /** The initiator comment parameter name */
    private static final String STARTUP_COMMENT_PARAMETER_NAME = "startupComment";

    /** Service facade for module functions */
    private ModuleService moduleService;
    
    private Logger logger = Logger.getLogger(InitiatorsController.class);

    /**
     * Constructor
     * 
     * @param moduleService - The module service
     */
    @Autowired
    public InitiatorsController(ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }

    /**
     * View the initiator
     * 
     * @param moduleName - The name of the module
     * @param initiatorName - The name of the initiator
     * @param model - The model
     * @return "modules/viewInitiator"
     * @throws SchedulerException - Exception if there was a scheduler problem
     */
    @RequestMapping("/modules/viewInitiator.htm")
    public String viewInitiator(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(INITIATOR_NAME_PARAMETER_NAME) String initiatorName, ModelMap model)
            throws SchedulerException
    {
        Initiator initiator = resolveInitiator(moduleName, initiatorName);
        model.addAttribute("initiator", initiator);
        model.addAttribute("moduleName", moduleName);
        
        model.addAttribute("startupControl", moduleService.getInitiatorStartupControl(moduleName, initiatorName));
        String view = null;
        if (initiator.getType().equals(SimpleInitiator.SIMPLE_INITIATOR_TYPE))
        {
            model.addAttribute("payloadCommand", new PayloadCommand(moduleName, initiatorName));
            view = "modules/initiators/viewSimpleInitiator";
        }
        else if (initiator.getType().equals(JmsMessageDrivenInitiatorImpl.JMS_MESSAGE_DRIVEN_INITIATOR_TYPE))
        {
            view = "modules/initiators/viewJmsMessageDrivenInitiator";
        }
        else if (initiator.getType().equals(EventDrivenInitiatorImpl.EVENT_DRIVEN_INITIATOR_TYPE))
        {
            view = "modules/initiators/viewEventDrivenInitiator";
        }
        else if (initiator.getType().equals(
            QuartzStatefulScheduledDrivenInitiator.QUARTZ_SCHEDULE_DRIVEN_INITIATOR_TYPE))
        {
        	QuartzDrivenInitiator quartzStatefulScheduledDrivenInitiator = (QuartzDrivenInitiator) initiator;
            Map<org.quartz.Trigger, String> triggers = new HashMap<org.quartz.Trigger, String>();
            Scheduler scheduler = quartzStatefulScheduledDrivenInitiator.getScheduler();
            for (String triggerGroupName : scheduler.getTriggerGroupNames())
            {
                for (String triggerName : scheduler.getTriggerNames(triggerGroupName))
                {
                    triggers.put(scheduler.getTrigger(triggerName, triggerGroupName), getTriggerStateString(scheduler,
                        triggerGroupName, triggerName));
                }
            }
            model.addAttribute("triggers", triggers);
            List<JobDetail> jobs = new ArrayList<JobDetail>();
            for (String jobGroup : scheduler.getJobGroupNames())
            {
                for (String jobName : scheduler.getJobNames(jobGroup))
                {
                    jobs.add(scheduler.getJobDetail(jobName, jobGroup));
                }
            }
            model.addAttribute("jobs", jobs);
            view = "modules/initiators/viewQuartzScheduleDrivenInitiator";
        }
        return view;
    }

    /**
     * Helper method to resolve the initiator given its name and the module name
     * 
     * @param moduleName - The name of the module
     * @param initiatorName - The name of the initiator
     * @return - The initiator
     */
    private Initiator resolveInitiator(String moduleName, String initiatorName)
    {
        Module module = moduleService.getModule(moduleName);
        if (module == null)
        {
            throw new IllegalArgumentException("No such Module[" + moduleName + "]");
        }
        Initiator initiator = module.getInitiator(initiatorName);
        if (initiator == null)
        {
            throw new IllegalArgumentException("No such Initiator[" + initiatorName + "]");
        }
        return initiator;
    }

    /**
     * Helper method to get the trigger state as a string
     * 
     * @param scheduler - The scheduler that holds the trigger
     * @param triggerGroupName - The trigger group name
     * @param triggerName - The name of the trigger
     * @return - The trigger state
     * @throws SchedulerException - Exception if there's a scheduler problem
     */
    private String getTriggerStateString(Scheduler scheduler, String triggerGroupName, String triggerName)
            throws SchedulerException
    {
        String result = null;
        int triggerState = scheduler.getTriggerState(triggerName, triggerGroupName);
        switch (triggerState)
        {
        case org.quartz.Trigger.STATE_NORMAL:
        {
            result = "Normal";
            break;
        }
        case org.quartz.Trigger.STATE_BLOCKED:
        {
            result = "Blocked";
            break;
        }
        case org.quartz.Trigger.STATE_COMPLETE:
        {
            result = "Complete";
            break;
        }
        case org.quartz.Trigger.STATE_ERROR:
        {
            result = "Error";
            break;
        }
        case org.quartz.Trigger.STATE_NONE:
        {
            result = "None";
            break;
        }
        case org.quartz.Trigger.STATE_PAUSED:
        {
            result = "Paused";
        }
            break;
        }
        return result;
    }

    /**
     * Submit a SimpleInitiator's form
     * 
     * @param payloadCommand - The command to execute
     * @param model - The model
     * @return "modules/viewInitiator"
     * @throws SchedulerException - Exception if there was a scheduler problem
     */
    @RequestMapping(value = "/modules/simpleInitiatorPost.htm", method = RequestMethod.POST)
    public String submitSimpleInitiator(ModelMap model,
            @ModelAttribute("payloadCommand") PayloadCommand payloadCommand)
            throws SchedulerException
    {
        String moduleName = payloadCommand.getModuleName();
        String initiatorName = payloadCommand.getInitiatorName();
        Initiator initiator = resolveInitiator(moduleName, initiatorName);
        if (initiator instanceof SimpleInitiator)
        {
            SimpleInitiator simpleInitiator = ((SimpleInitiator) initiator);
            boolean success = simpleInitiator.initiate(""+System.currentTimeMillis(),
                 payloadCommand.getPayloadContent());
            String initiationResult = "Initiation Failed";
            if (success)
            {
                initiationResult = "Initiation Successful";
            }
            model.addAttribute("initiationResult", initiationResult);
            return viewInitiator(moduleName, initiatorName, model);
        }
        // Default else
        throw new RuntimeException("Undisplayable initiator type:" + initiator.getClass().getName());
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
    @RequestMapping(value = "/modules/initiator.htm", method = RequestMethod.POST)
    public String controlInitiator(
    		@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(INITIATOR_NAME_PARAMETER_NAME) String initiatorName,
            @RequestParam(value=INITIATOR_ACTION_PARAMETER_NAME, required=false) String initiatorAction,
            @RequestParam(value=STARTUP_TYPE_PARAMETER_NAME,required=false) String startupType,
            @RequestParam(value=STARTUP_COMMENT_PARAMETER_NAME, required=false) String startupComment)
    {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (initiatorAction!=null){
	        if (initiatorAction.equalsIgnoreCase("start"))
	        {
	            moduleService.startInitiator(moduleName, initiatorName, currentUser);
	        }
	        else if (initiatorAction.equalsIgnoreCase("stop"))
	        {
	            moduleService.stopInitiator(moduleName, initiatorName, currentUser);
	        }else
	        {
	            throw new RuntimeException("Unknown initiator action:" + initiatorAction);
	        }
        } else if (startupType!=null){
            if (startupType.equalsIgnoreCase("manual")||startupType.equalsIgnoreCase("automatic")||startupType.equalsIgnoreCase("disabled"))
            {       	
            	//crude check to ensure comment is supplied when disabling
            	if (startupType.equalsIgnoreCase("disabled")){
            		logger.info("about to disable initiator, comment is["+startupComment+"]");
            		if (startupComment==null ||"".equals(startupComment.trim())){
            			throw new IllegalArgumentException("must supply comment when disabling Initiator");
            		}
            	}
                moduleService.updateInitiatorStartupType(moduleName, initiatorName, StartupType.valueOf(startupType), startupComment, currentUser);
            }
            else
            {
                throw new RuntimeException("Unknown startupType:" + startupType);
            }       	
        }else{
        	throw new RuntimeException("Either initiatorAction, or startupType must be specified");
        }
        return redirectViewInitiator(moduleName, initiatorName);
    }
    
    
    

    /**
     * Helper method to perform a redirect to an initiator view
     * 
     * @param moduleName - The module name
     * @param initiatorName - The initiator to redirect to
     * @return The redirect string
     */
    private String redirectViewInitiator(String moduleName, String initiatorName)
    {
        return "redirect:viewInitiator.htm?moduleName=" + moduleName + "&initiatorName=" + initiatorName;
    }
}
