package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.SystemUtils;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);

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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
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
        when(process.exitValue()).thenReturn(1);

        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.getInternalEventDrivenJob().setSuccessfulReturnCodes(List.of("1"));
        event.setProcess(process);

        event = broker.invoke(event);

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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);

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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
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

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, true);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(true, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_execute_due_to_day_of_week() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        ArrayList<Integer> todayList = new ArrayList<>();
        todayList.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent
            (false, false, todayList);

        event = broker.invoke(event);

        Assert.assertEquals(event.getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_ignore_due_to_day_of_week() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(240);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        ArrayList<Integer> daysOtherThanToday = new ArrayList<>();

        IntStream.range(1, 8).forEach(i -> {
            if(i != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                daysOtherThanToday.add(i);
            }
        });

        EnrichedContextualisedScheduledProcessEvent event
            = this.getEnrichedContextualisedScheduledProcessEvent(false, false, daysOtherThanToday);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED_IGNORED_DAY_OF_WEEK, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(true, event.isSuccessful());
    }

    @Test
    public void test_job_monitor_running_too_long() {
        // Mocking exception that is thrown in the java.lang.ProcessImpl Class
        when(process.exitValue()).thenThrow(new IllegalThreadStateException("process has not exited"));
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(1);

        JobMonitoringBroker broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setProcess(process);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertEquals(false, event.isJobStarting());
        Assert.assertEquals(false, event.isSkipped());
        Assert.assertEquals(false, event.isDryRun());
        Assert.assertEquals(false, event.isSuccessful());
        Assert.assertEquals(-1, event.getReturnCode());
        Assert.assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting."));
    }

    private EnrichedContextualisedScheduledProcessEvent getEnrichedContextualisedScheduledProcessEvent(boolean dryRun, boolean skip
        , ArrayList<Integer> daysOfWeekToRun) {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent
            = this.createEnrichedContextualisedScheduledProcessEvent(dryRun, skip);
        enrichedContextualisedScheduledProcessEvent.getInternalEventDrivenJob().setDaysOfWeekToRun(daysOfWeekToRun);

        this.invokeJobStarting(enrichedContextualisedScheduledProcessEvent);

        return enrichedContextualisedScheduledProcessEvent;
    }


    private EnrichedContextualisedScheduledProcessEvent getEnrichedContextualisedScheduledProcessEvent(boolean dryRun, boolean skip) {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            this.createEnrichedContextualisedScheduledProcessEvent(dryRun, skip);

        this.invokeJobStarting(enrichedContextualisedScheduledProcessEvent);

        return enrichedContextualisedScheduledProcessEvent;
    }

    private EnrichedContextualisedScheduledProcessEvent createEnrichedContextualisedScheduledProcessEvent(boolean dryRun, boolean skip) {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJobInstanceDto.setAgentName("agent name");


        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobInstanceDto.setCommandLine("pwd");
        }
        internalEventDrivenJobInstanceDto.setContextName("contextId");
        internalEventDrivenJobInstanceDto.setIdentifier("identifier");
        internalEventDrivenJobInstanceDto.setMinExecutionTime(1000L);
        internalEventDrivenJobInstanceDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobInstanceDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobInstanceDto);
        enrichedContextualisedScheduledProcessEvent.setResultError("err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput("out");
        enrichedContextualisedScheduledProcessEvent.setDryRun(dryRun);
        if(dryRun) {
            enrichedContextualisedScheduledProcessEvent.setDryRunParameters(new DryRunParametersDto());
        }
        enrichedContextualisedScheduledProcessEvent.setSkipped(skip);


        return enrichedContextualisedScheduledProcessEvent;
    }

    private void invokeJobStarting(EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent) {
        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        jobStartingBroker.setConfiguration(JobStartingBrokerTest.getTestConfiguration());
        jobStartingBroker.setConfiguredResourceId("test");
        jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);
    }
}
