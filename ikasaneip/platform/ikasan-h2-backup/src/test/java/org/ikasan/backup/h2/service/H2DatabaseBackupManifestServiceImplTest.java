package org.ikasan.backup.h2.persistence.service;

import org.ikasan.backup.h2.persistence.dao.H2DatabaseBackupManifestPersistenceDao;
import org.ikasan.backup.h2.persistence.dao.H2DatabaseBackupManifestPersistenceDaoImpl;
import org.ikasan.backup.h2.persistence.model.H2DatabaseBackupManifest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test methods for H2DatabaseBackupManifestServiceImplTest
 */
public class H2DatabaseBackupManifestServiceImplTest {

    private static final String PERSISTENCE_DIR = "./target/persistence";

    @Before
    public void setUp() {
        deleteFilesInDirectory();
    }


    /**
     * Test the save method of H2DatabaseBackupManifestServiceImpl
     */
    @Test
    public void test_save_and_find_success() {
        H2DatabaseBackupManifestService h2DatabaseBackupManifestService
                = H2DatabaseBackupManifestService.instance(PERSISTENCE_DIR, "ESB");

        H2DatabaseBackupManifest h2DatabaseBackupManifest = new H2DatabaseBackupManifest();
        h2DatabaseBackupManifest.setBackupName("ESB");
        h2DatabaseBackupManifest.setFailureMessage("Error has occurred!");
        h2DatabaseBackupManifest.setRetryCount(1);
        h2DatabaseBackupManifest.setFailureTimestamp(1234567890L);

        h2DatabaseBackupManifestService.save(h2DatabaseBackupManifest);

        H2DatabaseBackupManifest found = h2DatabaseBackupManifestService.find();

        Assert.assertEquals("ESB", found.getBackupName());
        Assert.assertEquals("Error has occurred!", found.getFailureMessage());
        Assert.assertEquals(1, found.getRetryCount());
        Assert.assertEquals(1234567890L, found.getFailureTimestamp());
    }

//    @Test
//    public void test_save_and_delete_success() {
//        H2DatabaseBackupManifestService h2DatabaseBackupManifestService
//                = H2DatabaseBackupManifestService.instance(PERSISTENCE_DIR, "ESB");
//
//        H2DatabaseBackupManifest h2DatabaseBackupManifest = new H2DatabaseBackupManifest();
//        h2DatabaseBackupManifest.setBackupName("ESB");
//        h2DatabaseBackupManifest.setFailureMessage("Error has occurred!");
//        h2DatabaseBackupManifest.setRetryCount(1);
//        h2DatabaseBackupManifest.setFailureTimestamp(1234567890L);
//
//        h2DatabaseBackupManifestService.save(h2DatabaseBackupManifest);
//
//        H2DatabaseBackupManifest found = h2DatabaseBackupManifestService.find();
//
//        Assert.assertNotNull(found);
//
//        h2DatabaseBackupManifestService.delete();
//
//        found = h2DatabaseBackupManifestService.find();
//
//        Assert.assertNull(found);
//    }
//
//
//    // Helper method to delete all files between tests to provide isolation
    private void deleteFilesInDirectory() {
//        Path directoryPath = Paths.get(PERSISTENCE_DIR);
//        File directory = directoryPath.toFile();
//        if (directory.exists()) {
//            for (File file : directory.listFiles()) {
//                file.delete();
//            }
//        }
    }


}