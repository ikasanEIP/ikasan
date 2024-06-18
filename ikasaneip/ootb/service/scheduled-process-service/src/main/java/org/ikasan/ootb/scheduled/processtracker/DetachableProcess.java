package org.ikasan.ootb.scheduled.processtracker;

import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;

import java.io.IOException;

public class DetachableProcess {
    private final SchedulerPersistenceService schedulerPersistenceService;
    private final String identity;
    private Process process;
    private ProcessHandle processHandle;
    private boolean detached;                   // The Process is now detached, only ProcessHandle information is available
    private boolean detachedAlreadyFinished;    // The Process is now detached and has already completed.
    private long pid;
    private final CommandProcessor commandProcessor;
    public DetachableProcess(SchedulerPersistenceService schedulerPersistenceService, String identity, CommandProcessor commandProcessor) {
        this.schedulerPersistenceService = schedulerPersistenceService;
        this.identity = identity;
        this.commandProcessor = commandProcessor;
    }

    public String getReturnCode() {
        String returnCode ;
        if (isDetached()) {
            returnCode =  schedulerPersistenceService.getPersistedReturnCode(identity);
        } else {
            int numericValue = process.exitValue();
            returnCode = "" + numericValue;
        }
        return returnCode;
    }

    public ProcessHandle.Info getInfo() {
        ProcessHandle.Info info;
        if (isDetached()) {
            info = getProcessHandle().info();
        } else {
            info = process.info();
        }
        return info;
    }

    public void removePersistedProcessData() throws IOException {
        schedulerPersistenceService.removeAll(identity);
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public ProcessHandle getProcessHandle() {
        return processHandle;
    }

    public void setProcessHandle(ProcessHandle processHandle) {
        this.processHandle = processHandle;
    }

    public boolean isDetached() {
        return detached;
    }

    public void setDetached(boolean detached) {
        this.detached = detached;
    }

    public String getIdentity() {
        return identity;
    }

    public boolean isDetachedAlreadyFinished() {
        return detachedAlreadyFinished;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public void setDetachedAlreadyFinished(boolean detachedAlreadyFinished) {
        this.detachedAlreadyFinished = detachedAlreadyFinished;
    }

    @Override
    public String toString() {
        return "DetachableProcess{" +
            "schedulerPersistenceService=" + schedulerPersistenceService +
            ", identity='" + identity + '\'' +
            ", process=" + process +
            ", processHandle=" + processHandle +
            ", detached=" + detached +
            ", detachedAlreadyFinished=" + detachedAlreadyFinished +
            ", pid=" + pid +
            ", commandProcessor=" + commandProcessor +
            '}';
    }
}
