package org.ikasan.spec.scheduled;

public interface ScheduledProcessEvent
{
    String getAgentName();

    void setAgentName(String agentName);

    String getJobName() ;

    void setJobName(String jobName);

    String getJobDescription() ;

    void setJobDescription(String jobDescription);

    String getJobGroup();

    void setJobGroup(String jobGroup);

    String getCommandLine();

    void setCommandLine(String commandLine);

    int getReturnCode();

    void setReturnCode(int result);

    boolean isSuccessful();

    void setSuccessful(boolean successful);

    String getResultOutput();

    void setResultOutput(String resultOutput);

    String getResultError();

    void setResultError(String resultError);

    long getPid();

    void setPid(long pid);

    String getUser();

    void setUser(String user);

    long getFireTime();

    void setFireTime(long fireTime);

    long getNextFireTime();

    void setNextFireTime(long nextFireTime);

    long getCompletionTime();

    void setCompletionTime(long completionTime);
}
