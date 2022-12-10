package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
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
        ContextInstance contextInstance = new ContextInstanceImpl();
        contextInstance.setName("contextName");
        contextInstance.setId("contextInstanceId");

        ContextInstanceCache.instance().put("contextName", contextInstance);

        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        configuration.setContextName("contextName");
        configuration.setChildContextNames(List.of("childContextId1", "childContextId2"));

        FileListToContextualisedScheduledProcessEventConverter converter
            = new FileListToContextualisedScheduledProcessEventConverter("agentName", "jobName");
        converter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "contextInstanceId");

        ContextualisedScheduledProcessEvent event = converter.convert(correlatedFileList);

        Assert.assertEquals("agentName", event.getAgentName());
        Assert.assertEquals("jobName", event.getJobName());
        Assert.assertEquals("contextName", event.getContextName());
        Assert.assertEquals("contextInstanceId", event.getContextInstanceId());
        Assert.assertEquals(2, event.getChildContextNames().size());
        Assert.assertEquals(true, event.isSuccessful());
    }
}
