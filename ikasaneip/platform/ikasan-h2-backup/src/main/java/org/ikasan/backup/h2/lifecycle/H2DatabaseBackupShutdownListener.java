package org.ikasan.backup.h2.lifecycle;

import org.ikasan.backup.h2.service.H2BackupServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class H2DatabaseBackupShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private static Logger logger = LoggerFactory.getLogger(H2DatabaseBackupShutdownListener.class);

    private H2BackupServiceImpl h2BackupService;
    public boolean backupOnShutdown;

    public H2DatabaseBackupShutdownListener(H2BackupServiceImpl h2BackupService, boolean backupOnShutdown) {
        this.h2BackupService = h2BackupService;
        if(this.h2BackupService == null) {
            throw new IllegalArgumentException("h2BackupService cannot be null");
        }

        this.backupOnShutdown = backupOnShutdown;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if(backupOnShutdown) {
            logger.info("Performing H2 database backup on shutdown.");
            try {
                this.h2BackupService.backup();
                logger.info(String.format("Successfully performed H2 database backup on shutdown for the following configuration %s"
                    , this.h2BackupService.getH2DatabaseBackup()));
            } catch (Exception e) {
                logger.error(String.format("An error has occurred performing H2 database backup on shutdown for the following configuration %s"
                    , this.h2BackupService.getH2DatabaseBackup()), e);
            }
            logger.info("H2 database backup on shutdown complete.");
        }
        else {
            logger.info("This module is configured not to perform H2 database backup on shutdown.");
        }
    }
}
