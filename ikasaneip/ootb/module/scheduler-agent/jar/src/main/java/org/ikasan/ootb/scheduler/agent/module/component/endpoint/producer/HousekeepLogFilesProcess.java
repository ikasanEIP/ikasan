package org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.configuration.HousekeepLogFilesProcessConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class HousekeepLogFilesProcess<T> implements Producer<T>, ConfiguredResource<HousekeepLogFilesProcessConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(HousekeepLogFilesProcess.class);

    private HousekeepLogFilesProcessConfiguration configuration;
    private String configuredResourceId;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");

    @Override
    public void invoke(T payload) throws EndpointException {

        Iterator<File> filesToHousekeep = findFilesToDelete();

        // delete or move
        if (!configuration.isShouldArchive()) {
            // delete
            if (!configuration.isShouldMove()) {
                while (filesToHousekeep.hasNext()) {
                    FileUtils.deleteQuietly(filesToHousekeep.next());
                }
            }
            else {
                // move
                while (filesToHousekeep.hasNext()) {
                    try {
                        FileUtils.moveFileToDirectory(filesToHousekeep.next(), new File(configuration.getFolderToMove()), true);
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        else {
            // archive
            Path archivedFile = Paths.get(configuration.getLogFolder(), "logFiles-"+sdf.format(new Date())+".tar.gz");
            try {
                createTarGzipFiles(filesToHousekeep, archivedFile);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            // move
            if (configuration.isShouldMove()) {
                try {
                    FileUtils.moveFileToDirectory(archivedFile.toFile(), new File(configuration.getFolderToMove()), true);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

    }

    private Iterator<File> findFilesToDelete() {
        Date oldestAllowedFileDate = DateUtils.addDays(new Date(), configuration.getTimeToLive() * (-1));
        File logDir = new File(configuration.getLogFolder());
        return FileUtils.iterateFiles(logDir, new AgeFileFilter(oldestAllowedFileDate), null);
    }

    public static void createTarGzipFiles(Iterator<File> filesToHousekeep, Path output) throws IOException {

        try (OutputStream fOut = Files.newOutputStream(output);
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            while (filesToHousekeep.hasNext()) {
                File file = filesToHousekeep.next();

                TarArchiveEntry tarEntry = new TarArchiveEntry(file, file.getName());

                tOut.putArchiveEntry(tarEntry);

                Files.copy(file.toPath(), tOut);

                tOut.closeArchiveEntry();

                // delete now after gzipped
                FileUtils.deleteQuietly(file);
            }
            tOut.finish();
        }
    }

    @Override
    public HousekeepLogFilesProcessConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(HousekeepLogFilesProcessConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configuredResourceId = id;
    }
}
