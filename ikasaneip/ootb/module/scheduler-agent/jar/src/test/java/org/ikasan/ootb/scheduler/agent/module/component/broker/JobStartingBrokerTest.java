package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awaitility.Awaitility;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobStartingBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.ProcessStatusDaoFSImp;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.SchedulerKryoProcessPersistenceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.model.SchedulerIkasanProcess;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.service.SchedulerDefaultPersistenceServiceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.service.SchedulerPersistenceService;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcessBuilder.SCHEDULER_PROCESS_TYPE;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobStartingBrokerTest {
    @Mock
    private SchedulerPersistenceService schedulerPersistenceServiceMock;
    @Mock
    private ProcessHandle processHandleMock;
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    // Test Fixtures
    private JobStartingBroker jobStartingBroker;
    private InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto;

    private static final String INSTANCE_ID = "AB1";
    private static final String JOB_NAME = "XYZ AA B";
    final String IDENTITY = INSTANCE_ID + "-" + "XYZ_AA_B";
    private String errorLog;
    private String outputLog;

    private EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent = null;
    @Before
    public void setUp() {
        enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();

        internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
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

        errorLog = tmpFolder.getRoot().getAbsolutePath()+"/err";
        outputLog = tmpFolder.getRoot().getAbsolutePath()+"/out";
        enrichedContextualisedScheduledProcessEvent.setResultError(errorLog);
        enrichedContextualisedScheduledProcessEvent.setResultOutput(outputLog);
        enrichedContextualisedScheduledProcessEvent.setContextInstanceId(INSTANCE_ID);
        enrichedContextualisedScheduledProcessEvent.setJobName(JOB_NAME);

        jobStartingBroker = new JobStartingBroker(
            new SchedulerDefaultPersistenceServiceImpl(
                new SchedulerKryoProcessPersistenceImpl(tmpFolder.getRoot().getAbsolutePath()),
                new ProcessStatusDaoFSImp(tmpFolder.getRoot().getAbsolutePath())));
        jobStartingBroker.setConfiguration(getTestConfiguration());
        jobStartingBroker.setConfiguredResourceId("test");
    }


    @Test
    public void test_job_start_skipped_success() {
        enrichedContextualisedScheduledProcessEvent.setSkipped(true);
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(0, enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getDetachableProcess());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }


    @Test
    public void test_job_start_dry_run_success() {
        enrichedContextualisedScheduledProcessEvent.setSkipped(false);
        enrichedContextualisedScheduledProcessEvent.setDryRun(true);

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(0, enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getDetachableProcess());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    @Test
    public void test_job_start_success() {
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    @Test
    public void test_job_start_success_custom_command() {
        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setExecutionEnvironmentProperties("cmd.exe|/c");
            internalEventDrivenJobInstanceDto.setCommandLine("dir");
        }
        else {
            internalEventDrivenJobInstanceDto.setExecutionEnvironmentProperties("/bin/bash|-c");
            internalEventDrivenJobInstanceDto.setCommandLine("pwd\nls");
        }

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    @Test
    public void test_job_start_success_with_context_parameters() {
        ContextParameterInstanceImpl contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("cmd");
        contextParameterInstance.setDefaultValue("defaultValue");
        contextParameterInstance.setValue("echo test");

        enrichedContextualisedScheduledProcessEvent.setContextParameters(List.of(contextParameterInstance));
        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("echo \"END\" \t OF \"CMD - %cmd%\"");
        } else {
            internalEventDrivenJobInstanceDto.setCommandLine("source $HOME/.some_profile \necho \"some_command(\\\"code = 'SOME_VAR'\\\");\"\\n | echo \"TEST\" | grep -i 'test' | echo \\\"END\\\" \\\t OF \\\"CMD - $cmd\\\"");
        }

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());

        final EnrichedContextualisedScheduledProcessEvent finalEnrichedContextualisedScheduledProcessEvent = enrichedContextualisedScheduledProcessEvent;

        // reading the output file
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
            Assert.assertEquals("\"END\" \t OF \"CMD - echo test\"", loadDataFile(finalEnrichedContextualisedScheduledProcessEvent.getResultOutput()).trim()));
    }

    @Test
    public void test_job_start_success_with_null_context_parameters() {
        ContextParameterInstanceImpl contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("cmd");
        contextParameterInstance.setDefaultValue("defaultValue");
        contextParameterInstance.setValue(null);
        enrichedContextualisedScheduledProcessEvent.setContextParameters(List.of(contextParameterInstance));
        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("echo \"END\" \t OF \"CMD - %cmd%\"");
        } else {
            internalEventDrivenJobInstanceDto.setCommandLine("source $HOME/.some_profile \necho \"some_command(\\\"code = 'SOME_VAR'\\\");\"\\n | echo \"TEST\" | grep -i 'test' | echo \\\"END\\\" \\\t OF \\\"CMD - $cmd\\\"");
        }

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());

        EnrichedContextualisedScheduledProcessEvent finalEnrichedContextualisedScheduledProcessEvent = enrichedContextualisedScheduledProcessEvent;
        if (SystemUtils.OS_NAME.contains("Windows")) {
            Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                Assert.assertEquals("\"END\" \t OF \"CMD - %cmd%\"", loadDataFile(finalEnrichedContextualisedScheduledProcessEvent.getResultOutput()).trim()));
                // cmd - if parameter is null will interpret the env replace literally if env is not set
        } else {
            Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                Assert.assertEquals("\"END\" \t OF \"CMD - \"", loadDataFile(finalEnrichedContextualisedScheduledProcessEvent.getResultOutput()).trim()));
        }
    }

    @Test
    public void test_job_start_success_with_empty_value_context_parameters() {
        ContextParameterInstanceImpl contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("cmd");
        contextParameterInstance.setValue(""); // set to empty string, expecting this to change to a space based on config setEnvironmentToAddSpaceForEmptyContextParam
        enrichedContextualisedScheduledProcessEvent.setContextParameters(List.of(contextParameterInstance));
        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("echo Some%cmd%.Value");    
        } else {
            internalEventDrivenJobInstanceDto.setCommandLine("echo Some$cmd.Value");
        }

        JobStartingBrokerConfiguration configuration = getTestConfiguration();

        configuration.setEnvironmentToAddSpaceForEmptyContextParam(List.of("cmd.exe", "/bin/bash"));
        jobStartingBroker.setConfiguration(configuration);
        jobStartingBroker.setConfiguredResourceId("test");

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());

        final EnrichedContextualisedScheduledProcessEvent finalEnrichedContextualisedScheduledProcessEvent = enrichedContextualisedScheduledProcessEvent;
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> 
            Assert.assertEquals("Some .Value", loadDataFile(finalEnrichedContextualisedScheduledProcessEvent.getResultOutput()).trim()));
    }

    @Test
    public void test_job_start_when_recover_from_agent_crash_and_process_still_running() {
        final long pid = 999L;

        jobStartingBroker = new JobStartingBroker(schedulerPersistenceServiceMock);
        jobStartingBroker.setConfiguration(getTestConfiguration());
        jobStartingBroker.setConfiguredResourceId("test");

        when(processHandleMock.pid()).thenReturn(pid);
        when(schedulerPersistenceServiceMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(processHandleMock);
        when(schedulerPersistenceServiceMock.findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, pid, "me", outputLog, errorLog));

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess());
        Assert.assertEquals(999, enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(processHandleMock, enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcessHandle());

        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());

        verify(schedulerPersistenceServiceMock, times(1)).find(SCHEDULER_PROCESS_TYPE, IDENTITY);
        verify(schedulerPersistenceServiceMock, times(1)).findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY);
    }

    @Test(expected = EndpointException.class)
    public void test_exception_empty_command_line() {
        internalEventDrivenJobInstanceDto.setCommandLine("");

        ContextParameterInstanceImpl contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("cmd");
        contextParameterInstance.setDefaultValue("defaultValue");
        contextParameterInstance.setValue("echo test");
        enrichedContextualisedScheduledProcessEvent.setContextParameters(List.of(contextParameterInstance));

        jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);
    }

    @Test
    public void test_job_start_not_skipped_days_of_week_not_today() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        internalEventDrivenJobInstanceDto.setDaysOfWeekToRun(List.of(dayOfWeek == 1 ? dayOfWeek + 1 : dayOfWeek -1));

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(0, enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getDetachableProcess());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    @Test
    public void test_job_start_not_skipped_days_of_week_today() {
        internalEventDrivenJobInstanceDto.setDaysOfWeekToRun(List.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    @Test
    public void test_job_start_not_skipped_days_of_week_empty() {
        internalEventDrivenJobInstanceDto.setDaysOfWeekToRun(Collections.emptyList());

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    @Test
    public void test_job_start_not_skipped_days_of_week_null() {
        internalEventDrivenJobInstanceDto.setDaysOfWeekToRun(null);

        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_script.sh")));
        Assert.assertTrue((enrichedContextualisedScheduledProcessEvent.getExecutionDetails().contains(IDENTITY+"_results")));
        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getDetachableProcess().getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertTrue(enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertFalse(enrichedContextualisedScheduledProcessEvent.isDryRun());
        Assert.assertNotNull(enrichedContextualisedScheduledProcessEvent.getExecutionDetails());
    }

    private class ContextParameterInstanceImpl implements ContextParameterInstance {
        private String value;
        private String name;
        private String defaultValue;

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    protected String loadDataFile(String fileName) throws IOException
    {
        return IOUtils.toString(loadDataFileStream(fileName), StandardCharsets.UTF_8);
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return new FileInputStream(fileName);
    }
    
    protected static JobStartingBrokerConfiguration getTestConfiguration() {
        JobStartingBrokerConfiguration configuration = new JobStartingBrokerConfiguration();
        configuration.setEnvironmentToAddSpaceForEmptyContextParam(new ArrayList<>());
        return configuration;
    }
}
