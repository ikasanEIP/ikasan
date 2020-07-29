package org.ikasan.dashboard.notification.model;

import java.util.List;

public class EmailNotification {

    private List<String> recipients;
    private String subject;
    private String body;
    private boolean isHtml;

    public EmailNotification(List<String> recipients, String subject, String body, boolean isHtml) {
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public boolean isHtml() {
        return isHtml;
    }
}
