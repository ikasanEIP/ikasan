package org.ikasan.ootb.scheduler.agent.module.service;

import org.awaitility.Awaitility;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatedScheduledConsumerConfiguration;
import org.ikasan.job.orchestration.model.job.FileEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.InternalEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.QuartzScheduleDrivenJobImpl;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.FileWatcherJobConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.provision.JobProvisionServiceException;
import org.ikasan.spec.scheduled.provision.JobProvisionServiceLockedException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.ikasan.ootb.scheduler.agent.module.AgentFlowProfiles.FILE;
import static org.ikasan.ootb.scheduler.agent.module.AgentFlowProfiles.QUARTZ;
import static org.mockito.AdditionalAnswers.answersWithDelay;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobProvisionServiceImplTest {
    @Mock
    private ModuleService moduleService;

    @Mock
    private ModuleActivator moduleActivator;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ConfigurationManagement configurationManagement;

    @Mock
    private SchedulerAgentConfiguredModuleConfiguration configureModuleConfiguration;

    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    private Flow flow;

    @Mock
    private FlowElement fileWatcherJobConverterElement;

    @Mock
    private ConfiguredResource<FileWatcherJobConverterConfiguration> fileWatcherJob;

    @Mock
    private FileWatcherJobConverterConfiguration fileWatcherJobConverterConfiguration;

    @Mock
    private FlowElement converterElement;

    @Mock
    private ConfiguredResource<ContextualisedConverterConfiguration> converter;

    @Mock
    private ContextualisedConverterConfiguration converterConfiguration;

    @Mock
    private FlowElement scheduledConsumerElement;

    @Mock
    private ConfiguredResource<CorrelatedScheduledConsumerConfiguration> scheduledConsumer;

    @Mock CorrelatedScheduledConsumerConfiguration scheduledConsumerConfiguration;

    @Mock
    private FlowElement fileAgeFilterElement;

    @Mock
    private ConfiguredResource<FileAgeFilterConfiguration> fileAgeFilter;

    @Mock FileAgeFilterConfiguration fileAgeFilterConfiguration;

    @Mock
    private FlowElement duplicateMessageFilterElement;

    @Mock
    private ConfiguredResource<SchedulerFileFilterConfiguration> duplicateMessageFilter;

    @Mock SchedulerFileFilterConfiguration schedulerFileFilterConfiguration;

    @Mock
    IkasanAuthentication ikasanAuthentication;

    @Mock
    private FlowElement contextFilterElement;

    @Mock
    private ConfiguredResource<ContextInstanceFilterConfiguration> contextFilter;

    @Mock
    private FlowElement blackoutRouterElement;

    @Mock
    private ConfiguredResource<BlackoutRouterConfiguration> blackoutRouter;

    @Mock
    BlackoutRouterConfiguration blackoutRouterConfiguration;

    @Mock
    private FlowElement scheduledStatusFilterElement;

    @Mock
    private ConfiguredResource<ScheduledProcessEventFilterConfiguration> scheduledProcessEventFilter;

    @Mock
    ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration;

    @Mock
    AgentInstanceRecoveryManager agentInstanceRecoveryManager;

    @Mock
    Configuration configuration;

    @Mock
    Flow flow1;

    @Mock
    Flow flow2;

    @Mock
    Flow flow3;

    @Mock
    FlowElement flowElement;

    @Mock
    ConfiguredResource configuredResource;

    @InjectMocks
    private JobProvisionServiceImpl service;

    @Test(expected = JobProvisionServiceException.class)
    public void test_provision_exception() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        this.service.provisionJobs(this.getJobs(), "system");
    }

    @Test
    public void test_provision_success() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhen();

        this.service.provisionJobs(this.getJobs(), "system");

        verify(scheduledConsumerConfiguration, times(2)).setJobName(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setJobGroupName(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setDescription(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setCronExpression(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setTimezone(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setEager(anyBoolean());
        verify(scheduledConsumerConfiguration, times(2)).setIgnoreMisfire(anyBoolean());
        verify(scheduledConsumerConfiguration, times(2)).setMaxEagerCallbacks(anyInt());
        verify(scheduledConsumerConfiguration, times(2)).setPassthroughProperties(anyMap());
        verify(scheduledConsumerConfiguration, times(2)).setPersistentRecovery(anyBoolean());
        verify(scheduledConsumerConfiguration, times(2)).setRecoveryTolerance(anyLong());

        verify(fileWatcherJobConverterConfiguration, times(1)).setJobName(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFilename(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFilePath(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFileNameSpelExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFilePathSpelExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setContextName(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setMoveDirectory(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setMoveDirectorySpelExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setMinFileAgeSeconds(anyInt());
        verify(fileWatcherJobConverterConfiguration, times(1)).setChildContextNames(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setBlackoutWindowCronExpressions(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setBlackoutWindowDateTimeRanges(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setSlaCronExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setTimeZone(anyString());

        verify(converterConfiguration, times(1)).setContextName(anyString());
        verify(converterConfiguration, times(1)).setJobName(anyString());
        verify(converterConfiguration, times(1)).setChildContextNames(anyList());

        verify(agentInstanceRecoveryManager, times(1)).init();

        verifyNoMoreInteractions(fileWatcherJobConverterConfiguration);
        verifyNoMoreInteractions(scheduledConsumerConfiguration);
        verifyNoMoreInteractions(converterConfiguration);
        verifyNoMoreInteractions(agentInstanceRecoveryManager);
    }

    @Test(expected = JobProvisionServiceLockedException.class)
    public void test_provision_twice_with_lock_acquisition_failure() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhenDelay();

        AtomicReference<JobProvisionServiceLockedException> exception =
            new AtomicReference<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() ->  {
            try {
                this.service.provisionJobs(this.getJobs(), "system");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });
        executor.submit(() -> {
            try {
                this.service.provisionJobs(this.getJobs(), "system");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });

        Awaitility.await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> Assert.assertNotNull(exception.get()));

        Assert.assertEquals("An attempt to provision jobs has failed to obtain the lock indicating that " +
            "another thread is currently performing a reconfiguration operation against the agent!", exception.get().getMessage());

        throw exception.get();
    }

    @Test(expected = JobProvisionServiceLockedException.class)
    public void test_remove_jobs_twice_with_lock_acquisition_failure() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhenDelay();

        AtomicReference<JobProvisionServiceLockedException> exception =
            new AtomicReference<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() ->  {
            try {
                this.service.removeJobs("contextName");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });
        executor.submit(() -> {
            try {
                this.service.removeJobs("contextName");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });

        Awaitility.await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> Assert.assertNotNull(exception.get()));

        Assert.assertEquals("An attempt to remove jobs for job plan[contextName] has failed to obtain the lock " +
            "indicating that another thread is currently performing a reconfiguration operation against the agent!"
            , exception.get().getMessage());

        throw exception.get();
    }

    @Test(expected = JobProvisionServiceLockedException.class)
    public void test_provision_jobs_followed_by_remove_jobs_with_lock_acquisition_failure() throws InterruptedException {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhenDelay();

        AtomicReference<JobProvisionServiceLockedException> exception =
            new AtomicReference<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() ->  {
            try {
                this.service.provisionJobs(this.getJobs(), "system");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });
        // Add sleep to make sure provision job thread gets lock
        Thread.sleep(200);
        executor.submit(() -> {
            try {
                this.service.removeJobs("contextName");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });

        Awaitility.await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> Assert.assertNotNull(exception.get()));

        Assert.assertEquals("An attempt to remove jobs for job plan[contextName] has failed to obtain the lock " +
                "indicating that another thread is currently performing a reconfiguration operation against the agent!"
            , exception.get().getMessage());

        throw exception.get();
    }

    @Test(expected = JobProvisionServiceLockedException.class)
    public void test_remove_jobs_followed_by_provision_jobs_with_lock_acquisition_failure() throws InterruptedException {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhenDelay();

        AtomicReference<JobProvisionServiceLockedException> exception =
            new AtomicReference<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() ->  {
            try {
                this.service.removeJobs("contextName");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });
        // Add sleep to make sure provision job thread gets lock
        Thread.sleep(200);
        executor.submit(() -> {
            try {
                this.service.provisionJobs(this.getJobs(), "system");
            }
            catch (JobProvisionServiceLockedException e) {
                exception.set(e);
            }
        });

        Awaitility.await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> Assert.assertNotNull(exception.get()));

        Assert.assertEquals("An attempt to provision jobs has failed to obtain the lock indicating that another " +
                "thread is currently performing a reconfiguration operation against the agent!"
            , exception.get().getMessage());

        throw exception.get();
    }

    @Test
    public void test_provision_job_configurations_only_success() {
        ReflectionTestUtils.setField(this.service, "moduleName", "moduleName");
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhen();

        when(this.moduleService.getModule(anyString())).thenReturn(this.module);

        this.service.provisionJobConfigurationsOnly(this.getJobs(), "system");

        verify(this.moduleService).getModule(anyString());
        verify(this.moduleService,times(3)).stopFlow(anyString(), anyString(),anyString());
        verify(this.moduleService,times(3)).startFlow(anyString(), anyString(),anyString());

        verify(scheduledConsumerConfiguration, times(2)).setJobName(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setJobGroupName(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setDescription(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setCronExpression(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setTimezone(anyString());
        verify(scheduledConsumerConfiguration, times(2)).setEager(anyBoolean());
        verify(scheduledConsumerConfiguration, times(2)).setIgnoreMisfire(anyBoolean());
        verify(scheduledConsumerConfiguration, times(2)).setMaxEagerCallbacks(anyInt());
        verify(scheduledConsumerConfiguration, times(2)).setPassthroughProperties(anyMap());
        verify(scheduledConsumerConfiguration, times(2)).setPersistentRecovery(anyBoolean());
        verify(scheduledConsumerConfiguration, times(2)).setRecoveryTolerance(anyLong());

        verify(fileWatcherJobConverterConfiguration, times(1)).setJobName(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFilename(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFilePath(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFileNameSpelExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setFilePathSpelExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setContextName(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setMoveDirectory(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setMoveDirectorySpelExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setMinFileAgeSeconds(anyInt());
        verify(fileWatcherJobConverterConfiguration, times(1)).setChildContextNames(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setBlackoutWindowCronExpressions(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setBlackoutWindowDateTimeRanges(any());
        verify(fileWatcherJobConverterConfiguration, times(1)).setSlaCronExpression(anyString());
        verify(fileWatcherJobConverterConfiguration, times(1)).setTimeZone(anyString());

        verify(converterConfiguration, times(1)).setContextName(anyString());
        verify(converterConfiguration, times(1)).setJobName(anyString());
        verify(converterConfiguration, times(1)).setChildContextNames(anyList());

        verify(agentInstanceRecoveryManager, times(1)).init();

        verifyNoMoreInteractions(fileWatcherJobConverterConfiguration);
        verifyNoMoreInteractions(scheduledConsumerConfiguration);
        verifyNoMoreInteractions(converterConfiguration);
        verifyNoMoreInteractions(agentInstanceRecoveryManager);
        verifyNoMoreInteractions(moduleService);
    }

    @Test
    public void test_remove_jobs_for_context_when_job_not_in_context() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SchedulerAgentConfiguredModuleConfiguration configuration = new SchedulerAgentConfiguredModuleConfiguration();
        configuration.setFlowContextMap(this.getJobContextMap("anotherContext"));
        configuration.setFlowDefinitions(this.getJobContextMap());
        configuration.setFlowDefinitionProfiles(this.getJobProfileMap());

        Assert.assertEquals(3, configuration.getFlowContextMap().size());
        Assert.assertEquals(3, configuration.getFlowDefinitions().size());
        Assert.assertEquals(2, configuration.getFlowDefinitionProfiles().size());

        when(moduleService.getModule(null)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configuration);
        when(module.getFlows()).thenReturn(List.of(flow1, flow2, flow3).stream().collect(Collectors.toList()));
        when(flow1.getName()).thenReturn("fileEventDrivenJobName");
        when(flow2.getName()).thenReturn("quartzScheduleDrivenJobName");

        this.service.removeJobs("contextName");

        verify(moduleActivator).deactivate(module);
        verify(moduleActivator).activate(module);
        verify(flow1, times(4)).getName();
        verify(flow2, times(4)).getName();
        verify(flow3, times(1)).getName();

        verifyNoMoreInteractions(fileWatcherJobConverterConfiguration
            , moduleActivator
            , scheduledConsumerConfiguration
            , converterConfiguration
            , flow1
            , flow2
            , flow3
            , flowElement
            , configuredResource
            , configurationManagement
            , agentInstanceRecoveryManager);

        Assert.assertEquals(3, configuration.getFlowContextMap().size());
        Assert.assertEquals(3, configuration.getFlowDefinitions().size());
        Assert.assertEquals(2, configuration.getFlowDefinitionProfiles().size());
    }

    @Test
    public void test_remove_jobs_for_context_success() {
        this.setupWhen();

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SchedulerAgentConfiguredModuleConfiguration configuration = new SchedulerAgentConfiguredModuleConfiguration();
        configuration.setFlowContextMap(this.getJobContextMap("contextName"));
        configuration.setFlowDefinitions(this.getJobContextMap());
        configuration.setFlowDefinitionProfiles(this.getJobProfileMap());

        Assert.assertEquals(3, configuration.getFlowContextMap().size());
        Assert.assertEquals(3, configuration.getFlowDefinitions().size());
        Assert.assertEquals(2, configuration.getFlowDefinitionProfiles().size());

        when(moduleService.getModule(null)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configuration);
        when(module.getFlows()).thenReturn(List.of(flow, flow2, flow3).stream().collect(Collectors.toList()));
        when(flow.getName()).thenReturn("fileEventDrivenJobName");
        when(flow2.getName()).thenReturn("quartzScheduleDrivenJobName");
        when(configurationManagement.getConfiguration(anyString())).thenReturn(this.configuration);

        this.service.removeJobs("contextName");

        verify(moduleActivator).deactivate(module);
        verify(moduleActivator).activate(module);
        verify(flow, times(4)).getName();
        verify(flow, times(2)).getFlowElement(anyString());
        verify(flow2, times(5)).getName();
        verify(flow2, times(4)).getFlowElement(anyString());
        verify(flow3, times(1)).getName();

        verify(configurationManagement, times(6)).getConfiguration(anyString());
        verify(configurationManagement, times(6)).deleteConfiguration(any());

        verifyNoMoreInteractions(fileWatcherJobConverterConfiguration
            , moduleActivator
            , scheduledConsumerConfiguration
            , converterConfiguration
            , flow
            , flow2
            , flow3
            , flowElement
            , configuredResource
            , configurationManagement
            , agentInstanceRecoveryManager);

        Assert.assertEquals(0, configuration.getFlowContextMap().size());
        Assert.assertEquals(0, configuration.getFlowDefinitions().size());
        Assert.assertEquals(0, configuration.getFlowDefinitionProfiles().size());
    }

    private void setupWhen() {
        when(moduleService.getModule(null)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModuleConfiguration);
        when(module.getFlow(anyString())).thenReturn(flow);
        when(flow.getFlowElement("JobExecution to FileWatcherJobEvent")).thenReturn(fileWatcherJobConverterElement);
        when(fileWatcherJobConverterElement.getFlowComponent()).thenReturn(fileWatcherJob);
        when(fileWatcherJob.getConfiguration()).thenReturn(fileWatcherJobConverterConfiguration);
        when(fileWatcherJob.getConfiguredResourceId()).thenReturn("id");
        when(fileWatcherJobConverterElement.getFlowComponent()).thenReturn(fileWatcherJob);
        when(fileWatcherJob.getConfiguration()).thenReturn(fileWatcherJobConverterConfiguration);
        when(fileWatcherJobConverterElement.getFlowComponent()).thenReturn(fileWatcherJob);
        when(fileWatcherJob.getConfiguration()).thenReturn(fileWatcherJobConverterConfiguration);
        when(flow.getFlowElement("JobExecution to ScheduledStatusEvent")).thenReturn(converterElement);
        when(flow2.getFlowElement("JobExecution to ScheduledStatusEvent")).thenReturn(converterElement);
        when(converterElement.getFlowComponent()).thenReturn(converter);
        when(converter.getConfiguration()).thenReturn(converterConfiguration);
        when(converter.getConfiguredResourceId()).thenReturn("id");
        when(flow.getFlowElement("Scheduled Consumer")).thenReturn(scheduledConsumerElement);
        when(flow2.getFlowElement("Scheduled Consumer")).thenReturn(scheduledConsumerElement);
        when(scheduledConsumerElement.getFlowComponent()).thenReturn(scheduledConsumer);
        when(scheduledConsumer.getConfiguration()).thenReturn(scheduledConsumerConfiguration);
        when(scheduledConsumer.getConfiguredResourceId()).thenReturn("id");
        when(flow.getFlowElement("Blackout Router")).thenReturn(blackoutRouterElement);
        when(flow2.getFlowElement("Blackout Router")).thenReturn(blackoutRouterElement);
        when(blackoutRouterElement.getFlowComponent()).thenReturn(blackoutRouter);
        when(blackoutRouter.getConfiguration()).thenReturn(blackoutRouterConfiguration);
        when(blackoutRouter.getConfiguredResourceId()).thenReturn("id");
        when(flow.getFlowElement("Publish Scheduled Status")).thenReturn(scheduledStatusFilterElement);
        when(flow2.getFlowElement("Publish Scheduled Status")).thenReturn(scheduledStatusFilterElement);
        when(scheduledStatusFilterElement.getFlowComponent()).thenReturn(scheduledProcessEventFilter);
        when(scheduledProcessEventFilter.getConfiguredResourceId()).thenReturn("id");
        when(scheduledProcessEventFilter.getConfiguration()).thenReturn(scheduledProcessEventFilterConfiguration);
        when(ikasanAuthentication.getPrincipal()).thenReturn("ikasan-user");
    }

    private void setupWhenDelay() {
        when(this.moduleService.getModule(null))
            .thenAnswer(answersWithDelay(1000, invocation -> module));
    }

    private Map<String, String> getJobContextMap() {
        Map<String, String> contextJobs = new HashMap<>();

        this.getJobs().forEach(job -> contextJobs.put(job.getJobName(), job.getContextName()));

        return contextJobs;
    }

    private Map<String, String> getJobContextMap(String contextName) {
        Map<String, String> contextJobs = new HashMap<>();

        this.getJobs().forEach(job -> contextJobs.put(job.getJobName(), contextName));

        return contextJobs;
    }

    private Map<String, String> getJobProfileMap() {
        Map<String, String> jobProfiles = new HashMap<>();

        jobProfiles.put("fileEventDrivenJobName", FILE);
        jobProfiles.put("quartzScheduleDrivenJobName", QUARTZ);

        return jobProfiles;
    }

    private List<SchedulerJob> getJobs() {
        List<String> childIds = new ArrayList<>();
        childIds.add("childId");

        InternalEventDrivenJobImpl internalEventDrivenJob = new InternalEventDrivenJobImpl();
        internalEventDrivenJob.setAgentName("agentName");
        internalEventDrivenJob.setJobName("internalEventDrivenJobName");
        internalEventDrivenJob.setContextName("contextName");
        internalEventDrivenJob.setChildContextNames(childIds);

        FileEventDrivenJobImpl fileEventDrivenJob = new FileEventDrivenJobImpl();
        fileEventDrivenJob.setAgentName("agentName");
        fileEventDrivenJob.setJobName("fileEventDrivenJobName");
        fileEventDrivenJob.setContextName("contextName");
        fileEventDrivenJob.setChildContextNames(childIds);
        fileEventDrivenJob.setFilenames(List.of("filenames"));
        fileEventDrivenJob.setFilePath("file-path");
        fileEventDrivenJob.setJobGroup("group");
        fileEventDrivenJob.setJobDescription("description");
        fileEventDrivenJob.setCronExpression("cronExpression");
        fileEventDrivenJob.setTimeZone("timeZone");
        fileEventDrivenJob.setEager(true);
        fileEventDrivenJob.setIgnoreMisfire(true);
        fileEventDrivenJob.setMaxEagerCallbacks(5);
        fileEventDrivenJob.setPassthroughProperties(new HashMap<>());
        fileEventDrivenJob.setPersistentRecovery(true);
        fileEventDrivenJob.setRecoveryTolerance(100);
        fileEventDrivenJob.setDirectoryDepth(5);
        fileEventDrivenJob.setEncoding("encoding");
        fileEventDrivenJob.setIgnoreFileRenameWhilstScanning(true);
        fileEventDrivenJob.setIncludeHeader(true);
        fileEventDrivenJob.setLogMatchedFilenames(true);
        fileEventDrivenJob.setIncludeTrailer(true);
        fileEventDrivenJob.setSortAscending(true);
        fileEventDrivenJob.setSortByModifiedDateTime(true);
        fileEventDrivenJob.setMinFileAgeSeconds(180);
        fileEventDrivenJob.setMoveDirectory("archive");
        fileEventDrivenJob.setDynamic(true);
        fileEventDrivenJob.setFilenameSpel("filename spel");
        fileEventDrivenJob.setFilePathSpel("file path spel");
        fileEventDrivenJob.setMoveDirectorySpel("move directory spel");
        fileEventDrivenJob.setSlaCronExpression("sla cron expression");

        QuartzScheduleDrivenJobImpl quartzScheduleDrivenJob = new QuartzScheduleDrivenJobImpl();
        quartzScheduleDrivenJob.setAgentName("agentName");
        quartzScheduleDrivenJob.setJobName("quartzScheduleDrivenJobName");
        quartzScheduleDrivenJob.setJobGroup("jobGroup");
        quartzScheduleDrivenJob.setJobDescription("description");
        quartzScheduleDrivenJob.setContextName("contextName");
        quartzScheduleDrivenJob.setCronExpression("cronExpression");
        quartzScheduleDrivenJob.setTimeZone("timezone");
        quartzScheduleDrivenJob.setEager(true);
        quartzScheduleDrivenJob.setIgnoreMisfire(true);
        quartzScheduleDrivenJob.setMaxEagerCallbacks(1);
        quartzScheduleDrivenJob.setPassthroughProperties(new HashMap<>());
        quartzScheduleDrivenJob.setPersistentRecovery(true);
        quartzScheduleDrivenJob.setRecoveryTolerance(5);

        quartzScheduleDrivenJob.setChildContextNames(childIds);
        ArrayList<SchedulerJob> jobs = new ArrayList<>();
        jobs.add(internalEventDrivenJob);
        jobs.add(fileEventDrivenJob);
        jobs.add(quartzScheduleDrivenJob);

        return jobs;
    }
}
