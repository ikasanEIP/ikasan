package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.util.List;

public class InternalEventDrivenJobDto implements InternalEventDrivenJob {

    @Override
    public List<String> getSuccessfulReturnCodes() {
        return null;
    }

    @Override
    public void setSuccessfulReturnCodes(List<String> successfulReturnCodes) {

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
    public long getMinExecutionTime() {
        return 0;
    }

    @Override
    public void setMinExecutionTime(long minExecutionTime) {

    }

    @Override
    public long getMaxExecutionTime() {
        return 0;
    }

    @Override
    public void setMaxExecutionTime(long maxExecutionTime) {

    }

    @Override
    public List<ContextParameter> getContextParameters() {
        return null;
    }

    @Override
    public void setContextParameters(List<ContextParameter> contextParameters) {

    }

    @Override
    public String getContextId() {
        return null;
    }

    @Override
    public void setContextId(String contextId) {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public void setIdentifier(String jobIdentifier) {

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
    public String getJobDescription() {
        return null;
    }

    @Override
    public void setJobDescription(String jobDescription) {

    }
}
