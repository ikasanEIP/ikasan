package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.SystemUtils;
import org.ikasan.cli.shell.operation.service.PersistenceService;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobMonitoringBrokerTest {

    private static Long DEFAULT_TIMEOUT = 240L;
    @Mock
    private Process process;
    @Mock
    private PersistenceService persistenceService;
    @Mock
    private ProcessHandle processHandle;
    @Mock
    private CompletableFuture<ProcessHandle> completableFuture;

    @Test
    public void test_job_monitor_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);

        event = broker.invoke(event);

        Assert.assertEquals(event.getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_fail_bad_command() throws InterruptedException {
        when(process.waitFor(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenReturn(true);
        when(process.exitValue()).thenReturn(1);
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setProcess(process);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_success_due_to_return_code() throws InterruptedException {
        when(process.waitFor(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenReturn(true);
        when(process.exitValue()).thenReturn(1);

        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.getInternalEventDrivenJob().setSuccessfulReturnCodes(List.of("1"));
        event.setProcess(process);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertTrue(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_fixed_execution_time_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
        event.getDryRunParameters().setFixedExecutionTimeMillis(1000);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertTrue(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }


    @Test
    public void test_job_monitor_dry_run_error_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
        event.getDryRunParameters().setError(true);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertTrue(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_error_due_to_percent_error_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
        event.getDryRunParameters().setJobErrorPercentage(100);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertTrue(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_skip_success() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, true);

        event = broker.invoke(event);

        Assert.assertEquals(0, event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertTrue(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_execute_due_to_day_of_week() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        ArrayList<Integer> todayList = new ArrayList<>();
        todayList.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent
            (false, false, todayList);

        event = broker.invoke(event);

        Assert.assertEquals(event.getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_ignore_due_to_day_of_week() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
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
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_running_too_long() throws InterruptedException {
        Long timeout = 1L;
        when(process.waitFor(timeout, TimeUnit.MINUTES)).thenReturn(false);

        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(timeout);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setProcess(process);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
        Assert.assertEquals(-1, event.getReturnCode());
        Assert.assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting."));
    }

    @Test
    public void test_job_monitor_when_recovered_from_agent_crash_and_process_still_running_then_ends_inside_timeout() {
        when(processHandle.onExit()).thenReturn(completableFuture);
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setProcessHandle(processHandle);

        event = broker.invoke(event);

        Assert.assertEquals(event.getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
        Assert.assertEquals(-1, event.getReturnCode());
        Assert.assertTrue(event.isDetached());
        Assert.assertTrue(event.getExecutionDetails().contains("The process was detached so we can not be certain of the exit status"));
    }

    @Test
    public void test_job_monitor_when_recovered_from_agent_crash_and_process_finished_during_agent_outage() {
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setProcessHandle(processHandle);  // This infers the process is detached and sets the detached flag
        event.setDetachedAlreadyFinished(true);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
        Assert.assertEquals(-1, event.getReturnCode());
        Assert.assertTrue(event.isDetached());
        Assert.assertTrue(event.isDetachedAlreadyFinished());
        Assert.assertTrue(event.getExecutionDetails().contains("The process was detached so we can not be certain of the exit status"));
    }
    @Test
    public void test_job_monitor_when_recovered_from_agent_crash_and_process_still_running_then_does_not_end_within_timeout() throws ExecutionException, InterruptedException, TimeoutException {
        when(processHandle.onExit()).thenReturn(completableFuture);
        when(completableFuture.get(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenThrow(new TimeoutException());
        when(processHandle.destroy()).thenReturn(true);
        JobMonitoringBrokerConfiguration configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);

        JobMonitoringBroker broker = new JobMonitoringBroker(persistenceService);
        broker.setConfiguration(configuration);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setProcessHandle(processHandle);  // This infers the process is detached and sets the detached flag

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
        Assert.assertEquals(-1, event.getReturnCode());
        Assert.assertTrue(event.isDetached());
        Assert.assertTrue(event.getExecutionDetails().contains("The process was detached so we can not be certain of the exit status"));
        Assert.assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting. Note this process was detached so may not behave normally"));
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
        JobStartingBroker jobStartingBroker = new JobStartingBroker(persistenceService);
        jobStartingBroker.setConfiguration(JobStartingBrokerTest.getTestConfiguration());
        jobStartingBroker.setConfiguredResourceId("test");
        jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);
    }
}
