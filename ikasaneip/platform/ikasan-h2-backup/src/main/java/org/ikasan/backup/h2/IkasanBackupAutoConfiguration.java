package org.ikasan.backup;

import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.ikasan.backup.h2.lifecycle.H2DatabaseBackupStartupListener;
import org.ikasan.backup.h2.persistence.service.H2DatabaseBackupManifestService;
import org.ikasan.backup.h2.service.H2BackupServiceImpl;
import org.ikasan.backup.h2.job.H2DatabaseBackupHousekeepingJobImpl;
import org.ikasan.backup.h2.lifecycle.H2DatabaseBackupShutdownListener;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.service.H2DatabaseValidator;
import org.ikasan.backup.h2.util.H2ConnectionUrlUtils;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.nio.file.FileSystems;

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

    @Value("${default.ikasan.h2.backup.cron.expression:17 0/5 * * * ?}")
    private String defaultIkasanDatabaseBackupCronExpression;

    @Value("${default.ikasan.h2.backup.on.module.shutdown:true}")
    private boolean defaultIkasanDatabaseBackupOnModuleShutdown;

    @Value("${h2.db.port}")
    private int defaultH2PortNumber;

    @Deprecated
    @Value("${default.h2.port.number.step:1000}")
    private int defaultH2PortNumberStep;

    @Value("${eai.datasource.url:}")
    private String eaiIkasanDatabaseUrl;

    @Value("${eai.datasource.username:}")
    private String eaiIkasanDatabaseUsername;

    @Value("${eai.datasource.password:}")
    private String eaiIkasanDatabasePassword;

    @Value("${eai.h2.backup.num.to.retain:2}")
    private int eaiIkasanDatabaseBackupNumToRetain;

    @Value("${eai.h2.backup.cron.expression:37 0/5 * * * ?}")
    private String eaiIkasanDatabaseBackupCronExpression;

    @Value("${eai.h2.backup.on.module.shutdown:true}")
    private boolean eaiIkasanDatabaseBackupOnModuleShutdown;

    @Value("${h2.db.port}")
    private int eaiH2PortNumber;

    @Deprecated
    @Value("${eai.h2.port.number.step:1000}")
    private int eaiH2PortNumberStep;

    @Value("${h2.backup.stop.flows.on.corrupt.database.detection:true}")
    private boolean ikasanDatabaseBackupStopFlowsOnCorruptDatabaseDetection;

    @Value("${h2.backup.database.test.port.bind.retries:5}")
    private int h2BackupDatabaseTestPortBindRetries;

    @Value("${h2.backup.number.of.invalid.backups.before.stopping.flows:5}")
    private int h2BackupNumberOfInvalidBackupsBeforeStoppingFlows;



    @Autowired
    private Module<Flow> module;

    /**
     * Returns the default H2DatabaseBackup object with the provided database backup details.
     *
     * @return the default H2DatabaseBackup object
     */
    @Bean("defaultIkasanDatabaseBackupDetails")
    public H2DatabaseBackup defaultIkasanDatabaseBackupDetails() {
        H2DatabaseBackup h2DatabaseBackupDetails = new H2DatabaseBackup();
        h2DatabaseBackupDetails.setDbUrl(this.defaultIkasanDatabaseUrl);
        h2DatabaseBackupDetails.setUsername(this.defaultIkasanDatabaseUsername);
        h2DatabaseBackupDetails.setPassword(this.defaultIkasanDatabasePassword);
        h2DatabaseBackupDetails.setNumOfBackupsToRetain(this.defaultIkasanDatabaseBackupNumToRetain);
        h2DatabaseBackupDetails.setDbBackupBaseDirectory(this.persistenceDir + FileSystems.getDefault().getSeparator() + "ESB");
        h2DatabaseBackupDetails.setDbBackupCronSchedule(this.defaultIkasanDatabaseBackupCronExpression);
        h2DatabaseBackupDetails.setTestH2Port(this.defaultH2PortNumber);
        return h2DatabaseBackupDetails;
    }

    @Bean("defaultIkasanDatabaseBackupValidator")
    public H2DatabaseValidator defaultIkasanDatabaseBackupValidator (
            @Qualifier("defaultIkasanDatabaseBackupDetails") H2DatabaseBackup defaultIkasanDatabaseBackupDetails) {
        return new H2DatabaseValidator(defaultIkasanDatabaseBackupDetails);
    }

    @Bean("defaultH2DatabaseBackupManifestService")
    public H2DatabaseBackupManifestService defaultH2DatabaseBackupManifestService (
            @Qualifier("defaultIkasanDatabaseBackupDetails") H2DatabaseBackup defaultIkasanDatabaseBackupDetails)
            throws InvalidH2ConnectionUrlException {
        return H2DatabaseBackupManifestService.instance(defaultIkasanDatabaseBackupDetails.getDbBackupBaseDirectory(),
                H2ConnectionUrlUtils.getDatabaseName(defaultIkasanDatabaseBackupDetails.getDbUrl()));
    }

    /**
     * Creates an instance of H2BackupServiceImpl with the provided defaultIkasanDatabaseBackupDetails.
     *
     * @param defaultIkasanDatabaseBackupDetails the default H2DatabaseBackup object to use
     * @param defaultIkasanDatabaseBackupValidator the database backup validator to use
     * @param defaultH2DatabaseBackupManifestService the manifest service to use
     *
     * @return an instance of H2BackupServiceImpl
     */
    @Bean("h2BackupService")
    public H2BackupServiceImpl h2BackupService(
            @Qualifier("defaultIkasanDatabaseBackupDetails") H2DatabaseBackup defaultIkasanDatabaseBackupDetails,
            @Qualifier("defaultIkasanDatabaseBackupValidator")  H2DatabaseValidator defaultIkasanDatabaseBackupValidator,
            @Qualifier("defaultH2DatabaseBackupManifestService") H2DatabaseBackupManifestService defaultH2DatabaseBackupManifestService
    ) {
        return new H2BackupServiceImpl(defaultIkasanDatabaseBackupDetails, this.module,
                this.ikasanDatabaseBackupStopFlowsOnCorruptDatabaseDetection, h2BackupDatabaseTestPortBindRetries,
                this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows, defaultIkasanDatabaseBackupValidator,
                defaultH2DatabaseBackupManifestService);
    }

    /**
     * Creates a HousekeepingJob for performing H2 database backups.
     *
     * @param h2BackupService The H2BackupServiceImpl object to use for performing backups
     * @param environment The Environment object
     * @param jobMonitor The JobMonitor object for monitoring the job
     *
     * @return The HousekeepingJob object for H2 database backups
     */
    @Bean
    public HousekeepingJob h2BackupHousekeepingJob(@Qualifier("h2BackupService") H2BackupServiceImpl h2BackupService
        , Environment environment, JobMonitor jobMonitor) {
        H2DatabaseBackupHousekeepingJobImpl job =  new H2DatabaseBackupHousekeepingJobImpl
            ("default-esb-h2-backup", h2BackupService, environment);
        job.setMonitor(jobMonitor);

        return job;
    }

    /**
     * Creates an instance of H2DatabaseBackupShutdownListener with the provided H2BackupServiceImpl and backupOnShutdown flag.
     *
     * @param h2BackupService The H2BackupServiceImpl object to use for backup

     * @return An instance of H2DatabaseBackupShutdownListener
     */
    @Bean
    public H2DatabaseBackupShutdownListener h2DatabaseBackupShutdownListener(@Qualifier("h2BackupService") H2BackupServiceImpl h2BackupService) {
        return new H2DatabaseBackupShutdownListener(h2BackupService, this.defaultIkasanDatabaseBackupOnModuleShutdown);
    }

    /**
     * Returns an instance of {@link H2DatabaseBackupStartupListener} for the EAI database if necessary.
     *
     * @param defaultH2DatabaseBackupManifestService  the manifest service

     * @return an instance of H2DatabaseBackupStartupListener
     */
    @Bean
    public H2DatabaseBackupStartupListener defaultH2DatabaseBackupStartupListener(
            @Qualifier("defaultH2DatabaseBackupManifestService") H2DatabaseBackupManifestService defaultH2DatabaseBackupManifestService) {
        return new H2DatabaseBackupStartupListener(defaultH2DatabaseBackupManifestService);
    }

    /**
     * Returns the eai H2DatabaseBackup object with the provided database backup details.
     *
     * @return the eai H2DatabaseBackup object
     */
    @Bean("eaiDatabaseBackupDetails")
    @ConditionalOnProperty(prefix = "eai-database", name = "present", havingValue = "true")
    public H2DatabaseBackup eaiDatabaseBackupDetails() {
        H2DatabaseBackup h2DatabaseBackupDetails = new H2DatabaseBackup();
        h2DatabaseBackupDetails.setDbUrl(this.eaiIkasanDatabaseUrl);
        h2DatabaseBackupDetails.setUsername(this.eaiIkasanDatabaseUsername);
        h2DatabaseBackupDetails.setPassword(this.eaiIkasanDatabasePassword);
        h2DatabaseBackupDetails.setNumOfBackupsToRetain(this.eaiIkasanDatabaseBackupNumToRetain);
        h2DatabaseBackupDetails.setDbBackupBaseDirectory(this.persistenceDir + FileSystems.getDefault().getSeparator() + "EAI");
        h2DatabaseBackupDetails.setDbBackupCronSchedule(this.eaiIkasanDatabaseBackupCronExpression);
        h2DatabaseBackupDetails.setTestH2Port(this.eaiH2PortNumber);
        return h2DatabaseBackupDetails;
    }

    @Bean("eaiDatabaseBackupValidator")
    @ConditionalOnBean(name = "eaiDatabaseBackupDetails")
    public H2DatabaseValidator eaiDatabaseBackupValidator (
            @Qualifier("eaiDatabaseBackupDetails") H2DatabaseBackup eaiDatabaseBackupDetails) {
        return new H2DatabaseValidator(eaiDatabaseBackupDetails);
    }

    @Bean("eaiH2DatabaseBackupManifestService")
    @ConditionalOnBean(name = "eaiDatabaseBackupDetails")
    public H2DatabaseBackupManifestService eaiH2DatabaseBackupManifestService (
            @Qualifier("eaiDatabaseBackupDetails") H2DatabaseBackup eaiDatabaseBackupDetails)
            throws InvalidH2ConnectionUrlException {
        return H2DatabaseBackupManifestService.instance(eaiDatabaseBackupDetails.getDbBackupBaseDirectory(),
                H2ConnectionUrlUtils.getDatabaseName(eaiDatabaseBackupDetails.getDbUrl()));
    }

    /**
     * Creates an instance of H2BackupServiceImpl with the provided defaultIkasanDatabaseBackupDetails.
     *
     * @param eaiDatabaseBackupDetails the H2DatabaseBackup object to use
     * @param eaiDatabaseBackupValidator the database backup validator to use
     * @param eaiH2DatabaseBackupManifestService the manifest service to use
     * @return an instance of H2BackupServiceImpl
     */
    @Bean("eaiH2BackupService")
    @ConditionalOnBean(name = "eaiDatabaseBackupDetails")
    public H2BackupServiceImpl eaiH2BackupService(
            @Qualifier("eaiDatabaseBackupDetails") H2DatabaseBackup eaiDatabaseBackupDetails,
            @Qualifier("eaiDatabaseBackupValidator") H2DatabaseValidator eaiDatabaseBackupValidator,
            @Qualifier("eaiH2DatabaseBackupManifestService") H2DatabaseBackupManifestService eaiH2DatabaseBackupManifestService) {
        return new H2BackupServiceImpl(eaiDatabaseBackupDetails, this.module,
                this.ikasanDatabaseBackupStopFlowsOnCorruptDatabaseDetection, h2BackupDatabaseTestPortBindRetries,
                this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows, eaiDatabaseBackupValidator,
                eaiH2DatabaseBackupManifestService);
    }

    /**
     * Creates and returns a HousekeepingJob for performing H2 database backups for EAI database.
     *
     * @param eaiH2BackupService The H2BackupServiceImpl object to use for performing backups
     * @param environment     The Environment object
     * @param jobMonitor      The JobMonitor object for monitoring the job
     *
     * @return The HousekeepingJob object for H2 database backups
     */
    @Bean
    @ConditionalOnBean(name = "eaiH2BackupService")
    public HousekeepingJob eaiH2BackupHousekeepingJob(@Qualifier("eaiH2BackupService") H2BackupServiceImpl eaiH2BackupService
        , Environment environment, JobMonitor jobMonitor) {
        H2DatabaseBackupHousekeepingJobImpl job =  new H2DatabaseBackupHousekeepingJobImpl
            ("eai-h2-backup", eaiH2BackupService, environment);
        job.setMonitor(jobMonitor);

        return job;
    }

    /**
     * Returns an instance of {@link H2DatabaseBackupShutdownListener} with the specified H2BackupServiceImpl and backupOnShutdown flag.
     *
     * @param eaiH2BackupService    the H2BackupServiceImpl object to use for backup

     * @return an instance of H2DatabaseBackupShutdownListener
     */
    @Bean
    @ConditionalOnBean(name = "eaiH2BackupService")
    public H2DatabaseBackupShutdownListener eaiH2DatabaseBackupShutdownListener(@Qualifier("eaiH2BackupService")H2BackupServiceImpl eaiH2BackupService) {
        return new H2DatabaseBackupShutdownListener(eaiH2BackupService, this.defaultIkasanDatabaseBackupOnModuleShutdown);
    }

    /**
     * Returns an instance of {@link H2DatabaseBackupStartupListener} for the EAI database if necessary.
     *
     * @param eaiH2DatabaseBackupManifestService   the manifest service

     * @return an instance of H2DatabaseBackupStartupListener
     */
    @Bean
    @ConditionalOnBean(name = "eaiH2DatabaseBackupManifestService")
    public H2DatabaseBackupStartupListener eaiH2DatabaseBackupStartupListener(
            @Qualifier("eaiH2DatabaseBackupManifestService") H2DatabaseBackupManifestService eaiH2DatabaseBackupManifestService) {
        return new H2DatabaseBackupStartupListener(eaiH2DatabaseBackupManifestService);
    }
}
