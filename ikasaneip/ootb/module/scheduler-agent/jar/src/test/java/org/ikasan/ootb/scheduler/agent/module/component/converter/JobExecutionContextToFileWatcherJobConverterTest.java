package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.FileWatcherJobConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.serialiser.model.JobExecutionContextDefaultImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobExecutionContextToFileWatcherJobConverterTest {
    @Mock
    JobExecutionContext jobExecutionContext;

    @Mock
    Trigger trigger;

    TriggerKey triggerKey = new TriggerKey("name", "group");

    @Test
    public void test_convert_success() {
        Date nextFireTime = new Date(System.currentTimeMillis() + 600000);

        when(jobExecutionContext.getNextFireTime()).thenReturn(nextFireTime);
        when(jobExecutionContext.getTrigger()).thenReturn(trigger);
        when(trigger.getDescription()).thenReturn("description");
        when(trigger.getKey()).thenReturn(triggerKey);
        JobDataMap jobDataMap = new JobDataMap();
        String correaltionID = UUID.randomUUID().toString();
        jobDataMap.put("correlationId", correaltionID);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);

        JobExecutionContextToFileWatcherJobConverter converter = new JobExecutionContextToFileWatcherJobConverter();
        FileWatcherJobConverterConfiguration configuration = new FileWatcherJobConverterConfiguration();
        configuration.setJobName("jobName");
        configuration.setContextName("contextName");
        configuration.setChildContextNames(List.of("child1", "child2"));
        configuration.setFilePath("filePath");
        configuration.setFilename("filename");
        configuration.setFilePathSpelExpression("file-path-spel");
        configuration.setFileNameSpelExpression("file-name-spel");
        configuration.setTimeZone("timezone");
        configuration.setMoveDirectory("move-directory");
        configuration.setBlackoutWindowCronExpressions(List.of("window1", "window2"));
        configuration.setBlackoutWindowDateTimeRanges(Map.of("date-time-1", "date-time-2"));
        configuration.setMinFileAgeSeconds(55);
        configuration.setSlaCronExpression("sla-cron-expression");

        converter.setConfiguration(configuration);

        FileWatcherJobEvent fileWatcherJobEvent = converter.convert(jobExecutionContext);

        Assert.assertEquals("jobName", fileWatcherJobEvent.getJobName());
        Assert.assertEquals("contextName", fileWatcherJobEvent.getContextName());
        Assert.assertEquals(2, fileWatcherJobEvent.getChildContextNames().size());
        Assert.assertEquals("filePath", fileWatcherJobEvent.getFilePath());
        Assert.assertEquals("filename", fileWatcherJobEvent.getFilename());
        Assert.assertEquals("file-path-spel", fileWatcherJobEvent.getFilePathSpelExpression());
        Assert.assertEquals("file-name-spel", fileWatcherJobEvent.getFileNameSpelExpression());
        Assert.assertEquals("timezone", fileWatcherJobEvent.getTimeZone());
        Assert.assertEquals("move-directory", fileWatcherJobEvent.getMoveDirectory());
        Assert.assertEquals(2, fileWatcherJobEvent.getBlackoutWindowCronExpressions().size());
        Assert.assertEquals(1, fileWatcherJobEvent.getBlackoutWindowDateTimeRanges().size());
        Assert.assertEquals(55, fileWatcherJobEvent.getMinFileAgeSeconds());
        Assert.assertEquals("sla-cron-expression", fileWatcherJobEvent.getSlaCronExpression());
        Assert.assertEquals(true, fileWatcherJobEvent.isDryRun());
        Assert.assertEquals("description", fileWatcherJobEvent.getJobDescription());
        Assert.assertEquals("group", fileWatcherJobEvent.getJobGroup());
        Assert.assertEquals(correaltionID, fileWatcherJobEvent.getCorrelationIdentifier());
    }
}
