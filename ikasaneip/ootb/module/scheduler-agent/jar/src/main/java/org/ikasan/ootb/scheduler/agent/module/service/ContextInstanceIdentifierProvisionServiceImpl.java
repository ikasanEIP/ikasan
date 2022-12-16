package org.ikasan.ootb.scheduler.agent.module.service;

import org.ikasan.component.endpoint.quartz.consumer.CorrelatedScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ContextInstanceIdentifierProvisionServiceImpl implements ContextInstanceIdentifierProvisionService {
    Logger logger = LoggerFactory.getLogger(ContextInstanceIdentifierProvisionServiceImpl.class);
    protected static final String SCHEDULED_CONSUMER = "Scheduled Consumer";
    protected static final String FILE_CONSUMER = "File Consumer";
    protected static final String SCHEDULED_CONSUMER_PROFILE = "QUARTZ";
    protected static final String FILE_CONSUMER_PROFILE = "FILE";
    @Value( "${module.name}" )
    private String moduleName;
    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void provision(ContextInstance contextInstance) {

        try {
            List<String> allFlows = getFlowsForModules(contextInstance);
            List<String> scheduledFlows = filterFlowsOnProfile(allFlows, SCHEDULED_CONSUMER_PROFILE);
            List<String> fileWatcherFlows = filterFlowsOnProfile(allFlows, FILE_CONSUMER_PROFILE);

            configureFlows(SCHEDULED_CONSUMER, contextInstance, scheduledFlows);
            configureFlows(FILE_CONSUMER, contextInstance, fileWatcherFlows);

            ContextInstanceCache.instance().put(contextInstance.getId(), contextInstance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }

    private List<String> getFlowsForModules(ContextInstance contextInstance) {
        List<String> flows = new ArrayList<>();
        ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configuredModule = getConfiguredResource(moduleService.getModule(moduleName));
        SchedulerAgentConfiguredModuleConfiguration  configuredModuleConfiguration = configuredModule.getConfiguration();

        Map<String, String> flowContextMap = configuredModuleConfiguration.getFlowContextMap();
        // flow name -> context name
        flowContextMap.entrySet().forEach(entry -> {
            if (entry.getValue().equals(contextInstance.getName())) {
                flows.add(entry.getKey());
            }
        });
        logger.info("The following flows will be reviewed  [" + flows + "]");
        return flows;
    }

    private List<String> filterFlowsOnProfile(List<String> allFlows, String profile) {
        List<String> flows = new ArrayList<>();
        ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configuredModule = getConfiguredResource(moduleService.getModule(moduleName));
        SchedulerAgentConfiguredModuleConfiguration  configuredModuleConfiguration = configuredModule.getConfiguration();
        // Map of FlowName to Plan Name
        Map<String, String> flowDefinitionProfilesMap = configuredModuleConfiguration.getFlowDefinitionProfiles();
        allFlows.forEach(flow -> {
            String flowProfile = flowDefinitionProfilesMap.get(flow);
            if (flowProfile != null && flowProfile.equals(profile)) {
                flows.add(flow);
            }
        });
        return flows;
    }

    private void configureFlows(String flowType, ContextInstance contextInstance, List<String> flowNames) {
        logger.info("Updating flows " + flowNames + " with correlation ID " + contextInstance.getId());
        Module<Flow> module = this.moduleService.getModule(moduleName);

        flowNames.forEach(flowName -> {
            Flow flow =  module.getFlow(flowName);
            updateConsumerConfiguration(flowType, contextInstance, flow);
        });
    }

    /**
     * Cast module to configured resource.
     *
     * @param module instance to be recast
     * @return correctly cast instance
     */
    protected ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> getConfiguredResource(Module<Flow> module)
    {
        return (ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration>)module;
    }

    /**
     * From this flow, get the component (e.g. FileWatcher, ScheduleConsumer, assumed only 1 per flow) and ensure its
     * config contains the correlation ID of the root job plan instance.
     * Then stop/start the flow so that config becomes active.
     * @param consumerType e.g. FileWatcher, ScheduleConsumer
     * @param contextInstance of the root of the job plans
     * @param flow containing the components to be updated
     */
    private void updateConsumerConfiguration(String consumerType, ContextInstance contextInstance, Flow flow) {
        // Note, the FILE_CONSUMER inherits from the SCHEDULED_CONSUMER
        ConfiguredResource<CorrelatedScheduledConsumerConfiguration> consumer =
            (ConfiguredResource<CorrelatedScheduledConsumerConfiguration>)flow.getFlowElement(consumerType).getFlowComponent();

        CorrelatedScheduledConsumerConfiguration correlatedConsumerConfiguration = consumer.getConfiguration();

        boolean flagToUpdate = false;

        if (correlatedConsumerConfiguration.getCorrelatingIdentifiers() == null) {
            correlatedConsumerConfiguration.setCorrelatingIdentifiers(Arrays.asList(contextInstance.getId()));
            flagToUpdate = true;
        } else if ( ! correlatedConsumerConfiguration.getCorrelatingIdentifiers().contains(contextInstance.getId())) {
            correlatedConsumerConfiguration.getCorrelatingIdentifiers().add(contextInstance.getId());
            flagToUpdate = true;
        }

        if (flagToUpdate) {
            this.configurationService.update(consumer);
            flow.stop();
            flow.start();
        }
    }
}
