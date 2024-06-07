package org.ikasan.cli.shell.migration.dao;

import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.model.MigrationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Test methods for KryoMigrationPersistenceDaoImpl
 */
class MigrationPersistenceDaoImplTest {

    private static final String PERSISTENCE_DIR = "./persistence";

    @BeforeEach
    void setUp() {
        deleteFilesInDirectory();
    }


    /**
     * Test the save method of KryoMigrationPersistenceDaoImpl
     */
    @Test
    void save_and_find_success() {
        MigrationPersistenceDaoImpl persistenceDao = new MigrationPersistenceDaoImpl("testDir");
        IkasanMigration migration = new IkasanMigration(MigrationType.H2_MIGRATION, "1.0", "2.0", "EAI"
            , 1234);

        persistenceDao.save(migration);

        IkasanMigration returnedMigration = persistenceDao.find(MigrationType.H2_MIGRATION, "1.0", "2.0", "EAI");
        assertEquals(migration, returnedMigration, "Save method did not correctly save the migration object.");
    }

    @Test
    void given_ikasan_migration_exists_when_find_then_successful() {
        MigrationPersistenceDaoImpl dao = new MigrationPersistenceDaoImpl(PERSISTENCE_DIR);
        String type = MigrationType.H2_MIGRATION;
        String sourceVersion = "1.0";
        String targetVersion = "2.0";

        // Assume that ikasanMigration file is created in the persistence directory
        IkasanMigration ikasanMigration = new IkasanMigration(type, sourceVersion, targetVersion, "EAI", System.currentTimeMillis());
        dao.save(ikasanMigration);

        IkasanMigration foundIkasanMigration = dao.find(type, sourceVersion, targetVersion, "EAI");

        assertNotNull(foundIkasanMigration);
        assertEquals(ikasanMigration, foundIkasanMigration);
    }

    @Test
    void test_find_given_ikasan_migration_does_not_exists_then_null_is_returned() {
        MigrationPersistenceDaoImpl dao = new MigrationPersistenceDaoImpl(PERSISTENCE_DIR);
        String type = MigrationType.H2_MIGRATION;
        String sourceVersion = "3.0";
        String targetVersion = "4.0";

        // No IkasanMigration file is created before the find method is called
        IkasanMigration foundIkasanMigration = dao.find(type, sourceVersion, targetVersion, "EAI");

        assertNull(foundIkasanMigration);
    }

    /**
     * Test the save method of KryoMigrationPersistenceDaoImpl
     */
    @Test
    void save_and_find_delete_success() {
        MigrationPersistenceDaoImpl persistenceDao = new MigrationPersistenceDaoImpl("testDir");
        IkasanMigration migration = new IkasanMigration(MigrationType.H2_MIGRATION, "1.0", "2.0", "EAI"
            , 1234);

        persistenceDao.save(migration);

        IkasanMigration returnedMigration = persistenceDao.find(MigrationType.H2_MIGRATION, "1.0", "2.0", "EAI");
        assertEquals(migration, returnedMigration, "Save method did not correctly save the migration object.");

        persistenceDao.delete(MigrationType.H2_MIGRATION, "1.0", "2.0", "EAI");

        returnedMigration = persistenceDao.find(MigrationType.H2_MIGRATION, "1.0", "2.0", "EAI");

        assertNull(returnedMigration);
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