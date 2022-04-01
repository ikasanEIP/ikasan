package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;

import java.util.ArrayList;
import java.util.List;

public class FileEventDrivenJobImpl extends QuartzScheduleDrivenJobImpl implements FileEventDrivenJob {

    private String filePath;

    /** filenames to be processed */
    private List<String> filenames = new ArrayList<String>();

    /** encoding of the files */
    private String encoding;

    /** include header when processing files */
    private boolean includeHeader;

    /** include trailer when processing files */
    private boolean includeTrailer;

    /** sort based on the lastModifiedDateTime */
    private boolean sortByModifiedDateTime;

    /** sort ascending = true; descending = false */
    private boolean sortAscending = true;

    /** depth of the directory tree to walk */
    private int directoryDepth = 1;

    /** log filenames found */
    private boolean logMatchedFilenames = false;

    private boolean ignoreFileRenameWhilstScanning = true;

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void setFilePath(String path) {
        this.filePath = path;
    }

    @Override
    public List<String> getFilenames() {
        return filenames;
    }

    @Override
    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean isIncludeHeader() {
        return includeHeader;
    }

    @Override
    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    @Override
    public boolean isIncludeTrailer() {
        return includeTrailer;
    }

    @Override
    public void setIncludeTrailer(boolean includeTrailer) {
        this.includeTrailer = includeTrailer;
    }

    @Override
    public boolean isSortByModifiedDateTime() {
        return sortByModifiedDateTime;
    }

    @Override
    public void setSortByModifiedDateTime(boolean sortByModifiedDateTime) {
        this.sortByModifiedDateTime = sortByModifiedDateTime;
    }

    @Override
    public boolean isSortAscending() {
        return sortAscending;
    }

    @Override
    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    @Override
    public int getDirectoryDepth() {
        return directoryDepth;
    }

    @Override
    public void setDirectoryDepth(int directoryDepth) {
        this.directoryDepth = directoryDepth;
    }

    @Override
    public boolean isLogMatchedFilenames() {
        return logMatchedFilenames;
    }

    @Override
    public void setLogMatchedFilenames(boolean logMatchedFilenames) {
        this.logMatchedFilenames = logMatchedFilenames;
    }

    @Override
    public boolean isIgnoreFileRenameWhilstScanning() {
        return ignoreFileRenameWhilstScanning;
    }

    @Override
    public void setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning) {
        this.ignoreFileRenameWhilstScanning = ignoreFileRenameWhilstScanning;
    }

    @Override
    public int getMinFileAgeSeconds() {
        return 0;
    }

    @Override
    public void setMinFileAgeSeconds(int minFileAgeSeconds) {

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileEventDrivenJobImpl{");
        sb.append("filePath='").append(filePath).append('\'');
        sb.append(", filenames=").append(filenames);
        sb.append(", encoding='").append(encoding).append('\'');
        sb.append(", includeHeader=").append(includeHeader);
        sb.append(", includeTrailer=").append(includeTrailer);
        sb.append(", sortByModifiedDateTime=").append(sortByModifiedDateTime);
        sb.append(", sortAscending=").append(sortAscending);
        sb.append(", directoryDepth=").append(directoryDepth);
        sb.append(", logMatchedFilenames=").append(logMatchedFilenames);
        sb.append(", ignoreFileRenameWhilstScanning=").append(ignoreFileRenameWhilstScanning);
        sb.append('}');
        return sb.toString();
    }
}
