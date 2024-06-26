package org.ikasan.ootb.scheduled.processtracker;

import org.apache.commons.lang3.StringUtils;
import org.ikasan.ootb.scheduled.processtracker.model.SchedulerIkasanProcess;
import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class extends the behaviour of org.ikasan.ootb.scheduler.agent.module.component.broker.processBuilder
 * Its aim is to start the Command Execution Job on the target OS, if the invoker
 * crashed, the process would still continue and leave its error/output/return status in such a way that
 * the invoker could determine success/failure when the invoker was restarted.
 * This is achieved by taking the commands that would have been directly executed by processBuilder, bundling them
 * up in a script, then executing the script and redirecting the return value to file system file, so that it is
 * persisted.
 * It currently supports the command processors on Unix and Windows hosts.
 */
public class DetachableProcessBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetachableProcessBuilder.class);
    public static final String SCHEDULER_PROCESS_TYPE = "scheduler";
    private final ProcessBuilder processBuilder;
    private String initialResultOutput;
    private String initialErrorOutput;

    private long initialFireTime;

    private String commandsToBeExecuted = "";
    private final SchedulerPersistenceService schedulerPersistenceService;

    private final DetachableProcess detachableProcess;

    /**
     * The constructor first looks to see if we are in recovery i.e. there is a serialised DetachableProcess
     * If there is a serialised DetachableProcess, it will be used, otherwise a new one is created.
     * @param schedulerPersistenceService used to serialise and deserialise a DetachableProcess.
     * @param processBuilder instance, preferably a new instance.
     * @param commandProcessorArguments to be executed
     * @param identity of this process
     */
    public DetachableProcessBuilder(SchedulerPersistenceService schedulerPersistenceService, ProcessBuilder processBuilder, String[] commandProcessorArguments, String identity) {
        this.processBuilder = processBuilder;

        this.detachableProcess = new DetachableProcess(schedulerPersistenceService,
            identity, CommandProcessor.getCommandProcessor(commandProcessorArguments));
        this.schedulerPersistenceService = schedulerPersistenceService;
        // Check to see if this process started previously
        SchedulerIkasanProcess preExistingIkasanProcess = (SchedulerIkasanProcess) schedulerPersistenceService
            .findIkasanProcess(SCHEDULER_PROCESS_TYPE, detachableProcess.getIdentity());

        if (preExistingIkasanProcess != null) {
            ProcessHandle processHandle = schedulerPersistenceService.find(SCHEDULER_PROCESS_TYPE, detachableProcess.getIdentity());
            detachableProcess.setDetached(true);
            initialResultOutput = preExistingIkasanProcess.getResultOutput();
            initialErrorOutput = preExistingIkasanProcess.getErrorOutput();
            initialFireTime = preExistingIkasanProcess.getFireTme();
            if (processHandle != null) {
                detachableProcess.setProcessHandle(processHandle);
                detachableProcess.setPid(processHandle.pid());
                LOGGER.info("DetachableProcessBuilder created using pre-existing process. Identity was[" + identity +
                    " detachable process[" + detachableProcess + "]");
            } else {
                detachableProcess.setDetachedAlreadyFinished(true);
                detachableProcess.setPid(preExistingIkasanProcess.getPid());
                LOGGER.info("DetachableProcessBuilder created using pre-existing process, the process is already complete. Identity was[" + identity +
                    " detachable process[" + detachableProcess + "]");
            }
            detachableProcess.setProcess(null); // for detached processes, we need to use the processHandle and not the process.
        } else {
            LOGGER.info("DetachableProcessBuilder created as new - NO pre-existing process. Identity was[" + identity +
                "] detachable process[" + detachableProcess + "]");
        }
    }

    /**
     * This is similar to the processBuilder command method.
     * @param commandsToBeExecuted will be prepared ready for starting
     */
    public void command(String commandsToBeExecuted)
    {
        isInitialised("Attempt to set commands to be executed " + commandsToBeExecuted);

        if (StringUtils.isBlank(commandsToBeExecuted)) {
            throw new EndpointException("Invalid commandLine[" + commandsToBeExecuted + "]");
        }
        this.commandsToBeExecuted = commandsToBeExecuted;
        String commandScriptPath;

        try {
            commandScriptPath = schedulerPersistenceService.createCommandScript(detachableProcess.getIdentity(), detachableProcess.getCommandProcessor().getScriptFilePostfix(), this.commandsToBeExecuted);
        } catch (IOException e) {
            throw new EndpointException(e);
        }
        String processExitStatusFile = schedulerPersistenceService.getResultAbsoluteFilePath(detachableProcess.getIdentity());

        // For Windows, we need to wrap the command in another Powershell script because start-process doesn't support script blocks.
        if (detachableProcess.getCommandProcessor().equals(CommandProcessor.WINDOWS_CMD) || detachableProcess.getCommandProcessor().equals(CommandProcessor.WINDOWS_POWSHELL)) {
            String wrapperScriptCommands = getWindowsWrapperCommands(commandScriptPath, processExitStatusFile);
            try {
                commandScriptPath = schedulerPersistenceService.createCommandWrapperScript(detachableProcess.getIdentity(), CommandProcessor.WINDOWS_POWSHELL.getScriptFilePostfix(), wrapperScriptCommands);
            } catch (IOException e) {
                throw new EndpointException(e);
            }
            LOGGER.info("Windows detachable process wrapper script[" + commandScriptPath + "] set to[" + wrapperScriptCommands + "]");
        }

        List<String> commands = getCommandString(commandScriptPath, processExitStatusFile);
        processBuilder.command(commands);
    }


    /**
     * Returns this process builder's operating system program and arguments.
     * The returned list IS a copy.
     */
    public List<String> command() {
        return new ArrayList<>(processBuilder.command());
    }

    /**
     * Create a list of operating system specific commands that will execute the supplied script and redirect the exit
     * status to supplied processExitStatusFile
     * NOTE both InitialResultOutput and InitialErrorOutput must already be set before invoking this method.
     * @param commandLineScriptName to be executed
     * @param processExitStatusFile to hold the exit status of the executed command
     * @return a list, including command process startup, that can be used in a processBuilder command method.
     */
    private List<String> getCommandString(String commandLineScriptName, String processExitStatusFile) {
        isInitialised("Attempt to set command " + commandLineScriptName);
        List<String> commands = new ArrayList<>();
        if (detachableProcess.getCommandProcessor().equals(CommandProcessor.WINDOWS_CMD) || detachableProcess.getCommandProcessor().equals(CommandProcessor.WINDOWS_POWSHELL)) {
            commands.addAll(Arrays.asList(CommandProcessor.WINDOWS_POWSHELL.getCommandArgs()));
            commands.add("Start-Process -FilePath Powershell -WindowStyle Hidden -RedirectStandardError \"" + getInitialErrorOutput() + "\" -RedirectStandardOutput \"" + getInitialResultOutput() + "\" -PassThru -ArgumentList \"/c\", " +
                "\"" + commandLineScriptName + "\"");
        } else {
            commands.addAll(Arrays.asList(detachableProcess.getCommandProcessor().getCommandArgs()));
            commands.add("chmod +x " + commandLineScriptName + "\n " + commandLineScriptName+ "\n RET=$?\n echo $RET > " + processExitStatusFile + "\n exit $RET");
        }
        LOGGER.info("About to execute command files [" + commandLineScriptName + "] return value going to [" + processExitStatusFile + "] with command string [" + commands + "]");
        return commands;
    }

    /**
     * For powershell, we need a wrapper around getCommandString() in order to safely store the output/error/exit code.
     * The exit code capture (below) looks a little clunky, it is a workaround for known Powershell issue
     * <a href="https://github.com/PowerShell/PowerShell/issues/20400#issuecomment-1740954070">Powershell exit code</a>
     * @param commandScript to be executed
     * @param processExitStatusFile that will be populated with the exit status
     * @return the commands for the wrapper script, as a string
     */
    private String getWindowsWrapperCommands(String commandScript, String processExitStatusFile) {
        StringBuilder wrapperCommands = new StringBuilder();
        wrapperCommands.append("$p=Start-Process -FilePath Powershell -WindowStyle Hidden -RedirectStandardError \"" + this.getInitialErrorOutput() + "\" -RedirectStandardOutput \"" + this.getInitialResultOutput() + "\" -PassThru -ArgumentList \"/c\", \"" + commandScript + "\" -Wait \r\n");
        wrapperCommands.append("$file=\"" + this.getInitialErrorOutput()+"\" \r\n");
        wrapperCommands.append("if ($p.ExitCode -ne 0) { \r\n");
        wrapperCommands.append("    set-content -Encoding \"utf8\" " + processExitStatusFile + " $p.ExitCode \r\n");
        wrapperCommands.append("} else { \r\n");
        wrapperCommands.append("    if (Test-Path $file) { \r\n");
        wrapperCommands.append("        $fileSize = (Get-Item $file).length  \r\n");
        wrapperCommands.append("        if ($fileSize -gt 0) {  \r\n");
        wrapperCommands.append("            set-content -Encoding \"utf8\" " + processExitStatusFile + " '-1' \r\n");
        wrapperCommands.append("        } else { \r\n");
        wrapperCommands.append("            set-content -Encoding \"utf8\" " + processExitStatusFile + " '0' \r\n");
        wrapperCommands.append("        } \r\n");
        wrapperCommands.append("    } else { \r\n");
        wrapperCommands.append("        set-content -Encoding \"utf8\" " + processExitStatusFile + " '-2' \r\n");
        wrapperCommands.append("    } \r\n");
        wrapperCommands.append("} \r\n");
        return wrapperCommands.toString();
    }


    /**
     * Ideally all dependents would be in the constructor but not all are known at time of construction.
     * Therefore, any actions that require these dependents to be set will be checked here
     * @param idenitityMessage use to populate the message string to reflect the caller.
     */
    private void isInitialised(String idenitityMessage) {
        if (getInitialResultOutput() == null || getInitialResultOutput().isEmpty() || getInitialErrorOutput() == null || getInitialErrorOutput().isEmpty()) {
            throw new EndpointException(idenitityMessage + " when output [" + getInitialResultOutput() + "] or error " + getInitialErrorOutput() + "] were not set");
        }
    }

    /*
     * This extends the processBuilder.start method to support the detachable functionality
     * @return A DetachableProcess which holds information about the process that has been started.
     * @throws IOException when io errors occur.
     */
    public DetachableProcess start() throws IOException {
        if (!detachableProcess.isDetached()) {
            Process process = processBuilder.start();
            long pidToTrack = process.pid();
            ProcessHandle processHandleToPersist = process.toHandle();

            // For windows, always the process to track is the first child, not the root process
            if (detachableProcess.getCommandProcessor().equals(CommandProcessor.WINDOWS_CMD) ||
                detachableProcess.getCommandProcessor().equals(CommandProcessor.WINDOWS_POWSHELL)) {
                try {
                    // Need to give the command processor enough time to spawn children
                    Thread.sleep(5000); // 5 seconds
                } catch (InterruptedException e) {
                    LOGGER.warn("Sleep interrupted while waiting for process to start, may result in script not found");
                }
                Optional<ProcessHandle> potentialChild = process.children().findFirst();
                if (potentialChild.isPresent()) {
                    ProcessHandle child = potentialChild.get();
                    pidToTrack = child.pid();
                    processHandleToPersist = child;
                    // For Windows, we need to track the child, not the root, we can't get the child 'process' so
                    // we use the processHandle, i.e. we use 'detached' mode.
                    detachableProcess.setDetached(true);
                    detachableProcess.setProcessHandle(child);
                } else {
                    LOGGER.error("Could not get child process for detachable pprocess " + detachableProcess +
                        " fall back to parent process, which may report false positive.");
                }
            } else {
                detachableProcess.setDetached(false);
            }

            detachableProcess.setDetachedAlreadyFinished(false);
            detachableProcess.setProcess(process);
            detachableProcess.setPid(pidToTrack);
            schedulerPersistenceService.persist(SCHEDULER_PROCESS_TYPE, detachableProcess.getIdentity(),
                processHandleToPersist, initialResultOutput, initialErrorOutput, initialFireTime);
            LOGGER.info("The process " + getDetachableProcess().getIdentity() +
                " was started with pid " + getDetachableProcess().getPid() + " using processBuilder " + processBuilder);
        } else {
            LOGGER.info("The process " + getDetachableProcess().getIdentity() +
                " was already running, re-attaching to pid " + getDetachableProcess().getPid() + " rather than starting a new one");
        }
        return detachableProcess;
    }

    public DetachableProcess getDetachableProcess() {
        return detachableProcess;
    }
    public void setInitialResultOutput(String initialResultOutput) {
        this.initialResultOutput = initialResultOutput;
    }
    public String getInitialResultOutput() {
        return initialResultOutput;
    }

    public void setInitialErrorOutput(String initialErrorOutput) {
        this.initialErrorOutput = initialErrorOutput;
    }
    public String getInitialErrorOutput() {
        return initialErrorOutput;
    }
    public long getInitialFireTime() {
        return initialFireTime;
    }
    public void setInitialFireTime(long initialFireTime) {
        this.initialFireTime = initialFireTime;
    }

    public String getScriptFilePath() {
        return schedulerPersistenceService.getScriptFilePath(detachableProcess.getIdentity(), detachableProcess.getCommandProcessor().getScriptFilePostfix());
    }

    public void directory(File directory) {
        this.processBuilder.directory(directory);
    }

    public void redirectOutput(File file) {
        this.processBuilder.redirectOutput(file);
    }

    public void redirectError(File file) {
        this.processBuilder.redirectError(file);
    }

    public Map<String, String> environment() {
        return processBuilder.environment();
    }

    protected ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }


    @Override
    public String toString() {
        return "DetachableProcessBuilder{" +
            "processBuilder=" + processBuilder +
            ", initialResultOutput='" + initialResultOutput + '\'' +
            ", initialErrorOutput='" + initialErrorOutput + '\'' +
            ", initialFireTime=" + initialFireTime +
            ", commandsToBeExecuted='" + commandsToBeExecuted + '\'' +
            ", detachableProcess=" + detachableProcess +
            '}';
    }
}