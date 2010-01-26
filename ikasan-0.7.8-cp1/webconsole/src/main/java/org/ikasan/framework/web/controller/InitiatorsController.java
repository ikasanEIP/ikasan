package org.ikasan.framework.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.common.component.Spec;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.SimpleInitiator;
import org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiatorImpl;
import org.ikasan.framework.initiator.scheduled.quartz.QuartzStatefulScheduledDrivenInitiator;
import org.ikasan.framework.initiator.scheduled.quartz.QuartzSchedulerInitiator;
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

    /** Service facade for module functions */
    private ModuleService moduleService;

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
        else if (initiator.getType().equals(
            QuartzStatefulScheduledDrivenInitiator.QUARTZ_SCHEDULE_DRIVEN_INITIATOR_TYPE))
        {
            QuartzSchedulerInitiator quartzStatefulScheduledDrivenInitiator = (QuartzSchedulerInitiator) initiator;
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
            boolean success = simpleInitiator.initiate("httpSubmissionPayload", Spec.TEXT_PLAIN,
                "manualHttpSubmission", payloadCommand.getPayloadContent());
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
    public String controlInitiator(@RequestParam(MODULE_NAME_PARAMETER_NAME) String moduleName,
            @RequestParam(INITIATOR_NAME_PARAMETER_NAME) String initiatorName,
            @RequestParam(INITIATOR_ACTION_PARAMETER_NAME) String initiatorAction) throws Exception
    {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (initiatorAction.equalsIgnoreCase("start"))
        {
            moduleService.startInitiator(moduleName, initiatorName, currentUser);
        }
        else if (initiatorAction.equalsIgnoreCase("stop"))
        {
            moduleService.stopInitiator(moduleName, initiatorName, currentUser);
        }
        else
        {
            throw new RuntimeException("Unknown initiator action:" + initiatorAction);
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
