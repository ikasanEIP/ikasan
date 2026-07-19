package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.FileWatcherJobConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutionContextToFileWatcherJobConverter implements Converter<JobExecutionContext, FileWatcherJobEvent>, ConfiguredResource<FileWatcherJobConverterConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutionContextToFileWatcherJobConverter.class);

    private FileWatcherJobConverterConfiguration configuration;
    private String configuredResourceId;

    @Override
    public FileWatcherJobEvent convert(JobExecutionContext jobExecutionContext) throws TransformationException {
        String contextInstanceIdentifier = null;

        if(jobExecutionContext.getMergedJobDataMap() != null) {
            contextInstanceIdentifier = (String) jobExecutionContext.getMergedJobDataMap()
                .get(CorrelatingScheduledConsumer.CORRELATION_ID);

            logger.debug(CorrelatingScheduledConsumer.CORRELATION_ID
                + " " + contextInstanceIdentifier);
        }

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName(this.configuration.getContextName());
        fileWatcherJobEvent.setJobName(this.configuration.getJobName());
        fileWatcherJobEvent.setMoveDirectory(this.configuration.getMoveDirectory());
        fileWatcherJobEvent.setMoveDirectorySpelExpression(this.configuration.getMoveDirectorySpelExpression());
        fileWatcherJobEvent.setFilename(this.configuration.getFilename());
        fileWatcherJobEvent.setFilePath(this.configuration.getFilePath());
        fileWatcherJobEvent.setMinFileAgeSeconds(this.configuration.getMinFileAgeSeconds());
        fileWatcherJobEvent.setFilePathSpelExpression(this.configuration.getFilePathSpelExpression());
        fileWatcherJobEvent.setFileNameSpelExpression(this.configuration.getFileNameSpelExpression());
        fileWatcherJobEvent.setChildContextNames(this.configuration.getChildContextNames());
        fileWatcherJobEvent.setBlackoutWindowCronExpressions(this.configuration.getBlackoutWindowCronExpressions());
        fileWatcherJobEvent.setBlackoutWindowDateTimeRanges(this.configuration.getBlackoutWindowDateTimeRanges());
        fileWatcherJobEvent.setSlaCronExpression(this.configuration.getSlaCronExpression());
        fileWatcherJobEvent.setTimeZone(this.configuration.getTimeZone());

        // This is where the correlation identifier is set onto the scheduler process event
        // that is sent to the dashboard.
        if(contextInstanceIdentifier != null && !contextInstanceIdentifier.isEmpty()) {
            // we know the event is dry run if there is a correlation event identifiers
            fileWatcherJobEvent.setDryRun(true);
            fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        }

        Trigger jobTrigger = jobExecutionContext.getTrigger();
        if (jobTrigger != null) {
            fileWatcherJobEvent.setJobDescription(jobTrigger.getDescription());

            TriggerKey triggerKey = jobTrigger.getKey();
            if (triggerKey != null) {
                fileWatcherJobEvent.setJobGroup(triggerKey.getGroup());
            }
        }

        fileWatcherJobEvent.setFireTime(jobExecutionContext.getFireTime().getTime());

        if (jobExecutionContext.getNextFireTime() != null) {
            fileWatcherJobEvent.setNextFireTime(jobExecutionContext.getNextFireTime().getTime());
        }

        return fileWatcherJobEvent;
    }

    @Override
    public FileWatcherJobConverterConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(FileWatcherJobConverterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configuredResourceId = id;
    }
}
