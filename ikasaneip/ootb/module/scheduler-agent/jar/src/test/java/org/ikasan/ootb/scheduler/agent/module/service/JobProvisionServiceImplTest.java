package org.ikasan.ootb.scheduler.agent.module.service;

import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.job.orchestration.model.job.FileEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.InternalEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.QuartzScheduleDrivenJobImpl;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    private SchedulerAgentConfiguredModuleConfiguration configureModuleConfiguration;

    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    private Flow flow;

    @Mock
    private FlowElement fileConsumerElement;

    @Mock
    private ConfiguredResource<ScheduledConsumerConfiguration> fileConsumer;

    @Mock
    private FileConsumerConfiguration fileConsumerConfiguration;

    @Mock
    private FlowElement converterElement;

    @Mock
    private ConfiguredResource<ContextualisedConverterConfiguration> converter;

    @Mock
    private ContextualisedConverterConfiguration converterConfiguration;

    @Mock
    private FlowElement scheduledConsumerElement;

    @Mock
    private ConfiguredResource<ScheduledConsumerConfiguration> scheduledConsumer;

    @Mock ScheduledConsumerConfiguration scheduledConsumerConfiguration;

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
    private FlowElement moveFileBrokerElement;

    @Mock
    private ConfiguredResource<MoveFileBrokerConfiguration> moveFileBroker;

    @Mock
    MoveFileBrokerConfiguration moveFileBrokerConfiguration;

    @Mock
    IkasanAuthentication ikasanAuthentication;

    @Mock
    private FlowElement contextFilterElement;

    @Mock
    private ConfiguredResource<ContextInstanceFilterConfiguration> contextFilter;

    @Mock
    ContextInstanceFilterConfiguration contextFilterConfiguration;

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

    @InjectMocks
    private JobProvisionServiceImpl service;

    @Test
    public void test_provision_success_no_spel_expressions() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        setupWhen();

        this.service.provisionJobs(this.getJobs(), "system");

        verify(scheduledConsumerConfiguration, times(1)).setJobName(anyString());
        verify(scheduledConsumerConfiguration, times(1)).setJobGroupName(anyString());
        verify(scheduledConsumerConfiguration, times(1)).setDescription(anyString());
        verify(scheduledConsumerConfiguration, times(1)).setCronExpression(anyString());
        verify(scheduledConsumerConfiguration, times(1)).setTimezone(anyString());
        verify(scheduledConsumerConfiguration, times(1)).setEager(anyBoolean());
        verify(scheduledConsumerConfiguration, times(1)).setIgnoreMisfire(anyBoolean());
        verify(scheduledConsumerConfiguration, times(1)).setMaxEagerCallbacks(anyInt());
        verify(scheduledConsumerConfiguration, times(1)).setPassthroughProperties(anyMap());
        verify(scheduledConsumerConfiguration, times(1)).setPersistentRecovery(anyBoolean());
        verify(scheduledConsumerConfiguration, times(1)).setRecoveryTolerance(anyLong());

        verify(fileConsumerConfiguration, times(1)).setJobName(anyString());
        verify(fileConsumerConfiguration, times(1)).setJobGroupName(anyString());
        verify(fileConsumerConfiguration, times(1)).setDescription(anyString());
        verify(fileConsumerConfiguration, times(1)).setCronExpression(anyString());
        verify(fileConsumerConfiguration, times(1)).setTimezone(anyString());
        verify(fileConsumerConfiguration, times(1)).setEager(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setIgnoreMisfire(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setMaxEagerCallbacks(anyInt());
        verify(fileConsumerConfiguration, times(1)).setPassthroughProperties(anyMap());
        verify(fileConsumerConfiguration, times(1)).setPersistentRecovery(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setRecoveryTolerance(anyLong());

        verify(fileAgeFilterConfiguration, times(1)).setFileAgeSeconds((anyInt()));
        verify(moveFileBrokerConfiguration, times(1)).setMoveDirectory((anyString()));

        verify(fileConsumerConfiguration, times(1)).setFilenames(anyList());
        verify(fileConsumerConfiguration, times(1)).setDirectoryDepth(anyInt());
        verify(fileConsumerConfiguration, times(1)).setEncoding(anyString());
        verify(fileConsumerConfiguration, times(1)).setIgnoreFileRenameWhilstScanning(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setIncludeHeader(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setLogMatchedFilenames(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setIgnoreMisfire(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setIncludeTrailer(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setSortAscending(anyBoolean());
        verify(fileConsumerConfiguration, times(1)).setSortByModifiedDateTime(anyBoolean());

        verify(contextFilterConfiguration, times(2)).setContextName(anyString());

        verify(schedulerFileFilterConfiguration, times(1)).setJobName(anyString());

        verify(converterConfiguration, times(2)).setContextName(anyString());
        verify(converterConfiguration, times(2)).setChildContextNames(anyList());

        verifyNoMoreInteractions(fileConsumerConfiguration);
        verifyNoMoreInteractions(scheduledConsumerConfiguration);
        verifyNoMoreInteractions(converterConfiguration);
        verifyNoMoreInteractions(contextFilterConfiguration);
    }

    @Test
    public void test_provision_success_with_spel_expressions() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        Map<String, List<Object>> spelMap
            = Map.of("contextName", List.of(true, "#fileName.replace(#someValue, 'thevaluetoreplace')", Map.of("#someValue", "contextName")));

        ReflectionTestUtils.setField(service,"spelExpressionsMap", spelMap);

        setupWhen();

        this.service.provisionJobs(this.getJobs(), "system");

        verify(fileConsumerConfiguration).setDynamicFileName(true);
        verify(fileConsumerConfiguration).setSpelExpression("#fileName.replace('contextName', 'thevaluetoreplace')");
    }

    @Test
    public void get_spel_replacement_string() {
        FileEventDrivenJobImpl fileEventDrivenJob = new FileEventDrivenJobImpl();
        fileEventDrivenJob.setContextName("contextName");
        fileEventDrivenJob.setJobName("jobName");
        assertEquals("'contextName'", service.getSpelReplacement("contextName", fileEventDrivenJob));
        assertEquals("'jobName'", service.getSpelReplacement("jobName", fileEventDrivenJob));
        assertNull(service.getSpelReplacement("unknownVar", fileEventDrivenJob));
    }

    @Test
    public void get_spel_replacement_list_strings() {
        FileEventDrivenJobImpl fileEventDrivenJob = new FileEventDrivenJobImpl();
        assertEquals("{}", service.getSpelReplacement("filenames", fileEventDrivenJob));
        fileEventDrivenJob.setFilenames(List.of("fileName1", "fileName2"));
        assertEquals("{'fileName1','fileName2'}", service.getSpelReplacement("filenames", fileEventDrivenJob));
    }

    @Test
    public void get_spel_replacement_map_of_string_string() {
        FileEventDrivenJobImpl fileEventDrivenJob = new FileEventDrivenJobImpl();
        assertEquals("{}", service.getSpelReplacement("passthroughProperties", fileEventDrivenJob));
        fileEventDrivenJob.setPassthroughProperties(Map.of("key1", "value1"));
        assertEquals("{'key1':'value1'}", service.getSpelReplacement("passthroughProperties", fileEventDrivenJob));
    }

    @Test
    public void test_remove_jobs_for_context() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SchedulerAgentConfiguredModuleConfiguration configuration = new SchedulerAgentConfiguredModuleConfiguration();
        configuration.setFlowContextMap(this.getJobContextMap());
        configuration.setFlowDefinitions(this.getJobContextMap());
        configuration.setFlowDefinitionProfiles(this.getJobContextMap());

        Assert.assertEquals(3, configuration.getFlowContextMap().size());
        Assert.assertEquals(3, configuration.getFlowDefinitions().size());
        Assert.assertEquals(3, configuration.getFlowDefinitionProfiles().size());

        when(moduleService.getModule(null)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configuration);

        this.service.removeJobs("contextName");

        verify(moduleActivator).deactivate(module);
        verify(moduleActivator).activate(module);

        verifyNoMoreInteractions(fileConsumerConfiguration);
        verifyNoMoreInteractions(moduleActivator);
        verifyNoMoreInteractions(scheduledConsumerConfiguration);
        verifyNoMoreInteractions(converterConfiguration);
        verifyNoMoreInteractions(contextFilterConfiguration);

        Assert.assertEquals(0, configuration.getFlowContextMap().size());
        Assert.assertEquals(0, configuration.getFlowDefinitions().size());
        Assert.assertEquals(0, configuration.getFlowDefinitionProfiles().size());
    }

    private void setupWhen() {
        when(moduleService.getModule(null)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModuleConfiguration);
        when(module.getFlow(anyString())).thenReturn(flow);
        when(flow.getFlowElement("File Consumer")).thenReturn(fileConsumerElement);
        when(fileConsumerElement.getFlowComponent()).thenReturn(fileConsumer);
        when(fileConsumer.getConfiguration()).thenReturn(fileConsumerConfiguration);
        when(flow.getFlowElement("JobExecution to ScheduledStatusEvent")).thenReturn(converterElement);
        when(converterElement.getFlowComponent()).thenReturn(converter);
        when(converter.getConfiguration()).thenReturn(converterConfiguration);
        when(flow.getFlowElement("Scheduled Consumer")).thenReturn(scheduledConsumerElement);
        when(scheduledConsumerElement.getFlowComponent()).thenReturn(scheduledConsumer);
        when(scheduledConsumer.getConfiguration()).thenReturn(scheduledConsumerConfiguration);
        when(flow.getFlowElement("File Age Filter")).thenReturn(fileAgeFilterElement);
        when(fileAgeFilterElement.getFlowComponent()).thenReturn(fileAgeFilter);
        when(fileAgeFilter.getConfiguration()).thenReturn(fileAgeFilterConfiguration);
        when(flow.getFlowElement("File Move Broker")).thenReturn(moveFileBrokerElement);
        when(moveFileBrokerElement.getFlowComponent()).thenReturn(moveFileBroker);
        when(moveFileBroker.getConfiguration()).thenReturn(moveFileBrokerConfiguration);
        when(flow.getFlowElement("Context Instance Active Filter")).thenReturn(contextFilterElement);
        when(contextFilterElement.getFlowComponent()).thenReturn(contextFilter);
        when(contextFilter.getConfiguration()).thenReturn(contextFilterConfiguration);
        when(flow.getFlowElement("Duplicate Message Filter")).thenReturn(duplicateMessageFilterElement);
        when(duplicateMessageFilterElement.getFlowComponent()).thenReturn(duplicateMessageFilter);
        when(duplicateMessageFilter.getConfiguration()).thenReturn(schedulerFileFilterConfiguration);
        when(flow.getFlowElement("Blackout Router")).thenReturn(blackoutRouterElement);
        when(blackoutRouterElement.getFlowComponent()).thenReturn(blackoutRouter);
        when(blackoutRouter.getConfiguration()).thenReturn(blackoutRouterConfiguration);
        when(flow.getFlowElement("Publish Scheduled Status")).thenReturn(scheduledStatusFilterElement);
        when(scheduledStatusFilterElement.getFlowComponent()).thenReturn(scheduledProcessEventFilter);
        when(scheduledProcessEventFilter.getConfiguration()).thenReturn(scheduledProcessEventFilterConfiguration);
        when(ikasanAuthentication.getPrincipal()).thenReturn("ikasan-user");
    }

    private Map<String, String> getJobContextMap() {
        Map<String, String> contextJobs = new HashMap<>();

        this.getJobs().forEach(job -> contextJobs.put(job.getJobName(), job.getContextName()));

        return contextJobs;
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
        fileEventDrivenJob.setFilenames(new ArrayList<>());
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
