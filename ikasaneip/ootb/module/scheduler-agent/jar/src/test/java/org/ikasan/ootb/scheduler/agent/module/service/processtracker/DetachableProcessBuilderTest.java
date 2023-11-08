package org.ikasan.ootb.scheduler.agent.module.service.processtracker;

import org.ikasan.ootb.scheduler.agent.module.service.processtracker.CommandProcessor;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcess;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcessBuilder;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.model.SchedulerIkasanProcess;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.service.SchedulerPersistenceService;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcessBuilder.SCHEDULER_PROCESS_TYPE;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DetachableProcessBuilderTest {
    private DetachableProcessBuilder detachableProcessBuilder;
    @Mock
    private SchedulerPersistenceService schedulerPersistenceServiceMock;
    @Mock
    private ProcessHandle processHandleMock;
    @Mock
    private ProcessBuilder processBuilderMock;
    @Mock
    private Process processMock;

    private static final String IDENTITY = "IdentityXYZ";
    private static final String[] COMMANDS = { "X", "Y" } ;
    private static final String STARDARD_OUTPUT_FILE = "OutputFileName";
    private static final String ERROR_FILE = "ErrortFileName";

    private static final Long PROCESS_ID = 999L;

    @Test
    public void if_the_underlying_process_had_previously_started_and_is_now_completed() {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, PROCESS_ID, "David", STARDARD_OUTPUT_FILE, ERROR_FILE);

        when(schedulerPersistenceServiceMock.findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(schedulerIkasanProcess);
        when(schedulerPersistenceServiceMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(null);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, new ProcessBuilder(), COMMANDS, IDENTITY);

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isTrue();
        assertThat(detachableProcessBuilder.getInitialResultOutput()).isEqualTo(STARDARD_OUTPUT_FILE);
        assertThat(detachableProcessBuilder.getInitialErrorOutput()).isEqualTo(ERROR_FILE);
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isTrue();
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);
    }

    @Test
    public void if_the_underlying_process_had_previously_started_and_is_not_yet_completed() {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, PROCESS_ID, "David", STARDARD_OUTPUT_FILE, ERROR_FILE);

        when(schedulerPersistenceServiceMock.findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(schedulerIkasanProcess);
        when(schedulerPersistenceServiceMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(processHandleMock);
        when(processHandleMock.pid()).thenReturn(PROCESS_ID);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, new ProcessBuilder(), COMMANDS, IDENTITY);

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isTrue();
        assertThat(detachableProcessBuilder.getInitialResultOutput()).isEqualTo(STARDARD_OUTPUT_FILE);
        assertThat(detachableProcessBuilder.getInitialErrorOutput()).isEqualTo(ERROR_FILE);
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);
    }

    @Test
    public void if_the_underlying_process_has_not_yet_been_started() {
        when(schedulerPersistenceServiceMock.findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(null);
        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, new ProcessBuilder(), COMMANDS, IDENTITY);

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isFalse();
        assertThat(detachableProcessBuilder.getInitialResultOutput()).isNull();
        assertThat(detachableProcessBuilder.getInitialErrorOutput()).isNull();
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getPid()).isEqualTo(0);
    }

    @Test(expected = EndpointException.class)
    public void when_command_called_with_not_parameters_exception_should_be_thrown() {

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, new ProcessBuilder(), COMMANDS, IDENTITY);
        detachableProcessBuilder.command(null);
    }

    @Test
    public void when_command_called_with_valid_params_expect_them_to_be_set_on_internal_processBuilder() throws IOException {
        final String COMMAND = "dir";
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);
        when(schedulerPersistenceServiceMock.createCommandScript(IDENTITY, cp.getScriptFilePostfix(), COMMAND)).thenReturn("XX");
        when(schedulerPersistenceServiceMock.getResultAbsoluteFilePath(IDENTITY)).thenReturn(STARDARD_OUTPUT_FILE);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, new ProcessBuilder(), COMMANDS, IDENTITY);
        detachableProcessBuilder.command(COMMAND);

        List<String> commands = detachableProcessBuilder.getProcessBuilder().command();

        assertThat(commands.size()).isEqualTo(3);
        assertThat(commands.get(0)).isEqualTo(cp.getCommandArgs()[0]);
        assertThat(commands.get(1)).isEqualTo(cp.getCommandArgs()[1]);
        if (cp.equals(CommandProcessor.UNIX_BASH)) {
            assertThat(commands.get(2)).isEqualTo(
                """
                chmod +x XX
                 XX
                 RET=$?
                 echo $RET > OutputFileName
                 exit $RET\
                """);
        } else {
            assertThat(commands.get(2)).isEqualTo(
                """
                & XX
                 $RET=$LASTEXITCODE
                 set-content -Encoding "utf8" OutputFileName $RET 
                 exit $RET\
                """);
        }
    }

    @Test
    public void if_process_is_not_detached_and_start_is_called_ensure_new_process_starts() throws IOException {
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);

        when(processBuilderMock.start()).thenReturn(processMock);
        when(processMock.pid()).thenReturn(PROCESS_ID);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, processBuilderMock, cp.getCommandArgs(), IDENTITY);
        detachableProcessBuilder.start();

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isFalse();
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getProcess()).isEqualTo(processMock);
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);

        Mockito.verify(schedulerPersistenceServiceMock, times(1)).persist(any(), any(), any(), any(), any());
    }

    @Test
    public void if_process_is_detached_and_start_is_called_ensure_new_process_is_not_created() throws IOException {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, PROCESS_ID, "David", STARDARD_OUTPUT_FILE, ERROR_FILE);
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);
        when(schedulerPersistenceServiceMock.findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(schedulerIkasanProcess);
        when(schedulerPersistenceServiceMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(processHandleMock);
        when(processHandleMock.pid()).thenReturn(PROCESS_ID);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, processBuilderMock, cp.getCommandArgs(), IDENTITY);
        detachableProcessBuilder.start();

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isTrue();
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getProcessHandle()).isEqualTo(processHandleMock);
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);

        Mockito.verify(schedulerPersistenceServiceMock, times(0)).persist(any(), any(), any(), any(), any());
    }
}
