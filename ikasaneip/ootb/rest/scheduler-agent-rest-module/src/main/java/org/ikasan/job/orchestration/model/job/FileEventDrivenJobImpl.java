package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.ReplacementPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileEventDrivenJobImpl extends QuartzScheduleDrivenJobImpl implements FileEventDrivenJob {

    private String filePath;

    /** filenames to be processed */
    private List<String> filenames = new ArrayList<String>();

    private String moveDirectory;

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

    private int minFileAgeSeconds;

    /** cron expression on expected time of file availability */
    private String slaCronExpression;
    private boolean isDynamic;
    private String filePathSpel;
    private String filenameSpel;

    private Set<ReplacementPair> filenameReplacementPairs;
    private Set<ReplacementPair> filePathReplacementPairs;
    private String moveDirectorySpel;
    private Set<ReplacementPair> moveDirectoryReplacementPairs;

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
    public String getMoveDirectory() {
        return this.moveDirectory;
    }

    @Override
    public void setMoveDirectory(String moveDirectory) {
        this.moveDirectory = moveDirectory;
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
        return this.minFileAgeSeconds;
    }

    @Override
    public void setMinFileAgeSeconds(int minFileAgeSeconds) {
        this.minFileAgeSeconds = minFileAgeSeconds;
    }

    @Override
    public String getSlaCronExpression() {
        return slaCronExpression;
    }

    @Override
    public void setSlaCronExpression(String slaCronExpression) {
        this.slaCronExpression = slaCronExpression;
    }

    @Override
    public boolean isDynamic() {
        return isDynamic;
    }

    @Override
    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

    @Override
    public String getFilePathSpel() {
        return filePathSpel;
    }

    @Override
    public void setFilePathSpel(String filePathSpel) {
        this.filePathSpel = filePathSpel;
    }

    @Override
    public String getFilenameSpel() {
        return filenameSpel;
    }

    @Override
    public void setFilenameSpel(String filenameSpel) {
        this.filenameSpel = filenameSpel;
    }

    @Override
    public Set<ReplacementPair> getFilenameReplacementPairs() {
        return filenameReplacementPairs;
    }

    @Override
    public void setFilenameReplacementPairs(Set<ReplacementPair> filenameReplacementPairs) {
        this.filenameReplacementPairs = filenameReplacementPairs;
    }

    @Override
    public Set<ReplacementPair> getFilePathReplacementPairs() {
        return filePathReplacementPairs;
    }

    @Override
    public void setFilePathReplacementPairs(Set<ReplacementPair> filePathReplacementPairs) {
        this.filePathReplacementPairs = filePathReplacementPairs;
    }

    @Override
    public String getMoveDirectorySpel() {
        return moveDirectorySpel;
    }

    @Override
    public void setMoveDirectorySpel(String moveDirectorySpel) {
        this.moveDirectorySpel = moveDirectorySpel;
    }

    @Override
    public Set<ReplacementPair> getMoveDirectoryReplacementPairs() {
        return moveDirectoryReplacementPairs;
    }

    @Override
    public void setMoveDirectoryReplacementPairs(Set<ReplacementPair> moveDirectoryReplacementPairs) {
        this.moveDirectoryReplacementPairs = moveDirectoryReplacementPairs;
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
        sb.append(", slaCronExpression=").append(slaCronExpression);
        sb.append(", isDynamic=").append(isDynamic);
        sb.append(", filePathSpel=").append(filePathSpel);
        sb.append(", filenameSpel=").append(filenameSpel);
        sb.append(", moveDirectorySpel=").append(moveDirectorySpel);
        sb.append('}');
        return sb.toString();
    }
}
