package org.ikasan.dashboard.notification.configuration;

public class EmailNotificationConfiguration {
    private String id;
    private Long lastExecutedTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLastExecutedTimestamp() {
        return lastExecutedTimestamp;
    }

    public void setLastExecutedTimestamp(Long lastExecutedTimestamp) {
        this.lastExecutedTimestamp = lastExecutedTimestamp;
    }
}
