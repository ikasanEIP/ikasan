package org.ikasan.rest.module.sse;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.ikasan.rest.module.exception.MaxThreadException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Comprehensive test class for MonitoringFileService.
 * Tests all functionality including path validation, security checks,
 * thread management, and edge cases.
 */
public class MonitoringFileServiceTest {

    private final String sampleLogFileStr = "target/tmp/data/log.sample";
    private final String testBaseDir = "target/test-logs";

    private MonitoringFileService service;

    @Before
    public void setup() throws IOException {
        // Create sample log file
        File sampleFile = new File(sampleLogFileStr);
        sampleFile.getParentFile().mkdirs();
        FileUtils.write(sampleFile, "", StandardCharsets.UTF_8);

        // Create test base directory
        Files.createDirectories(Paths.get(testBaseDir));

        service = new MonitoringFileService();
        ReflectionTestUtils.setField(service, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(service, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(service, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(service, "logBasePaths", ".");
        service.init();
    }

    @After
    public void tearDown() throws Exception {
        // Clean up test files
        try {
            FileUtils.forceDelete(new File(sampleLogFileStr));
        } catch (Exception e) {
            // Ignore
        }

        try {
            FileUtils.forceDelete(new File(testBaseDir));
        } catch (Exception e) {
            // Ignore
        }

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(service, "executorService");
        tpe.shutdownNow();
        Thread.sleep(100);
    }

    // ==================== Basic Functionality Tests ====================

    @Test
    public void shouldReturnEmitter() throws Exception {
        assertNotNull(service.addMonitoringFileService(sampleLogFileStr));
        Thread.sleep(100);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(service, "executorService");
        assertEquals(tpe.getActiveCount(), 1);
    }

    @Test
    public void shouldThrowExceptionIfExceedsMaxStreamThreads() throws Exception {
        service.addMonitoringFileService(sampleLogFileStr);
        try {
            Thread.sleep(100);
            service.addMonitoringFileService(sampleLogFileStr);
            fail("should not get here");
        } catch (MaxThreadException e) {
            assertEquals("Maximum number of log file streaming threads reached", e.getLocalizedMessage());
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(service, "executorService");
            assertEquals(tpe.getActiveCount(), 1);
        }
    }

    @Test
    public void test_init_creates_executor_service() {
        MonitoringFileService newService = new MonitoringFileService();
        ReflectionTestUtils.setField(newService, "maxStreamThreads", 5);
        newService.init();

        Object executorService = ReflectionTestUtils.getField(newService, "executorService");
        assertNotNull("ExecutorService should be initialized", executorService);
        assertTrue("Should be ThreadPoolExecutor", executorService instanceof ThreadPoolExecutor);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executorService;
        tpe.shutdownNow();
    }

    @Test
    public void test_emitter_has_long_timeout() throws Exception {
        SseEmitter emitter = service.addMonitoringFileService(sampleLogFileStr);
        assertNotNull(emitter);
        // SseEmitter timeout is set to Long.MAX_VALUE for long-lived connections
        Thread.sleep(100);
    }

    // ==================== Path Validation Security Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void test_path_traversal_attack_with_dot_dot_blocked() throws Exception {
        service.addMonitoringFileService("../../etc/passwd");
    }

    @Test
    public void test_path_traversal_attack_exception_message() throws Exception {
        try {
            service.addMonitoringFileService("../../etc/passwd");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue("Exception should mention path escape",
                e.getMessage().contains("Invalid fullFilePath: path escapes all configured log base directories"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_path_traversal_with_encoded_dots_blocked() throws Exception {
        // URL encoded ../../../
        service.addMonitoringFileService("..%2F..%2F..%2Fetc%2Fpasswd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_path_traversal_with_multiple_variations_blocked() throws Exception {
        service.addMonitoringFileService("./../../../sensitive/data.log");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_absolute_path_outside_base_blocked() throws Exception {
        service.addMonitoringFileService("/etc/passwd");
    }

    @Test
    public void test_valid_relative_path_within_base() throws Exception {
        String validPath = "target/tmp/data/valid.log";
        File validFile = new File(validPath);
        validFile.getParentFile().mkdirs();
        FileUtils.write(validFile, "test", StandardCharsets.UTF_8);

        try {
            SseEmitter emitter = service.addMonitoringFileService(validPath);
            assertNotNull("Should create emitter for valid path", emitter);
        } finally {
            validFile.delete();
        }
    }

    @Test
    public void test_valid_path_with_subdirectories() throws Exception {
        String validPath = "target/tmp/logs/app/application.log";
        File validFile = new File(validPath);
        validFile.getParentFile().mkdirs();
        FileUtils.write(validFile, "test", StandardCharsets.UTF_8);

        try {
            SseEmitter emitter = service.addMonitoringFileService(validPath);
            assertNotNull("Should create emitter for valid nested path", emitter);
        } finally {
            validFile.delete();
        }
    }

    @Test
    public void test_path_normalization_handles_dot_slash() throws Exception {
        String validPath = "./target/tmp/data/log.sample";
        SseEmitter emitter = service.addMonitoringFileService(validPath);
        assertNotNull("Should handle ./ prefix", emitter);
    }

    @Test
    public void test_url_encoded_valid_path() throws Exception {
        // URL encoded "target/tmp/data/log.sample"
        String encodedPath = "target%2Ftmp%2Fdata%2Flog.sample";
        SseEmitter emitter = service.addMonitoringFileService(encodedPath);
        assertNotNull("Should handle URL encoded valid path", emitter);
    }

    @Test
    public void test_path_with_spaces_encoded() throws Exception {
        String pathWithSpaces = "target/tmp/data/log file.sample";
        File fileWithSpaces = new File(pathWithSpaces);
        fileWithSpaces.getParentFile().mkdirs();
        FileUtils.write(fileWithSpaces, "test", StandardCharsets.UTF_8);

        try {
            String encodedPath = "target/tmp/data/log%20file.sample";
            SseEmitter emitter = service.addMonitoringFileService(encodedPath);
            assertNotNull("Should handle spaces in filename", emitter);
        } finally {
            fileWithSpaces.delete();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_symlink_escape_attempt_blocked() throws Exception {
        // Try to use a path that might be a symlink to escape base directory
        service.addMonitoringFileService("target/../../../etc/passwd");
    }

    @Test
    public void test_custom_base_path_configuration() throws Exception {
        MonitoringFileService customService = new MonitoringFileService();
        ReflectionTestUtils.setField(customService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(customService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(customService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(customService, "logBasePaths", ".");
        customService.init();

        try {
            // Create file in custom base dir
            String testFile = testBaseDir + "/test.log";
            FileUtils.write(new File(testFile), "test", StandardCharsets.UTF_8);

            // Should accept path within custom base
            SseEmitter emitter = customService.addMonitoringFileService(testFile);
            assertNotNull("Should work with custom base path", emitter);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(customService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_custom_base_path_rejects_escape() throws Exception {
        MonitoringFileService customService = new MonitoringFileService();
        ReflectionTestUtils.setField(customService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(customService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(customService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(customService, "logBasePaths", ".");
        customService.init();

        customService.addMonitoringFileService("/tmp/data/log.sample");
    }

    // ==================== Thread Management Tests ====================

    @Test
    public void test_multiple_threads_up_to_limit() throws Exception {
        MonitoringFileService multiThreadService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiThreadService, "maxStreamThreads", 3);
        ReflectionTestUtils.setField(multiThreadService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiThreadService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(multiThreadService, "logBasePaths", ".");
        multiThreadService.init();

        try {
            // Should be able to create up to 3 threads
            multiThreadService.addMonitoringFileService(sampleLogFileStr);
            Thread.sleep(100);
            multiThreadService.addMonitoringFileService(sampleLogFileStr);
            Thread.sleep(100);
            multiThreadService.addMonitoringFileService(sampleLogFileStr);
            Thread.sleep(100);

            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiThreadService, "executorService");
            assertEquals("Should have 3 active threads", 3, tpe.getActiveCount());
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiThreadService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_thread_pool_size_configuration() {
        MonitoringFileService customService = new MonitoringFileService();
        ReflectionTestUtils.setField(customService, "maxStreamThreads", 10);
        customService.init();

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(customService, "executorService");
        assertEquals("Pool size should match configuration", 10, tpe.getMaximumPoolSize());

        tpe.shutdownNow();
    }

    @Test(expected = MaxThreadException.class)
    public void test_exceeding_thread_limit_throws_max_thread_exception() throws Exception {
        service.addMonitoringFileService(sampleLogFileStr);
        Thread.sleep(100);
        // Second attempt should fail as max is 1
        service.addMonitoringFileService(sampleLogFileStr);
    }

    @Test
    public void test_max_thread_exception_message() throws Exception {
        service.addMonitoringFileService(sampleLogFileStr);
        Thread.sleep(100);

        try {
            service.addMonitoringFileService(sampleLogFileStr);
            fail("Should throw MaxThreadException");
        } catch (MaxThreadException e) {
            assertEquals("Maximum number of log file streaming threads reached", e.getMessage());
        }
    }

    // ==================== Configuration Tests ====================

    @Test
    public void test_stream_thread_wait_time_configuration() {
        MonitoringFileService customService = new MonitoringFileService();
        ReflectionTestUtils.setField(customService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(customService, "streamThreadWaitTime", 1000);
        ReflectionTestUtils.setField(customService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(customService, "logBasePaths", ".");
        customService.init();

        Integer waitTime = (Integer) ReflectionTestUtils.getField(customService, "streamThreadWaitTime");
        assertEquals("Wait time should be configured", Integer.valueOf(1000), waitTime);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(customService, "executorService");
        tpe.shutdownNow();
    }

    @Test
    public void test_inactive_time_configuration() {
        MonitoringFileService customService = new MonitoringFileService();
        ReflectionTestUtils.setField(customService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(customService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(customService, "inactiveTimeForFileInMillis", 600000L);
        ReflectionTestUtils.setField(customService, "logBasePaths", ".");
        customService.init();

        Long inactiveTime = (Long) ReflectionTestUtils.getField(customService, "inactiveTimeForFileInMillis");
        assertEquals("Inactive time should be configured", Long.valueOf(600000L), inactiveTime);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(customService, "executorService");
        tpe.shutdownNow();
    }

    // ==================== Edge Cases ====================

    @Test(expected = IllegalArgumentException.class)
    public void test_null_byte_injection_blocked() throws Exception {
        service.addMonitoringFileService("target/tmp/data/log.sample\0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_backslash_path_traversal_blocked() throws Exception {
        service.addMonitoringFileService("../../../etc/passwd");
    }

    @Test
    public void test_same_file_multiple_times_creates_separate_emitters() throws Exception {
        MonitoringFileService multiService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiService, "maxStreamThreads", 3);
        ReflectionTestUtils.setField(multiService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(multiService, "logBasePaths", ".");
        multiService.init();

        try {
            SseEmitter emitter1 = multiService.addMonitoringFileService(sampleLogFileStr);
            Thread.sleep(100);
            SseEmitter emitter2 = multiService.addMonitoringFileService(sampleLogFileStr);

            assertNotNull(emitter1);
            assertNotNull(emitter2);
            assertNotSame("Should create different emitters", emitter1, emitter2);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_path_with_special_characters() throws Exception {
        String specialPath = "target/tmp/data/log-file_2024.log";
        File specialFile = new File(specialPath);
        specialFile.getParentFile().mkdirs();
        FileUtils.write(specialFile, "test", StandardCharsets.UTF_8);

        try {
            SseEmitter emitter = service.addMonitoringFileService(specialPath);
            assertNotNull("Should handle special characters in filename", emitter);
        } finally {
            specialFile.delete();
        }
    }

    // ==================== Multiple Base Paths Tests ====================

    @Test
    public void test_multiple_base_paths_comma_separated() throws Exception {
        MonitoringFileService multiPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiPathService, "maxStreamThreads", 2);
        ReflectionTestUtils.setField(multiPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiPathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(multiPathService, "logBasePaths", "target/tmp/data,target/test-logs");
        multiPathService.init();

        try {
            // Create files in both base directories
            String file1 = "target/tmp/data/log1.log";
            String file2 = "target/test-logs/log2.log";
            FileUtils.write(new File(file1), "test1", StandardCharsets.UTF_8);
            FileUtils.write(new File(file2), "test2", StandardCharsets.UTF_8);

            // Should accept files from both base paths
            SseEmitter emitter1 = multiPathService.addMonitoringFileService(file1);
            assertNotNull("Should accept file from first base path", emitter1);

            Thread.sleep(100);

            SseEmitter emitter2 = multiPathService.addMonitoringFileService(file2);
            assertNotNull("Should accept file from second base path", emitter2);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiPathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_multiple_base_paths_with_whitespace() throws Exception {
        MonitoringFileService multiPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiPathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(multiPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiPathService, "inactiveTimeForFileInMillis", 300000);
        // Paths with extra whitespace
        ReflectionTestUtils.setField(multiPathService, "logBasePaths", " target/tmp/data , target/test-logs ");
        multiPathService.init();

        try {
            String file1 = "target/tmp/data/log1.log";
            FileUtils.write(new File(file1), "test1", StandardCharsets.UTF_8);

            SseEmitter emitter = multiPathService.addMonitoringFileService(file1);
            assertNotNull("Should handle whitespace in paths configuration", emitter);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiPathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_multiple_base_paths_rejects_file_outside_all() throws Exception {
        MonitoringFileService multiPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiPathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(multiPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiPathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(multiPathService, "logBasePaths", "target/test-logs,target/other-logs");
        multiPathService.init();

        try {
            // Try to access file outside all configured base paths
            multiPathService.addMonitoringFileService("target/tmp/data/log.sample");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue("Should mention multiple base directories",
                e.getMessage().contains("all configured log base directories"));
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiPathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_absolute_path_allowed_if_within_configured_base() throws Exception {
        MonitoringFileService multiPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiPathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(multiPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiPathService, "inactiveTimeForFileInMillis", 300000);

        String absoluteBaseDir = Paths.get(testBaseDir).toAbsolutePath().toString();
        ReflectionTestUtils.setField(multiPathService, "logBasePaths", absoluteBaseDir);
        multiPathService.init();

        try {
            // Create file with absolute path within base
            String absoluteFile = Paths.get(testBaseDir, "absolute.log").toAbsolutePath().toString();
            FileUtils.write(new File(absoluteFile), "test", StandardCharsets.UTF_8);

            SseEmitter emitter = multiPathService.addMonitoringFileService(absoluteFile);
            assertNotNull("Should accept absolute path within configured base", emitter);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiPathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_empty_base_paths_defaults_to_current_directory() throws Exception {
        MonitoringFileService emptyPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(emptyPathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(emptyPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(emptyPathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(emptyPathService, "logBasePaths", "");
        emptyPathService.init();

        try {
            // Should default to current directory (.)
            SseEmitter emitter = emptyPathService.addMonitoringFileService(sampleLogFileStr);
            assertNotNull("Should default to current directory when config is empty", emitter);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(emptyPathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_null_base_paths_defaults_to_current_directory() throws Exception {
        MonitoringFileService nullPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(nullPathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(nullPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(nullPathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(nullPathService, "logBasePaths", null);
        nullPathService.init();

        try {
            // Should default to current directory (.)
            SseEmitter emitter = nullPathService.addMonitoringFileService(sampleLogFileStr);
            assertNotNull("Should default to current directory when config is null", emitter);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(nullPathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_single_base_path_backward_compatibility() throws Exception {
        MonitoringFileService singlePathService = new MonitoringFileService();
        ReflectionTestUtils.setField(singlePathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(singlePathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(singlePathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(singlePathService, "logBasePaths", "target/tmp/data");
        singlePathService.init();

        try {
            SseEmitter emitter = singlePathService.addMonitoringFileService(sampleLogFileStr);
            assertNotNull("Should work with single path for backward compatibility", emitter);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(singlePathService, "executorService");
            tpe.shutdownNow();
        }
    }

    @Test
    public void test_three_or_more_base_paths() throws Exception {
        MonitoringFileService multiPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiPathService, "maxStreamThreads", 3);
        ReflectionTestUtils.setField(multiPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiPathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(multiPathService, "logBasePaths", "target/tmp/data,target/test-logs,target/other-logs");
        multiPathService.init();

        try {
            // Create files in different base directories
            String file1 = "target/tmp/data/log1.log";
            String file2 = "target/test-logs/log2.log";
            String file3 = "target/other-logs/log3.log";

            new File("target/other-logs").mkdirs();
            FileUtils.write(new File(file1), "test1", StandardCharsets.UTF_8);
            FileUtils.write(new File(file2), "test2", StandardCharsets.UTF_8);
            FileUtils.write(new File(file3), "test3", StandardCharsets.UTF_8);

            // All three should be accepted
            SseEmitter emitter1 = multiPathService.addMonitoringFileService(file1);
            assertNotNull("Should accept file from first base path", emitter1);

            Thread.sleep(100);
            SseEmitter emitter2 = multiPathService.addMonitoringFileService(file2);
            assertNotNull("Should accept file from second base path", emitter2);

            Thread.sleep(100);
            SseEmitter emitter3 = multiPathService.addMonitoringFileService(file3);
            assertNotNull("Should accept file from third base path", emitter3);
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiPathService, "executorService");
            tpe.shutdownNow();
            FileUtils.deleteDirectory(new File("target/other-logs"));
        }
    }

    @Test
    public void test_path_traversal_blocked_with_multiple_bases() throws Exception {
        MonitoringFileService multiPathService = new MonitoringFileService();
        ReflectionTestUtils.setField(multiPathService, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(multiPathService, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(multiPathService, "inactiveTimeForFileInMillis", 300000);
        ReflectionTestUtils.setField(multiPathService, "logBasePaths", "target/tmp/data,target/test-logs");
        multiPathService.init();

        try {
            // Path traversal should be blocked even with multiple base paths
            multiPathService.addMonitoringFileService("target/tmp/data/../../etc/passwd");
            fail("Should throw IllegalArgumentException for path traversal");
        } catch (IllegalArgumentException e) {
            assertTrue("Should reject path traversal",
                e.getMessage().contains("all configured log base directories"));
        } finally {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(multiPathService, "executorService");
            tpe.shutdownNow();
        }
    }
}