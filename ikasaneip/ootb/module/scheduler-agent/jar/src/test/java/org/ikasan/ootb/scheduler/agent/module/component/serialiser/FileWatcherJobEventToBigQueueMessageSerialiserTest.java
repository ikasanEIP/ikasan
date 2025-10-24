package org.ikasan.ootb.scheduler.agent.module.component.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileWatcherJobEventToBigQueueMessageSerialiserTest {
    private FileWatcherJobEventToBigQueueMessageSerialiser serialiser;
    private FileWatcherJobEvent event;

    @BeforeEach
    public void setUp() {
        serialiser = new FileWatcherJobEventToBigQueueMessageSerialiser();
        event = new FileWatcherJobEvent();
    }

    @Test
    public void test_serialise_success()  {
        event.setCorrelationIdentifier("testCorrelation");
        event.setJobName("jobName");
        event.setContextName("contextName");
        event.setChildContextNames(List.of("child1", "child2"));
        event.setFilePath("filePath");
        event.setFilename("filename");
        event.setFilePathSpelExpression("file-path-spel");
        event.setFileNameSpelExpression("file-name-spel");
        event.setTimeZone("timezone");
        event.setMoveDirectory("move-directory");
        event.setBlackoutWindowCronExpressions(List.of("window1", "window2"));
        event.setBlackoutWindowDateTimeRanges(Map.of("date-time-1", "date-time-2"));
        event.setMinFileAgeSeconds(55);
        event.setSlaCronExpression("sla-cron-expression");

        byte[] result = serialiser.serialise(event);
        assertNotNull(result, "Serialised result should not be null");

        FileWatcherJobEvent fileWatcherJobEvent = serialiser.deserialise(result);
        Assert.assertEquals("Deserialised event should match the original"
            , event, fileWatcherJobEvent);
    }

}

