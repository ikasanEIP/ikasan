package org.ikasan.ootb.scheduler.agent.module.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatedScheduledConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.configurationService.util.ReflectionUtils;
import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.provision.JobProvisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobProvisionServiceImpl implements JobProvisionService {

    Logger logger = LoggerFactory.getLogger(JobProvisionServiceImpl.class);

    @Value( "${module.name}" )
    private String moduleName;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleActivator moduleActivator;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * This a map of context name to list of objects
     *  [0] = isDynamic File name (boolean)
     *  [1] = spelExpression (String)
     *  [2] = any parameters to replace on the spel expression (Map<String, String>)
     *  E.g.
     *   scheduled.dynamic.filename.spel.expressions= \
     *    { \
     *    'CONTEXT-369160711': { \
     *      true,\
     *      "#fileNamePattern?.contains('yyyyMMdd') ? \
     *      (T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache).getContextParameter(#contextName, 'BusinessDate') != null ? \
     *      #fileNamePattern.replace('yyyyMMdd', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache).getContextParameter(#contextName, 'BusinessDate')) : #fileNamePattern) : #fileNamePattern" , \
     *       {'#contextName':'contextId'} \
     *      } \
     *    }
     */
    @Value("#{${scheduled.dynamic.filename.spel.expressions:{T(java.util.Collections).emptyMap()}}}")
    Map<String, List<Object>> spelExpressionsMap;

    private ObjectMapper mapper;

    /**
     * Constructor
     */
    public JobProvisionServiceImpl() {
        this.mapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("org.ikasan.spec.scheduled.job.model")
            .allowIfSubType("org.ikasan.job.orchestration.model.job")
            .allowIfSubType("org.ikasan.job.orchestration.model.context")
            .allowIfSubType("java.util.ArrayList")
            .allowIfSubType("java.util.HashMap")
            .build();
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);

        this.mapper.registerModule(simpleModule);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void provisionJobs(List<SchedulerJob> jobs, String actor) {
        try
        {
            long now = System.currentTimeMillis();
            logger.info(String.format("Provisioning %s jobs for agent %s", jobs.size(), this.moduleName));

            Module<Flow> module = this.moduleService.getModule(moduleName);
            logger.info(String.format("Deactivating module [%s]", this.moduleName));
            moduleActivator.deactivate(module);
            logger.info(String.format("Deactivated module [%s]", this.moduleName));

            ConfiguredResource<ConfiguredModuleConfiguration> configuredModule = getConfiguredResource(module);
            ConfiguredModuleConfiguration configuredModuleConfiguration = configuredModule.getConfiguration();

            logger.info(String.format("Updating module configuration [%s]", this.moduleName));
            this.updateInitialModuleConfiguration(jobs, configuredModuleConfiguration);
            this.configurationService.update(configuredModule);
            logger.info(String.format("Updated module configuration [%s]", this.moduleName));

            logger.info(String.format("Activating module [%s]", this.moduleName));
            moduleActivator.activate(module);
            logger.info(String.format("Activated module [%s]", this.moduleName));

            logger.info(String.format("Configuring components [%s]", this.moduleName));
            this.configureComponents(jobs, module);
            logger.info(String.format("Configured components [%s]", this.moduleName));

            logger.info(String.format("Updating startup types [%s]", this.moduleName));
            this.updateModuleConfigurationStartupType(jobs, configuredModuleConfiguration);
            this.configurationService.update(configuredModule);
            logger.info(String.format("Updated startup types [%s]", this.moduleName));

            logger.info(String.format("Starting jobs [%s]", this.moduleName));
            this.startJobs(jobs);
            logger.info(String.format("Finished provisioning %s jobs. Time taken %s milliseconds."
                , jobs.size(), System.currentTimeMillis()-now));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JobProvisionServiceException(e);
        }
    }

    @Override
    public void removeJobs(String contextName) {
        logger.info(String.format("Removing jobs for context[%s].", contextName));
        Module<Flow> module = this.moduleService.getModule(moduleName);
        logger.info(String.format("Deactivating module [%s]", this.moduleName));
        moduleActivator.deactivate(module);
        logger.info(String.format("Deactivated module [%s]", this.moduleName));

        ConfiguredResource<ConfiguredModuleConfiguration> configuredModule = getConfiguredResource(module);
        ConfiguredModuleConfiguration configuredModuleConfiguration = configuredModule.getConfiguration();

        this.clearFlowConfig(configuredModuleConfiguration, contextName);
        this.configurationService.update(configuredModule);

        logger.info(String.format("Activating module [%s]", this.moduleName));
        moduleActivator.activate(module);
        logger.info(String.format("Activated module [%s]", this.moduleName));

        logger.info(String.format("Finished removing jobs for context[%s].", contextName));
    }

    /**
     * Update the initial module configuration in order to define the job types. All jobs are initially set
     * to start manually allowing for the relevant components to be configured.
     *
     * @param jobs
     * @param configuredModuleConfiguration
     */
    private void updateInitialModuleConfiguration(List<SchedulerJob> jobs, ConfiguredModuleConfiguration configuredModuleConfiguration) {
        String contextName = jobs.get(0).getContextName();
        clearFlowConfig(configuredModuleConfiguration, contextName);

        jobs.forEach(job -> {
            if (configuredModuleConfiguration instanceof SchedulerAgentConfiguredModuleConfiguration) {
                SchedulerAgentConfiguredModuleConfiguration configuration = (SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration;
                configuration.getFlowContextMap().put(job.getJobName(), job.getContextName());

            }
            if(job instanceof FileEventDrivenJob) {
                configuredModuleConfiguration.getFlowDefinitions().put(job.getJobName(), "MANUAL");
                configuredModuleConfiguration.getFlowDefinitionProfiles().put(job.getJobName(), "FILE");
            }
            else if(job instanceof QuartzScheduleDrivenJob) {
                configuredModuleConfiguration.getFlowDefinitions().put(job.getJobName(), "MANUAL");
                configuredModuleConfiguration.getFlowDefinitionProfiles().put(job.getJobName(), "QUARTZ");
            }
            else if(job instanceof InternalEventDrivenJob) {
                configuredModuleConfiguration.getFlowDefinitions().put(job.getJobName(), "MANUAL");
                configuredModuleConfiguration.getFlowDefinitionProfiles().put(job.getJobName(), "SCHEDULER_JOB");
            }
        });

        configuredModuleConfiguration.getFlowDefinitions().put("Scheduled Process Event Outbound Flow", "AUTOMATIC");
        configuredModuleConfiguration.getFlowDefinitionProfiles().put("Scheduled Process Event Outbound Flow", "OUTBOUND");
    }

    private void clearFlowConfig(ConfiguredModuleConfiguration configuredModuleConfiguration, String contextName) {
        if (configuredModuleConfiguration instanceof SchedulerAgentConfiguredModuleConfiguration) {
            SchedulerAgentConfiguredModuleConfiguration configuration = (SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration;

            List<String> flowsInThatContext = new ArrayList<>();
            for (String jobName : configuration.getFlowContextMap().keySet()) {
                if (configuration.getFlowContextMap().get(jobName).equalsIgnoreCase(contextName)) {
                    flowsInThatContext.add(jobName);
                }
            }

            if (configuration.getFlowDefinitions() != null &&
                configuration.getFlowDefinitions().keySet() != null &&
                configuration.getFlowDefinitions().keySet().size() > 0 &&
                configuration.getFlowContextMap() != null) {
                configuration.getFlowDefinitions().keySet().removeAll(flowsInThatContext);
            }
            if (configuration.getFlowDefinitionProfiles() != null &&
                configuration.getFlowDefinitionProfiles().keySet() != null &&
                configuration.getFlowDefinitionProfiles().keySet().size() > 0 &&
                configuration.getFlowContextMap() != null) {
                configuration.getFlowDefinitionProfiles().keySet().removeAll(flowsInThatContext);
            }

            configuration.getFlowContextMap().keySet().removeAll(flowsInThatContext);
        }
    }

    /**
     * Method to set all job startup types to the type configured on the job.
     *
     * @param jobs
     * @param configuredModuleConfiguration
     */
    private void updateModuleConfigurationStartupType(List<SchedulerJob> jobs, ConfiguredModuleConfiguration configuredModuleConfiguration) {
        jobs.forEach(job -> {
            configuredModuleConfiguration.getFlowDefinitions().put(job.getJobName(), job.getStartupControlType());
        });
    }

    /**
     * Configure all relevant components.
     *
     * @param jobs
     * @param module
     */
    private void configureComponents(List<SchedulerJob> jobs, Module<Flow> module) {
        jobs.forEach(job -> {
            if(job instanceof FileEventDrivenJob) {
                configureFileEventDrivenFlowComponents(module, job);
            }
            else if(job instanceof QuartzScheduleDrivenJob) {
                configureQuartzScheduledFlowComponents(module, job);
            }
        });
    }

    private void configureQuartzScheduledFlowComponents(Module<Flow> module, SchedulerJob job) {
        Flow flow = module.getFlow(job.getJobName());
        ConfiguredResource<CorrelatedScheduledConsumerConfiguration> consumer = (ConfiguredResource<CorrelatedScheduledConsumerConfiguration>)flow
            .getFlowElement("Scheduled Consumer").getFlowComponent();

        CorrelatedScheduledConsumerConfiguration configuration = consumer.getConfiguration();
        this.updateScheduleConsumerConfiguration((QuartzScheduleDrivenJob) job, configuration);

        this.configurationService.update(consumer);

        ConfiguredResource<ContextInstanceFilterConfiguration> contextFilter = (ConfiguredResource<ContextInstanceFilterConfiguration>)flow
            .getFlowElement("Context Instance Active Filter").getFlowComponent();

        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = contextFilter.getConfiguration();

        contextInstanceFilterConfiguration.addContextInstanceIds(configuration.getCorrelatingIdentifiers());

        this.configurationService.update(contextFilter);

        ConfiguredResource<ContextualisedConverterConfiguration> converter = (ConfiguredResource<ContextualisedConverterConfiguration>)flow
            .getFlowElement("JobExecution to ScheduledStatusEvent").getFlowComponent();

        ContextualisedConverterConfiguration converterConfiguration = converter.getConfiguration();
        converterConfiguration.setContextName(job.getContextName());
        converterConfiguration.setChildContextNames(job.getChildContextNames());

        this.configurationService.update(converter);

        ConfiguredResource<BlackoutRouterConfiguration> blackoutRouter = (ConfiguredResource<BlackoutRouterConfiguration>)flow
            .getFlowElement("Blackout Router").getFlowComponent();

        BlackoutRouterConfiguration blackoutRouterConfiguration = blackoutRouter.getConfiguration();
        blackoutRouterConfiguration.setCronExpressions(((QuartzScheduleDrivenJob) job).getBlackoutWindowCronExpressions());
        blackoutRouterConfiguration.setDateTimeRanges(((QuartzScheduleDrivenJob) job).getBlackoutWindowDateTimeRanges());

        this.configurationService.update(blackoutRouter);

        ConfiguredResource<ScheduledProcessEventFilterConfiguration> scheduledProcessEventFilter = (ConfiguredResource<ScheduledProcessEventFilterConfiguration>)flow
            .getFlowElement("Publish Scheduled Status").getFlowComponent();

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration = scheduledProcessEventFilter.getConfiguration();
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(((QuartzScheduleDrivenJob) job).isDropEventOnBlackout());

        this.configurationService.update(scheduledProcessEventFilter);
    }

    private void configureFileEventDrivenFlowComponents(Module<Flow> module, SchedulerJob job) {
        Flow flow = module.getFlow(job.getJobName());
        ConfiguredResource<CorrelatedFileConsumerConfiguration> consumer = (ConfiguredResource<CorrelatedFileConsumerConfiguration>)flow
            .getFlowElement("File Consumer").getFlowComponent();

        CorrelatedFileConsumerConfiguration configuration = (CorrelatedFileConsumerConfiguration)consumer.getConfiguration();
        this.updateFileConsumerConfiguration((FileEventDrivenJob) job, configuration);

        this.configurationService.update(consumer);

        ConfiguredResource<ContextInstanceFilterConfiguration> contextFilter = (ConfiguredResource<ContextInstanceFilterConfiguration>)flow
            .getFlowElement("Context Instance Active Filter").getFlowComponent();

        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = contextFilter.getConfiguration();
        contextInstanceFilterConfiguration.addContextInstanceIds(configuration.getCorrelatingIdentifiers());

        this.configurationService.update(contextFilter);

        ConfiguredResource<FileAgeFilterConfiguration> filter = (ConfiguredResource<FileAgeFilterConfiguration>)flow
            .getFlowElement("File Age Filter").getFlowComponent();

        FileAgeFilterConfiguration filterConfiguration = filter.getConfiguration();
        filterConfiguration.setFileAgeSeconds(((FileEventDrivenJob) job).getMinFileAgeSeconds());
        filterConfiguration.setJobName(job.getJobName());

        this.configurationService.update(filter);

        ConfiguredResource<SchedulerFileFilterConfiguration> schedulerFileFilterConfigurationConfiguredResource = (ConfiguredResource<SchedulerFileFilterConfiguration>)flow
            .getFlowElement("Duplicate Message Filter").getFlowComponent();

        SchedulerFileFilterConfiguration schedulerFileFilterConfiguration = schedulerFileFilterConfigurationConfiguredResource.getConfiguration();
        schedulerFileFilterConfiguration.setJobName(job.getJobName());

        this.configurationService.update(schedulerFileFilterConfigurationConfiguredResource);


        ConfiguredResource<ContextualisedConverterConfiguration> converter = (ConfiguredResource<ContextualisedConverterConfiguration>)flow
            .getFlowElement("JobExecution to ScheduledStatusEvent").getFlowComponent();

        ContextualisedConverterConfiguration converterConfiguration = converter.getConfiguration();
        converterConfiguration.setContextName(job.getContextName());
        converterConfiguration.setChildContextNames(job.getChildContextNames());

        this.configurationService.update(converter);

        ConfiguredResource<BlackoutRouterConfiguration> blackoutRouter = (ConfiguredResource<BlackoutRouterConfiguration>)flow
            .getFlowElement("Blackout Router").getFlowComponent();

        BlackoutRouterConfiguration blackoutRouterConfiguration = blackoutRouter.getConfiguration();
        blackoutRouterConfiguration.setCronExpressions(((FileEventDrivenJob) job).getBlackoutWindowCronExpressions());
        blackoutRouterConfiguration.setDateTimeRanges(((FileEventDrivenJob) job).getBlackoutWindowDateTimeRanges());

        this.configurationService.update(blackoutRouter);

        ConfiguredResource<ScheduledProcessEventFilterConfiguration> scheduledProcessEventFilter = (ConfiguredResource<ScheduledProcessEventFilterConfiguration>)flow
            .getFlowElement("Publish Scheduled Status").getFlowComponent();

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration = scheduledProcessEventFilter.getConfiguration();
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(((FileEventDrivenJob) job).isDropEventOnBlackout());

        this.configurationService.update(scheduledProcessEventFilter);

        ConfiguredResource<MoveFileBrokerConfiguration> broker = (ConfiguredResource<MoveFileBrokerConfiguration>)flow
            .getFlowElement("File Move Broker").getFlowComponent();

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = broker.getConfiguration();
        moveFileBrokerConfiguration.setMoveDirectory(((FileEventDrivenJob) job).getMoveDirectory());
        moveFileBrokerConfiguration.setJobName(job.getJobName());

        this.configurationService.update(broker);
    }

    /**
     * Method to star all jobs if they are configured to be started.
     *
     * @param jobs
     */
    private void startJobs(List<SchedulerJob> jobs) {
        String user = UserUtil.getUser();
        jobs.forEach(job -> {
            if(job.getStartupControlType().equals("AUTOMATIC")) {
                this.moduleService.startFlow(job.getAgentName(), job.getJobName(), user);
            }
        });
    }

    /**
     * Update the scheduled consumer configuration.
     *
     * @param job
     * @param scheduledConsumerConfiguration
     */
    private void updateScheduleConsumerConfiguration(QuartzScheduleDrivenJob job, ScheduledConsumerConfiguration scheduledConsumerConfiguration) {
        scheduledConsumerConfiguration.setJobName(job.getJobName());
        scheduledConsumerConfiguration.setJobGroupName(job.getJobGroup());
        scheduledConsumerConfiguration.setDescription(job.getJobDescription());
        scheduledConsumerConfiguration.setCronExpression(job.getCronExpression());
        scheduledConsumerConfiguration.setTimezone(job.getTimeZone());
        scheduledConsumerConfiguration.setEager(job.isEager());
        scheduledConsumerConfiguration.setIgnoreMisfire(job.isIgnoreMisfire());
        scheduledConsumerConfiguration.setMaxEagerCallbacks(job.getMaxEagerCallbacks());
        scheduledConsumerConfiguration.setPassthroughProperties(job.getPassthroughProperties());
        scheduledConsumerConfiguration.setPersistentRecovery(job.isPersistentRecovery());
        scheduledConsumerConfiguration.setRecoveryTolerance(job.getRecoveryTolerance());
    }

    /**
     * Update the file consumer configuration.
     *
     * @param job
     * @param fileConsumerConfiguration
     */
    private void updateFileConsumerConfiguration(FileEventDrivenJob job, CorrelatedFileConsumerConfiguration fileConsumerConfiguration) {
        fileConsumerConfiguration.setFilenames(job.getFilenames());
        fileConsumerConfiguration.setJobName(job.getJobName());
        fileConsumerConfiguration.setJobGroupName(job.getJobGroup());
        fileConsumerConfiguration.setDescription(job.getJobDescription());
        fileConsumerConfiguration.setCronExpression(job.getCronExpression());
        fileConsumerConfiguration.setTimezone(job.getTimeZone());
        fileConsumerConfiguration.setEager(job.isEager());
        fileConsumerConfiguration.setIgnoreMisfire(job.isIgnoreMisfire());
        fileConsumerConfiguration.setMaxEagerCallbacks(job.getMaxEagerCallbacks());
        fileConsumerConfiguration.setPassthroughProperties(job.getPassthroughProperties());
        fileConsumerConfiguration.setPersistentRecovery(job.isPersistentRecovery());
        fileConsumerConfiguration.setRecoveryTolerance(job.getRecoveryTolerance());
        fileConsumerConfiguration.setDirectoryDepth(job.getDirectoryDepth());
        fileConsumerConfiguration.setEncoding(job.getEncoding());
        fileConsumerConfiguration.setIgnoreFileRenameWhilstScanning(job.isIgnoreFileRenameWhilstScanning());
        fileConsumerConfiguration.setIncludeHeader(job.isIncludeHeader());
        fileConsumerConfiguration.setLogMatchedFilenames(job.isLogMatchedFilenames());
        fileConsumerConfiguration.setIncludeTrailer(job.isIncludeTrailer());
        fileConsumerConfiguration.setSortAscending(job.isSortAscending());
        fileConsumerConfiguration.setSortByModifiedDateTime(job.isSortByModifiedDateTime());

        if (spelExpressionsMap != null && !spelExpressionsMap.isEmpty()) {
            for (String contextId : spelExpressionsMap.keySet()) {
                if (contextId.equals(job.getContextName())) {
                    List<Object> spelParams = spelExpressionsMap.get(contextId);
                    // [0] = isDynamic File name (boolean)
                    // [1] = spelExpression (String)
                    // [2] = any parameters to replace on the spel expression (Map<String, String>)
                    boolean isDynamic = (boolean) spelParams.get(0);
                    if (isDynamic) {
                        fileConsumerConfiguration.setDynamicFileName(true);
                        String spelExpression = (String) spelParams.get(1);
                        Map<String, String> spelExpressionParamsToReplace = (Map<String, String>) spelParams.get(2);
                        if (spelExpressionParamsToReplace != null && !spelExpressionParamsToReplace.isEmpty()) {
                            for (String key : spelExpressionParamsToReplace.keySet()) {
                                String replacementValue = getSpelReplacement(spelExpressionParamsToReplace.get(key), job);
                                if (replacementValue != null) {
                                    spelExpression = spelExpression.replace(key, replacementValue);
                                }
                            }
                        }
                        logger.info("Setting spel expression on fileConsumerConfiguration: " + spelExpression);
                        fileConsumerConfiguration.setSpelExpression(spelExpression);
                    }
                }
            }
        }
    }

    /**
     * Cast module to configured resource.
     *
     * @param module
     * @return
     */
    protected ConfiguredResource<ConfiguredModuleConfiguration> getConfiguredResource(Module<Flow> module)
    {
        return (ConfiguredResource<ConfiguredModuleConfiguration>)module;
    }

    /**
     * Get the fieldName value of an object.
     * This is very specific for spel expressions and quoting strings
     * Only currently used in this class, hence here.
     *
     */
    protected String getSpelReplacement(String fieldName, Object clazz) {
        try {
            Object property = ReflectionUtils.getProperty(clazz, fieldName);
            if (property != null) {
                if (property instanceof String) {
                    return "'" + property + "'";
                } else if (property instanceof List) {
                    // assumes its a list of strings so far this is the case
                    StringBuilder builder = new StringBuilder();
                    builder.append("{");
                    int i = 0;
                    for (Object object : ((List<?>) property)) {
                        i++;
                        builder.append("'" + object + "'");
                        if (((List<?>) property).size() != i) {
                            builder.append(",");
                        }
                    }
                    builder.append("}");
                    return builder.toString();

                } else if (property instanceof Map) {
                    int i = 0;
                    // assumes the map is a map of string to string so far this is the case
                    Map map = (Map) property;
                    StringBuilder builder = new StringBuilder();
                    builder.append("{");
                    for (Object key : map.keySet()) {
                        i++;
                        builder.append("'" + key + "'");
                        builder.append(":");
                        builder.append("'" + map.get(key) + "'");
                        if (((Map) property).size() != i) {
                            builder.append(",");
                        }
                    }
                    builder.append("}");
                    return builder.toString();
                } else {
                    return property.toString();
                }
            }
        } catch (Exception e) {
            logger.warn(String.format("Could not get field name [%s] on class [%s]. Error [%s]",
                fieldName, clazz.getClass().getName(), e.getMessage()));
        }

        return null;
    }
}
