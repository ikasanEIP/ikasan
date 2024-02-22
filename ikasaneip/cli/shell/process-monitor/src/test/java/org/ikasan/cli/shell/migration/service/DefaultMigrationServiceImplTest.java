package org.ikasan.cli.shell.migration.service;

import org.ikasan.cli.shell.migration.dao.MigrationPersistenceDao;
import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.model.MigrationType;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Test class for DefaultMigrationServiceImpl.
 * This class tests the functionality of the save method of DefaultMigrationServiceImpl.
 */
public class DefaultMigrationServiceImplTest {

    /**
     * Tests that an IkasanMigration object is saved properly using the MigrationPersistenceDao.
     */
    @Test
    public void test_save_success() {
        // Mock dependencies
        MigrationPersistenceDao migrationPersistenceDao = mock(MigrationPersistenceDao.class);
        IkasanMigration ikasanMigration = mock(IkasanMigration.class);

        // Setup SUT
        DefaultMigrationServiceImpl defaultMigrationService = new DefaultMigrationServiceImpl(migrationPersistenceDao);
        
        // Perform action
        defaultMigrationService.save(ikasanMigration);
        
        // Assertion
        verify(migrationPersistenceDao, times(1)).save(ikasanMigration);
    }

    /**
     * Tests that an IllegalArgumentException is thrown when a null MigrationPersistenceDao
     *
     * is passed to the DefaultMigrationServiceImpl constructor.
     */
    @Test
    public void test_save_throws_illegal_argument_exception() {
        // Assertion
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DefaultMigrationServiceImpl(null);
        });
    }

    /**
     * Method to test the success scenario of the {@link DefaultMigrationServiceImpl#find(String, String, String)} method.
     */
    @Test
    public void test_find_success() {
        // Arrange
        MigrationPersistenceDao migrationPersistenceDao = mock(MigrationPersistenceDao.class);
        DefaultMigrationServiceImpl service = new DefaultMigrationServiceImpl(migrationPersistenceDao);
        IkasanMigration migration = new IkasanMigration(MigrationType.H2_MIGRATION, "v1.0.0", "v2.0.0", System.currentTimeMillis());
        when(migrationPersistenceDao.find(MigrationType.H2_MIGRATION, "v1.0.0", "v2.0.0")).thenReturn(migration);

        // Act
        IkasanMigration result = service.find(MigrationType.H2_MIGRATION, "v1.0.0", "v2.0.0");

        // Assert
        assertNotNull(result, "The found migration should not be null.");
        assertEquals(MigrationType.H2_MIGRATION, result.getType());
        assertEquals("v1.0.0", result.getSourceVersion());
        assertEquals("v2.0.0", result.getTargetVersion());

        verify(migrationPersistenceDao, times(1)).find(MigrationType.H2_MIGRATION
            , "v1.0.0", "v2.0.0");
    }

    /**
     * Test method for {@link DefaultMigrationServiceImpl#delete(String, String, String)}.
     *
     * Verifies that the delete method on the MigrationPersistenceDao is called with the correct parameters.
     */
    @Test
    public void test_delete_success() {
        // Initialize mocks
        MigrationPersistenceDao migrationPersistenceDao = mock(MigrationPersistenceDao.class);

        // Create an instance with the mocked DAO
        DefaultMigrationServiceImpl defaultMigrationService = new DefaultMigrationServiceImpl(migrationPersistenceDao);

        // Test data
        String type = MigrationType.H2_MIGRATION;
        String sourceVersion = "1.0";
        String targetVersion = "2.0";

        // Call method under test
        defaultMigrationService.delete(type, sourceVersion, targetVersion);

        // Verify that the delete method on DAO was called with the right parameters
        verify(migrationPersistenceDao, times(1)).delete(MigrationType.H2_MIGRATION, sourceVersion, targetVersion);
    }

    /**
     * This method is used to test the "delete" method in the DefaultMigrationServiceImpl class.
     * It verifies that the delete method on the MigrationPersistenceDao is called with the correct parameters.
     */
    @Test
    public void test_delete_nulls() {
        // Initialize mocks
        MigrationPersistenceDao migrationPersistenceDao = mock(MigrationPersistenceDao.class);

        // Create an instance with the mocked DAO
        DefaultMigrationServiceImpl defaultMigrationService = new DefaultMigrationServiceImpl(migrationPersistenceDao);

        // Call method under test with null strings
        assertThrows(IllegalArgumentException.class, () -> {
            defaultMigrationService.delete(null, null, null);
        });

        // Verify that the delete method on DAO was never called
        verify(migrationPersistenceDao,never()).delete(null, null, null);
    }
}