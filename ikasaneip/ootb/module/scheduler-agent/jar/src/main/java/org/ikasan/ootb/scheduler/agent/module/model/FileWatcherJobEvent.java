package org.ikasan.ootb.scheduler.agent.module.model;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.spec.scheduled.event.model.Outcome;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class FileWatcherJobEvent implements Serializable {
    private String contextName;
    private String jobName;
    private List<String> childContextNames;
    private String correlationIdentifier;
    private String filePath;
    private String filename;
    private String fileNameSpelExpression;
    private String filePathSpelExpression;
    private String moveDirectory;
    private String moveDirectorySpelExpression;
    private int minFileAgeSeconds = 0;
    /** cron expression on expected time of file availability */
    private String slaCronExpression;
    private String timeZone;
    private List<String> blackoutWindowCronExpressions;
    private Map<String,String> blackoutWindowDateTimeRanges;
    private CorrelatedFileList correlatedFileList;
    private long fireTime;
    private long nextFireTime;
    private String jobGroup;
    private String jobDescription;
    private boolean dryRun;
    private String outcome;

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public List<String> getChildContextNames() {
        return childContextNames;
    }

    public void setChildContextNames(List<String> childContextNames) {
        this.childContextNames = childContextNames;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCorrelationIdentifier() {
        return correlationIdentifier;
    }

    public void setCorrelationIdentifier(String correlationIdentifier) {
        this.correlationIdentifier = correlationIdentifier;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileNameSpelExpression() {
        return fileNameSpelExpression;
    }

    public void setFileNameSpelExpression(String fileNameSpelExpression) {
        this.fileNameSpelExpression = fileNameSpelExpression;
    }

    public String getFilePathSpelExpression() {
        return filePathSpelExpression;
    }

    public void setFilePathSpelExpression(String filePathSpelExpression) {
        this.filePathSpelExpression = filePathSpelExpression;
    }

    public String getMoveDirectory() {
        return moveDirectory;
    }

    public void setMoveDirectory(String moveDirectory) {
        this.moveDirectory = moveDirectory;
    }

    public String getMoveDirectorySpelExpression() {
        return moveDirectorySpelExpression;
    }

    public void setMoveDirectorySpelExpression(String moveDirectorySpelExpression) {
        this.moveDirectorySpelExpression = moveDirectorySpelExpression;
    }

    public int getMinFileAgeSeconds() {
        return minFileAgeSeconds;
    }

    public void setMinFileAgeSeconds(int minFileAgeSeconds) {
        this.minFileAgeSeconds = minFileAgeSeconds;
    }

    public String getSlaCronExpression() {
        return slaCronExpression;
    }

    public void setSlaCronExpression(String slaCronExpression) {
        this.slaCronExpression = slaCronExpression;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<String> getBlackoutWindowCronExpressions() {
        return blackoutWindowCronExpressions;
    }

    public void setBlackoutWindowCronExpressions(List<String> blackoutWindowCronExpressions) {
        this.blackoutWindowCronExpressions = blackoutWindowCronExpressions;
    }

    public Map<String, String> getBlackoutWindowDateTimeRanges() {
        return blackoutWindowDateTimeRanges;
    }

    public void setBlackoutWindowDateTimeRanges(Map<String, String> blackoutWindowDateTimeRanges) {
        this.blackoutWindowDateTimeRanges = blackoutWindowDateTimeRanges;
    }

    public CorrelatedFileList getCorrelatedFileList() {
        return correlatedFileList;
    }

    public void setCorrelatedFileList(CorrelatedFileList correlatedFileList) {
        this.correlatedFileList = correlatedFileList;
    }

    public long getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public long getFireTime() {
        return fireTime;
    }

    public void setFireTime(long fireTime) {
        this.fireTime = fireTime;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FileWatcherJobEvent.class.getSimpleName() + "[", "]")
            .add("contextName='" + contextName + "'")
            .add("jobName='" + jobName + "'")
            .add("childContextNames=" + childContextNames)
            .add("correlationIdentifier='" + correlationIdentifier + "'")
            .add("filePath='" + filePath + "'")
            .add("filename='" + filename + "'")
            .add("fileNameSpelExpression='" + fileNameSpelExpression + "'")
            .add("filePathSpelExpression='" + filePathSpelExpression + "'")
            .add("moveDirectory='" + moveDirectory + "'")
            .add("moveDirectorySpelExpression='" + moveDirectorySpelExpression + "'")
            .add("minFileAgeSeconds=" + minFileAgeSeconds)
            .add("slaCronExpression='" + slaCronExpression + "'")
            .add("timeZone='" + timeZone + "'")
            .add("blackoutWindowCronExpressions=" + blackoutWindowCronExpressions)
            .add("blackoutWindowDateTimeRanges=" + blackoutWindowDateTimeRanges)
            .add("correlatedFileList=" + correlatedFileList)
            .add("fireTime=" + fireTime)
            .add("nextFireTime=" + nextFireTime)
            .add("jobGroup='" + jobGroup + "'")
            .add("jobDescription='" + jobDescription + "'")
            .add("dryRun=" + dryRun)
            .add("outcome='" + outcome + "'")
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileWatcherJobEvent that = (FileWatcherJobEvent) o;
        return minFileAgeSeconds == that.minFileAgeSeconds && fireTime == that.fireTime
            && nextFireTime == that.nextFireTime && dryRun == that.dryRun
            && Objects.equals(contextName, that.contextName) && Objects.equals(jobName, that.jobName)
            && Objects.equals(childContextNames, that.childContextNames)
            && Objects.equals(correlationIdentifier, that.correlationIdentifier)
            && Objects.equals(filePath, that.filePath) && Objects.equals(filename, that.filename)
            && Objects.equals(fileNameSpelExpression, that.fileNameSpelExpression)
            && Objects.equals(filePathSpelExpression, that.filePathSpelExpression)
            && Objects.equals(moveDirectory, that.moveDirectory)
            && Objects.equals(moveDirectorySpelExpression, that.moveDirectorySpelExpression)
            && Objects.equals(slaCronExpression, that.slaCronExpression)
            && Objects.equals(timeZone, that.timeZone)
            && Objects.equals(blackoutWindowCronExpressions, that.blackoutWindowCronExpressions)
            && Objects.equals(blackoutWindowDateTimeRanges, that.blackoutWindowDateTimeRanges)
            && Objects.equals(correlatedFileList, that.correlatedFileList)
            && Objects.equals(jobGroup, that.jobGroup)
            && Objects.equals(jobDescription, that.jobDescription)
            && Objects.equals(outcome, that.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextName, jobName, childContextNames, correlationIdentifier, filePath
            , filename, fileNameSpelExpression, filePathSpelExpression, moveDirectory, moveDirectorySpelExpression, minFileAgeSeconds
            , slaCronExpression, timeZone, blackoutWindowCronExpressions, blackoutWindowDateTimeRanges
            , correlatedFileList, fireTime, nextFireTime, jobGroup, jobDescription, dryRun, outcome);
    }
}
