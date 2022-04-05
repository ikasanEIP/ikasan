package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FileListToContextualisedScheduledProcessEventConverterTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_agent_name() {
        new FileListToContextualisedScheduledProcessEventConverter(null, "jobName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_job_name() {
        new FileListToContextualisedScheduledProcessEventConverter("agentName", null);
    }

    @Test
    public void test_convert_success() {
        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        configuration.setContextId("contextid");
        configuration.setChildContextIds(List.of("childContextId1", "childContextId2"));

        FileListToContextualisedScheduledProcessEventConverter converter
            = new FileListToContextualisedScheduledProcessEventConverter("agentName", "jobName");
        converter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        ContextualisedScheduledProcessEvent event = converter.convert(files);

        Assert.assertEquals("agentName", event.getAgentName());
        Assert.assertEquals("jobName", event.getJobName());
        Assert.assertEquals("contextid", event.getContextId());
        Assert.assertEquals(2, event.getChildContextIds().size());
        Assert.assertEquals(true, event.isSuccessful());
    }
}
