package org.ikasan.ootb.scheduler.agent.rest.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.job.orchestration.model.context.*;
import org.ikasan.job.orchestration.model.job.InternalEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.SchedulerJobImpl;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterDto;
import org.ikasan.spec.scheduled.context.model.*;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.ikasan.spec.scheduled.instance.model.JobLockInstance;
import org.ikasan.spec.scheduled.instance.model.SchedulerJobInstance;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperFactory {

    /**
     * Create an ObjectMapper that can be used in the
     * all relevant concrete type mappings for Context and ContextInstance.
     *
     * @return
     */
    public static ObjectMapper newInstance() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(newSimpleModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }


    /**
     * Create a SimpleModule that can be used in the
     * all relevant concrete type mappings for Context and ContextInstance.
     *
     * @return
     */
    public static SimpleModule newSimpleModule() {
        return new SimpleModule()
            .addAbstractTypeMapping(And.class, AndImpl.class)
            .addAbstractTypeMapping(Or.class, OrImpl.class)
            .addAbstractTypeMapping(Not.class, NotImpl.class)
            .addAbstractTypeMapping(ContextTemplate.class, ContextTemplateImpl.class)
            .addAbstractTypeMapping(Context.class, ContextImpl.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterDto.class)
            .addAbstractTypeMapping(SchedulerJob.class, SchedulerJobImpl.class)
            .addAbstractTypeMapping(JobDependency.class, JobDependencyImpl.class)
            .addAbstractTypeMapping(ContextDependency.class, ContextDependencyImpl.class)
            .addAbstractTypeMapping(LogicalGrouping.class, LogicalGroupingImpl.class)
            .addAbstractTypeMapping(LogicalOperator.class, LogicalOperatorImpl.class)
            .addAbstractTypeMapping(ContextInstance.class, ContextInstanceImpl.class)
            .addAbstractTypeMapping(SchedulerJobInstance.class, SchedulerJobInstanceImpl.class)
            .addAbstractTypeMapping(ContextParameterInstance.class, ContextParameterInstanceImpl.class)
            .addAbstractTypeMapping(JobLock.class, JobLockImpl.class)
            .addAbstractTypeMapping(JobLockInstance.class, JobLockInstanceImpl.class)
            .addAbstractTypeMapping(ScheduledProcessEvent.class, ContextualisedScheduledProcessEventImpl.class)
            .addAbstractTypeMapping(ContextualisedScheduledProcessEvent.class, ContextualisedScheduledProcessEventImpl.class)
            .addAbstractTypeMapping(SchedulerJobInitiationEvent.class, SchedulerJobInitiationEventImpl.class)
            .addAbstractTypeMapping(InternalEventDrivenJob.class, InternalEventDrivenJobImpl.class)
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);
    }

}
