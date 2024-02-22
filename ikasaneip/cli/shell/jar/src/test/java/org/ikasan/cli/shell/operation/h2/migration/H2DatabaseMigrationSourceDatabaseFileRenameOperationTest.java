package org.ikasan.cli.shell.operation.h2.migration;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class H2DatabaseMigrationSourceDatabaseFileRenameOperationTest {
    String tempDirectory = "./migrate";

    @Test
    void testExecuteSuccessfulMove() {
        String dbName = "testDB";

        // Create a temp source db file to be moved
        try {
            Files.createDirectory(Paths.get(tempDirectory));
            Files.createFile(Paths.get(tempDirectory + "/" + dbName + ".mv.db"));
        } catch (IOException e) {
            fail("Unable to setup test condition. Could not create a source db file.");
        }

        H2DatabaseMigrationSourceDatabaseFileRenameOperation operation = 
            new H2DatabaseMigrationSourceDatabaseFileRenameOperation("1.4.200", tempDirectory, dbName);
        
        try {
            operation.execute();
            File backupFilesDir = new File(tempDirectory);
            AtomicBoolean backupFound = new AtomicBoolean(false);
            Arrays.stream(backupFilesDir.listFiles()).forEach(file -> {
                if(file.getName().contains(dbName + ".mv.db-backup-1.4.200")) {
                    backupFound.set(true);
                }
            });

            assertTrue(backupFound.get(), "Backup file does not exist after execute.");
        } catch (RuntimeException e) {
            fail("Unexpected exception thrown during execute.", e);
        } finally {
            cleanupTestFiles();
        }
    }

    // Method to delete files created during test
    @After
    public void cleanupTestFiles() {
        try {
            FileUtils.forceDelete(new File(this.tempDirectory));
        } catch (IOException e) {
            System.out.println("Exception during test cleanup: " + e.getMessage());
        }
    }

    @Test
    void testExecuteMoveFails() {
        String tempDirectory = System.getProperty("java.io.tmpdir");
        String dbName = "nonexistentDB";
        H2DatabaseMigrationSourceDatabaseFileRenameOperation operation = 
            new H2DatabaseMigrationSourceDatabaseFileRenameOperation("1.4.200", tempDirectory, dbName);
        
        assertThrows(RuntimeException.class, operation::execute,
            "Expected RuntimeException but none was thrown.");
    }
}