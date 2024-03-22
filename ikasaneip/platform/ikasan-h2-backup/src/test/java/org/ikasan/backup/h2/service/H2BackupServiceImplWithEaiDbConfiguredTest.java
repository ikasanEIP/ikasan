package org.ikasan.backup.h2.service;

import org.ikasan.backup.IkasanBackupAutoConfiguration;
import org.ikasan.backup.h2.IkasanBackupAutoTestConfiguration;
import org.ikasan.backup.h2.IkasanBackupWithEaiAutoTestConfiguration;
import org.ikasan.backup.h2.lifecycle.H2DatabaseBackupShutdownListener;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IkasanBackupWithEaiAutoTestConfiguration.class, IkasanBackupAutoConfiguration.class})
@TestPropertySource("classpath:test-application-with-eai.properties")
public class H2BackupServiceImplWithEaiDbConfiguredTest {

    @Autowired
    List<HousekeepingJob> housekeepingJobs;

    @Autowired
    H2BackupServiceImpl eaiH2BackupService;

    @Autowired
    H2DatabaseBackup eaiDatabaseBackupDetails;

    @Autowired
    H2DatabaseBackupShutdownListener eaiH2DatabaseBackupShutdownListener;
    @Test
    public void test_beans_wired_suecssfullly() {
        Assert.assertNotNull(eaiH2BackupService);
        Assert.assertNotNull(eaiDatabaseBackupDetails);
        Assert.assertNotNull(eaiH2DatabaseBackupShutdownListener);
    }

    @Test
    public void test_assert_housekeeping_job_collection_loaded_successfully() {
        Assert.assertEquals(2, this.housekeepingJobs.size());
    }
}
