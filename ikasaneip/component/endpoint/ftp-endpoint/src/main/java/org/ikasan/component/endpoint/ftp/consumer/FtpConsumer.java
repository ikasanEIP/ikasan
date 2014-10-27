/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.ftp.consumer;

import org.apache.log4j.Logger;

import org.ikasan.component.endpoint.ftp.common.BaseFileTransferMappedRecord;
import org.ikasan.component.endpoint.ftp.common.ClientConnectionException;
import org.ikasan.component.endpoint.ftp.common.ClientInitialisationException;
import org.ikasan.component.endpoint.ftp.endpoint.FtpEndpoint;
import org.ikasan.component.endpoint.ftp.endpoint.FtpEndpointFactory;
import org.ikasan.component.endpoint.ftp.endpoint.FtpEndpointImpl;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;
import org.quartz.*;


import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * Implementation of a generic client FTP consumer.
 *
 * @author Ikasan Development Team
 */
public class FtpConsumer
        implements Consumer<EventListener, EventFactory>, ConfiguredResource<FtpConsumerConfiguration>, Job {
    /**
     * class logger
     */
    private static Logger logger = Logger.getLogger(FtpConsumer.class);


    /**
     * Scheduler
     */
    private Scheduler scheduler;

    /**
     * scheduled job factory
     */
    private ScheduledJobFactory scheduledJobFactory;

    /**
     * consumer event factory
     */
    private EventFactory<FlowEvent<?, ?>> flowEventFactory;

    /**
     * consumer event listener
     */
    private EventListener eventListener;

    /**
     * configuredResourceId
     */
    private String configuredResourceId;

    /**
     * job identifying name
     */
    private String name;

    /**
     * job identifying group
     */
    private String group;

    /**
     * Ftp consumer configuration - default to vanilla instance
     */
    protected FtpConsumerConfiguration configuration = new FtpConsumerConfiguration();

    /**
     * Ftp tech endpoint factory
     */
    private FtpEndpointFactory ftpEndpointFactory;

    /**
     * Ftp tech endpoint connector using FTP Client libraries.
     */
    private FtpEndpoint ftpEndpoint;

    /**
     * Default constructor
     */
    public FtpConsumer() {
        // nothing to do with the default constructor
    }

    /**
     * Constructor
     *
     * @param scheduler
     * @param scheduledJobFactory
     * @param name
     * @param group
     * @param configuration
     */
    public FtpConsumer(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, String name, String group, FtpConsumerConfiguration configuration, FtpEndpointFactory ftpEndpointFactory) {
        this.scheduler = scheduler;
        if (scheduler == null) {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }

        this.scheduledJobFactory = scheduledJobFactory;
        if (scheduledJobFactory == null) {
            throw new IllegalArgumentException("scheduledJobFactory cannot be 'null'");
        }

        this.name = name;
        if (name == null) {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.group = group;
        if (group == null) {
            throw new IllegalArgumentException("group cannot be 'null'");
        }

        this.configuration = configuration;
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be 'null'");
        }

        this.ftpEndpointFactory = ftpEndpointFactory;
        if (ftpEndpointFactory == null) {
            throw new IllegalArgumentException("ftpEndpointFactory cannot be 'null'");
        }
    }


    public void setEventFactory(EventFactory flowEventFactory) {
        this.flowEventFactory = flowEventFactory;
    }

    public FtpConsumerConfiguration getConfiguration() {
        return this.configuration;
    }

    public String getConfiguredResourceId() {
        return this.configuredResourceId;
    }

    public void setConfiguration(FtpConsumerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    /**
     * Start the underlying tech
     */
    public void start() {
        try {
            JobDetail jobDetail = scheduledJobFactory.createJobDetail(this, this.name, this.group);

            // create trigger
            JobKey jobkey = jobDetail.getKey();

            Trigger trigger = getCronTrigger(jobkey, configuration.getCronExpression());
            Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
            logger.info("Scheduled consumer for flow ["
                    + jobkey.getName()
                    + "] module [" + jobkey.getGroup()
                    + "] starting at [" + scheduledDate + "]");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {
            ftpEndpoint = ftpEndpointFactory.createFtpEndpoint(configuration);
        } catch (ClientInitialisationException ftpClientInitialisationException) {
            throw new RuntimeException(ftpClientInitialisationException);
        } catch (ClientConnectionException ftpClientConnectionException) {
            throw new RuntimeException(ftpClientConnectionException);
        }

    }

    /**
     * Stop the scheduled job and triggers
     */
    public void stop() {
        ftpEndpoint.closeSession();

        try {
            JobKey jobKey = new JobKey(name, group);
            if (this.scheduler.checkExists(jobKey)) {
                this.scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Is the underlying tech actively running
     *
     * @return isRunning
     */
    public boolean isRunning() {
        try {
            if (this.scheduler.isShutdown() || this.scheduler.isInStandbyMode()) {
                return false;
            }

            JobKey jobKey = new JobKey(this.name, this.group);
            if (this.scheduler.checkExists(jobKey)) {
                return true;
            }

            return false;
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Set the consumer event listener
     *
     * @param eventListener
     */
    public void setListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Callback from the scheduler.
     *
     * @param context
     */
    public void execute(JobExecutionContext context) {

        BaseFileTransferMappedRecord consumedFile = ftpEndpoint.getFile();

        if(consumedFile!=null) {
            FlowEvent<?, ?> flowEvent = createFlowEvent(consumedFile);
            this.eventListener.invoke(flowEvent);
        }
    }

    /**
     * Override this is you want control over the flow event created by this
     * consumer
     *
     * @param consumedFile
     * @return
     */
    protected FlowEvent<?, ?> createFlowEvent(BaseFileTransferMappedRecord consumedFile) {

        FlowEvent<?, ?> flowEvent = this.flowEventFactory.newEvent(consumedFile.getName(), consumedFile);
        return flowEvent;
    }

    /**
     * Method factory for creating a cron trigger
     *
     * @return jobDetail
     * @throws java.text.ParseException
     */
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression) throws ParseException {
        return newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup()).withSchedule(cronSchedule(cronExpression)).build();
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.Consumer#getEventFactory()
     */
    public EventFactory getEventFactory() {
        return this.flowEventFactory;
    }


}
