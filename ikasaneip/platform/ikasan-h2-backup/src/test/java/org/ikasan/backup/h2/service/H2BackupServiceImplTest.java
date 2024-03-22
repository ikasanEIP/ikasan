package org.ikasan.backup.h2.service;

import org.h2.tools.Server;
import org.ikasan.backup.IkasanBackupAutoConfiguration;
import org.ikasan.backup.h2.IkasanBackupAutoTestConfiguration;
import org.ikasan.backup.h2.exception.H2DatabaseValidationException;
import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.util.H2BackupUtils;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.TestSocketUtils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IkasanBackupAutoConfiguration.class, IkasanBackupAutoTestConfiguration.class})
@TestPropertySource("classpath:test-application.properties")
@EnableConfigurationProperties
public class H2BackupServiceImplTest {

    private static final String DATABASE_DIRECTORY = Paths.get("./target", "database-dir").toString();
    private static final String CORRUPT_DATABASE_DIRECTORY = Paths.get("./target", "corrupt-database-dir").toString();

    @Autowired
    List<HousekeepingJob> housekeepingJobs;

    int port = TestSocketUtils.findAvailableTcpPort();

    Server server;

    @Before
    public void setup() throws IOException, SQLException {
        H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", DATABASE_DIRECTORY);
        H2BackupUtils.unzipFile("./src/test/resources/data/corrupt-db.zip", CORRUPT_DATABASE_DIRECTORY);
        server = Server.createTcpServer("-tcpPort", Integer.toString(port), "-tcpAllowOthers");
        server.start();
    }

    @After
    public void teardown() throws IOException {
        server.stop();
        H2BackupUtils.cleanDirectory(DATABASE_DIRECTORY);
        H2BackupUtils.cleanDirectory(CORRUPT_DATABASE_DIRECTORY);
    }


    @Test
    public void test_backup_success() throws IOException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE;NON_KEYWORDS=VALUE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup);
        h2BackupService.backup();

        // There will be 2 files as there is the warning file written to the backup directory.
        Assert.assertEquals(2, Files.list(Paths.get(DATABASE_DIRECTORY
                + FileSystems.getDefault().getSeparator() + H2BackupServiceImpl.BACKUP_DIRECTORY))
            .collect(Collectors.toList()).size());
    }

    @Test
    public void test_backup_rollover_success() throws IOException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE;NON_KEYWORDS=VALUE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup);

        // take 6 backups
        h2BackupService.backup();
        h2BackupService.backup();
        h2BackupService.backup();
        h2BackupService.backup();
        h2BackupService.backup();
        h2BackupService.backup();

        // There will be 4 files as there is the warning file written to the backup directory
        // as well as the 3 retained files.
        Assert.assertEquals(4, Files.list(Paths.get(DATABASE_DIRECTORY
                + FileSystems.getDefault().getSeparator() + H2BackupServiceImpl.BACKUP_DIRECTORY))
            .collect(Collectors.toList()).size());
    }

    @Test
    public void test_backup_empty_zip_after_db_file_deleted() throws IOException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE;NON_KEYWORDS=VALUE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupUtils.deleteFile(DATABASE_DIRECTORY+FileSystems.getDefault().getSeparator()+"esb.mv.db");

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup);
        h2BackupService.backup();

        // Only the warning file will exist in the backup directory.
        Assert.assertEquals(1, Files.list(Paths.get(DATABASE_DIRECTORY
                + FileSystems.getDefault().getSeparator() + H2BackupServiceImpl.BACKUP_DIRECTORY))
            .collect(Collectors.toList()).size());
    }

    @Test(expected = H2DatabaseValidationException.class)
    public void test_backup_corrupt_db() throws IOException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY
            +"/esb;IFEXISTS=FALSE;NON_KEYWORDS=VALUE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup);
        h2BackupService.runDatabaseValidationTest("./src/test/resources/data/corrupt-db.zip");
    }

    @Test
    public void test_backup_good_db() throws IOException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+CORRUPT_DATABASE_DIRECTORY
            +"/esb;IFEXISTS=FALSE;NON_KEYWORDS=VALUE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(CORRUPT_DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup);

        try {
            h2BackupService.runDatabaseValidationTest("./src/test/resources/data/esb-backup-20240321-06-11-00.zip");
        }
        catch (Exception e) {
            fail(String.format("No exception should be thrown here! However the following exception occurred [%s]", e.getMessage()));
        }
    }


    @Test
    public void test_assert_housekeeping_job_collection_loaded_successfully() {
        Assert.assertEquals(1, this.housekeepingJobs.size());
    }
}
