package org.ikasan.ootb.scheduler.agent.module.boot;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.EvaluationOtherwise;
import org.ikasan.ootb.scheduler.agent.module.boot.components.QuartzSchedulerFileEventJobFlowComponentFactory;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class QuartzSchedulerFileEventJobFlowFactory {

    @Value( "${module.name}" )
    private String moduleName;

    @Autowired
    private BuilderFactory builderFactory;

    @Autowired
    private QuartzSchedulerFileEventJobFlowComponentFactory componentFactory;

    public Flow create(String jobName)
    {
        EvaluationOtherwise<Flow> flowBuilder =  builderFactory.getModuleBuilder(moduleName).getFlowBuilder(jobName)
            .withDescription("The " + jobName + " Quartz Schedule Flow is responsible for kicking off jobs on a scheduled basis based on the configured cron expression.")
            .consumer("Scheduled Consumer", componentFactory.getScheduledConsumer())
            .converter("JobExecution to FileWatcherJobEvent", componentFactory.getFileWatcherJobConverter())
            .filter("Context Instances Active Filter", componentFactory.getContextInstancesActiveFilter())
            .splitter("File Watcher Event Correlation Identifier Splitter", componentFactory.getFileWatcherEventCorrelationIdentifierSplitter())
            .singleRecipientRouter("File Watcher Job Queue Size Weighted Router", componentFactory.getFileWatcherJobQueueSizeWeightedRouter());

        // We iterate over the internal file watcher event queues in the cache and create a route for each
        InternalFileWatcherJobQueueCache.instance().keys().forEach(key -> {
            flowBuilder.when(key, builderFactory.getRouteBuilder()
                .producer("File Watcher Event Producer - " + key, componentFactory.getInternalFileWatcherEventBigQueueProducer(key)));
        });

        // we should never get here!
        return flowBuilder.otherwise(builderFactory.getRouteBuilder()
            .producer("No File Watcher Flows Available Producer", this.componentFactory.getNoFileWatcherFlowsAvailableProducer()));
    }
}
