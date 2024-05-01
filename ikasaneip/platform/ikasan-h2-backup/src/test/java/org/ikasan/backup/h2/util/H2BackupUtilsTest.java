package org.ikasan.backup.h2.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertThrows;

/**
 *
 * This test class is for the `H2BackupUtils` class with a focus on the `unzipFile` method.
 * The `unzipFile` method takes in the full path of the backup file to unzip and
 * the directory where the file will be unzipped to, then unzips the file into the specific directory.
 *
 */
public class H2BackupUtilsTest {
    private static final String TEMP_DIRECTORY = Paths.get("./target", "unzip-dir").toString();

    @Test
    public void testUnzipFile() throws IOException {
        H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", TEMP_DIRECTORY);

        Assert.assertTrue(Files.exists(Paths.get(TEMP_DIRECTORY+ FileSystems.getDefault().getSeparator()+"esb.mv.db")));
    }

    @Test
    public void test_unzip_empty_file_exception() {
        String emptyFile = Paths.get("./target", "empty.zip").toString();
        createEmptyFile(emptyFile);

        assertThrows(IOException.class, () -> H2BackupUtils.unzipFile(emptyFile, TEMP_DIRECTORY));
    }

    private void createEmptyFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));

            Files.createFile(Paths.get(fileName));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
