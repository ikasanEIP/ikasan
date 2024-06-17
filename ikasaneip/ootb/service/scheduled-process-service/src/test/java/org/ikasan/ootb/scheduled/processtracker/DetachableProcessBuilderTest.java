package org.ikasan.ootb.scheduled.processtracker;


import org.ikasan.ootb.scheduled.processtracker.dao.ProcessStatusDao;
import org.ikasan.ootb.scheduled.processtracker.dao.SchedulerProcessPersistenceDao;
import org.ikasan.ootb.scheduled.processtracker.model.SchedulerIkasanProcess;
import org.ikasan.ootb.scheduled.processtracker.service.SchedulerDefaultPersistenceServiceImpl;
import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ikasan.ootb.scheduled.processtracker.DetachableProcessBuilder.SCHEDULER_PROCESS_TYPE;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DetachableProcessBuilderTest {
    private DetachableProcessBuilder detachableProcessBuilder;
    @Mock
    private SchedulerPersistenceService schedulerPersistenceServiceMock;
    private SchedulerPersistenceService schedulerPersistenceService;
    @Mock
    private SchedulerProcessPersistenceDao schedulerProcessPersistenceDaoMock;
    @Mock
    private ProcessStatusDao processStatusDaoMock;
    @Mock
    private ProcessHandle processHandleMock;
    @Mock
    private ProcessHandle.Info processHandleInfoMock;
    @Mock
    private ProcessBuilder processBuilderMock;
    @Mock
    private Process processMock;

    private static final String IDENTITY = "IdentityXYZ";
    private static final String[] COMMANDS = { "X", "Y" } ;
    private static final String STARDARD_OUTPUT_FILE = "OutputFileName";
    private static final String ERROR_FILE = "ErrortFileName";
    private static final long FIRE_TIME = 1L;

    private static final Long PROCESS_ID = 999L;
    private static final Long CHILD_PROCESS_ID = 998L;
    @Before
    public void setUp() {
        schedulerPersistenceService = new SchedulerDefaultPersistenceServiceImpl(schedulerProcessPersistenceDaoMock, processStatusDaoMock);
    }

    @Test
    public void if_the_underlying_process_had_previously_started_and_is_now_completedx() {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, PROCESS_ID, "David", STARDARD_OUTPUT_FILE, ERROR_FILE, FIRE_TIME);
        when(schedulerProcessPersistenceDaoMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(schedulerIkasanProcess, null);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, new ProcessBuilder(), COMMANDS, IDENTITY);

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isTrue();
        assertThat(detachableProcessBuilder.getInitialResultOutput()).isEqualTo(STARDARD_OUTPUT_FILE);
        assertThat(detachableProcessBuilder.getInitialErrorOutput()).isEqualTo(ERROR_FILE);
        assertThat(detachableProcessBuilder.getInitialFireTime()).isEqualTo(FIRE_TIME);
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isTrue();
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);
    }

    @Test
    public void if_the_underlying_process_had_previously_started_and_is_not_yet_completed() {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, PROCESS_ID, "David", STARDARD_OUTPUT_FILE, ERROR_FILE, FIRE_TIME);
        when(schedulerPersistenceServiceMock.findIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(schedulerIkasanProcess);
        when(schedulerPersistenceServiceMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(processHandleMock);
        when(processHandleMock.pid()).thenReturn(PROCESS_ID);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceServiceMock, new ProcessBuilder(), COMMANDS, IDENTITY);

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isTrue();
        assertThat(detachableProcessBuilder.getInitialResultOutput()).isEqualTo(STARDARD_OUTPUT_FILE);
        assertThat(detachableProcessBuilder.getInitialErrorOutput()).isEqualTo(ERROR_FILE);
        assertThat(detachableProcessBuilder.getInitialFireTime()).isEqualTo(FIRE_TIME);
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);
    }

    @Test
    public void if_the_underlying_process_has_not_yet_been_started() {
        when(schedulerProcessPersistenceDaoMock.find(SCHEDULER_PROCESS_TYPE, IDENTITY)).thenReturn(null);
        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, new ProcessBuilder(), COMMANDS, IDENTITY);

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isFalse();
        assertThat(detachableProcessBuilder.getInitialResultOutput()).isNull();
        assertThat(detachableProcessBuilder.getInitialErrorOutput()).isNull();
        assertThat(detachableProcessBuilder.getInitialFireTime()).isEqualTo(0);
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getPid()).isEqualTo(0);
    }

    @Test(expected = EndpointException.class)
    public void when_command_called_with_not_parameters_exception_should_be_thrown() {
        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, new ProcessBuilder(), COMMANDS, IDENTITY);
        detachableProcessBuilder.command(null);
    }

    @Test
    public void when_windows_command_called_with_valid_params_expect_them_to_be_set_on_internal_processBuilder() throws IOException {
        final String COMMAND = "dir";
        String[] inputCommands = {CommandProcessor.WINDOWS_POWSHELL.getName(), COMMAND};
        CommandProcessor cp = CommandProcessor.getCommandProcessor(inputCommands);

        when(processStatusDaoMock.createCommandScript(IDENTITY, CommandProcessor.WINDOWS_POWSHELL.getScriptFilePostfix(), COMMAND)).thenReturn("XX");
        when(processStatusDaoMock.getResultAbsoluteFilePath(IDENTITY)).thenReturn(STARDARD_OUTPUT_FILE);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, new ProcessBuilder(), inputCommands, IDENTITY);
        detachableProcessBuilder.setInitialErrorOutput("errorfile");
        detachableProcessBuilder.setInitialResultOutput("resultfile");
        detachableProcessBuilder.setInitialFireTime(2L);
        detachableProcessBuilder.command(COMMAND);

        List<String> commands = detachableProcessBuilder.getProcessBuilder().command();

        assertThat(commands.size()).isEqualTo(3);
        assertThat(commands.get(0)).isEqualTo(cp.getCommandArgs()[0]);
        assertThat(commands.get(1)).isEqualTo(cp.getCommandArgs()[1]);

        assertThat(commands.get(2)).isEqualTo(
            "Start-Process -FilePath Powershell -WindowStyle Hidden -RedirectStandardError \"errorfile\" -RedirectStandardOutput \"resultfile\" -PassThru -ArgumentList \"/c\", \"null\"");
    }

    @Test
    public void when_unix_command_called_with_valid_params_expect_them_to_be_set_on_internal_processBuilder() throws IOException {
        final String COMMAND = "ls";
        String[] inputCommands = {CommandProcessor.UNIX_BASH.getName(), COMMAND};
        CommandProcessor cp = CommandProcessor.getCommandProcessor(inputCommands);

        when(processStatusDaoMock.createCommandScript(IDENTITY, CommandProcessor.UNIX_BASH.getScriptFilePostfix(), COMMAND)).thenReturn("XX");
        when(processStatusDaoMock.getResultAbsoluteFilePath(IDENTITY)).thenReturn(STARDARD_OUTPUT_FILE);

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, new ProcessBuilder(), inputCommands, IDENTITY);
        detachableProcessBuilder.setInitialErrorOutput("errorfile");
        detachableProcessBuilder.setInitialResultOutput("resultfile");
        detachableProcessBuilder.setInitialFireTime(2L);
        detachableProcessBuilder.command(COMMAND);

        List<String> commands = detachableProcessBuilder.getProcessBuilder().command();

        assertThat(commands.size()).isEqualTo(3);
        assertThat(commands.get(0)).isEqualTo(cp.getCommandArgs()[0]);
        assertThat(commands.get(1)).isEqualTo(cp.getCommandArgs()[1]);

        assertThat(commands.get(2)).isEqualTo(
            "chmod +x XX\n" +
            " XX\n" +
            " RET=$?\n" +
            " echo $RET > OutputFileName\n" +
            " exit $RET");
    }

    @Test
    public void if_windows_process_started_note_it_will_always_be_detached() throws IOException {
        String[] commands = {CommandProcessor.WINDOWS_POWSHELL.getName()};
        CommandProcessor cp = CommandProcessor.getCommandProcessor(commands);
        when(processBuilderMock.start()).thenReturn(processMock);
        when(processMock.pid()).thenReturn(PROCESS_ID);
        when(processMock.toHandle()).thenReturn(processHandleMock);
        when(processMock.children()).thenReturn(Stream.of(processHandleMock));
        when(processHandleMock.pid()).thenReturn(CHILD_PROCESS_ID);
        when(processHandleMock.info()).thenReturn(processHandleInfoMock);
        when(processHandleInfoMock.user()).thenReturn(Optional.of("david"));

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, processBuilderMock, cp.getCommandArgs(), IDENTITY);
        detachableProcessBuilder.start();

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isTrue();
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getProcess()).isEqualTo(processMock);
        assertThat(detachableProcess.getProcessHandle()).isEqualTo(processHandleMock);
        assertThat(detachableProcess.getPid()).isEqualTo(CHILD_PROCESS_ID);
        Mockito.verify(schedulerProcessPersistenceDaoMock, times(1)).save(any());
    }

    @Test
    public void if_unix_process_is_not_detached_and_start_is_called_ensure_new_process_starts() throws IOException {
        String[] commands = {CommandProcessor.UNIX_BASH.getName()};
        CommandProcessor cp = CommandProcessor.getCommandProcessor(commands);
        when(processBuilderMock.start()).thenReturn(processMock);
        when(processMock.pid()).thenReturn(PROCESS_ID);
        when(processMock.toHandle()).thenReturn(processHandleMock);
        when(processHandleMock.info()).thenReturn(processHandleInfoMock);
        when(processHandleInfoMock.user()).thenReturn(Optional.of("david"));

        detachableProcessBuilder = new DetachableProcessBuilder(schedulerPersistenceService, processBuilderMock, cp.getCommandArgs(), IDENTITY);
        detachableProcessBuilder.start();

        DetachableProcess detachableProcess = detachableProcessBuilder.getDetachableProcess();

        assertThat(detachableProcess.isDetached()).isFalse();
        assertThat(detachableProcess.isDetachedAlreadyFinished()).isFalse();
        assertThat(detachableProcess.getProcess()).isEqualTo(processMock);
        assertThat(detachableProcess.getPid()).isEqualTo(PROCESS_ID);
        Mockito.verify(schedulerProcessPersistenceDaoMock, times(1)).save(any());
    }

    @Test
    public void if_unix_process_is_detached_and_start_is_called_ensure_new_process_is_not_created() throws IOException {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess(SCHEDULER_PROCESS_TYPE, IDENTITY, PROCESS_ID, "David", STARDARD_OUTPUT_FILE, ERROR_FILE, 1L);
        String[] commands = {CommandProcessor.UNIX_BASH.getName()};
        CommandProcessor cp = CommandProcessor.getCommandProcessor(commands);

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

        Mockito.verify(schedulerPersistenceServiceMock, times(0)).persist(any(), any(), any(ProcessHandle.class), any(), any(), anyLong());
    }
}
