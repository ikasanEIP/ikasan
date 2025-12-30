package org.ikasan.ootb.scheduler.agent.module.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.AgentFlowProfiles;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.FileWatcherJobConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.converters.ObjectMapperFactory;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
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
import org.ikasan.spec.scheduled.provision.JobProvisionServiceException;
import org.ikasan.spec.scheduled.provision.JobProvisionServiceLockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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

    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    @Autowired
    private AgentInstanceRecoveryManager agentInstanceRecoveryManager;

    private final ReentrantLock lock = new ReentrantLock();
    private ObjectMapper objectMapper;

    /**
     * Constructor
     */
    public JobProvisionServiceImpl() {
        this.objectMapper = ObjectMapperFactory.newInstance();
    }

    @Override
    public synchronized void provisionJobs(List<SchedulerJob> jobs, String actor) {
        if(!lock.tryLock()) {
            String message = "An attempt to provision jobs has failed to obtain the lock indicating that " +
                "another thread is currently performing a reconfiguration operation against the agent!";
            logger.warn(message);
            throw new JobProvisionServiceLockedException(message);
        }

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

            agentInstanceRecoveryManager.init();
        }
        catch (Exception e) {
            logger.error("An error has occurred attempting to provision jobs!", e);
            throw new JobProvisionServiceException(e);
        }
        finally {
            lock.unlock();
        }
    }


    /**
     * Provision job configurations for a list of SchedulerJob objects with only configuration changes.
     *
     * @param jobs the list of SchedulerJob objects to provision configurations for
     * @param actor the actor performing the job provisioning
     */
    public void provisionJobConfigurationsOnly(List<SchedulerJob> jobs, String actor) {
        try
        {
            long now = System.currentTimeMillis();
            logger.info(String.format("Provisioning %s job configurations for agent %s", jobs.size(), this.moduleName));

            Module<Flow> module = this.moduleService.getModule(moduleName);

            logger.info(String.format("Stopping jobs [%s]", this.moduleName));
            this.stopJobs(jobs);
            logger.info(String.format("Finished stopping %s jobs. Time taken %s milliseconds."
                , jobs.size(), System.currentTimeMillis()-now));

            logger.info(String.format("Configuring components [%s]", this.moduleName));
            this.configureComponents(jobs, module);
            logger.info(String.format("Configured components [%s]", this.moduleName));

            logger.info(String.format("Starting jobs [%s]", this.moduleName));
            this.startJobs(jobs);
            logger.info(String.format("Finished provisioning %s jobs. Time taken %s milliseconds."
                , jobs.size(), System.currentTimeMillis()-now));

            agentInstanceRecoveryManager.init();
        }
        catch (Exception e) {
            logger.error("An error has occurred attempting to provision job configurations olny!", e);
            throw new JobProvisionServiceException(e);
        }
    }

    @Override
    public synchronized void removeJobs(String contextName) {
        if(!lock.tryLock()) {
            String message = String.format("An attempt to remove jobs for job plan[%s] has failed to obtain the lock indicating that " +
                "another thread is currently performing a reconfiguration operation against the agent!", contextName);
            logger.warn(message);
            throw new JobProvisionServiceLockedException(message);
        }

        try {
            logger.info(String.format("Removing jobs for context[%s].", contextName));
            Module<Flow> module = this.moduleService.getModule(moduleName);
            ConfiguredResource<ConfiguredModuleConfiguration> configuredModule = getConfiguredResource(module);
            ConfiguredModuleConfiguration configuredModuleConfiguration = configuredModule.getConfiguration();

            this.deleteComponentConfigurations(module.getFlows(), configuredModuleConfiguration, contextName);

            logger.info(String.format("Deactivating module [%s]", this.moduleName));
            moduleActivator.deactivate(module);
            logger.info(String.format("Deactivated module [%s]", this.moduleName));

            this.clearFlowConfig(configuredModuleConfiguration, contextName);
            this.configurationService.update(configuredModule);

            logger.info(String.format("Activating module [%s]", this.moduleName));
            moduleActivator.activate(module);
            logger.info(String.format("Activated module [%s]", this.moduleName));

            logger.info(String.format("Finished removing jobs for context[%s].", contextName));
        }
        catch (Exception e) {
            logger.error(String.format("An error has occurred attempting to remove jobs for context[%s]!", contextName)
                , e);
            throw new JobProvisionServiceException(e);
        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Update the initial module configuration in order to define the job types. All jobs are initially set
     * to start manually allowing for the relevant components to be configured.
     *
     * @param jobs
     * @param configuredModuleConfiguration
     */
    private void updateInitialModuleConfiguration(List<SchedulerJob> jobs, ConfiguredModuleConfiguration configuredModuleConfiguration) throws JsonProcessingException {
        String contextName = jobs.get(0).getContextName();
        clearFlowConfig(configuredModuleConfiguration, contextName);

        for (SchedulerJob job : jobs) {
            if (configuredModuleConfiguration instanceof SchedulerAgentConfiguredModuleConfiguration) {
                SchedulerAgentConfiguredModuleConfiguration configuration = (SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration;
                configuration.getFlowContextMap().put(job.getAggregateJobName(), job.getContextName());
            }
            if (job instanceof FileEventDrivenJob) {
                configuredModuleConfiguration.getFlowDefinitions().put(job.getAggregateJobName(), "MANUAL");
                configuredModuleConfiguration.getFlowDefinitionProfiles().put(job.getAggregateJobName(), AgentFlowProfiles.FILE);

                if (configuredModuleConfiguration instanceof SchedulerAgentConfiguredModuleConfiguration) {
                    ((SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration)
                            .getFileWatcherJobMap().put(job.getAggregateJobName(), objectMapper.writeValueAsString(job));
                }
            } else if (job instanceof QuartzScheduleDrivenJob) {
                configuredModuleConfiguration.getFlowDefinitions().put(job.getAggregateJobName(), "MANUAL");
                configuredModuleConfiguration.getFlowDefinitionProfiles().put(job.getAggregateJobName(), AgentFlowProfiles.QUARTZ);

                if (configuredModuleConfiguration instanceof SchedulerAgentConfiguredModuleConfiguration) {
                    ((SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration)
                        .getScheduledJobMap().put(job.getAggregateJobName(), objectMapper.writeValueAsString(job));
                }
            } else if (job instanceof InternalEventDrivenJob) {
                configuredModuleConfiguration.getFlowDefinitions().put(job.getAggregateJobName(), "MANUAL");
                configuredModuleConfiguration.getFlowDefinitionProfiles().put(job.getAggregateJobName(), AgentFlowProfiles.SCHEDULER_JOB);
            }
        }
    }

    /**
     * Clear the flow configuration for a specific context in the given ConfiguredModuleConfiguration.
     *
     * @param configuredModuleConfiguration the ConfiguredModuleConfiguration containing the flow configurations
     * @param contextName the name of the context to clear the flow configurations for
     */
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
        jobs.forEach(job -> configuredModuleConfiguration.getFlowDefinitions().put(job.getAggregateJobName(), job.getStartupControlType()));
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
                configureQuartzSchedulerFileEventJobFlowComponents(module, job);
            }
            else if(job instanceof QuartzScheduleDrivenJob) {
                configureQuartzScheduledFlowComponents(module, job);
            }
        });
    }

    /**
     * Delete all relevant component configurations.
     *
     * @param flows
     * @param configuredModuleConfiguration
     */
    private void deleteComponentConfigurations(List<Flow> flows, ConfiguredModuleConfiguration configuredModuleConfiguration,
                                               String contextName) {
        if (configuredModuleConfiguration instanceof SchedulerAgentConfiguredModuleConfiguration) {
            SchedulerAgentConfiguredModuleConfiguration configuration = (SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration;
            flows.forEach(flow -> {
                if(configuration.getFlowDefinitionProfiles().containsKey(flow.getName())) {
                    if (configuration.getFlowDefinitionProfiles().get(flow.getName()).equals(AgentFlowProfiles.FILE)
                        && configuration.getFlowContextMap().get(flow.getName()).equals(contextName)) {
                        this.deleteQuartzSchedulerFileEventJobFlowComponents(flow);

                        // remove the job from the file matcher job map on the module configuration
                        configuration.getFileWatcherJobMap().remove(flow.getName());
                    } else if (configuration.getFlowDefinitionProfiles().get(flow.getName()).equals(AgentFlowProfiles.QUARTZ)
                        && configuration.getFlowContextMap().get(flow.getName()).equals(contextName)) {
                        this.deleteConfigurationsForQuartzScheduledFlowComponents(flow);

                        // remove the job from the scheduled job map on the module configuration
                        configuration.getScheduledJobMap().remove(flow.getName());
                    }
                }
            });
        }
    }

    /**
     * Configure Quartz scheduled flow components in the provided module based on the given SchedulerJob.
     *
     * @param module the Module of the Flow containing the components to be configured
     * @param job the SchedulerJob providing the configuration details for the components
     */
    private void configureQuartzScheduledFlowComponents(Module<Flow> module, SchedulerJob job) {
        Flow flow = module.getFlow(job.getAggregateJobName());
        ConfiguredResource<ScheduledConsumerConfiguration> consumer = (ConfiguredResource<ScheduledConsumerConfiguration>)flow
            .getFlowElement("Scheduled Consumer").getFlowComponent();

        ScheduledConsumerConfiguration configuration = consumer.getConfiguration();
        this.updateScheduleConsumerConfiguration((QuartzScheduleDrivenJob) job, configuration);

        this.configurationService.update(consumer);

        ConfiguredResource<ContextualisedConverterConfiguration> converter = (ConfiguredResource<ContextualisedConverterConfiguration>)flow
            .getFlowElement("JobExecution to ScheduledStatusEvent").getFlowComponent();

        ContextualisedConverterConfiguration converterConfiguration = converter.getConfiguration();
        converterConfiguration.setContextName(job.getContextName());
        converterConfiguration.setChildContextNames(job.getChildContextNames());
        converterConfiguration.setJobName(job.getJobName());

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

    /**
     * Delete configurations related to Quartz scheduled flow components from the provided Flow.
     *
     * @param flow the Flow from which to delete the Quartz scheduled flow components configurations
     */
    private void deleteConfigurationsForQuartzScheduledFlowComponents(Flow flow) {
        ConfiguredResource<ScheduledConsumerConfiguration> consumer = (ConfiguredResource<ScheduledConsumerConfiguration>)flow
            .getFlowElement("Scheduled Consumer").getFlowComponent();

        Configuration configuration = this.configurationManagement.getConfiguration(consumer.getConfiguredResourceId());

        if(configuration != null) {
            this.configurationManagement.deleteConfiguration(configuration);
        }

        ConfiguredResource<ContextualisedConverterConfiguration> converter = (ConfiguredResource<ContextualisedConverterConfiguration>)flow
            .getFlowElement("JobExecution to ScheduledStatusEvent").getFlowComponent();

        configuration = this.configurationManagement.getConfiguration(converter.getConfiguredResourceId());

        if(configuration != null) {
            this.configurationManagement.deleteConfiguration(configuration);
        }

        ConfiguredResource<BlackoutRouterConfiguration> blackoutRouter = (ConfiguredResource<BlackoutRouterConfiguration>)flow
            .getFlowElement("Blackout Router").getFlowComponent();

        configuration = this.configurationManagement.getConfiguration(blackoutRouter.getConfiguredResourceId());

        if(configuration != null) {
            this.configurationManagement.deleteConfiguration(configuration);
        }

        ConfiguredResource<ScheduledProcessEventFilterConfiguration> scheduledProcessEventFilter = (ConfiguredResource<ScheduledProcessEventFilterConfiguration>)flow
            .getFlowElement("Publish Scheduled Status").getFlowComponent();

        configuration = this.configurationManagement.getConfiguration(scheduledProcessEventFilter.getConfiguredResourceId());

        if(configuration != null) {
            this.configurationManagement.deleteConfiguration(configuration);
        }
    }

    /**
     * Configure Quartz scheduler file event job flow components.
     *
     * @param module the Module of the Flow
     * @param job the SchedulerJob to be configured
     */
    private void configureQuartzSchedulerFileEventJobFlowComponents(Module<Flow> module, SchedulerJob job) {
        Flow flow = module.getFlow(job.getAggregateJobName());
        ConfiguredResource<ScheduledConsumerConfiguration> consumer = (ConfiguredResource<ScheduledConsumerConfiguration>)flow
            .getFlowElement("Scheduled Consumer").getFlowComponent();

        ScheduledConsumerConfiguration configuration = consumer.getConfiguration();
        this.updateFileWatcherScheduleConsumerConfiguration((FileEventDrivenJob) job, configuration);

        this.configurationService.update(consumer);


        ConfiguredResource<FileWatcherJobConverterConfiguration> converter = (ConfiguredResource<FileWatcherJobConverterConfiguration>)flow
            .getFlowElement("JobExecution to FileWatcherJobEvent").getFlowComponent();

        FileWatcherJobConverterConfiguration converterConfiguration = converter.getConfiguration();
        this.updateFileWatcherJobConverterConfiguration((FileEventDrivenJob) job, converterConfiguration);

        this.configurationService.update(converter);
    }

    /**
     * Deletes the configuration related to the Quartz scheduler file event job flow components from the provided Flow.
     *
     * @param flow the Flow from which to delete the Quartz scheduler file event job flow components configuration
     */
    private void deleteQuartzSchedulerFileEventJobFlowComponents(Flow flow) {
        ConfiguredResource<ScheduledConsumerConfiguration> consumer = (ConfiguredResource<ScheduledConsumerConfiguration>)flow
            .getFlowElement("Scheduled Consumer").getFlowComponent();

        Configuration consumerConfiguration = this.configurationManagement.getConfiguration(consumer.getConfiguredResourceId());

        if(consumerConfiguration != null) {
            this.configurationManagement.deleteConfiguration(consumerConfiguration);
        }

        ConfiguredResource<FileWatcherJobConverterConfiguration> converter = (ConfiguredResource<FileWatcherJobConverterConfiguration>)flow
            .getFlowElement("JobExecution to FileWatcherJobEvent").getFlowComponent();

        Configuration configuration = this.configurationManagement.getConfiguration(converter.getConfiguredResourceId());

        if(configuration != null) {
            this.configurationManagement.deleteConfiguration(configuration);
        }
    }

    /**
     * Starts the jobs that have a startup control type of "AUTOMATIC".
     *
     * @param jobs the list of SchedulerJob objects to start
     */
    private void startJobs(List<SchedulerJob> jobs) {
        String user = UserUtil.getUser();
        jobs.forEach(job -> {
            if(job.getStartupControlType().equals("AUTOMATIC")) {
                this.moduleService.startFlow(job.getAgentName(), job.getAggregateJobName(), user);
            }
        });
    }

    /**
     * Stop the execution of the specified SchedulerJobs by calling stopFlow method in ModuleService for each job.
     *
     * @param jobs the list of SchedulerJob objects to stop
     */
    private void stopJobs(List<SchedulerJob> jobs) {
        String user = UserUtil.getUser();
        jobs.forEach(job -> {
            this.moduleService.stopFlow(job.getAgentName(), job.getAggregateJobName(), user);
        });
    }


    /**
     * Updates the ScheduledConsumerConfiguration with information from a QuartzScheduleDrivenJob.
     *
     * @param job the QuartzScheduleDrivenJob providing the configuration details
     * @param scheduledConsumerConfiguration the ScheduledConsumerConfiguration to be updated
     */
    private void updateScheduleConsumerConfiguration(QuartzScheduleDrivenJob job, ScheduledConsumerConfiguration scheduledConsumerConfiguration) {
        scheduledConsumerConfiguration.setJobName(job.getAggregateJobName());
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
     * Updates the configuration of a ScheduledConsumerConfiguration with information from a FileEventDrivenJob.
     *
     * @param job the FileEventDrivenJob providing the configuration details
     * @param scheduledConsumerConfiguration the ScheduledConsumerConfiguration to be updated
     */
    private void updateFileWatcherScheduleConsumerConfiguration(FileEventDrivenJob job, ScheduledConsumerConfiguration scheduledConsumerConfiguration) {
        scheduledConsumerConfiguration.setJobName(job.getAggregateJobName());
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
     * Updates the configuration of a FileWatcherJobConverterConfiguration with information from a FileEventDrivenJob.
     *
     * @param job the FileEventDrivenJob providing the configuration details
     * @param fileConsumerConfiguration the FileWatcherJobConverterConfiguration to be updated
     */
    private void updateFileWatcherJobConverterConfiguration(FileEventDrivenJob job, FileWatcherJobConverterConfiguration fileConsumerConfiguration) {
        fileConsumerConfiguration.setContextName(job.getContextName());
        fileConsumerConfiguration.setJobName(job.getJobName());
        fileConsumerConfiguration.setMoveDirectory(job.getMoveDirectory());
        if(job.getFilenames() != null && job.getFilenames().size() > 0) {
            fileConsumerConfiguration.setFilename(job.getFilenames().get(0));
        }
        fileConsumerConfiguration.setFilePath(job.getFilePath());
        fileConsumerConfiguration.setMinFileAgeSeconds(job.getMinFileAgeSeconds());
        fileConsumerConfiguration.setFilePathSpelExpression(job.getFilePathSpel());
        fileConsumerConfiguration.setFileNameSpelExpression(job.getFilenameSpel());
        fileConsumerConfiguration.setChildContextNames(job.getChildContextNames());
        fileConsumerConfiguration.setBlackoutWindowCronExpressions(job.getBlackoutWindowCronExpressions());
        fileConsumerConfiguration.setBlackoutWindowDateTimeRanges(job.getBlackoutWindowDateTimeRanges());
        fileConsumerConfiguration.setSlaCronExpression(job.getSlaCronExpression());
        fileConsumerConfiguration.setTimeZone(job.getTimeZone());
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
}
