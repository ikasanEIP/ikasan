package org.ikasan.rest.module;

import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Scheduler controller exposing Search functionality to Platform Scheduler.
 */
@RequestMapping("/rest/scheduler")
@RestController
public class SchedulerApplication
{
    /** logger */
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerApplication.class);

    @Autowired
    private Scheduler platformScheduler;

    @Autowired
    private ModuleContainer moduleContainer;

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
     * Allows to trigger a scheduled flow with immediate effect rather than waiting for next cron.
     * @param moduleName to invoke
     * @param flowName to invoke
     * @param correlationId relating to the dashboard instance that these events are for (the response will include
     *                          the correlation ID so that it can be correlated back to the correct dashboard instance
     * @return the response
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/{moduleName}/{flowName}/{correlationId}",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity triggerNow(@PathVariable("moduleName") String moduleName,
                                     @PathVariable("flowName") String flowName,
                                     @PathVariable("correlationId") Optional <String> correlationId)
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
                if (flowConfigurationConsumerFlowElement != null && flowConfigurationConsumerFlowElement.getFlowComponent() != null)
                {
                    Consumer consumer = resolveProxiedComponent( flowConfigurationConsumerFlowElement.getFlowComponent());
                    if (consumer instanceof ScheduledConsumer)
                    {
                        LOG.info("Triggering module[{}], flow[{}] correlationId [{}] now!", moduleName, flowName, correlationId);
                        ScheduledConsumer scheduledConsumer = (ScheduledConsumer) consumer;
                        JobDetail jobDetail = ((ScheduledComponent<JobDetail>) consumer).getJobDetail();

                        TriggerBuilder triggerBuilder = newTrigger()
                            .withIdentity((scheduledConsumer.getConfiguration().getJobName()  != null && !scheduledConsumer.getConfiguration().getJobName().isEmpty())
                                    ? scheduledConsumer.getConfiguration().getJobName() : "name",
                                (scheduledConsumer.getConfiguration().getJobGroupName() != null && !scheduledConsumer.getConfiguration().getJobGroupName().isEmpty())
                                    ? scheduledConsumer.getConfiguration().getJobGroupName() + " (manual fire)" : "group (manual fire)")
                            .withDescription(scheduledConsumer.getConfiguration().getDescription())
                            .forJob(jobDetail);
                        if (correlationId.isPresent()) {
                            triggerBuilder.usingJobData(CorrelatingScheduledConsumer.CORRELATION_ID, correlationId.get());
                        }
                        scheduledConsumer.scheduleAsEagerTrigger(triggerBuilder.build(), 0);
                    }
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

    protected <T> T resolveProxiedComponent(T component)
    {
        try
        {
            if(AopUtils.isAopProxy(component) && component instanceof Advised)
            {
                Advised advised = (Advised) component;
                return (T) advised.getTargetSource().getTarget();
            }
        }
        catch (Exception e)
        {
            LOG.warn("Unable to unwrap proxied target for component [" + component.getClass().getName() + "]. Returning component as is.", e);
        }

        return component;
    }
}
