package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobExecutionToContextualisedScheduledProcessEventConverterTest {

    @Mock
    JobExecutionContext jobExecutionContext;

    @Mock
    Trigger trigger;

    TriggerKey triggerKey = new TriggerKey("name", "group");

    @Test
    void test_exception_null_module_name_constructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JobExecutionToContextualisedScheduledProcessEventConverter(null);
        });
    }

    @Test
    void test_convert_success() {
        Date fireTime = new Date();
        Date nextFireTime = new Date(System.currentTimeMillis() + 600000);

        when(jobExecutionContext.getFireTime()).thenReturn(fireTime);
        when(jobExecutionContext.getNextFireTime()).thenReturn(nextFireTime);
        when(jobExecutionContext.getTrigger()).thenReturn(trigger);
        when(trigger.getDescription()).thenReturn("description");
        when(trigger.getKey()).thenReturn(triggerKey);
        JobDataMap jobDataMap = new JobDataMap();
        String correaltionID = UUID.randomUUID().toString();
        jobDataMap.put("correlationId", correaltionID);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);

        ContextInstance contextInstance = new ContextInstanceImpl();
        contextInstance.setName("contextName");
        contextInstance.setId("contextInstanceId");

        ContextInstanceCache.instance().put("contextInstanceId", contextInstance);

        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        configuration.setChildContextNames(List.of("childContextId1", "childContextId2"));
        configuration.setContextName("contextName");
        configuration.setJobName("jobName");

        JobExecutionToContextualisedScheduledProcessEventConverter converter
            = new JobExecutionToContextualisedScheduledProcessEventConverter("moduleName");
        converter.setConfiguration(configuration);

        ContextualisedScheduledProcessEvent event = converter.convert(jobExecutionContext);

        assertEquals(fireTime.getTime(), event.getFireTime());
        assertEquals(nextFireTime.getTime(), event.getNextFireTime());
        assertEquals("moduleName", event.getAgentName());
        assertEquals("jobName", event.getJobName()); // IKASAN-2174 - return jobName provided to the component.
        assertEquals("contextName", event.getContextName());
        assertEquals(correaltionID, event.getContextInstanceId());
        assertTrue(event.isSuccessful());
        assertEquals("description", event.getJobDescription());
        assertEquals("group", event.getJobGroup());
    }

    @Test
    void test_convert_exception_null_configuration() {
        assertThrows(TransformationException.class, () -> {
            JobExecutionToContextualisedScheduledProcessEventConverter converter
                = new JobExecutionToContextualisedScheduledProcessEventConverter("moduleName");

            converter.convert(jobExecutionContext);
        });
    }

}
