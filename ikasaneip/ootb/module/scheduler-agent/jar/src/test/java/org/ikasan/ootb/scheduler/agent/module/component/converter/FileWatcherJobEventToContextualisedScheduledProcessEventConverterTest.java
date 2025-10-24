package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FileWatcherJobEventToContextualisedScheduledProcessEventConverterTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_agent_name() {
        new FileWatcherJobEventToContextualisedScheduledProcessEventConverter(null);
    }

    @Test
    public void test_convert_success() {
        ContextInstance contextInstance = new ContextInstanceImpl();
        contextInstance.setName("contextName");
        contextInstance.setId("contextInstanceId");

        ContextInstanceCache.instance().put("contextInstanceId", contextInstance);

        FileWatcherJobEventToContextualisedScheduledProcessEventConverter converter
            = new FileWatcherJobEventToContextualisedScheduledProcessEventConverter("agentName");

        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "contextInstanceId");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setContextName("contextName");
        event.setChildContextNames(List.of("childContextId1", "childContextId2"));
        event.setCorrelationIdentifier("contextInstanceId");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("src/test/resources/data/archive");
        event.setJobName("jobName");

        ContextualisedScheduledProcessEvent contextualisedScheduledProcessEvent = converter.convert(event);

        Assert.assertEquals("agentName", contextualisedScheduledProcessEvent.getAgentName());
        Assert.assertEquals("jobName", contextualisedScheduledProcessEvent.getJobName());
        Assert.assertEquals("contextName", contextualisedScheduledProcessEvent.getContextName());
        Assert.assertEquals("contextInstanceId", contextualisedScheduledProcessEvent.getContextInstanceId());
        Assert.assertEquals(2, contextualisedScheduledProcessEvent.getChildContextNames().size());
        Assert.assertTrue(contextualisedScheduledProcessEvent.isSuccessful());
    }
}
