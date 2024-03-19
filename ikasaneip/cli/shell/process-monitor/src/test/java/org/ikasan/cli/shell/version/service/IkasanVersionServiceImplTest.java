package org.ikasan.cli.shell.version.service;

import org.ikasan.cli.shell.migration.dao.MigrationPersistenceDaoImpl;
import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.model.MigrationType;
import org.ikasan.cli.shell.version.dao.IkasanVersionPersistenceDao;
import org.ikasan.cli.shell.version.dao.IkasanVersionPersistenceDaoImpl;
import org.ikasan.cli.shell.version.model.IkasanVersion;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test methods for KryoMigrationPersistenceDaoImpl
 */
class IkasanVersionServiceImplTest {

    private static final String PERSISTENCE_DIR = "./persistence";

    @BeforeEach
    void setUp() {
        deleteFilesInDirectory();
    }


    /**
     * Test the save method of KryoMigrationPersistenceDaoImpl
     */
    @Test
    void test_save_and_find_success() {
        IkasanVersionPersistenceDao persistenceDao = new IkasanVersionPersistenceDaoImpl(PERSISTENCE_DIR);
        IkasanVersionService ikasanVersionService = new IkasanVersionServiceImpl(persistenceDao);

        ikasanVersionService.writeVersion("1.0.0");

        IkasanVersion ikasanVersion = ikasanVersionService.find();

        Assert.assertEquals("1.0.0", ikasanVersion.getVersion());
    }

    @Test
    void test_save_and_delete_success() {
        IkasanVersionPersistenceDao persistenceDao = new IkasanVersionPersistenceDaoImpl(PERSISTENCE_DIR);
        IkasanVersionService ikasanVersionService = new IkasanVersionServiceImpl(persistenceDao);

        ikasanVersionService.writeVersion("1.0.0");

        ikasanVersionService.delete();

        IkasanVersion ikasanVersion = ikasanVersionService.find();

        Assert.assertNull(ikasanVersion);
    }


    // Helper method to delete all files between tests to provide isolation
    private void deleteFilesInDirectory() {
        Path directoryPath = Paths.get(PERSISTENCE_DIR);
        File directory = directoryPath.toFile();
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }


}