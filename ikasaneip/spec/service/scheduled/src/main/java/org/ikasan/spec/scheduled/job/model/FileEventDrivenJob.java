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

    List<String> getFilenames();

    void setFilenames(List<String> filenames);

    String getEncoding();

    void setEncoding(String encoding);

    boolean isIncludeHeader();

    void setIncludeHeader(boolean includeHeader);

    boolean isIncludeTrailer();

    void setIncludeTrailer(boolean includeTrailer);

    boolean isSortByModifiedDateTime();

    void setSortByModifiedDateTime(boolean sortByModifiedDateTime);

    boolean isSortAscending();

    void setSortAscending(boolean sortAscending);

    int getDirectoryDepth();

    void setDirectoryDepth(int directoryDepth);

    boolean isLogMatchedFilenames();

    void setLogMatchedFilenames(boolean logMatchedFilenames);

    boolean isIgnoreFileRenameWhilstScanning();

    void setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning);

    int getMinFileAgeSeconds();

    void setMinFileAgeSeconds(int minFileAgeSeconds);
}
