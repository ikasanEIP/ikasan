package org.ikasan.rest.module;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.systemevent.SystemEventService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Scheduler controller exposing Search functionality to Platform Scheduler.
 */
@RequestMapping("/rest/scheduler")
@RestController
public class SchedulerApplication
{
    public static final String MANUAL_TRIGGER_OF_SCHEDULED_FLOW_SYSTEM_EVENT_ACTION = "Manual Trigger of flow "
        + "requested";

    @Autowired
    private Scheduler platformScheduler;

    @Autowired
    private ModuleContainer moduleContainer;

    @Autowired
    private SystemEventService systemEventService;

    /**
     * Gets All Triggers
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get()
    {
        try
        {
            if ( !platformScheduler.isShutdown() )
            {

                List<Trigger> triggers = new ArrayList<Trigger>();

                for (String triggerGroupName : platformScheduler.getTriggerGroupNames())
                {
                    Set<TriggerKey> keys = platformScheduler.getTriggerKeys(GroupMatcher.groupEquals(triggerGroupName));
                    for (TriggerKey key : keys)
                    {
                        triggers.add(platformScheduler.getTrigger(key));
                    }
                }
                return new ResponseEntity(triggers, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity(new ErrorDto("Scheduler was shutdown"), HttpStatus.BAD_REQUEST);

            }
        }
        catch (SchedulerException e)
        {
            return new ResponseEntity(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Allows to trigger a scheduled flow with imediate effect rather than waiting for next cron.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/{moduleName}/{flowName}",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity triggerNow(@PathVariable("moduleName") String moduleName,
                                     @PathVariable("flowName") String flowName)
    {
        try
        {
            if ( !platformScheduler.isShutdown() )
            {

                Module<Flow> module = moduleContainer.getModule(moduleName);
                if ( module == null )
                {
                    return new ResponseEntity(
                        new ErrorDto("Could not get module from module container using name:  [" + moduleName + "]"),
                        HttpStatus.BAD_REQUEST
                    );
                }

                Flow flow = module.getFlow(flowName);
                if ( flow == null )
                {
                    return new ResponseEntity(
                        new ErrorDto("Could not get flow from module container using name:  [" + flowName + "]"),
                        HttpStatus.BAD_REQUEST
                    );
                }

                FlowConfiguration flowConfiguration = flow.getFlowConfiguration();
                FlowElement<Consumer> flowConfigurationConsumerFlowElement = flowConfiguration.getConsumerFlowElement();

                if ( flowConfigurationConsumerFlowElement != null
                    && flowConfigurationConsumerFlowElement.getFlowComponent() != null )
                {

                    ScheduledConsumer consumer;
                    if ( (AopUtils.isJdkDynamicProxy(flowConfigurationConsumerFlowElement.getFlowComponent())) )
                    {
                        consumer = getTargetObject(flowConfigurationConsumerFlowElement.getFlowComponent(),
                            ScheduledConsumer.class
                                                  );
                    }
                    else if ( flowConfigurationConsumerFlowElement.getFlowComponent() instanceof ScheduledConsumer )
                    {

                        consumer = (ScheduledConsumer) flowConfigurationConsumerFlowElement.getFlowComponent();

                    }
                    else
                    {
                        return new ResponseEntity(new ErrorDto(
                            "Consumer of given module[" + moduleName + "] flow [" + flowName
                                + "] is not of ScheduledConsumer type and cannot be triggered using this API."),
                            HttpStatus.BAD_REQUEST
                        );

                    }
                    //log the request
                    this.systemEventService.logSystemEvent(moduleName + "." + flowName,
                        MANUAL_TRIGGER_OF_SCHEDULED_FLOW_SYSTEM_EVENT_ACTION, getUser()
                                                          );
                    triggerScheduledConsumer(consumer);

                }

                return new ResponseEntity(HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity(new ErrorDto("Scheduler was shutdown"), HttpStatus.BAD_REQUEST);

            }
        }
        catch (SchedulerException e)
        {
            return new ResponseEntity(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private void triggerScheduledConsumer(ScheduledConsumer consumer) throws SchedulerException
    {

        JobDetail jobDetail = ((ScheduledComponent<JobDetail>) consumer).getJobDetail();
        Trigger trigger = newTrigger().withIdentity("name", "group").forJob(jobDetail).build();
        consumer.scheduleAsEagerTrigger(trigger, 0);
    }

    private <T> T getTargetObject(Object proxy, Class<T> targetClass)
    {
        try
        {
            if ( AopUtils.isJdkDynamicProxy(proxy) )
            {
                return (T) ((Advised) proxy).getTargetSource().getTarget();
            }
            else
            {
                return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String getUser()
    {
        String user = "unknown";
        SecurityContext context = SecurityContextHolder.getContext();
        if ( context != null )
        {
            user = context.getAuthentication().getPrincipal().toString();
        }
        return user;
    }
}
