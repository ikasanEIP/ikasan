package org.ikasan.backup.h2.persistence.model;

public class H2DatabaseBackupManifest {
    private String backupName;
    private String failureMessage;
    private int retryCount;
    private long failureTimestamp;

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getFailureTimestamp() {
        return failureTimestamp;
    }

    public void setFailureTimestamp(long failureTimestamp) {
        this.failureTimestamp = failureTimestamp;
    }
}
