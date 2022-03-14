package org.ikasan.ootb.scheduler.agent.module.boot.components;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.HousekeepLogFilesProcess;
import org.ikasan.ootb.scheduler.agent.module.configuration.HousekeepLogFilesProcessConfiguration;
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

    @Resource
    BuilderFactory builderFactory;


    public Consumer getScheduledConsumer()
    {
        ScheduledConsumerConfiguration configuration = new ScheduledConsumerConfiguration();
        configuration.setCronExpression("20 20 03 * * ?");
        return builderFactory.getComponentBuilder().scheduledConsumer()
            .setConfiguration(configuration)
            .setConfiguredResourceId(moduleName+"-scheduledConsumerConfiguration")
            .build();
    }


    public Producer getHousekeepLogFilesProcess() {
        HousekeepLogFilesProcessConfiguration configuration = new HousekeepLogFilesProcessConfiguration();
        HousekeepLogFilesProcess housekeepLogFilesProcess = new HousekeepLogFilesProcess();
        housekeepLogFilesProcess.setConfiguration(configuration);
        housekeepLogFilesProcess.setConfiguredResourceId(moduleName+"-housekeepLogFilesProcess");

        return housekeepLogFilesProcess;
    }



}

