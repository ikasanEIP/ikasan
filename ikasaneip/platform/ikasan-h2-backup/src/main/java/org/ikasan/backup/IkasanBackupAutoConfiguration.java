package org.ikasan.backup;

import org.ikasan.backup.h2.job.H2DatabaseBackupHousekeepingJobImpl;
import org.ikasan.backup.h2.lifecycle.H2DatabaseBackupShutdownListener;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.service.H2BackupServiceImpl;
import org.ikasan.housekeeping.HousekeepingJobImpl;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

public class IkasanBackupAutoConfiguration {

    @Value("${persistence.dir:./persistence}")
    private String persistenceDir;

    @Value("${datasource.url:}")
    private String defaultIkasanDatabaseUrl;

    @Value("${datasource.username:}")
    private String defaultIkasanDatabaseUsername;

    @Value("${datasource.password:}")
    private String defaultIkasanDatabasePassword;

    @Value("${default.ikasan.h2.backup.num.to.retain:2}")
    private int defaultIkasanDatabaseBackupNumToRetain;

    @Value("${default.ikasan.h2.backup.cron.expression:17 0/1 * * * ?}")
    private String defaultIkasanDatabaseBackupCronExpression;

    @Value("${default.ikasan.h2.backup.on.module.shutdown:true}")
    private boolean defaultIkasanDatabaseBackupOnModuleShutdown;

    /**
     * Returns the default H2DatabaseBackup object with the provided database backup details.
     *
     * @return the default H2DatabaseBackup object
     */
    @Bean
    public H2DatabaseBackup defaultIkasanDatabaseBackupDetails() {
        H2DatabaseBackup h2DatabaseBackupDetails = new H2DatabaseBackup();
        h2DatabaseBackupDetails.setDbUrl(this.defaultIkasanDatabaseUrl);
        h2DatabaseBackupDetails.setUsername(this.defaultIkasanDatabaseUsername);
        h2DatabaseBackupDetails.setPassword(this.defaultIkasanDatabasePassword);
        h2DatabaseBackupDetails.setNumOfBackupsToRetain(this.defaultIkasanDatabaseBackupNumToRetain);
        h2DatabaseBackupDetails.setDbBackupBaseDirectory(this.persistenceDir);
        h2DatabaseBackupDetails.setDbBackupCronSchedule(this.defaultIkasanDatabaseBackupCronExpression);
        return h2DatabaseBackupDetails;
    }

    /**
     * Creates an instance of H2BackupServiceImpl with the provided defaultIkasanDatabaseBackupDetails.
     *
     * @param defaultIkasanDatabaseBackupDetails the default H2DatabaseBackup object to use
     *
     * @return an instance of H2BackupServiceImpl
     */
    @Bean
    public H2BackupServiceImpl h2BackupService(H2DatabaseBackup defaultIkasanDatabaseBackupDetails) {
        return new H2BackupServiceImpl(defaultIkasanDatabaseBackupDetails);
    }

    @Bean
    public HousekeepingJob h2BackupHousekeepingJob(H2BackupServiceImpl h2BackupService
        , Environment environment, JobMonitor jobMonitor) {
        H2DatabaseBackupHousekeepingJobImpl job =  new H2DatabaseBackupHousekeepingJobImpl
            ("default-esb-h2-backup", h2BackupService, environment);
        job.setMonitor(jobMonitor);

        return job;
    }

    @Bean
    public H2DatabaseBackupShutdownListener h2DatabaseBackupShutdownListener(H2BackupServiceImpl h2BackupService) {
        return new H2DatabaseBackupShutdownListener(h2BackupService, this.defaultIkasanDatabaseBackupOnModuleShutdown);
    }
}
