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

import java.util.*;

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
            List<String> allFlowsForPlanName = filterFlowNamesForGivenPlanName(contextInstance.getName());
            List<String> scheduledFlows = filterFlowNamesThatContainTargetElement(allFlowsForPlanName, SCHEDULED_CONSUMER_PROFILE);
            List<String> fileWatcherFlows = filterFlowNamesThatContainTargetElement(allFlowsForPlanName, FILE_CONSUMER_PROFILE);

            updateConsumerOnTargetFlows(SCHEDULED_CONSUMER, scheduledFlows, contextInstance.getId());
            updateConsumerOnTargetFlows(FILE_CONSUMER, fileWatcherFlows, contextInstance.getId());

            ContextInstanceCache.instance().put(contextInstance.getId(), contextInstance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }

    public void update(String correlationId) {
        // If the contextInstanceCache has somehow dropped the instance before we can remove its correlation ID
        // raise an error but try to recover anyway (extra resilience)
        try {
            Set<String> allFlows = getModuleConfiguration().getFlowContextMap().keySet();
            List<String> scheduledFlows = filterFlowNamesThatContainTargetElementAndCorrelationId(allFlows, SCHEDULED_CONSUMER_PROFILE, SCHEDULED_CONSUMER, correlationId);
            List<String> fileWatcherFlows = filterFlowNamesThatContainTargetElementAndCorrelationId(allFlows, FILE_CONSUMER_PROFILE, FILE_CONSUMER, correlationId);

            removeCorrelationIdOnTargetFlows(SCHEDULED_CONSUMER, scheduledFlows, correlationId);
            removeCorrelationIdOnTargetFlows(FILE_CONSUMER, fileWatcherFlows, correlationId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }

    private List<String> filterFlowNamesForGivenPlanName(String planName) {
        List<String> flows = new ArrayList<>();
        // flow name -> context name
        Map<String, String> flowContextMap = getModuleConfiguration().getFlowContextMap();
        flowContextMap.forEach((key, value) -> {
            if (value.equals(planName)) {
                flows.add(key);
            }
        });
        logger.info("The following flows will be reviewed  [" + flows + "] because they belong to plan [" + planName + "]");
        return flows;
    }

    private SchedulerAgentConfiguredModuleConfiguration getModuleConfiguration() {
        ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configuredModule =
            (ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration>)moduleService.getModule(moduleName);
        return configuredModule.getConfiguration();
    }

    private List<String> filterFlowNamesThatContainTargetElementAndCorrelationId(Collection<String> allFlows, String elementProfile, String consumerType, String correlationId) {
        List<String> filterFlowsThatContainTargetElementAndCorrelationID = new ArrayList<>();
        List<String> filterFlowsThatContainTargetElement = filterFlowNamesThatContainTargetElement(allFlows, elementProfile);

        Module<Flow> module = this.moduleService.getModule(moduleName);

        filterFlowsThatContainTargetElement.forEach(flowName -> {
            Flow flow =  module.getFlow(flowName);
            ConfiguredResource<CorrelatedScheduledConsumerConfiguration> consumer =
                (ConfiguredResource<CorrelatedScheduledConsumerConfiguration>)flow.getFlowElement(consumerType).getFlowComponent();

            CorrelatedScheduledConsumerConfiguration correlatedConsumerConfiguration = consumer.getConfiguration();
            if (correlatedConsumerConfiguration != null && correlatedConsumerConfiguration.getCorrelatingIdentifiers().contains(correlationId)) {
                filterFlowsThatContainTargetElementAndCorrelationID.add(flowName);
            }
        });

        return filterFlowsThatContainTargetElementAndCorrelationID;
    }

    private List<String> filterFlowNamesThatContainTargetElement(Collection<String> allFlows, String elementProfile) {
        List<String> flows = new ArrayList<>();
        // Map of FlowName to Plan Name
        Map<String, String> flowDefinitionProfilesMap = getModuleConfiguration().getFlowDefinitionProfiles();
        allFlows.forEach(flow -> {
            String flowProfile = flowDefinitionProfilesMap.get(flow);
            if (flowProfile != null && flowProfile.equals(elementProfile)) {
                flows.add(flow);
            }
        });
        return flows;
    }

    private void removeCorrelationIdOnTargetFlows(String consumerType, List<String> flowNames, String correlationId) {
        logger.info("Updating flows " + flowNames + " removing correlation ID " + correlationId + " for component type " + consumerType);
        Module<Flow> module = this.moduleService.getModule(moduleName);

        flowNames.forEach(flowName -> {
            Flow flow =  module.getFlow(flowName);
            ConfiguredResource<CorrelatedScheduledConsumerConfiguration> consumer =
                (ConfiguredResource<CorrelatedScheduledConsumerConfiguration>)flow.getFlowElement(consumerType).getFlowComponent();

            CorrelatedScheduledConsumerConfiguration correlatedConsumerConfiguration = consumer.getConfiguration();
            if (correlatedConsumerConfiguration.getCorrelatingIdentifiers().contains(correlationId)) {
                logger.info("Removing correlation id [" + correlationId + "] from consumer [" + correlatedConsumerConfiguration.getJobName() + "] and stop/starting flow");

                // Stop flow here, to prevent any triggers (maybe there is a way to suspend)
                flow.stop();
                correlatedConsumerConfiguration.getCorrelatingIdentifiers().remove(correlationId);
                // If we don't force the update to be persisted, the old value will be used when the flow starts
                // because persisted configuration is used at startup
                configurationService.update(consumer);

                // This needs to be out of the context before the flow restarts otherwise it will be added back into the trigger.
                if (ContextInstanceCache.instance().getByCorrelationId(correlationId) != null) {
                    ContextInstanceCache.instance().remove(correlationId);
                }
                flow.start();
            } else {
                logger.warn("Expected to remove correlation id [" + correlationId + "] from consumer [" + correlatedConsumerConfiguration.getJobName() + "] but it was not there");
            }
        });
    }

    private void updateConsumerOnTargetFlows(String consumerType, List<String> flowNames, String correlationId) {
        logger.info("Updating flows " + flowNames + " with correlation ID " + correlationId + " for component " + consumerType);
        Module<Flow> module = this.moduleService.getModule(moduleName);

        flowNames.forEach(flowName -> {
            Flow flow =  module.getFlow(flowName);
            updateConsumerConfiguration(consumerType, correlationId, flow);
        });
    }

    /**
     * From this flow, get the component (e.g. FileWatcher, ScheduleConsumer, assumed only 1 per flow) and ensure its
     * config contains the correlation ID of the root job plan instance.
     * Then stop/start the flow so that config becomes active.
     * @param consumerType e.g. FileWatcher, ScheduleConsumer
     * @param correlationId of the root of the job plans
     * @param flow containing the components to be updated
     */
    private void updateConsumerConfiguration(String consumerType, String correlationId, Flow flow) {
        ConfiguredResource<CorrelatedScheduledConsumerConfiguration> consumer =
            (ConfiguredResource<CorrelatedScheduledConsumerConfiguration>)flow.getFlowElement(consumerType).getFlowComponent();

        CorrelatedScheduledConsumerConfiguration correlatedConsumerConfiguration = consumer.getConfiguration();

        boolean flagToUpdate = false;

        if (correlatedConsumerConfiguration.getCorrelatingIdentifiers() == null) {
            correlatedConsumerConfiguration.setCorrelatingIdentifiers(Arrays.asList(correlationId));
            flagToUpdate = true;
        } else if ( ! correlatedConsumerConfiguration.getCorrelatingIdentifiers().contains(correlationId)) {
            correlatedConsumerConfiguration.getCorrelatingIdentifiers().add(correlationId);
            flagToUpdate = true;
        }

        if (flagToUpdate) {
            this.configurationService.update(consumer);
            flow.stop();
            flow.start();
        }
    }
}
