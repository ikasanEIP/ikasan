package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileListToContextualisedScheduledProcessEventConverterTest {

    @Test
    void test_exception_constructor_null_agent_name() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FileListToContextualisedScheduledProcessEventConverter(null);
        });
    }

    @Test
    void test_convert_success() {
        ContextInstance contextInstance = new ContextInstanceImpl();
        contextInstance.setName("contextName");
        contextInstance.setId("contextInstanceId");

        ContextInstanceCache.instance().put("contextInstanceId", contextInstance);

        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        configuration.setContextName("contextName");
        configuration.setChildContextNames(List.of("childContextId1", "childContextId2"));
        configuration.setJobName("jobName");

        FileListToContextualisedScheduledProcessEventConverter converter
            = new FileListToContextualisedScheduledProcessEventConverter("agentName");
        converter.setConfiguration(configuration);

        List<File> files = List.of(new File("."));

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "contextInstanceId");

        ContextualisedScheduledProcessEvent event = converter.convert(correlatedFileList);

        assertEquals("agentName", event.getAgentName());
        assertEquals("jobName", event.getJobName());
        assertEquals("contextName", event.getContextName());
        assertEquals("contextInstanceId", event.getContextInstanceId());
        assertEquals(2, event.getChildContextNames().size());
        assertTrue(event.isSuccessful());
    }
}
