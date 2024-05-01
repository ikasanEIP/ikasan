package org.ikasan.backup.h2.service;

import org.ikasan.backup.IkasanBackupAutoConfiguration;
import org.ikasan.backup.h2.IkasanBackupAutoTestConfiguration;
import org.ikasan.backup.h2.lifecycle.H2DatabaseBackupShutdownListener;
import org.ikasan.backup.h2.lifecycle.H2DatabaseBackupStartupListener;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IkasanBackupAutoTestConfiguration.class, IkasanBackupAutoConfiguration.class})
@TestPropertySource("classpath:test-application-with-eai.properties")
public class H2BackupServiceImplWithEaiDbConfiguredTest {


    @Autowired
    @Qualifier("eaiH2BackupService")
    H2BackupServiceImpl eaiH2BackupService;

    @Autowired
    @Qualifier("eaiDatabaseBackupDetails")
    H2DatabaseBackup eaiDatabaseBackupDetails;

    @Autowired
    @Qualifier("eaiDatabaseBackupValidator")
    H2DatabaseValidator eaiDatabaseBackupValidator;

    @Autowired
    @Qualifier("eaiH2DatabaseBackupShutdownListener")
    H2DatabaseBackupShutdownListener eaiH2DatabaseBackupShutdownListener;

    @Autowired
    @Qualifier("eaiH2DatabaseBackupStartupListener")
    H2DatabaseBackupStartupListener eaiH2DatabaseBackupStartupListener;

    @Test
    public void test_beans_wired_succssfullly() {
        Assert.assertNotNull(eaiH2BackupService);
        Assert.assertNotNull(eaiDatabaseBackupDetails);
        Assert.assertNotNull(eaiDatabaseBackupValidator);
        Assert.assertNotNull(eaiH2DatabaseBackupShutdownListener);
        Assert.assertNotNull(eaiH2DatabaseBackupStartupListener);
    }
}
