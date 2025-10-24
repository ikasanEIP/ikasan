package org.ikasan.ootb.scheduler.agent.module.component;

import org.ikasan.ootb.scheduler.agent.module.component.splitter.FileWatcherEventCorrelationIdentifierSplitter;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileWatcherEventCorrelationIdentifierSplitterTest {

    @Test
    public void test_splitter_success() {

        FileWatcherJobEvent originalEvent = mock(FileWatcherJobEvent.class);
        when(originalEvent.getContextName()).thenReturn("sampleContext");

        ContextInstance mockContextInstance1 = mock(ContextInstance.class);
        when(mockContextInstance1.getName()).thenReturn("sampleContext");
        when(mockContextInstance1.getId()).thenReturn("sampleId1");

        ContextInstance mockContextInstance2 = mock(ContextInstance.class);
        when(mockContextInstance2.getName()).thenReturn("sampleContext");
        when(mockContextInstance2.getId()).thenReturn("sampleId2");

        ContextInstanceCache.instance().put("sampleId1", mockContextInstance1);
        ContextInstanceCache.instance().put("sampleId2", mockContextInstance2);
        FileWatcherEventCorrelationIdentifierSplitter splitter = new FileWatcherEventCorrelationIdentifierSplitter();

        // Act
        List<FileWatcherJobEvent> splitEvents = splitter.split(originalEvent);

        // Assert
        Assertions.assertNotNull(splitEvents);
        Assertions.assertEquals(2, splitEvents.size());
        Assertions.assertEquals("sampleId1", splitEvents.get(0).getCorrelationIdentifier());
        Assertions.assertEquals("sampleId2", splitEvents.get(1).getCorrelationIdentifier());
    }

}
