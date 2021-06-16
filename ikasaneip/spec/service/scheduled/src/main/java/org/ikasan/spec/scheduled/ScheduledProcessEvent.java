package org.ikasan.spec.scheduled;

public interface ScheduledProcessEvent {
    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName() ;

    public void setJobName(String jobName);

    public String getJobDescription() ;

    public void setJobDescription(String jobDescription);

    public String getJobGroup();

    public void setJobGroup(String jobGroup);

    public String getCommandLine();

    public void setCommandLine(String commandLine);

    public int getResult();

    public void setResult(int result) ;

    public String getResultOutput();

    public void setResultOutput(String resultOutput);

    public String getResultError();

    public void setResultError(String resultError);

    public long getPid();

    public void setPid(long pid);

    public String getUser();

    public void setUser(String user);

    public long getFireTime();

    public void setFireTime(long fireTime);

    public long getNextFireTime();

    public void setNextFireTime(long nextFireTime);
}
