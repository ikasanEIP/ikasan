package org.ikasan.backup.h2.service;

import org.ikasan.backup.IkasanBackupAutoConfiguration;
import org.ikasan.backup.h2.IkasanBackupAutoTestConfiguration;
import org.ikasan.backup.h2.exception.H2DatabaseValidationException;
import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.persistence.model.H2DatabaseBackupManifest;
import org.ikasan.backup.h2.persistence.service.H2DatabaseBackupManifestService;
import org.ikasan.backup.h2.util.H2BackupUtils;
import org.ikasan.backup.h2.util.H2ConnectionUrlUtils;
import org.h2.tools.Server;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.module.Module;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IkasanBackupAutoConfiguration.class, IkasanBackupAutoTestConfiguration.class})
@TestPropertySource("classpath:test-application.properties")
public class H2DatabaseValidatorTest {

    private static final String DATABASE_DIRECTORY = Paths.get("./target","database-dir").toString();
    private static final String CORRUPT_DATABASE_DIRECTORY = Paths.get("./target","corrupt-database-dir").toString();

    int port = SocketUtils.findAvailableTcpPort();

    @Before
    public void setup() throws IOException, SQLException {
        H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", DATABASE_DIRECTORY);
        H2BackupUtils.unzipFile("./src/test/resources/data/corrupt-db.zip", CORRUPT_DATABASE_DIRECTORY);
    }

    @After
    public void teardown() throws IOException {
        H2BackupUtils.cleanDirectory(Paths.get("./target","database-dir").toString());
        H2BackupUtils.cleanDirectory(Paths.get("./target","corrupt-database-dir").toString());
    }


    @Test(expected = H2DatabaseValidationException.class)
    public void test_backup_corrupt_db() throws IOException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY
            +"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2DatabaseValidator h2BackupService = new H2DatabaseValidator(h2DatabaseBackup);
        h2BackupService.runDatabaseValidationTest("./src/test/resources/data/corrupt-db.zip", SocketUtils.findAvailableTcpPort());
    }

    @Test
    public void test_backup_good_db() {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY
            +"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(CORRUPT_DATABASE_DIRECTORY);

        H2DatabaseValidator h2BackupService = new H2DatabaseValidator(h2DatabaseBackup);

        try {
            h2BackupService.runDatabaseValidationTest("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", SocketUtils.findAvailableTcpPort());
        }
        catch (Exception e) {
            fail(String.format("No exception should be thrown here! However the following exception occurred [%s]", e.getMessage()));
        }
    }
}
