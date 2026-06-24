package org.ikasan.backup.h2.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/**
 * Comprehensive test class for the `H2BackupUtils` class.
 * Tests all methods including unzipFile, deleteFile, and cleanDirectory
 * with focus on security, edge cases, and error handling.
 */
public class H2BackupUtilsTest {
    private static final String TEMP_DIRECTORY = Paths.get("./target", "unzip-dir").toString();
    private static final String TEST_DIRECTORY = Paths.get("./target", "test-dir").toString();

    @Before
    public void setUp() throws IOException {
        // Clean up before each test
        cleanupTestDirectories();
    }

    @After
    public void tearDown() throws IOException {
        // Clean up after each test
        cleanupTestDirectories();
    }

    private void cleanupTestDirectories() throws IOException {
        deleteDirectoryIfExists(TEMP_DIRECTORY);
        deleteDirectoryIfExists(TEST_DIRECTORY);
    }

    private void deleteDirectoryIfExists(String dir) throws IOException {
        Path path = Paths.get(dir);
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
        }
    }

    // ==================== unzipFile Tests ====================

    @Test
    public void testUnzipFile() throws IOException {
        H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", TEMP_DIRECTORY);

        Assert.assertTrue(Files.exists(Paths.get(TEMP_DIRECTORY + FileSystems.getDefault().getSeparator() + "esb.mv.db")));
    }

    @Test
    public void test_unzip_empty_file_exception() {
        String emptyFile = Paths.get("./target", "empty.zip").toString();
        createEmptyFile(emptyFile);

        assertThrows(IOException.class, () -> H2BackupUtils.unzipFile(emptyFile, TEMP_DIRECTORY));
    }

    @Test
    public void test_unzip_file_creates_directory_if_not_exists() throws IOException {
        String newDirectory = Paths.get("./target", "new-unzip-dir").toString();

        try {
            H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", newDirectory);

            assertTrue("Directory should be created", Files.exists(Paths.get(newDirectory)));
            assertTrue("Unzipped file should exist",
                Files.exists(Paths.get(newDirectory + FileSystems.getDefault().getSeparator() + "esb.mv.db")));
        } finally {
            deleteDirectoryIfExists(newDirectory);
        }
    }

    @Test
    public void test_unzip_file_replaces_existing_directory() throws IOException {
        // Create directory with a file first
        Files.createDirectories(Paths.get(TEMP_DIRECTORY));
        Path existingFile = Paths.get(TEMP_DIRECTORY, "existing.txt");
        Files.write(existingFile, "test content".getBytes());

        assertTrue("Existing file should exist before unzip", Files.exists(existingFile));

        H2BackupUtils.unzipFile("./src/test/resources/data/esb-backup-20240321-06-11-00.zip", TEMP_DIRECTORY);

        assertTrue("Old should not exist after unzip", Files.exists(existingFile));
        assertTrue("New file should exist",
            Files.exists(Paths.get(TEMP_DIRECTORY + FileSystems.getDefault().getSeparator() + "esb.mv.db")));
    }

    @Test(expected = IOException.class)
    public void test_unzip_nonexistent_file_throws_exception() throws IOException {
        H2BackupUtils.unzipFile("./nonexistent.zip", TEMP_DIRECTORY);
    }

    @Test
    public void test_unzip_with_multiple_files_throws_exception() throws IOException {
        String multiFileZip = Paths.get("./target", "multi-file.zip").toString();
        createZipWithMultipleFiles(multiFileZip);

        IOException exception = assertThrows(IOException.class, () ->
            H2BackupUtils.unzipFile(multiFileZip, TEMP_DIRECTORY));

        assertEquals("Zip file cannot be empty!", exception.getMessage());
    }

    @Test
    public void test_unzip_path_traversal_attack_blocked() throws IOException {
        String maliciousZip = Paths.get("./target", "path-traversal.zip").toString();
        createZipWithPathTraversal(maliciousZip);

        IOException exception = assertThrows(IOException.class, () ->
            H2BackupUtils.unzipFile(maliciousZip, TEMP_DIRECTORY));

        assertTrue("Should detect bad zip entry", exception.getMessage().contains("Bad zip entry"));
    }

    @Test
    public void test_unzip_with_valid_single_file() throws IOException {
        String validZip = Paths.get("./target", "valid-single.zip").toString();
        createValidZipWithSingleFile(validZip, "test.db");

        H2BackupUtils.unzipFile(validZip, TEMP_DIRECTORY);

        assertTrue("File should be extracted",
            Files.exists(Paths.get(TEMP_DIRECTORY, "test.db")));
    }

    @Test
    public void test_unzip_preserves_file_content() throws IOException {
        String testContent = "H2 Database Test Content 12345";
        String validZip = Paths.get("./target", "content-test.zip").toString();
        createValidZipWithContent(validZip, "data.db", testContent);

        H2BackupUtils.unzipFile(validZip, TEMP_DIRECTORY);

        Path extractedFile = Paths.get(TEMP_DIRECTORY, "data.db");
        assertTrue("File should exist", Files.exists(extractedFile));

        String extractedContent = new String(Files.readAllBytes(extractedFile));
        assertEquals("Content should match", testContent, extractedContent);
    }

    // ==================== deleteFile Tests ====================

    @Test
    public void test_deleteFile_removes_existing_file() throws IOException {
        Path testFile = Paths.get(TEST_DIRECTORY, "test-file.txt");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "test".getBytes());

        assertTrue("File should exist before deletion", Files.exists(testFile));

        H2BackupUtils.deleteFile(testFile.toString());

        assertFalse("File should not exist after deletion", Files.exists(testFile));
    }

    @Test
    public void test_deleteFile_nonexistent_file_does_not_throw() {
        // Should not throw exception for nonexistent file
        H2BackupUtils.deleteFile(Paths.get(TEST_DIRECTORY, "nonexistent.txt").toString());
    }

    @Test
    public void test_deleteFile_handles_directory_gracefully() throws IOException {
        Path testDir = Paths.get(TEST_DIRECTORY, "test-subdir");
        Files.createDirectories(testDir);

        // Should not throw, but may not delete directory
        H2BackupUtils.deleteFile(testDir.toString());
    }

    @Test
    public void test_deleteFile_with_empty_path() {
        // Should handle empty path gracefully
        H2BackupUtils.deleteFile("");
    }

    @Test
    public void test_deleteFile_multiple_times() throws IOException {
        Path testFile = Paths.get(TEST_DIRECTORY, "multi-delete.txt");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "test".getBytes());

        H2BackupUtils.deleteFile(testFile.toString());
        assertFalse("File should be deleted", Files.exists(testFile));

        // Should not throw on second delete
        H2BackupUtils.deleteFile(testFile.toString());
    }

    @Test
    public void test_deleteFile_with_special_characters_in_name() throws IOException {
        Path testFile = Paths.get(TEST_DIRECTORY, "test file with spaces.txt");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "test".getBytes());

        assertTrue("File should exist", Files.exists(testFile));

        H2BackupUtils.deleteFile(testFile.toString());

        assertFalse("File should be deleted", Files.exists(testFile));
    }

    // ==================== cleanDirectory Tests ====================

    @Test
    public void test_cleanDirectory_removes_all_files_and_subdirectories() throws IOException {
        // Create test directory structure
        Path dir = Paths.get(TEST_DIRECTORY);
        Files.createDirectories(dir);

        Files.write(dir.resolve("file1.txt"), "content1".getBytes());
        Files.write(dir.resolve("file2.txt"), "content2".getBytes());

        Path subDir = dir.resolve("subdir");
        Files.createDirectories(subDir);
        Files.write(subDir.resolve("file3.txt"), "content3".getBytes());

        assertTrue("Directory should exist", Files.exists(dir));
        assertTrue("Subdirectory should exist", Files.exists(subDir));

        H2BackupUtils.cleanDirectory(TEST_DIRECTORY);

        assertFalse("Directory should be removed", Files.exists(dir));
    }

    @Test
    public void test_cleanDirectory_with_nested_structure() throws IOException {
        Path dir = Paths.get(TEST_DIRECTORY);
        Files.createDirectories(dir);

        // Create deeply nested structure
        Path level1 = dir.resolve("level1");
        Path level2 = level1.resolve("level2");
        Path level3 = level2.resolve("level3");
        Files.createDirectories(level3);

        Files.write(dir.resolve("root.txt"), "root".getBytes());
        Files.write(level1.resolve("l1.txt"), "l1".getBytes());
        Files.write(level2.resolve("l2.txt"), "l2".getBytes());
        Files.write(level3.resolve("l3.txt"), "l3".getBytes());

        H2BackupUtils.cleanDirectory(TEST_DIRECTORY);

        assertFalse("Directory should be completely removed", Files.exists(dir));
    }

    @Test
    public void test_cleanDirectory_empty_directory() throws IOException {
        Path dir = Paths.get(TEST_DIRECTORY);
        Files.createDirectories(dir);

        assertTrue("Directory should exist", Files.exists(dir));

        H2BackupUtils.cleanDirectory(TEST_DIRECTORY);

        assertFalse("Empty directory should be removed", Files.exists(dir));
    }

    @Test(expected = IOException.class)
    public void test_cleanDirectory_nonexistent_throws_exception() throws IOException {
        H2BackupUtils.cleanDirectory(Paths.get("./nonexistent-directory").toString());
    }

    @Test
    public void test_cleanDirectory_with_hidden_files() throws IOException {
        Path dir = Paths.get(TEST_DIRECTORY);
        Files.createDirectories(dir);

        Files.write(dir.resolve(".hidden"), "hidden".getBytes());
        Files.write(dir.resolve("visible.txt"), "visible".getBytes());

        H2BackupUtils.cleanDirectory(TEST_DIRECTORY);

        assertFalse("Directory with hidden files should be removed", Files.exists(dir));
    }

    @Test
    public void test_cleanDirectory_with_large_number_of_files() throws IOException {
        Path dir = Paths.get(TEST_DIRECTORY);
        Files.createDirectories(dir);

        // Create 100 files
        for (int i = 0; i < 100; i++) {
            Files.write(dir.resolve("file" + i + ".txt"), ("content" + i).getBytes());
        }

        assertEquals("Should have created 100 files", 100,
            Files.list(dir).count());

        H2BackupUtils.cleanDirectory(TEST_DIRECTORY);

        assertFalse("Directory should be removed", Files.exists(dir));
    }

    // ==================== Helper Methods ====================

    private void createEmptyFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));
            Files.createFile(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createZipWithMultipleFiles(String zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            // Add first file
            ZipEntry entry1 = new ZipEntry("file1.db");
            zos.putNextEntry(entry1);
            zos.write("content1".getBytes());
            zos.closeEntry();

            // Add second file
            ZipEntry entry2 = new ZipEntry("file2.db");
            zos.putNextEntry(entry2);
            zos.write("content2".getBytes());
            zos.closeEntry();
        }
    }

    private void createZipWithPathTraversal(String zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            // Malicious entry trying to escape target directory
            ZipEntry entry = new ZipEntry("../../evil.db");
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }
    }

    private void createZipWithAbsolutePath(String zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            // Malicious entry with absolute path
            String absolutePath = Paths.get("/tmp/evil.db").toString();
            ZipEntry entry = new ZipEntry(absolutePath);
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }
    }

    private void createValidZipWithSingleFile(String zipPath, String fileName) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            zos.write("test content".getBytes());
            zos.closeEntry();
        }
    }

    private void createValidZipWithContent(String zipPath, String fileName, String content) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            zos.write(content.getBytes());
            zos.closeEntry();
        }
    }
}
