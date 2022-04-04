package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.SystemUtils;
import org.ikasan.ootb.scheduled.model.Outcome;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobMonitoringBrokerTest {

    @Mock
    private Process process;

    @Test
    public void test_job_monitor_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false, false);

        event = broker.invoke(event);

        Assert.assertEquals(event.getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_fail_bad_command() {
        when(process.exitValue()).thenReturn(1);
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false, true);
        event.setProcess(process);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(false, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_success_due_to_return_code() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false, true);
        event.getInternalEventDrivenJob().setSuccessfulReturnCodes(List.of("1"));

        event = broker.invoke(event);

        Assert.assertEquals(event.getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false, false);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(true, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_fixed_execution_time_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false, false);
        event.getDryRunParameters().setFixedExecutionTimeMillis(1000);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(true, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }


    @Test
    public void test_job_monitor_dry_run_error_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false, false);
        event.getDryRunParameters().setError(true);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(true, event.isDryRun());
        Assert.assertEquals(false, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_error_due_to_percent_error_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false, false);
        event.getDryRunParameters().setJobErrorPercentage(100);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(true, event.isDryRun());
        Assert.assertEquals(false, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_skip_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, true, false);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(true, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    private EnrichedContextualisedScheduledProcessEvent getEnrichedContextualisedScheduledProcessEvent(boolean dryRun, boolean skip, boolean badCommand) {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        InternalEventDrivenJobDto internalEventDrivenJobDto = new InternalEventDrivenJobDto();
        internalEventDrivenJobDto.setAgentName("agent name");


        if (SystemUtils.OS_NAME.contains("Windows")) {
            if(badCommand) {
                internalEventDrivenJobDto.setCommandLine("DIR BAD_COMMAND");
            }
            else {
                internalEventDrivenJobDto.setCommandLine("java -version");
            }
        }
        else {
            if(badCommand) {
                internalEventDrivenJobDto.setCommandLine("ls -la ls -la ls -la");
            }
            else {
                internalEventDrivenJobDto.setCommandLine("pwd");
            }
        }
        internalEventDrivenJobDto.setContextId("contextId");
        internalEventDrivenJobDto.setIdentifier("identifier");
        internalEventDrivenJobDto.setMinExecutionTime(1000L);
        internalEventDrivenJobDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobDto);
        enrichedContextualisedScheduledProcessEvent.setResultError("err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput("out");
        enrichedContextualisedScheduledProcessEvent.setDryRun(dryRun);
        if(dryRun) {
            enrichedContextualisedScheduledProcessEvent.setDryRunParameters(new DryRunParametersDto());
        }
        enrichedContextualisedScheduledProcessEvent.setSkipped(skip);

        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        return enrichedContextualisedScheduledProcessEvent;
    }
}
