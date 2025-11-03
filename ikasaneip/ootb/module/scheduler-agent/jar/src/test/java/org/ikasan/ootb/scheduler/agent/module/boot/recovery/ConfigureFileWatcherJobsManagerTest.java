package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.exception.FileWatcherJobMigrationException;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.service.JobProvisionServiceImpl;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigureFileWatcherJobsManagerTest {
    String moduleName = "moduleName";
    @Mock
    ModuleService moduleService;
    @Mock
    ConfigurationService configurationService;
    @Mock
    ContextInstanceRestService contextInstanceRestService;
    @Mock
    JobProvisionServiceImpl jobProvisionService;
    @Mock
    ConfiguredModuleImpl module;
    @Mock
    SchedulerAgentConfiguredModuleConfiguration configuration;
    @Mock
    FileEventDrivenJob fileEventDrivenJob;

    @Test(expected = IllegalArgumentException.class)
    public void test_module_name_null_constructor_exception() {
        new ConfigureFileWatcherJobsManager(null, this.moduleService, this.configurationService,
            this.contextInstanceRestService, this.jobProvisionService, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_module_service_null_constructor_exception() {
        new ConfigureFileWatcherJobsManager(this.moduleName, null, this.configurationService,
            this.contextInstanceRestService, this.jobProvisionService, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_configuration_service_null_constructor_exception() {
        new ConfigureFileWatcherJobsManager(this.moduleName, this.moduleService, null,
            this.contextInstanceRestService, this.jobProvisionService, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_context_instance_rest_service_null_constructor_exception() {
        new ConfigureFileWatcherJobsManager(this.moduleName, this.moduleService, this.configurationService,
            null, this.jobProvisionService, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_job_provision_service_null_constructor_exception() {
        new ConfigureFileWatcherJobsManager(this.moduleName, this.moduleService, this.configurationService,
            this.contextInstanceRestService, null, true);
    }

    @Test
    public void test_file_watcher_update_not_required_success() {
        ConfigureFileWatcherJobsManager configureFileWatcherJobsManager = new ConfigureFileWatcherJobsManager
            (this.moduleName, this.moduleService, this.configurationService, this.contextInstanceRestService
                , this.jobProvisionService, false);

        configureFileWatcherJobsManager.migrateFileWatcherJobs();

        Mockito.verifyNoMoreInteractions(this.moduleService,
            this.configurationService,
            this.contextInstanceRestService,
            this.jobProvisionService);
    }

    @Test
    public void test_file_watcher_update_not_required_already_run_success() {

        ConfigureFileWatcherJobsManager configureFileWatcherJobsManager = new ConfigureFileWatcherJobsManager
            (this.moduleName, this.moduleService, this.configurationService, this.contextInstanceRestService
                , this.jobProvisionService, true);

        when(this.moduleService.getModule(this.moduleName)).thenReturn(this.module);
        doNothing().when(this.configurationService).configure(module);
        when(module.getConfiguration()).thenReturn(this.configuration);

        when(this.configuration.getManagementFlagsMap()).thenReturn(
            Map.of(SchedulerAgentConfiguredModuleConfiguration.UPDATE_FILE_WATCHER_JOB_CONFIG_COMPLETE_FLAG, "true"));

        // run the migration!
        configureFileWatcherJobsManager.migrateFileWatcherJobs();

        verify(this.moduleService).getModule(this.moduleName);
        verify(this.configurationService).configure(this.module);
        verify(this.module).getConfiguration();
        verify(this.configuration, times(2)).getManagementFlagsMap();

        verifyNoMoreInteractions(this.moduleService,
            this.configurationService,
            this.contextInstanceRestService,
            this.jobProvisionService,
            this.configuration);
    }

    @Test
    public void test_file_watcher_update_run_success() {

        ConfigureFileWatcherJobsManager configureFileWatcherJobsManager = new ConfigureFileWatcherJobsManager
            (this.moduleName, this.moduleService, this.configurationService, this.contextInstanceRestService
                , this.jobProvisionService, true);

        when(this.moduleService.getModule(this.moduleName)).thenReturn(this.module);
        doNothing().when(this.configurationService).configure(module);
        when(module.getConfiguration()).thenReturn(this.configuration);

        when(this.configuration.getManagementFlagsMap()).thenReturn(
            Map.of(SchedulerAgentConfiguredModuleConfiguration.UPDATE_FILE_WATCHER_JOB_CONFIG_COMPLETE_FLAG, "false"));


        when(this.configuration.getManagementFlagsMap()).thenReturn(
            new HashMap<>());
        HashMap<String, String> fileWatcherProfiles = new HashMap<>();
        fileWatcherProfiles.put("jobName1", "FILE");
        fileWatcherProfiles.put("jobName2", "FILE");
        fileWatcherProfiles.put("jobName3", "OTHER");

        when(this.configuration.getFlowDefinitionProfiles()).thenReturn(
            fileWatcherProfiles);

        HashMap<String, String> flowContextMap = new HashMap<>();
        flowContextMap.put("jobName1", "JobPlan1");
        flowContextMap.put("jobName2", "JobPlan1");
        flowContextMap.put("jobName3", "JobPlan1");

        when(this.configuration.getFlowContextMap()).thenReturn(
            flowContextMap);

        when(this.contextInstanceRestService.getFileEventJob(anyString(), anyString()))
            .thenReturn(this.fileEventDrivenJob);

        // run the migration!
        configureFileWatcherJobsManager.migrateFileWatcherJobs();

        verify(this.moduleService).getModule(this.moduleName);
        verify(this.configurationService).configure(this.module);
        verify(this.module).getConfiguration();
        verify(this.configuration, times(2)).getManagementFlagsMap();
        verify(this.configuration, times(1)).getFlowDefinitionProfiles();
        verify(this.configuration, times(4)).getFlowContextMap();
        verify(this.contextInstanceRestService, times(2))
            .getFileEventJob(anyString(), anyString());
        verify(this.jobProvisionService, times(1))
            .provisionJobConfigurationsOnly(anyList(), anyString());

        verifyNoMoreInteractions(this.moduleService,
            this.configurationService,
            this.contextInstanceRestService,
            this.jobProvisionService,
            this.configuration);
    }

    @Test
    public void test_file_watcher_update_run_success_already_run_flag_false() {

        ConfigureFileWatcherJobsManager configureFileWatcherJobsManager = new ConfigureFileWatcherJobsManager
            (this.moduleName, this.moduleService, this.configurationService, this.contextInstanceRestService
                , this.jobProvisionService, true);

        when(this.moduleService.getModule(this.moduleName)).thenReturn(this.module);
        doNothing().when(this.configurationService).configure(module);
        when(module.getConfiguration()).thenReturn(this.configuration);

        when(this.configuration.getManagementFlagsMap()).thenReturn(
            new HashMap<>());
        HashMap<String, String> fileWatcherProfiles = new HashMap<>();
        fileWatcherProfiles.put("jobName1", "FILE");
        fileWatcherProfiles.put("jobName2", "FILE");
        fileWatcherProfiles.put("jobName3", "OTHER");

        when(this.configuration.getFlowDefinitionProfiles()).thenReturn(
            fileWatcherProfiles);

        HashMap<String, String> flowContextMap = new HashMap<>();
        flowContextMap.put("jobName1", "JobPlan1");
        flowContextMap.put("jobName2", "JobPlan1");
        flowContextMap.put("jobName3", "JobPlan1");

        when(this.configuration.getFlowContextMap()).thenReturn(
            flowContextMap);

        when(this.contextInstanceRestService.getFileEventJob(anyString(), anyString()))
            .thenReturn(this.fileEventDrivenJob);

        // run the migration!
        configureFileWatcherJobsManager.migrateFileWatcherJobs();

        verify(this.moduleService).getModule(this.moduleName);
        verify(this.configurationService).configure(this.module);
        verify(this.module).getConfiguration();
        verify(this.configuration, times(2)).getManagementFlagsMap();
        verify(this.configuration, times(1)).getFlowDefinitionProfiles();
        verify(this.configuration, times(4)).getFlowContextMap();
        verify(this.contextInstanceRestService, times(2))
            .getFileEventJob(anyString(), anyString());
        verify(this.jobProvisionService, times(1))
            .provisionJobConfigurationsOnly(anyList(), anyString());

        verifyNoMoreInteractions(this.moduleService,
            this.configurationService,
            this.contextInstanceRestService,
            this.jobProvisionService,
            this.configuration);
    }

    @Test(expected = FileWatcherJobMigrationException.class)
    public void test_file_watcher_update_rest_service_exception() {

        ConfigureFileWatcherJobsManager configureFileWatcherJobsManager = new ConfigureFileWatcherJobsManager
            (this.moduleName, this.moduleService, this.configurationService, this.contextInstanceRestService
                , this.jobProvisionService, true);

        when(this.moduleService.getModule(this.moduleName)).thenReturn(this.module);
        doNothing().when(this.configurationService).configure(module);
        when(module.getConfiguration()).thenReturn(this.configuration);

        when(this.configuration.getManagementFlagsMap()).thenReturn(
            Map.of(SchedulerAgentConfiguredModuleConfiguration.UPDATE_FILE_WATCHER_JOB_CONFIG_COMPLETE_FLAG, "false"));


        when(this.configuration.getManagementFlagsMap()).thenReturn(
            new HashMap<>());
        HashMap<String, String> fileWatcherProfiles = new HashMap<>();
        fileWatcherProfiles.put("jobName1", "FILE");
        fileWatcherProfiles.put("jobName2", "FILE");
        fileWatcherProfiles.put("jobName3", "OTHER");

        when(this.configuration.getFlowDefinitionProfiles()).thenReturn(
            fileWatcherProfiles);

        HashMap<String, String> flowContextMap = new HashMap<>();
        flowContextMap.put("jobName1", "JobPlan1");
        flowContextMap.put("jobName2", "JobPlan1");
        flowContextMap.put("jobName3", "JobPlan1");

        when(this.configuration.getFlowContextMap()).thenReturn(
            flowContextMap);

        when(this.contextInstanceRestService.getFileEventJob(anyString(), anyString()))
            .thenThrow(RestClientException.class);

        // run the migration!
        configureFileWatcherJobsManager.migrateFileWatcherJobs();
    }
}
