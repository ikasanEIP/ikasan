package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobExecutionToContextualisedScheduledProcessEventConverterTest {

    @Mock
    JobExecutionContext jobExecutionContext;

    @Mock
    Trigger trigger;

    TriggerKey triggerKey = new TriggerKey("name", "group");

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_module_name_constructor() {
        new JobExecutionToContextualisedScheduledProcessEventConverter(null, "jobName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_jo_name_constructor() {
        new JobExecutionToContextualisedScheduledProcessEventConverter("moduleName", null);
    }

    @Test
    public void test_convert_success() {
        Date fireTime = new Date();
        Date nextFireTime = new Date(System.currentTimeMillis() + 600000);

        when(jobExecutionContext.getFireTime()).thenReturn(fireTime);
        when(jobExecutionContext.getNextFireTime()).thenReturn(nextFireTime);
        when(jobExecutionContext.getTrigger()).thenReturn(trigger);
        when(trigger.getDescription()).thenReturn("description");
        when(trigger.getKey()).thenReturn(triggerKey);

        ContextInstance contextInstance = new ContextInstanceImpl();
        contextInstance.setName("contextName");
        contextInstance.setId("contextInstanceId");

        ContextInstanceCache.instance().put("contextName", contextInstance);

        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        configuration.setChildContextNames(List.of("childContextId1", "childContextId2"));
        configuration.setContextName("contextName");

        JobExecutionToContextualisedScheduledProcessEventConverter converter
            = new JobExecutionToContextualisedScheduledProcessEventConverter("moduleName", "jobName");
        converter.setConfiguration(configuration);

        ContextualisedScheduledProcessEvent event = converter.convert(jobExecutionContext);

        Assert.assertEquals(fireTime.getTime(), event.getFireTime());
        Assert.assertEquals(nextFireTime.getTime(), event.getNextFireTime());
        Assert.assertEquals("moduleName", event.getAgentName());
        Assert.assertEquals("name", event.getJobName());
        Assert.assertEquals("contextName", event.getContextName());
        Assert.assertEquals("contextInstanceId", event.getContextInstanceId());
        Assert.assertEquals(true, event.isSuccessful());
        Assert.assertEquals("description", event.getJobDescription());
        Assert.assertEquals("group", event.getJobGroup());
    }

    @Test(expected = TransformationException.class)
    public void test_convert_exception_null_configutation() {
        JobExecutionToContextualisedScheduledProcessEventConverter converter
            = new JobExecutionToContextualisedScheduledProcessEventConverter("moduleName", "jobName");

        converter.convert(jobExecutionContext);
    }

}
