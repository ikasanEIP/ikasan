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

    boolean isIgnoreFileRenameWhilstScanning();

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
}
