package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.util.List;

public class InternalEventDrivenJobDto implements InternalEventDrivenJob {

    @Override
    public String getJobIdentifier() {
        return null;
    }

    @Override
    public void setJobIdentifier(String jobIdentifier) {

    }

    @Override
    public String getAgentName() {
        return null;
    }

    @Override
    public void setAgentName(String agentName) {

    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public void setJobName(String jobName) {

    }

    @Override
    public String getContextId() {
        return null;
    }

    @Override
    public void setContextId(String contextId) {

    }

    @Override
    public List<String> getSuccessfulReturnCodes() {
        return null;
    }

    @Override
    public void setSuccessfulReturnCodes(List<String> successfulReturnCodes) {

    }

    @Override
    public long getSecondsToWaitForProcessStart() {
        return 0;
    }

    @Override
    public void setSecondsToWaitForProcessStart(long secondsToWaitForProcessStart) {

    }

    @Override
    public String getWorkingDirectory() {
        return null;
    }

    @Override
    public void setWorkingDirectory(String workingDirectory) {

    }

    @Override
    public String getCommandLine() {
        return null;
    }

    @Override
    public void setCommandLine(String commandLine) {

    }

    @Override
    public String getStdErr() {
        return null;
    }

    @Override
    public void setStdErr(String stdErr) {

    }

    @Override
    public String getStdOut() {
        return null;
    }

    @Override
    public void setStdOut(String stdOut) {

    }

    @Override
    public boolean isRetryOnFail() {
        return false;
    }

    @Override
    public void setRetryOnFail(boolean retryOnFail) {

    }
}
