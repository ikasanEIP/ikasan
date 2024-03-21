package org.ikasan.backup.h2.service;

import org.ikasan.backup.IkasanBackupAutoConfiguration;
import org.ikasan.backup.h2.IkasanBackupAutoTestConfiguration;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IkasanBackupAutoConfiguration.class, IkasanBackupAutoTestConfiguration.class})
@TestPropertySource("classpath:test-application.properties")
@EnableConfigurationProperties
public class H2BackupServiceImplTest {

    @Autowired
    H2BackupServiceImpl h2BackupService;

    @Autowired
    List<HousekeepingJob> housekeepingJobs;

    @Test
    public void test_backup_success() throws InterruptedException {
        for(int i=0; i<1000; i++) {
            h2BackupService.backup();
            Thread.sleep(1000);
        }
    }

    @Test
    public void test_assert_housekeeping_job_collection_loaded_successfully() {
        Assert.assertEquals(1, this.housekeepingJobs.size());
    }
}
