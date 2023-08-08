package org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker;

import org.apache.commons.lang3.StringUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.model.SchedulerIkasanProcess;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.service.SchedulerPersistenceService;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class extends the behaviour of processBuilder in order to expose the return status of a process that
 * is independent from java (e.g. was invoked by a different java process or has become detached from the Java process
 * that created it).
 *
 * This is achieved by taking the commands that would have been directly executed by processBuilder, bundling them
 * up in a script, then executing the script and redirecting the return value to file system file, so that it is
 * persisted even if the calling Java code terminates.
 *
 * It currently supports the command processors on Unix and Windows hosts.
 */
public class DetachableProcessBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetachableProcessBuilder.class);
    public static final String SCHEDULER_PROCESS_TYPE = "scheduler";
    private final ProcessBuilder processBuilder;
    private String initialResultOutput;
    private String initialErrorOutput;

    private String commandsToBeExecuted = "";
    private final SchedulerPersistenceService schedulerPersistenceService;

    private final DetachableProcess detachableProcess;

    /**
     * Create th e DetachableProcessBuilder
     * @param schedulerPersistenceService used to persist script and exit status
     * @param processBuilder instance, preferrably a new instance.
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
            if (processHandle != null) {
                LOGGER.info("Starting Broker Monitoring pre-existing process");
                detachableProcess.setProcessHandle(processHandle);
                detachableProcess.setPid(processHandle.pid());
            } else {
                LOGGER.info("Starting Broker Monitoring but pre-existing process is already complete");
                detachableProcess.setDetachedAlreadyFinished(true);
                detachableProcess.setPid(preExistingIkasanProcess.getPid());
            }
            detachableProcess.setProcess(null); // for detached processes, we need to use the processHandle and not the process.
        }
    }

    /**
     * This is similar to the processBuilder command method.
     * @param commandsToBeExecuted will be prepared ready for starting
     */
    public void command(String commandsToBeExecuted)
    {
        if (StringUtils.isBlank(commandsToBeExecuted)) {
            throw new EndpointException("Invalid commandLine [" + commandsToBeExecuted + "]");
        }
        this.commandsToBeExecuted = commandsToBeExecuted;
        String commandScript;
        try {
            commandScript = schedulerPersistenceService.createCommandScript(detachableProcess.getIdentity(), detachableProcess.getCommandProcessor().getScriptFilePostfix(), this.commandsToBeExecuted);
        } catch (IOException e) {
            throw new EndpointException(e);
        }
        String processExitStatusFile = schedulerPersistenceService.getResultAbsoluteFilePath(detachableProcess.getIdentity());
        List<String> commands = getCommandString(commandScript, processExitStatusFile);
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
     * @param commandLineScriptName to be executed
     * @param processExitStatusFile to hold the exit status of the executed command
     * @return a list, including command process startup, that can be used in a processBuilder.command method.
     */
    private List<String> getCommandString(String commandLineScriptName, String processExitStatusFile) {
        List<String> commands = new ArrayList<>();
        switch(detachableProcess.getCommandProcessor()) {
            case WINDOWS_POWSHELL:
                commands.addAll(Arrays.asList(detachableProcess.getCommandProcessor().getCommandArgs()));
                commands.add("& " + commandLineScriptName+ "\r\n $RET=$LASTEXITCODE\r\n set-content -Encoding \"utf8\" " + processExitStatusFile + " $RET \r\n exit $RET");
                break;
            case WINDOWS_CMD:
                commands.addAll(Arrays.asList(CommandProcessor.WINDOWS_POWSHELL.getCommandArgs()));
                commands.add("& " + commandLineScriptName+ "\r\n $RET=$LASTEXITCODE\r\n set-content -Encoding \"utf8\" " + processExitStatusFile + " $RET \r\n exit $RET");
                break;
            default: // Bash is deemed the default
                commands.addAll(Arrays.asList(detachableProcess.getCommandProcessor().getCommandArgs()));
                commands.add("chmod +x " + commandLineScriptName + "\n " + commandLineScriptName+ "\n RET=$?\n echo $RET > " + processExitStatusFile + "\n exit $RET");
        }
        LOGGER.info("About to execute command files [" + commandLineScriptName + "] return value going to [" + processExitStatusFile + "] with command string [" + commands + "]");
        return commands;
    }


    /*
     * This extends the processBuilder.start method to support the detachable functionality
     * @return A DetachableProcess which holds information about the process that has been started.
     * @throws IOException when io errors occur.
     */
    public DetachableProcess start() throws IOException {
        if (!detachableProcess.isDetached()) {
            Process process = processBuilder.start();
            detachableProcess.setDetached(false);
            detachableProcess.setDetachedAlreadyFinished(false);
            detachableProcess.setProcess(process);
            detachableProcess.setPid(process.pid());
            schedulerPersistenceService.persist(SCHEDULER_PROCESS_TYPE,
                detachableProcess.getIdentity(),
                process,
                initialResultOutput, initialErrorOutput);
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

    public void setInitialErrorOutput(String initialErrorOutput) {
        this.initialErrorOutput = initialErrorOutput;
    }

    public String getInitialResultOutput() {
        return initialResultOutput;
    }

    public String getInitialErrorOutput() {
        return initialErrorOutput;
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

}
