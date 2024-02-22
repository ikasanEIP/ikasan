package org.ikasan.cli.shell.migration.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.model.MigrationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Test methods for KryoMigrationPersistenceDaoImpl
 */
class KryoMigrationPersistenceDaoImplTest {

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
        KryoMigrationPersistenceDaoImpl persistenceDao = new KryoMigrationPersistenceDaoImpl("testDir");
        IkasanMigration migration = new IkasanMigration(MigrationType.H2_MIGRATION, "1.0", "2.0"
            , 1234);

        persistenceDao.save(migration);

        IkasanMigration returnedMigration = persistenceDao.find(MigrationType.H2_MIGRATION, "1.0", "2.0");
        assertEquals(migration, returnedMigration, "Save method did not correctly save the migration object.");
    }

    @Test
    void given_ikasan_migration_exists_when_find_then_successful() {
        KryoMigrationPersistenceDaoImpl dao = new KryoMigrationPersistenceDaoImpl(PERSISTENCE_DIR);
        String type = MigrationType.H2_MIGRATION;
        String sourceVersion = "1.0";
        String targetVersion = "2.0";

        // Assume that ikasanMigration file is created in the persistence directory
        IkasanMigration ikasanMigration = new IkasanMigration(type, sourceVersion, targetVersion, System.currentTimeMillis());
        dao.save(ikasanMigration);

        IkasanMigration foundIkasanMigration = dao.find(type, sourceVersion, targetVersion);

        assertNotNull(foundIkasanMigration);
        assertEquals(ikasanMigration, foundIkasanMigration);
    }

    @Test
    void test_find_given_ikasan_migration_does_not_exists_then_null_is_returned() {
        KryoMigrationPersistenceDaoImpl dao = new KryoMigrationPersistenceDaoImpl(PERSISTENCE_DIR);
        String type = MigrationType.H2_MIGRATION;
        String sourceVersion = "3.0";
        String targetVersion = "4.0";

        // No IkasanMigration file is created before the find method is called
        IkasanMigration foundIkasanMigration = dao.find(type, sourceVersion, targetVersion);

        assertNull(foundIkasanMigration);
    }

    /**
     * Test the save method of KryoMigrationPersistenceDaoImpl
     */
    @Test
    void save_and_find_delete_success() {
        KryoMigrationPersistenceDaoImpl persistenceDao = new KryoMigrationPersistenceDaoImpl("testDir");
        IkasanMigration migration = new IkasanMigration(MigrationType.H2_MIGRATION, "1.0", "2.0"
            , 1234);

        persistenceDao.save(migration);

        IkasanMigration returnedMigration = persistenceDao.find(MigrationType.H2_MIGRATION, "1.0", "2.0");
        assertEquals(migration, returnedMigration, "Save method did not correctly save the migration object.");

        persistenceDao.delete(MigrationType.H2_MIGRATION, "1.0", "2.0");

        returnedMigration = persistenceDao.find(MigrationType.H2_MIGRATION, "1.0", "2.0");

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