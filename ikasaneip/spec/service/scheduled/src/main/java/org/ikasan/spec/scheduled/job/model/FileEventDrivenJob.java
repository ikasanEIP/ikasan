package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface FileEventDrivenJob extends QuartzScheduleDrivenJob {

    /**
     * Get the file path of the file we are waiting on in order to raise the event.
     *
     * @return
     */
    String getFilePath();

    /**
     * Set the file path to the file that generates the event.
     *
     * @param path
     */
    void setFilePath(String path);

    /**
     * Get the filenames this job is waiting on
     *
     * @return
     */
    List<String> getFilenames();

    /**
     * Set the file names this job is waiting on.
     * @param filenames
     */
    void setFilenames(List<String> filenames);

    /**
     * Get the directory to move this files to.
     *
     * @return
     */
    String getMoveDirectory();

    /**
     * Set the directory to move the files to.
     *
     * @param moveDirectory
     */
    void setMoveDirectory(String moveDirectory);

    /**
     * Get the encoding of the files.
     *
     * @return
     */
    String getEncoding();

    /**
     * Set encoding of the files.
     * @param encoding
     */
    void setEncoding(String encoding);

    /**
     * Include header when processing files
     * @return
     */
    boolean isIncludeHeader();

    /**
     * Set flag to include header when processing files.
     * @param includeHeader
     */
    void setIncludeHeader(boolean includeHeader);

    /**
     * Include trailer when processing files
     *
     * @return
     */
    boolean isIncludeTrailer();

    /**
     * Set include trailer when processing files.
     *
     * @param includeTrailer
     */
    void setIncludeTrailer(boolean includeTrailer);

    /**
     * Is sort based on the lastModifiedDateTime
     *
     * @return
     */
    boolean isSortByModifiedDateTime();

    /**
     * Set sort based on the lastModifiedDateTime
     *
     * @return
     */
    void setSortByModifiedDateTime(boolean sortByModifiedDateTime);

    /**
     *  Is sort ascending = true; descending = false
     *
     * @return
     */
    boolean isSortAscending();

    /**
     * Set sort ascending = true; descending = false
     *
     * @param sortAscending
     */
    void setSortAscending(boolean sortAscending);

    /**
     * Get depth of the directory tree to walk.
     *
     * @return
     */
    int getDirectoryDepth();

    /**
     * Set depth of the directory tree to walk.
     *
     * @param directoryDepth
     */
    void setDirectoryDepth(int directoryDepth);

    /**
     * Is log filenames found.
     * @return
     */
    boolean isLogMatchedFilenames();

    /**
     * Set log filenames found.
     *
     * @param logMatchedFilenames
     */
    void setLogMatchedFilenames(boolean logMatchedFilenames);

    /**
     * Check if the file rename should be ignored while scanning.
     *
     * @return true if file rename should be ignored, false otherwise
     */
    boolean isIgnoreFileRenameWhilstScanning();

    /**
     * Sets whether to ignore file renaming while scanning.
     *
     * @param ignoreFileRenameWhilstScanning true to ignore file renaming, false otherwise
     */
    void setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning);

    /**
     * Get the min age of file before processing
     *
     * @return
     */
    int getMinFileAgeSeconds();

    /**
     * Set the min age of file before processing
     *
     * @param minFileAgeSeconds
     */
    void setMinFileAgeSeconds(int minFileAgeSeconds);

    /**
     * Get the cron expression for the SLA of the file.
     * This will be used to alert if the file has not been received by this cron expression
     *
     * @return slaCronExpression
     */
    String getSlaCronExpression();

    /**
     * Set the SLA Cron Expression
     *
     * @param slaCronExpression in quartz format
     */
    void setSlaCronExpression(String slaCronExpression);


    /**
     * Check if the file watcher is dynamic or not.
     *
     * @return true if the file watcher is dynamic, false otherwise
     */
    boolean isDynamic();


    /**
     * Sets whether the file watcher is dynamic or not.
     *
     * @param isDynamic true if the file watcher is dynamic, false otherwise
     */
    void setDynamic(boolean isDynamic);

    /**
     * Get the SpEL expression for determining the file path.
     *
     * @return the SpEL expression for determining the file path
     */
    String getFilePathSpel();

    /**
     * Set the Spring Expression Language (SpEL) for determining the file path.
     * This SpEL expression will be evaluated to fetch the file path at runtime.
     *
     * @param filePathSpel the SpEL expression for file path
     */
    void setFilePathSpel(String filePathSpel);

    /**
     * Retrieves the SpEL expression for generating the filename dynamically.
     *
     * @return the SpEL expression for generating the filename
     */
    String getFilenameSpel();

    /**
     * Set the SpEL expression for dynamically determining the filename to be processed.
     *
     * @param filenameSpel the SpEL expression for the filename
     */
    void setFilenameSpel(String filenameSpel);
}
