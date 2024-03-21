package org.ikasan.backup.h2.lifecycle;

import org.ikasan.spec.housekeeping.H2DatabaseBackupHousekeepingJob;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.List;

public class H2DatabaseBackupShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private static Logger logger = LoggerFactory.getLogger(H2DatabaseBackupShutdownListener.class);

    private List<HousekeepingJob> housekeepingJobs;

    public H2DatabaseBackupShutdownListener(List<HousekeepingJob> housekeepingJobs) {
        this.housekeepingJobs = housekeepingJobs;
        if(this.housekeepingJobs == null) {
            throw new IllegalArgumentException("housekeepingJobs cannot be null");
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("Performing H2 database backup on shutdown.");
        housekeepingJobs.forEach(housekeepingJob -> {
            if(housekeepingJob instanceof H2DatabaseBackupHousekeepingJob) {
                try {
                    ((H2DatabaseBackupHousekeepingJob) housekeepingJob).backup();
                    logger.info(String.format("Successfully performed H2 database backup on shutdown for the following configuration %s"
                        , housekeepingJob));
                }
                catch (Exception e) {
                    logger.error(String.format("An error has occurred performing H2 database backup on shutdown for the following configuration %s"
                            , housekeepingJob), e);
                }
            }
        });
        logger.info("H2 database backup on shutdown complete.");
    }
}
