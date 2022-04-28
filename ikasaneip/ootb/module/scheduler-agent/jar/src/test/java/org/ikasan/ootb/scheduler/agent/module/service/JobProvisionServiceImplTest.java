package org.ikasan.ootb.scheduler.agent.module.service;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.job.orchestration.model.job.FileEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.InternalEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.QuartzScheduleDrivenJobImpl;
import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.ContextualisedFileConsumerConfiguration;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ConfiguredModuleConfiguration configureModule;

    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    private Flow flow;

    @Mock
    private FlowElement fileConsumerElement;

    @Mock
    private ConfiguredResource<ScheduledConsumerConfiguration> fileConsumer;

    @Mock
    private ContextualisedFileConsumerConfiguration fileConsumerConfiguration;

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
    private FlowElement moveFileBrokerElement;

    @Mock
    private ConfiguredResource<MoveFileBrokerConfiguration> moveFileBroker;

    @Mock
    MoveFileBrokerConfiguration moveFileBrokerConfiguration;

    @Mock
    IkasanAuthentication ikasanAuthentication;

    @InjectMocks
    private JobProvisionServiceImpl service;

    @Test
    public void test_provision_success() {
        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        when(moduleService.getModule(null)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
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
        when(ikasanAuthentication.getPrincipal()).thenReturn("ikasan-user");


        this.service.provisionJobs(this.getJobs());

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
        verify(fileConsumerConfiguration, times(1)).setContextId(anyString());

        verify(converterConfiguration, times(2)).setContextId(anyString());
        verify(converterConfiguration, times(2)).setChildContextIds(anyList());

        verifyNoMoreInteractions(fileConsumerConfiguration);
        verifyNoMoreInteractions(scheduledConsumerConfiguration);
        verifyNoMoreInteractions(converterConfiguration);
    }

    private List<SchedulerJob> getJobs() {
        List<String> childIds = new ArrayList<>();
        childIds.add("childId");

        InternalEventDrivenJobImpl internalEventDrivenJob = new InternalEventDrivenJobImpl();
        internalEventDrivenJob.setAgentName("agentName");
        internalEventDrivenJob.setJobName("jobName");
        internalEventDrivenJob.setContextId("contextId");
        internalEventDrivenJob.setChildContextIds(childIds);

        FileEventDrivenJobImpl fileEventDrivenJob = new FileEventDrivenJobImpl();
        fileEventDrivenJob.setAgentName("agentName");
        fileEventDrivenJob.setJobName("jobName");
        fileEventDrivenJob.setContextId("contextId");
        fileEventDrivenJob.setChildContextIds(childIds);
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
        quartzScheduleDrivenJob.setJobName("jobName");
        quartzScheduleDrivenJob.setJobGroup("jobGroup");
        quartzScheduleDrivenJob.setJobDescription("description");
        quartzScheduleDrivenJob.setContextId("contextId");
        quartzScheduleDrivenJob.setCronExpression("cronExpression");
        quartzScheduleDrivenJob.setTimeZone("timezone");
        quartzScheduleDrivenJob.setEager(true);
        quartzScheduleDrivenJob.setIgnoreMisfire(true);
        quartzScheduleDrivenJob.setMaxEagerCallbacks(1);
        quartzScheduleDrivenJob.setPassthroughProperties(new HashMap<>());
        quartzScheduleDrivenJob.setPersistentRecovery(true);
        quartzScheduleDrivenJob.setRecoveryTolerance(5);

        quartzScheduleDrivenJob.setChildContextIds(childIds);
        ArrayList<SchedulerJob> jobs = new ArrayList<>();
        jobs.add(internalEventDrivenJob);
        jobs.add(fileEventDrivenJob);
        jobs.add(quartzScheduleDrivenJob);

        return jobs;
    }
}
