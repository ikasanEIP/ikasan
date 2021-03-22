package org.ikasan.endpoint.sftp.consumer;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.quartz.Scheduler;

/**
 * Defines a sftp consumer component for use by factories
 */
public class SftpConsumer extends ScheduledConsumer
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
}
