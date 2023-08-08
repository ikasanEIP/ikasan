package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.SystemUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.CommandProcessor;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.DetachableProcess;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.dao.ProcessStatusDaoFSImp;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.dao.SchedulerKryoProcessPersistenceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.service.SchedulerDefaultPersistenceServiceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.service.SchedulerPersistenceService;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.ikasan.ootb.scheduler.agent.module.component.broker.JobMonitoringBroker.DEFAULT_ERROR_RETURN_CODE;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobMonitoringBrokerTest {
    private static final Long DEFAULT_TIMEOUT = 240L;
    @Mock
    private Process processMock;
    @Mock
    private SchedulerPersistenceService schedulerPersistenceServiceMock;
    @Mock
    private ProcessHandle processHandleMock;
    @Mock
    private CompletableFuture<ProcessHandle> completableFutureMock;
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private JobMonitoringBrokerConfiguration configuration;
    private JobMonitoringBroker broker;
    private static final Long PROCESS_ID = 999L;
    private static final String INSTANCE_ID = "X";
    private static final String JOB_NAME = "Y";
    private static final String IDENTITY = INSTANCE_ID +"-"+ JOB_NAME;
    @Before
    public void setUp() {
        configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);
        broker = new JobMonitoringBroker();
        broker.setConfiguration(configuration);
    }

    @Test
    public void test_job_monitor_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event = broker.invoke(event);

        Assert.assertEquals(event.getDetachableProcess().getPid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_fail_bad_command() throws InterruptedException {
        when(processMock.waitFor(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenReturn(true);
        when(processMock.exitValue()).thenReturn(1);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.getDetachableProcess().setProcess(processMock);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
    }



    @Test
    public void test_job_monitor_success_due_to_return_code() throws InterruptedException {
        when(processMock.waitFor(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenReturn(true);
        when(processMock.exitValue()).thenReturn(1);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.getInternalEventDrivenJob().setSuccessfulReturnCodes(List.of("1"));
        event.getDetachableProcess().setProcess(processMock);

        event = broker.invoke(event);

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_dry_run_success() {
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

        ArrayList<Integer> todayList = new ArrayList<>();
        todayList.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent
            (false, false, todayList);

        event = broker.invoke(event);

        Assert.assertEquals(event.getDetachableProcess().getProcess().pid(), event.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());
    }

    @Test
    public void test_job_monitor_ignore_due_to_day_of_week() {
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
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);
        Long timeout = 1L;
        when(processMock.waitFor(timeout, TimeUnit.MINUTES)).thenReturn(false);
        configuration.setTimeout(timeout);

        EnrichedContextualisedScheduledProcessEvent event = this.getMockedEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setContextInstanceId(INSTANCE_ID);
        event.setJobName(JOB_NAME);
        DetachableProcess detachableProcess = new DetachableProcess(schedulerPersistenceServiceMock, IDENTITY, cp);
        detachableProcess.setDetached(false);
        detachableProcess.setDetachedAlreadyFinished(false);
        detachableProcess.setProcess(processMock);
        detachableProcess.setProcessHandle(null);
        detachableProcess.setPid(PROCESS_ID);
        event.setDetachableProcess(detachableProcess);

        event = broker.invoke(event);

        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());
        Assert.assertEquals(DEFAULT_ERROR_RETURN_CODE, event.getReturnCode());
        Assert.assertFalse(event.getDetachableProcess().isDetached());
        Assert.assertFalse(event.getDetachableProcess().isDetachedAlreadyFinished());

        Assert.assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting."));
    }

    @Test
    public void test_job_monitor_when_recovered_from_agent_crash_and_process_still_running_then_ends_inside_timeout() {
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);

        when(schedulerPersistenceServiceMock.getPersistedReturnCode(IDENTITY)).thenReturn("0");
        when(processHandleMock.onExit()).thenReturn(completableFutureMock);

        EnrichedContextualisedScheduledProcessEvent event = this.getMockedEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setContextInstanceId(INSTANCE_ID);
        event.setJobName(JOB_NAME);
        DetachableProcess detachableProcess = new DetachableProcess(schedulerPersistenceServiceMock, IDENTITY, cp);
        detachableProcess.setDetached(true);
        detachableProcess.setDetachedAlreadyFinished(false);
        detachableProcess.setProcess(null);
        detachableProcess.setProcessHandle(processHandleMock);
        detachableProcess.setPid(PROCESS_ID);
        event.setDetachableProcess(detachableProcess);

        event = broker.invoke(event);

        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());

        Assert.assertEquals(0, event.getReturnCode());
        Assert.assertTrue(event.getDetachableProcess().isDetached());
        Assert.assertFalse(event.getDetachableProcess().isDetachedAlreadyFinished());
        Assert.assertTrue(event.getExecutionDetails().contains("The process was detached, the processHandle and output file will be used to determine the return value."));
    }

    @Test
    public void test_job_monitor_when_recovered_from_agent_crash_and_process_finished_during_agent_outage() {
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);

        when(schedulerPersistenceServiceMock.getPersistedReturnCode(IDENTITY)).thenReturn("0");

        EnrichedContextualisedScheduledProcessEvent event = this.getMockedEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setContextInstanceId(INSTANCE_ID);
        event.setJobName(JOB_NAME);
        DetachableProcess detachableProcess = new DetachableProcess(schedulerPersistenceServiceMock, IDENTITY, cp);
        detachableProcess.setDetached(true);
        detachableProcess.setDetachedAlreadyFinished(true);
        detachableProcess.setProcess(null);
        detachableProcess.setProcessHandle(processHandleMock);
        detachableProcess.setPid(PROCESS_ID);
        event.setDetachableProcess(detachableProcess);

        event = broker.invoke(event);

        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertTrue(event.isSuccessful());

        Assert.assertEquals(0, event.getReturnCode());
        Assert.assertTrue(event.getDetachableProcess().isDetached());
        Assert.assertTrue(event.getDetachableProcess().isDetachedAlreadyFinished());
        Assert.assertTrue(event.getExecutionDetails().contains("The process was detached, the processHandle and output file will be used to determine the return value."));
    }
    @Test
    public void test_job_monitor_when_recovered_from_agent_crash_and_process_still_running_then_does_not_end_within_timeout() throws ExecutionException, InterruptedException, TimeoutException {
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);

        when(processHandleMock.onExit()).thenReturn(completableFutureMock);
        when(completableFutureMock.get(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenThrow(new TimeoutException());
        when(processHandleMock.destroy()).thenReturn(true);

        EnrichedContextualisedScheduledProcessEvent event = this.getMockedEnrichedContextualisedScheduledProcessEvent(false, false);
        event.setContextInstanceId(INSTANCE_ID);
        event.setJobName(JOB_NAME);
        DetachableProcess detachableProcess = new DetachableProcess(schedulerPersistenceServiceMock, IDENTITY, cp);
        detachableProcess.setDetached(true);
        detachableProcess.setDetachedAlreadyFinished(false);
        detachableProcess.setProcess(null);
        detachableProcess.setProcessHandle(processHandleMock);
        detachableProcess.setPid(PROCESS_ID);
        event.setDetachableProcess(detachableProcess);

        event = broker.invoke(event);

        Assert.assertFalse(event.isJobStarting());
        Assert.assertFalse(event.isSkipped());
        Assert.assertFalse(event.isDryRun());
        Assert.assertFalse(event.isSuccessful());

        Assert.assertEquals(DEFAULT_ERROR_RETURN_CODE, event.getReturnCode());
        Assert.assertTrue(event.getDetachableProcess().isDetached());
        Assert.assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting. Note this process was detached so may not behave normally"));
        Assert.assertTrue(event.getExecutionDetails().contains("WARNING : There were problems getting the return status from the detached process, it will be treated as an error, issue was"));
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

    private EnrichedContextualisedScheduledProcessEvent getMockedEnrichedContextualisedScheduledProcessEvent(boolean dryRun, boolean skip) {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            this.createEnrichedContextualisedScheduledProcessEvent(dryRun, skip);
        return enrichedContextualisedScheduledProcessEvent;
    }

    private EnrichedContextualisedScheduledProcessEvent createEnrichedContextualisedScheduledProcessEvent(boolean dryRun, boolean skip) {
        final String INSTANCE_ID = "AB1";
        final String JOB_NAME = "XYZ";

        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJobInstanceDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("dir");
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

        enrichedContextualisedScheduledProcessEvent.setResultError(tmpFolder.getRoot().getAbsolutePath()+"/err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput(tmpFolder.getRoot().getAbsolutePath()+"/out");
        enrichedContextualisedScheduledProcessEvent.setJobName(JOB_NAME);
        enrichedContextualisedScheduledProcessEvent.setContextInstanceId(INSTANCE_ID);
        enrichedContextualisedScheduledProcessEvent.setDryRun(dryRun);
        if(dryRun) {
            enrichedContextualisedScheduledProcessEvent.setDryRunParameters(new DryRunParametersDto());
        }
        enrichedContextualisedScheduledProcessEvent.setSkipped(skip);


        return enrichedContextualisedScheduledProcessEvent;
    }

    private void invokeJobStarting(EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent) {
        JobStartingBroker jobStartingBroker = new JobStartingBroker(new SchedulerDefaultPersistenceServiceImpl(
            new SchedulerKryoProcessPersistenceImpl(tmpFolder.getRoot().getAbsolutePath()),
            new ProcessStatusDaoFSImp(tmpFolder.getRoot().getAbsolutePath())));
        jobStartingBroker.setConfiguration(JobStartingBrokerTest.getTestConfiguration());
        jobStartingBroker.setConfiguredResourceId("test");
        jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);
    }
}
