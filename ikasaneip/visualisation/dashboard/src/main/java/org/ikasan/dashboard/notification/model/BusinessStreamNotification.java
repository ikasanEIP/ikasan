package org.ikasan.dashboard.notification.model;

import java.util.List;

public class BusinessStreamNotification {
    private String jobName;
    private String emailBodyTemplate;
    private String emailSubjectTemplate;
    private String businessStreamName;
    private List<String> recipientList;
    private String cronExpression;
    private boolean isHtml;
    private Integer resultSize;
    private boolean isNewExclusionsOnlyNotification;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getEmailBodyTemplate() {
        return emailBodyTemplate;
    }

    public void setEmailBodyTemplate(String emailBodyTemplate) {
        this.emailBodyTemplate = emailBodyTemplate;
    }

    public String getEmailSubjectTemplate() {
        return emailSubjectTemplate;
    }

    public void setEmailSubjectTemplate(String emailSubjectTemplate) {
        this.emailSubjectTemplate = emailSubjectTemplate;
    }

    public String getBusinessStreamName() {
        return businessStreamName;
    }

    public void setBusinessStreamName(String businessStreamName) {
        this.businessStreamName = businessStreamName;
    }

    public List<String> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<String> recipientList) {
        this.recipientList = recipientList;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setIsHtml(boolean html) {
        isHtml = html;
    }

    public Integer getResultSize() {
        return resultSize;
    }

    public void setResultSize(Integer resultSize) {
        this.resultSize = resultSize;
    }

    public boolean isNewExclusionsOnlyNotification() {
        return isNewExclusionsOnlyNotification;
    }

    public void setIsNewExclusionsOnlyNotification(boolean newExclusionsOnlyNotification) {
        isNewExclusionsOnlyNotification = newExclusionsOnlyNotification;
    }
}
