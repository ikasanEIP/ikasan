package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.apache.commons.io.FileUtils;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.configuration.HousekeepLogFilesProcessConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.HousekeepLogFilesProcess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HousekeepLogFilesProcessTest {

    HousekeepLogFilesProcessConfiguration configuration;
    HousekeepLogFilesProcess housekeepLogFilesProcess;

    @BeforeEach
    void setup() throws IOException {

        // clean any files from previous test classes
        clean();

        configuration = new HousekeepLogFilesProcessConfiguration();
        configuration.setLogFolder("src/test/resources/data/housekeep");
        configuration.setFolderToMove("src/test/resources/data/housekeep/archive");
        configuration.setTimeToLive(100);

        housekeepLogFilesProcess = new HousekeepLogFilesProcess();
        housekeepLogFilesProcess.setConfiguration(configuration);

        Files.createDirectory(Paths.get(configuration.getLogFolder()));
        Files.createDirectory(Paths.get(configuration.getFolderToMove()));

        LocalDate oldDate = LocalDate.of(2020, 12, 31);
        Instant instant = oldDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Path path1 = Paths.get(configuration.getLogFolder(), "logFile-1.txt");
        Path path2 = Paths.get(configuration.getLogFolder(), "logFile-2.txt");
        Path path3 = Paths.get(configuration.getLogFolder(), "logFile-3.txt");

        Files.createFile(path1);
        Files.createFile(path2);
        Files.createFile(path3);

        Files.setLastModifiedTime(path1, FileTime.from(instant));
        Files.setLastModifiedTime(path2, FileTime.from(instant));
    }

    @AfterEach
    void clean() throws IOException {
        FileUtils.deleteDirectory(new File("src/test/resources/data/housekeep"));
    }

    @Test
    void test_housekeep_delete() {

        assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        assertEquals(1, files.size());
        assertEquals("logFile-3.txt", files.stream().findFirst().get().getName() );
    }

    @Test
    void test_housekeep_move() {

        assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        configuration.setShouldMove(true);
        housekeepLogFilesProcess.setConfiguration(configuration);

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        assertEquals(1, files.size());

        Collection<File> filesInArchive = FileUtils.listFiles(new File("src/test/resources/data/housekeep/archive"), null, false);

        assertEquals(2, filesInArchive.size());

        assertEquals("logFile-3.txt", files.stream().findFirst().get().getName() );
    }

    @Test
    void test_housekeep_archive() {

        assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        configuration.setShouldArchive(true);
        housekeepLogFilesProcess.setConfiguration(configuration);

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        assertEquals(2, files.size());

        for (File file : files.stream().collect(Collectors.toList())) {
            assertTrue(file.getName().equals("logFile-3.txt") || (file.getName().contains("logFiles-") && file.getName().contains(".tar.gz")) );
        }

        Collection<File> filesInArchive = FileUtils.listFiles(new File("src/test/resources/data/housekeep/archive"), null, false);

        assertEquals(0, filesInArchive.size());
    }

    @Test
    void test_housekeep_archive_and_move() {

        assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        configuration.setShouldArchive(true);
        configuration.setShouldMove(true);
        housekeepLogFilesProcess.setConfiguration(configuration);

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        assertEquals(1, files.size());
        assertEquals("logFile-3.txt", files.stream().collect(Collectors.toList()).get(0).getName() );

        Collection<File> filesInArchive = FileUtils.listFiles(new File("src/test/resources/data/housekeep/archive"), null, false);

        assertEquals(1, filesInArchive.size());
        assertTrue(filesInArchive.stream().collect(Collectors.toList()).get(0).getName().contains("logFiles-") );
        assertTrue(filesInArchive.stream().collect(Collectors.toList()).get(0).getName().contains(".tar.gz") );
    }
}
