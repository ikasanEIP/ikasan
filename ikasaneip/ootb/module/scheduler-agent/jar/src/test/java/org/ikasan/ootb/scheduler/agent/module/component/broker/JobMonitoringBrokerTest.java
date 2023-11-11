package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.SystemUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.CommandProcessor;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcess;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.ProcessStatusDaoFSImp;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.SchedulerKryoProcessPersistenceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.service.SchedulerDefaultPersistenceServiceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.service.SchedulerPersistenceService;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.ikasan.ootb.scheduler.agent.module.component.broker.JobMonitoringBroker.DEFAULT_ERROR_RETURN_CODE;
import static org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcessBuilder.SCHEDULER_PROCESS_TYPE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    @Mock
    private ErrorReportingService errorReportingService;
    @TempDir
    public File tmpFolder;

    private JobMonitoringBrokerConfiguration configuration;
    private JobMonitoringBroker broker;
    private static final Long PROCESS_ID = 999L;
    private static final String INSTANCE_ID = "X";
    private static final String JOB_NAME = "Y";
    private static final String IDENTITY = INSTANCE_ID +"-"+ JOB_NAME;

    @BeforeEach
    void setUp() {
        configuration = new JobMonitoringBrokerConfiguration();
        configuration.setTimeout(DEFAULT_TIMEOUT);
        broker = new JobMonitoringBroker("flowName");
        broker.setConfiguration(configuration);
        broker.setErrorReportingService(this.errorReportingService);
    }

    @Test
    void test_job_monitor_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event = broker.invoke(event);

        assertEquals(event.getDetachableProcess().getPid(), event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_fail_bad_command() throws InterruptedException {
        when(processMock.waitFor(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenReturn(true);
        when(processMock.exitValue()).thenReturn(1);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.getDetachableProcess().setProcess(processMock);

        event = broker.invoke(event);

        verify(this.errorReportingService, times(1)).notify(anyString(), any(), any(Throwable.class), anyString());

        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertFalse(event.isSuccessful());
    }


    @Test
    void test_job_monitor_success_due_to_return_code() throws InterruptedException {
        when(processMock.waitFor(DEFAULT_TIMEOUT, TimeUnit.MINUTES)).thenReturn(true);
        when(processMock.exitValue()).thenReturn(1);

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, false);
        event.getInternalEventDrivenJob().setSuccessfulReturnCodes(List.of("1"));
        event.getDetachableProcess().setProcess(processMock);

        event = broker.invoke(event);

        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_dry_run_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);

        event = broker.invoke(event);

        assertEquals(0, event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertTrue(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_dry_run_fixed_execution_time_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
        event.getDryRunParameters().setFixedExecutionTimeMillis(1000);

        event = broker.invoke(event);

        assertEquals(0, event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertTrue(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_dry_run_error_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
        event.getDryRunParameters().setError(true);

        event = broker.invoke(event);

        assertEquals(0, event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertTrue(event.isDryRun());
        assertFalse(event.isSuccessful());
    }

    @Test
    void test_job_monitor_dry_run_error_due_to_percent_error_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(true, false);
        event.getDryRunParameters().setJobErrorPercentage(100);

        event = broker.invoke(event);

        assertEquals(0, event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertTrue(event.isDryRun());
        assertFalse(event.isSuccessful());
    }

    @Test
    void test_job_monitor_skip_success() {
        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent(false, true);

        event = broker.invoke(event);

        assertEquals(0, event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertTrue(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_execute_due_to_day_of_week() {

        ArrayList<Integer> todayList = new ArrayList<>();
        todayList.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        EnrichedContextualisedScheduledProcessEvent event = this.getEnrichedContextualisedScheduledProcessEvent
            (false, false, todayList);

        event = broker.invoke(event);

        assertEquals(event.getDetachableProcess().getProcess().pid(), event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_ignore_due_to_day_of_week() {
        ArrayList<Integer> daysOtherThanToday = new ArrayList<>();

        IntStream.range(1, 8).forEach(i -> {
            if(i != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                daysOtherThanToday.add(i);
            }
        });

        EnrichedContextualisedScheduledProcessEvent event
            = this.getEnrichedContextualisedScheduledProcessEvent(false, false, daysOtherThanToday);

        event = broker.invoke(event);

        assertEquals(0, event.getPid());
        assertEquals(Outcome.EXECUTION_INVOKED_IGNORED_DAY_OF_WEEK, event.getOutcome());
        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());
    }

    @Test
    void test_job_monitor_running_too_long() throws InterruptedException, IOException {
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

        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertFalse(event.isSuccessful());
        assertEquals(DEFAULT_ERROR_RETURN_CODE, event.getReturnCode());
        assertFalse(event.getDetachableProcess().isDetached());
        assertFalse(event.getDetachableProcess().isDetachedAlreadyFinished());

        assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting."));

        verify(schedulerPersistenceServiceMock, times(0)).remove(SCHEDULER_PROCESS_TYPE, IDENTITY);
        verify(schedulerPersistenceServiceMock, times(1)).removeAll(IDENTITY, cp.getScriptFilePostfix());
    }

    @Test
    void test_job_monitor_when_recovered_from_agent_crash_and_process_still_running_then_ends_inside_timeout() throws IOException {
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

        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());

        assertEquals(0, event.getReturnCode());
        assertTrue(event.getDetachableProcess().isDetached());
        assertFalse(event.getDetachableProcess().isDetachedAlreadyFinished());
        assertTrue(event.getExecutionDetails().contains("The process was detached, the processHandle and output file will be used to determine the return value."));

        verify(schedulerPersistenceServiceMock, times(0)).remove(SCHEDULER_PROCESS_TYPE, IDENTITY);
        verify(schedulerPersistenceServiceMock, times(1)).removeAll(IDENTITY, cp.getScriptFilePostfix());
    }

    @Test
    void test_job_monitor_when_recovered_from_agent_crash_and_process_finished_during_agent_outage() throws IOException {
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

        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertTrue(event.isSuccessful());

        assertEquals(0, event.getReturnCode());
        assertTrue(event.getDetachableProcess().isDetached());
        assertTrue(event.getDetachableProcess().isDetachedAlreadyFinished());
        assertTrue(event.getExecutionDetails().contains("The process was detached, the processHandle and output file will be used to determine the return value."));

        verify(schedulerPersistenceServiceMock, times(0)).remove(SCHEDULER_PROCESS_TYPE, IDENTITY);
        verify(schedulerPersistenceServiceMock, times(1)).removeAll(IDENTITY, cp.getScriptFilePostfix());
    }

    @Test
    void test_job_monitor_when_recovered_from_agent_crash_and_process_still_running_then_does_not_end_within_timeout() throws ExecutionException, InterruptedException, TimeoutException, IOException {
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

        assertFalse(event.isJobStarting());
        assertFalse(event.isSkipped());
        assertFalse(event.isDryRun());
        assertFalse(event.isSuccessful());

        assertEquals(DEFAULT_ERROR_RETURN_CODE, event.getReturnCode());
        assertTrue(event.getDetachableProcess().isDetached());
        assertTrue(event.getExecutionDetails().contains("Killing the process. If more time is required, please raise this to the administrator to change the timeout setting. Note this process was detached so may not behave normally"));
        assertTrue(event.getExecutionDetails().contains("WARNING : There were problems getting the return status from the detached process, it will be treated as an error, issue was"));

        verify(schedulerPersistenceServiceMock, times(0)).remove(SCHEDULER_PROCESS_TYPE, IDENTITY);
        verify(schedulerPersistenceServiceMock, times(1)).removeAll(IDENTITY, cp.getScriptFilePostfix());
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

        enrichedContextualisedScheduledProcessEvent.setResultError(tmpFolder.getAbsolutePath()+"/err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput(tmpFolder.getAbsolutePath()+"/out");
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
            new SchedulerKryoProcessPersistenceImpl(tmpFolder.getAbsolutePath()),
            new ProcessStatusDaoFSImp(tmpFolder.getAbsolutePath())));
        jobStartingBroker.setConfiguration(JobStartingBrokerTest.getTestConfiguration());
        jobStartingBroker.setConfiguredResourceId("test");
        jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);
    }
}
