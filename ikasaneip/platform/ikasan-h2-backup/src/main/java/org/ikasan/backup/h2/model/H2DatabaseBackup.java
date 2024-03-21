package org.ikasan.backup.h2.model;

import java.util.StringJoiner;

public class H2DatabaseBackup {
    private String dbUrl;
    private String username;
    private String password;
    private String dbBackupCronSchedule;
    private String dbBackupBaseDirectory;
    private int numOfBackupsToRetain = 2;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbBackupCronSchedule() {
        return dbBackupCronSchedule;
    }

    public void setDbBackupCronSchedule(String dbBackupCronSchedule) {
        this.dbBackupCronSchedule = dbBackupCronSchedule;
    }

    public String getDbBackupBaseDirectory() {
        return dbBackupBaseDirectory;
    }

    public void setDbBackupBaseDirectory(String dbBackupBaseDirectory) {
        this.dbBackupBaseDirectory = dbBackupBaseDirectory;
    }

    public int getNumOfBackupsToRetain() {
        return numOfBackupsToRetain;
    }

    public void setNumOfBackupsToRetain(int numOfBackupsToRetain) {
        this.numOfBackupsToRetain = numOfBackupsToRetain;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", H2DatabaseBackup.class.getSimpleName() + "[", "]")
            .add("dbUrl='" + dbUrl + "'")
            .add("username='*********'")
            .add("password='*********'")
            .add("dbBackupCronSchedule='" + dbBackupCronSchedule + "'")
            .add("dbBackupBaseDirectory='" + dbBackupBaseDirectory + "'")
            .add("numOfBackupsToRetain=" + numOfBackupsToRetain)
            .toString();
    }
}
