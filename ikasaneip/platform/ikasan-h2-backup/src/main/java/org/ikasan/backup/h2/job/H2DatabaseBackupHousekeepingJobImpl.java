package org.ikasan.backup.h2.job;

import org.ikasan.backup.h2.service.H2BackupServiceImpl;
import org.ikasan.housekeeping.HousekeepingJobImpl;
import org.springframework.core.env.Environment;

import java.util.StringJoiner;

public class H2DatabaseBackupHousekeepingJobImpl extends HousekeepingJobImpl implements H2DatabaseBackupHousekeepingJob {

    private String h2DbBackupCronExpression;
    private H2BackupServiceImpl h2BackupService;

    public H2DatabaseBackupHousekeepingJobImpl(String jobName, H2BackupServiceImpl h2BackupService, Environment environment) {
        super(jobName, h2BackupService, environment);
        this.h2BackupService = h2BackupService;
        this.h2DbBackupCronExpression = h2BackupService.getH2DatabaseBackup().getDbBackupCronSchedule();
    }

    @Override
    public void backup() {
        this.h2BackupService.backup();
    }

    @Override
    public void setCronExpression(String cronExpression) {
        this.h2DbBackupCronExpression = cronExpression;
    }

    @Override
    public String getCronExpression() {
        return this.h2DbBackupCronExpression;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", H2DatabaseBackupHousekeepingJobImpl.class.getSimpleName() + "[", "]")
            .add(h2BackupService.getH2DatabaseBackup().toString())
            .toString();
    }
}
