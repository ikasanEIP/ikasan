package org.ikasan.ootb.scheduler.agent.module.component.converter.configuration;

import java.util.List;
import java.util.Map;

public class FileWatcherJobConverterConfiguration {
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
    private int minFileAgeSeconds;
    /** cron expression on expected time of file availability */
    private String slaCronExpression;
    private String timeZone;
    private List<String> blackoutWindowCronExpressions;
    private Map<String,String> blackoutWindowDateTimeRanges;

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<String> getChildContextNames() {
        return childContextNames;
    }

    public void setChildContextNames(List<String> childContextNames) {
        this.childContextNames = childContextNames;
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
}
