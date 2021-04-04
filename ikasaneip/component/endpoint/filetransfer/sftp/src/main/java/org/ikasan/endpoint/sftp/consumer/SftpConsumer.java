package org.ikasan.endpoint.sftp.consumer;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

/**
 * Defines a sftp consumer component for use by factories
 */
public class SftpConsumer extends ScheduledConsumer implements Job
{
    /**
     * Constructor
     *
     * @param scheduler the Quartz Scheduler
     */
    public SftpConsumer(Scheduler scheduler)
    {
        super(scheduler);
    }

    public SftpConsumerConfiguration getConfiguration(){
        return (SftpConsumerConfiguration) super.getConfiguration();
    }

    public void execute(JobExecutionContext context){
        super.execute(context);
    }
}
