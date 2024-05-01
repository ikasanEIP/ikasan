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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import java.io.File;
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
public class H2BackupServiceImplTest {

    private static final String DATABASE_DIRECTORY = Paths.get("./target","database-dir").toString();
    private static final String CORRUPT_DATABASE_DIRECTORY = Paths.get("./target","corrupt-database-dir").toString();

    @Autowired
    List<HousekeepingJob> housekeepingJobs;

    @Autowired
    Module<Flow> module;

    private H2DatabaseValidator mockH2DatabaseValidator = Mockito.mock(H2DatabaseValidator.class);

    int port = SocketUtils.findAvailableTcpPort();

    Server server;

    @Before
    public void setup() throws IOException, SQLException {
        H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", DATABASE_DIRECTORY);
        H2BackupUtils.unzipFile("./src/test/resources/data/corrupt-db.zip", CORRUPT_DATABASE_DIRECTORY);
        server = Server.createTcpServer("-tcpPort", Integer.toString(port), "-tcpAllowOthers", "-ifNotExists");
        server.start();
        Mockito.reset(mockH2DatabaseValidator, module);
    }

    @After
    public void teardown() throws IOException {
        server.stop();
        H2BackupUtils.cleanDirectory(Paths.get("./target","database-dir").toString());
        H2BackupUtils.cleanDirectory(Paths.get("./target","corrupt-database-dir").toString());
    }


    @Test
    public void test_backup_success() throws IOException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup, module
                , false, 5, 5
                , new H2DatabaseValidator(h2DatabaseBackup), H2DatabaseBackupManifestService.instance(h2DatabaseBackup.getDbBackupBaseDirectory()
                , "esb"));
        h2BackupService.backup();

        // There will be 2 files as there is the warning file written to the backup directory.
        Assert.assertEquals(2, Files.list(Paths.get(DATABASE_DIRECTORY
                + FileSystems.getDefault().getSeparator() + H2BackupServiceImpl.BACKUP_DIRECTORY))
            .collect(Collectors.toList()).size());
    }

    @Test
    public void test_backup_rollover_success() throws IOException, InterruptedException, InvalidH2ConnectionUrlException {
        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);
        h2DatabaseBackup.setTestH2Port(port+1000);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup
                , module, false, 5, 5
                , new H2DatabaseValidator(h2DatabaseBackup), H2DatabaseBackupManifestService
                .instance(h2DatabaseBackup.getDbBackupBaseDirectory(), H2ConnectionUrlUtils.getDatabaseName(h2DatabaseBackup.getDbUrl())));

        // take 6 backups
        h2BackupService.backup();
        Thread.sleep(1500);
        h2BackupService.backup();
        Thread.sleep(1500);
        h2BackupService.backup();
        Thread.sleep(1500);
        h2BackupService.backup();
        Thread.sleep(1500);
        h2BackupService.backup();
        Thread.sleep(1500);
        h2BackupService.backup();
        Thread.sleep(1500);


        // There will be 4 files as there is the warning file written to the backup directory
        // as well as the 3 retained files
        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(3, TimeUnit.SECONDS)
                .await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assert.assertEquals(4, Files.list(Paths.get(DATABASE_DIRECTORY
                                + FileSystems.getDefault().getSeparator() + H2BackupServiceImpl.BACKUP_DIRECTORY))
                        .collect(Collectors.toList()).size()));
    }

    @Test
    public void test_backup_with_database_corruption_leading_to_flows_stopping() throws IOException, InterruptedException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        Flow flow1 = Mockito.mock(Flow.class);
        Flow flow2 = Mockito.mock(Flow.class);
        when(module.getFlows()).thenReturn(List.of(flow1, flow2));

        doThrow(new H2DatabaseValidationException("The database is invalid!", new Exception()))
                .when(this.mockH2DatabaseValidator).runDatabaseValidationTest(anyString(), anyInt());

        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup
                , module, true, 5
                , 5, this.mockH2DatabaseValidator
                , H2DatabaseBackupManifestService.instance(h2DatabaseBackup.getDbBackupBaseDirectory()
                , H2ConnectionUrlUtils.getDatabaseName(h2DatabaseBackup.getDbUrl())));

        h2BackupService.backup();
        Thread.sleep(1500);

        H2DatabaseBackupManifest h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(1, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(2, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(3, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());


        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(4, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(5, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        // for this backup the retry count will still be 5 as no further backups are taken after
        // the retry limit is reached
        Assert.assertEquals(5, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        verify(this.mockH2DatabaseValidator, times(5))
                .runDatabaseValidationTest(anyString(), anyInt());
        verify(this.module).getFlows();
        verify(flow1).stop();
        verify(flow2).stop();

        verifyNoMoreInteractions(this.mockH2DatabaseValidator);
        verifyNoMoreInteractions(this.module);
        verifyNoMoreInteractions(flow1);
        verifyNoMoreInteractions(flow2);
    }

    @Test
    public void test_backup_with_database_corruption_and_subsequent_recovery_followed_by_5_failures_and_flows_stopping() throws IOException, InterruptedException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        Flow flow1 = Mockito.mock(Flow.class);
        Flow flow2 = Mockito.mock(Flow.class);
        when(module.getFlows()).thenReturn(List.of(flow1, flow2));

        doThrow(new H2DatabaseValidationException("The database is invalid!", new Exception()))
                .doNothing()
                .doThrow(new H2DatabaseValidationException("The database is invalid!", new Exception()))
                .when(this.mockH2DatabaseValidator).runDatabaseValidationTest(anyString(), anyInt());

        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup
                , module, true, 5
                , 5, this.mockH2DatabaseValidator
                , H2DatabaseBackupManifestService.instance(h2DatabaseBackup.getDbBackupBaseDirectory()
                , H2ConnectionUrlUtils.getDatabaseName(h2DatabaseBackup.getDbUrl())));

        h2BackupService.backup();
        Thread.sleep(1500);

        H2DatabaseBackupManifest h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(1, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNull(h2DatabaseBackupManifest);

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(1, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(2, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(3, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());


        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(4, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(5, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        // for this backup the retry count will still be 5 as no further backups are taken after
        // the retry limit is reached
        Assert.assertEquals(5, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        verify(this.mockH2DatabaseValidator, times(7)).runDatabaseValidationTest(anyString(), anyInt());

        verify(this.module, times(1)).getFlows();
        verify(flow1).stop();
        verify(flow2).stop();
        verifyNoMoreInteractions(this.mockH2DatabaseValidator);
        verifyNoMoreInteractions(this.module);
        verifyNoMoreInteractions(flow1);
        verifyNoMoreInteractions(flow2);
    }

    @Test
    public void test_backup_with_database_corruption_and_subsequent_recovery_followed_by_5_failures_and_flows_not_configured_to_stop() throws IOException, InterruptedException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        Flow flow1 = Mockito.mock(Flow.class);
        Flow flow2 = Mockito.mock(Flow.class);
        when(module.getFlows()).thenReturn(List.of(flow1, flow2));

        doThrow(new H2DatabaseValidationException("The database is invalid!", new Exception()))
                .doNothing()
                .doThrow(new H2DatabaseValidationException("The database is invalid!", new Exception()))
                .when(this.mockH2DatabaseValidator).runDatabaseValidationTest(anyString(), anyInt());

        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup
                , module, false, 5
                , 5, this.mockH2DatabaseValidator
                , H2DatabaseBackupManifestService.instance(h2DatabaseBackup.getDbBackupBaseDirectory()
                , H2ConnectionUrlUtils.getDatabaseName(h2DatabaseBackup.getDbUrl())));

        h2BackupService.backup();
        Thread.sleep(1500);

        H2DatabaseBackupManifest h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(1, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNull(h2DatabaseBackupManifest);

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(1, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(2, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(3, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());


        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(4, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(5, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        // for this backup the retry count will still be 5 as no further backups are taken after
        // the retry limit is reached
        Assert.assertEquals(5, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        verify(this.mockH2DatabaseValidator, times(7)).runDatabaseValidationTest(anyString(),  anyInt());

        verifyNoMoreInteractions(this.mockH2DatabaseValidator);
        verifyNoMoreInteractions(this.module);
        verifyNoMoreInteractions(flow1);
        verifyNoMoreInteractions(flow2);
    }

    @Test
    public void test_backup_with_database_corruption_and_subsequent_recovery() throws IOException, InterruptedException, InvalidH2ConnectionUrlException, SQLException, H2DatabaseValidationException {
        Flow flow1 = Mockito.mock(Flow.class);
        Flow flow2 = Mockito.mock(Flow.class);
        when(module.getFlows()).thenReturn(List.of(flow1, flow2));

        doThrow(new H2DatabaseValidationException("The database is invalid!", new Exception()))
                .doNothing()
                .when(this.mockH2DatabaseValidator).runDatabaseValidationTest(anyString(), anyInt());

        H2DatabaseBackup h2DatabaseBackup = new H2DatabaseBackup();
        h2DatabaseBackup.setDbUrl("jdbc:h2:tcp://localhost:"+port+"/"+DATABASE_DIRECTORY+"/esb;IFEXISTS=FALSE");
        h2DatabaseBackup.setUsername("sa");
        h2DatabaseBackup.setPassword("sa");
        h2DatabaseBackup.setNumOfBackupsToRetain(3);
        h2DatabaseBackup.setDbBackupBaseDirectory(DATABASE_DIRECTORY);

        H2BackupServiceImpl h2BackupService = new H2BackupServiceImpl(h2DatabaseBackup
                , module, true, 5
                , 5, this.mockH2DatabaseValidator
                , H2DatabaseBackupManifestService.instance(h2DatabaseBackup.getDbBackupBaseDirectory()
                , H2ConnectionUrlUtils.getDatabaseName(h2DatabaseBackup.getDbUrl())));

        h2BackupService.backup();
        Thread.sleep(1500);

        H2DatabaseBackupManifest h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNotNull(h2DatabaseBackupManifest);
        Assert.assertEquals(1, h2DatabaseBackupManifest.getRetryCount());
        Assert.assertEquals("The database is invalid!", h2DatabaseBackupManifest.getFailureMessage());
        Assert.assertEquals("esb", h2DatabaseBackupManifest.getBackupName());

        h2BackupService.backup();
        Thread.sleep(1500);

        h2DatabaseBackupManifest = h2BackupService.getH2DatabaseBackupManifestService().find();

        Assert.assertNull(h2DatabaseBackupManifest);

        verify(this.mockH2DatabaseValidator, times(2)).runDatabaseValidationTest(anyString(), anyInt());

        verifyNoMoreInteractions(this.mockH2DatabaseValidator);
        verifyNoMoreInteractions(this.module);
        verifyNoMoreInteractions(flow1);
        verifyNoMoreInteractions(flow2);
    }



    @Test
    public void test_assert_housekeeping_job_collection_loaded_successfully() {
        Assert.assertEquals(1, this.housekeepingJobs.size());
    }
}
