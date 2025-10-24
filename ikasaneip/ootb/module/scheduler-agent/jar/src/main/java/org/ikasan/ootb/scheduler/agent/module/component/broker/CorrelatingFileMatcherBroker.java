package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatingFileMessageProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.DynamicFileMatcher;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class CorrelatingFileMatcherBroker implements Broker<FileWatcherJobEvent, FileWatcherJobEvent>
    , EndpointListener<String, IOException>, ManagedResource {
    private static Logger logger = LoggerFactory.getLogger(CorrelatingFileMessageProvider.class);

    private static final String FQN_PATH_SEPARATOR_LINUX = "/";

    /** record all filenames returned from the fileMatcher */
    private List<String> filenames = new ArrayList<>();
    private IOException matcherException;

    private boolean active = false;

    @Override
    public FileWatcherJobEvent invoke(FileWatcherJobEvent fileWatcherJobEvent) throws EndpointException {
        if(fileWatcherJobEvent.isDryRun()) {
            return fileWatcherJobEvent;
        }

        this.matcherException = null;

        String correlatingIdentifier = fileWatcherJobEvent.getCorrelationIdentifier();

        // We need to initialise the file matcher based on the payload!
        DynamicFileMatcher fileMatcher = getFileMatcher(fileWatcherJobEvent.getFilePath(), fileWatcherJobEvent.getFilename(), true,
            true, 1, fileWatcherJobEvent.getFileNameSpelExpression(),
            fileWatcherJobEvent.getFilePathSpelExpression(), true);

        logger.debug(CorrelatingScheduledConsumer.CORRELATION_ID
            + " " + correlatingIdentifier);

        if(correlatingIdentifier == null || correlatingIdentifier
            .equals(CorrelatingScheduledConsumer.EMPTY_CORRELATION_ID)) {
            return null;
        }

        List<File> files = new ArrayList<>();
        this.getFilenames().clear();

        try {
            try {
                fileMatcher.setCorrelatingIdentifier(correlatingIdentifier);

                // Note, this class is a listener, file matcher can invoke this.onMessage and thus update filenames.
                fileMatcher.invoke();
            }
            catch(IOException e) {
                throw new EndpointException(e);
            }
        }
        catch(ConcurrentModificationException e) {
            if(isActive()) {
                throw e;
            }

            throw new ForceTransactionRollbackException("File processing interrupted by a stop request.");
        }

        if(matcherException != null) {
            throw new EndpointException(matcherException);
        }

        for(String filename:this.getFilenames()) {
            File file = new File(filename);
            files.add(file);
        }


        if(files.size() > 0) {

            for(File file:files) {
                logger.info("Matching filename for " + file.getAbsolutePath());
            }

            fileWatcherJobEvent.setCorrelatedFileList(new CorrelatedFileList(files, correlatingIdentifier));
            return fileWatcherJobEvent;
        }

        return null;
    }

    /**
     * Retrieves a FileMatcher based on the provided parameters.
     *
     * @param filePath the file path to search within. If null or empty, assumes the files are fully qualified.
     * @param filename the name of the file to search for.
     * @param dynamicFileName whether the filename is dynamic or static.
     * @param ignoreFileNameWhistScanning whether to ignore filename while scanning.
     * @param directoryDepth the depth of the directory tree to walk.
     * @param filenameSpelExpression the SpEL expression for the filename.
     * @param filePathSpelExpression the SpEL expression for the file path.
     * @param followSymbolicLinks whether to follow symbolic links.
     * @return a FileMatcher instance based on the provided parameters.
     */
    protected DynamicFileMatcher getFileMatcher(String filePath, String filename, boolean dynamicFileName,
                                         boolean ignoreFileNameWhistScanning, int directoryDepth,
                                         String filenameSpelExpression, String filePathSpelExpression,
                                         boolean followSymbolicLinks)
    {
        String path;
        String name;

        if(filePath == null || filePath.isEmpty()) {
            // assume files are fully qualified
            filename = modifyPathForUnix(filename);

            int lastIndexOffullPath;

            lastIndexOffullPath = filename.lastIndexOf(FQN_PATH_SEPARATOR_LINUX);

            path = filename.substring(0,lastIndexOffullPath);
            name = filename.substring(++lastIndexOffullPath);
        }
        else {
            path = modifyPathForUnix(filePath);
            name = filename;
        }


        return new DynamicFileMatcher(
            false,
            path,
            name,
            1,
            this,
            filenameSpelExpression,
            filePathSpelExpression,
            true);
    }

    /**
     * Modifies the provided file path for Unix-style systems by applying necessary adjustments.
     *
     * @param filePath the file path to be modified
     * @return the modified file path with appropriate adjustments
     */
    protected String modifyPathForUnix(String filePath) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (!isWindows && filePath != null && !filePath.startsWith("/") && !filePath.startsWith(".")) {
            // assume relative reference and prefix accordingly
            filePath = "./" + filePath;
        }

        return filePath;
    }

    protected List<String> getFilenames() {
        return this.filenames;
    }

    @Override
    public void onMessage(String filename)
    {
        filenames.add(filename);
    }

    @Override
    public void onException(IOException e) {
        this.matcherException = e;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void startManagedResource() {
        this.active = true;
    }

    @Override
    public void stopManagedResource() {
        this.active = false;
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    @Override
    public boolean isCriticalOnStartup() {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {

    }
}
