package org.ikasan.backup.h2.lifecycle;

import org.ikasan.backup.h2.persistence.service.H2DatabaseBackupManifestService;
import org.ikasan.backup.h2.service.H2BackupServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

public class H2DatabaseBackupStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(H2DatabaseBackupStartupListener.class);

    private H2DatabaseBackupManifestService h2DatabaseBackupManifestService;

    public H2DatabaseBackupStartupListener(H2DatabaseBackupManifestService h2DatabaseBackupManifestService) {
        this.h2DatabaseBackupManifestService = h2DatabaseBackupManifestService;
        if(this.h2DatabaseBackupManifestService == null) {
            throw new IllegalArgumentException("h2DatabaseBackupManifestService cannot be null");
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Removing H2 database backup manifest on startup.");
        try {
            this.h2DatabaseBackupManifestService.delete();
            logger.info(String.format("Successfully removed H2 database backup manifest on startup"));
        } catch (Exception e) {
            logger.warn(String.format("An error has occurred removing H2 database manifest on startup.", e));
        }
    }
}
