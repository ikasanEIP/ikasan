/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;

import java.util.List;

/**
 * File consumer configuration bean.
 * 
 * @author mitcje
 */
public class FileConsumerConfiguration extends ScheduledConsumerConfiguration
{
    /** filenames to be processed */
    private List<String> filenames;

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

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isIncludeHeader() {
        return includeHeader;
    }

    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    public boolean isIncludeTrailer() {
        return includeTrailer;
    }

    public void setIncludeTrailer(boolean includeTrailer) {
        this.includeTrailer = includeTrailer;
    }

    public boolean isSortByModifiedDateTime() {
        return sortByModifiedDateTime;
    }

    public void setSortByModifiedDateTime(boolean sortByModifiedDateTime) {
        this.sortByModifiedDateTime = sortByModifiedDateTime;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public int getDirectoryDepth()
    {
        return directoryDepth;
    }

    public void setDirectoryDepth(int directoryDepth)
    {
        this.directoryDepth = directoryDepth;
    }

    public boolean isLogMatchedFilenames() {
        return logMatchedFilenames;
    }

    public void setLogMatchedFilenames(boolean logMatchedFilenames) {
        this.logMatchedFilenames = logMatchedFilenames;
    }

    public boolean isIgnoreFileRenameWhilstScanning() {
        return ignoreFileRenameWhilstScanning;
    }

    public void setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning) {
        this.ignoreFileRenameWhilstScanning = ignoreFileRenameWhilstScanning;
    }
}
