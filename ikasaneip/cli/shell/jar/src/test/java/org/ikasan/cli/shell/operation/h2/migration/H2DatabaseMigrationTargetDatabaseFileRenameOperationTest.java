package org.ikasan.cli.shell.operation.h2.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class H2DatabaseMigrationTargetDatabaseFileRenameOperationTest {

    @TempDir
    Path testDirectory;

    /**
     * Test of the execute method of the H2DatabaseMigrationTargetDatabaseFileRenameOperation class.
     * This method should rename the newly created target database to the original database name. 
     */
    @Test
    void testExecute() {
        String databaseName = "TestDatabase";
        String persistenceDirectory = testDirectory.toString();

        // Create new database file
        Path newDatabaseFilePath = testDirectory.resolve(databaseName + "-new.mv.db");
        try {
            Files.createFile(newDatabaseFilePath);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        H2DatabaseMigrationTargetDatabaseFileRenameOperation operation =
            new H2DatabaseMigrationTargetDatabaseFileRenameOperation(persistenceDirectory, databaseName);

        String result = operation.execute();

        // Verify if new database file is renamed to original
        Path originalDatabaseFilePath = testDirectory.resolve(databaseName + ".mv.db");
        assertTrue(Files.exists(originalDatabaseFilePath));
        String expectedMessage = String.format("Successfully renamed newly created target database to the original database name from [%s] to [%s]",
            persistenceDirectory + "/" + databaseName + "-new.mv.db",
            persistenceDirectory + "/" + databaseName + ".mv.db");
        assertEquals(expectedMessage, result);
    }

    /**
     * Test of the execute method when the new datase file is not found. 
     * The method should throw RuntimeException.
     */
    @Test
    void testExecuteNewFileNotFound() {
        String databaseName = "TestDatabase";
        String persistenceDirectory = testDirectory.toString();
        H2DatabaseMigrationTargetDatabaseFileRenameOperation operation =
            new H2DatabaseMigrationTargetDatabaseFileRenameOperation(persistenceDirectory, databaseName);
        assertThrows(RuntimeException.class, operation::execute);
    }
}