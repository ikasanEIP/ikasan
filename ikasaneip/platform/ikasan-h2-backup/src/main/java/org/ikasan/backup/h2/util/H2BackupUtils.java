package org.ikasan.backup.h2.util;

import org.h2.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class H2BackupUtils {

    private static Logger logger = LoggerFactory.getLogger(H2BackupUtils.class);

    /**
     * Unzips a file into the specified directory.
     *
     * @param backupFileName the full path of the backup file to unzip
     * @param unzipDirectory the directory where the file will be unzipped to
     *
     * @throws IOException if an I/O error occurs during unzipping
     */
    public static void unzipFile(String backupFileName, String unzipDirectory) throws IOException {
        File unzipDir = new File(unzipDirectory);

        if(unzipDir.exists()) unzipDir.delete();

        try (java.util.zip.ZipFile zipFile = new ZipFile(new File(backupFileName))) {
            if(zipFile.size() != 1) throw new IOException("Zip file cannot be empty!");
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(unzipDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = new FileOutputStream(entryDestination)) {
                        IOUtils.copy(in, out);
                    }
                }
            }
        }
    }

    /**
     * Deletes a file at the given file path.
     *
     * @param filePath the path of the file to be deleted
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if(file.exists()) {
                Files.delete(file.toPath());
            }
        }
        catch (IOException e) {
            logger.warn("Unable to delete file: " + filePath, e);
        }
    }

    /**
     * Cleans the directory by deleting all files and directories within it.
     * @param directory the directory to clean
     *
     * @throws IOException if an I/O error occurs during file deletion.
     */
    public static void cleanDirectory(String directory) throws IOException {
        Path dir = Paths.get(directory);

        List<Path> files = Files
            .walk(dir)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        for(Path file: files) {
            Files.delete(file);
        }
    }

}
