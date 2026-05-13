package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.AgentFlowProfiles;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.exception.FileWatcherJobMigrationException;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.service.JobProvisionServiceImpl;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class ConfigureFileWatcherJobsManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigureFileWatcherJobsManager.class);

    private String moduleName;
    private ModuleService moduleService;
    private ConfigurationService configurationService;
    private ContextInstanceRestService contextInstanceRestService;
    private JobProvisionServiceImpl jobProvisionService;
    private boolean requiresUpdate;

    /**
     * Constructor for configuring FileWatcher Jobs Manager.
     *
     * @param moduleName Name of the module to configure the jobs for.
     * @param moduleService Service for providing user access to modules.
     * @param configurationService Service for configuring resources.
     * @param contextInstanceRestService Service for interacting with context instances.
     * @param jobProvisionService Service for job provisioning.
     * @param requiresUpdate Boolean flag indicating if an update is required.
     */
    public ConfigureFileWatcherJobsManager(String moduleName, ModuleService moduleService,
                                           ConfigurationService configurationService,
                                           ContextInstanceRestService contextInstanceRestService,
                                           JobProvisionServiceImpl jobProvisionService,
                                           boolean requiresUpdate) {
        this.moduleName = moduleName;
        if(this.moduleName == null) {
            throw new IllegalArgumentException("moduleName cannot be null!");
        }
        this.moduleService = moduleService;
        if(this.moduleService == null) {
            throw new IllegalArgumentException("moduleService cannot be null!");
        }
        this.configurationService = configurationService;
        if(this.configurationService == null) {
            throw new IllegalArgumentException("configurationService cannot be null!");
        }
        this.contextInstanceRestService = contextInstanceRestService;
        if(this.contextInstanceRestService == null) {
            throw new IllegalArgumentException("contextInstanceRestService cannot be null!");
        }
        this.jobProvisionService = jobProvisionService;
        if(this.jobProvisionService == null) {
            throw new IllegalArgumentException("jobProvisionService cannot be null!");
        }
        this.requiresUpdate = requiresUpdate;
    }

    /**
     * Method to run File Watcher job update.
     *
     * Checks if an update is required. If required, gets the module by name using the module service,
     * configures the module, and checks if file watcher job update is necessary. If necessary,
     * updates the file watcher jobs with relevant configurations and flags.
     *
     * If an update is not required, logs a message indicating so.
     *
     * @PostConstruct - Automatically called after the bean has been created and all dependencies have been injected
     */
    @PostConstruct
    public void migrateFileWatcherJobs() {
        if(requiresUpdate) {
            Module module = moduleService.getModule(moduleName);
            ConfiguredResource<SchedulerAgentConfiguredModuleConfiguration> configuredModule = getConfiguredResource(module);
            configurationService.configure(configuredModule);
            SchedulerAgentConfiguredModuleConfiguration configuration = configuredModule.getConfiguration();
            if(!configuration.getManagementFlagsMap().containsKey(SchedulerAgentConfiguredModuleConfiguration
                .UPDATE_FILE_WATCHER_JOB_CONFIG_COMPLETE_FLAG) ||
                configuration.getManagementFlagsMap().get(SchedulerAgentConfiguredModuleConfiguration
                    .UPDATE_FILE_WATCHER_JOB_CONFIG_COMPLETE_FLAG).equals("false")) {
                LOG.info("Commencing File watcher job update!");
                List<SchedulerJob> schedulerJobList = new ArrayList<>();
                configuration.getFlowDefinitionProfiles().entrySet().forEach(profile -> {
                    if (profile.getValue().equals(AgentFlowProfiles.FILE)) {
                        String jobName = profile.getKey().contains("_") ?
                            profile.getKey().substring(0, profile.getKey().lastIndexOf("_")):
                            profile.getKey();

                        LOG.info("Requesting job details for file watcher job [{}] for job plan[{}]", jobName
                            , configuration.getFlowContextMap().get(profile.getKey()));

                        try {
                            FileEventDrivenJob fileEventDrivenJob = this.contextInstanceRestService.getFileEventJob(jobName
                                , configuration.getFlowContextMap().get(profile.getKey()));

                            LOG.info("Adding [{}] to updated file watcher jobs in order for relevant component configurations to be applied."
                                , fileEventDrivenJob.toString());
                            schedulerJobList.add(fileEventDrivenJob);
                        }
                        catch (Exception e) {
                            LOG.error("", e);
                            throw new FileWatcherJobMigrationException(String.format("An exception has occurred attempting to migrate file" +
                                " watcher jobs for job plan[%s]! The Ikasan Enterprise Scheduler Dashboard is required to be active for this" +
                                " process to run successfully!", configuration.getFlowContextMap().get(profile.getKey())), e);
                        }
                    }
                });
                if(!schedulerJobList.isEmpty()) {
                    this.jobProvisionService.provisionJobConfigurationsOnly(schedulerJobList, "system");
                }
                configuration.getManagementFlagsMap().put(SchedulerAgentConfiguredModuleConfiguration
                    .UPDATE_FILE_WATCHER_JOB_CONFIG_COMPLETE_FLAG, "true");
                LOG.info("File watcher job update complete!");
            }
            else {
                LOG.info("File watcher job update not necessary as it has already run!");
            }
        }
        else {
            LOG.info("File watcher job update not necessary as configuration 'file.watcher.job.migration.required' is set to false!");
        }
    }

    /**
     * Retrieves the configured resource associated with the provided module.
     *
     * @param module The module for which the configured resource is retrieved.
     * @return ConfiguredResource instance representing the configuration for the module.
     */
    private ConfiguredResource<ConfiguredModuleConfiguration> getConfiguredResource(Module<Flow> module) {
        return (ConfiguredResource<ConfiguredModuleConfiguration>)module;
    }
}
