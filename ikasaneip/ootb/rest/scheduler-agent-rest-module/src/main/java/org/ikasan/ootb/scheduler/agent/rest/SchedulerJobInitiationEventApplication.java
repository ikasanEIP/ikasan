package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.ootb.scheduler.agent.rest.cache.InboundJobQueueCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.*;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Module application implementing the REST contract
 */
@RequestMapping("/rest/schedulerJobInitiation")
@RestController
public class SchedulerJobInitiationEventApplication
{
    private static Logger logger = LoggerFactory.getLogger(SchedulerJobInitiationEventApplication.class);

    private ObjectMapper mapper;

    public SchedulerJobInitiationEventApplication() {
        this.mapper = new ObjectMapper();
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class)
            .addAbstractTypeMapping(SchedulerJobInitiationEvent.class, SchedulerJobInitiationEventDto.class)
            .addAbstractTypeMapping(ScheduledProcessEvent.class, ContextualisedScheduledProcessEventDto.class)
            .addAbstractTypeMapping(InternalEventDrivenJobInstance.class, InternalEventDrivenJobInstanceDto.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterInstanceDto.class);

        this.mapper.registerModule(simpleModule);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity raiseSchedulerJobInitiationEvent(@RequestBody SchedulerJobInitiationEventDto schedulerJobInitiationEvent)
    {
        try {
            logger.info("Received - {}", schedulerJobInitiationEvent);
            String queueName = schedulerJobInitiationEvent.getAgentName()+"-"+schedulerJobInitiationEvent.getJobName()+"-inbound-queue";
            IBigQueue inboundQueue = InboundJobQueueCache.instance().get(queueName);

            BigQueueMessage bigQueueMessage = new BigQueueMessageBuilder()
                .withMessage(schedulerJobInitiationEvent)
                .withMessageProperties(getProperties(schedulerJobInitiationEvent))
                .build();

            inboundQueue.enqueue(mapper.writeValueAsBytes(bigQueueMessage));
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    private Map<String, String> getProperties(SchedulerJobInitiationEventDto schedulerJobInitiationEvent) {
        Map<String, String> properties = new HashMap<>();
        if (schedulerJobInitiationEvent.getContextName() != null) {
            properties.put("contextName", schedulerJobInitiationEvent.getContextName());
        }
        if (schedulerJobInitiationEvent.getContextInstanceId() != null) {
            properties.put("contextInstanceId", schedulerJobInitiationEvent.getContextInstanceId());
        }
        return properties;
    }

}
