package org.ikasan.ootb.scheduler.agent.module.boot.components;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.ImportContextParametersProcess;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Importing Context parameters from the Dashboard flow component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class ImportContextParametersFlowComponentFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Value( "${context.parameters.scheduled.consumer.cron}" )
    String cron;

    @Resource
    BuilderFactory builderFactory;


    public Consumer getScheduledConsumer()
    {
        ScheduledConsumerConfiguration configuration = new ScheduledConsumerConfiguration();
        configuration.setCronExpression(cron);
        return builderFactory.getComponentBuilder().scheduledConsumer()
            .setConfiguration(configuration)
            .setConfiguredResourceId(moduleName+"-contextParametersScheduledConsumerConfiguration")
            .build();
    }


    public Producer getImportContextParametersProcess() {
        return new ImportContextParametersProcess();
    }

}

