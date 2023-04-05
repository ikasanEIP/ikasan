package org.ikasan.ootb.scheduler.agent.module.boot.components;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.HousekeepLogFilesProcess;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.configuration.HousekeepLogFilesProcessConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * File scheduler job event flow component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class HousekeepLogFilesFlowComponentFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Value( "${housekeep.scheduled.consumer.cron}" )
    String cron;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    HousekeepLogFilesProcessConfiguration housekeepLogFilesProcessConfiguration;


    public Consumer getScheduledConsumer()
    {
        ScheduledConsumerConfiguration configuration = new ScheduledConsumerConfiguration();
        configuration.setCronExpression(cron);
        return builderFactory.getComponentBuilder().scheduledConsumer()
            .setConfiguration(configuration)
            .setConfiguredResourceId(moduleName+"-scheduledConsumerConfiguration")
            .build();
    }


    public Producer getHousekeepLogFilesProcess() {
        HousekeepLogFilesProcess housekeepLogFilesProcess = new HousekeepLogFilesProcess();
        housekeepLogFilesProcess.setConfiguration(housekeepLogFilesProcessConfiguration);
        housekeepLogFilesProcess.setConfiguredResourceId(moduleName+"-housekeepLogFilesProcess");

        return housekeepLogFilesProcess;
    }



}

