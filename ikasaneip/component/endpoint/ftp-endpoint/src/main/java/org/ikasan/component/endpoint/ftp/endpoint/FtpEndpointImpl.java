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
package org.ikasan.component.endpoint.ftp.endpoint;

import org.apache.log4j.Logger;
import org.ikasan.component.endpoint.ftp.common.*;
import org.ikasan.component.endpoint.ftp.consumer.FtpConsumerConfiguration;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Ftp Endpoint Contract which if going to be pulled by FtpConsumer on scheduled basis.
 *
 * @author Ikasan Development Team.
 */
public class FtpEndpointImpl implements FtpEndpoint {

    /**
     * class logger
     */
    private static Logger logger = Logger.getLogger(FtpEndpointImpl.class);

    /**
     * Common library used by both inbound and outbound connectors
     */
    private FileTransferProtocolClient ftpClient;

    /**
     * client Id used by this ftp endpoint
     */
    private String clientID;


    /**
     * source directory
     */
    private String sourceDirectory;
//
//    /** dao class for base file transfer classes */
//    private BaseFileTransferDao persistence;
//
    /**
     * regexp for pattern matching filenames
     */
    private String filenamePattern;

    /**
     * min age for matched files in seconds
     */
    private long minAge;

    /**
     * Whether we filter duplicates
     */
    private boolean filterDuplicates;

    /**
     * Whether we use File name in the duplicates filter
     */
    private boolean filterOnFilename;

    /**
     * Whether we use Last Modified date as a duplicates filter
     */
    private boolean filterOnLastModifiedDate;

    public FtpEndpointImpl(FileTransferProtocolClient ftpClient, String clientID, String sourceDirectory, String filenamePattern, long minAge, boolean filterDuplicates, boolean filterOnFilename, boolean filterOnLastModifiedDate) {
        this.ftpClient = ftpClient;
        this.clientID = clientID;
        this.sourceDirectory = sourceDirectory;
        this.filenamePattern = filenamePattern;
        this.minAge = minAge;
        this.filterDuplicates = filterDuplicates;
        this.filterOnFilename = filterOnFilename;
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
    }

    /**
     */
    @Override
    public BaseFileTransferMappedRecord get() throws Exception {
        logger.info("execute called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        List<ClientListEntry> entries = getList();

        //sourcePath = entry.getUri().getPath();
        // We change the path to be file based as opposed to URI based,
        // means that root starts as '/' as opposed to '//' which
        // some FTP servers don't like
        BaseFileTransferMappedRecord record = ftpClient.get(entries.get(0));

        return record;
    }

    /**
     * retrieves a list of new files
     *
     * @return List<ClientListEntry>
     * @throws Exception Exception thrown by Connector
     */
    private List<ClientListEntry> getList() throws ClientCommandCdException, URISyntaxException, ClientCommandLsException {

        Date now = new Date();
        List<ClientListEntry> result = new ArrayList<ClientListEntry>();
        // Get the client Connection
        ftpClient.ensureConnection();
        // CD to the src dir
        // ftpClient.cd(sourceDirectory);
        // Getting a complete listing of current directory (sourceDirectory)
        List<ClientListEntry> allFiles = ftpClient.ls(sourceDirectory);
        //logFileList(allFiles, "Unfiltered file list"); //$NON-NLS-1$

        // Filter the files
        logger.debug("Filtering entries based on default filter list..."); //$NON-NLS-1$
        List<ClientListEntry> filteredFiles = allFiles;
        filterDefaults(allFiles);

        // Process filtered files
        if (filteredFiles.size() > 0) {
            for (ClientListEntry entry : filteredFiles) {
                entry.setClientId(clientID);

                // skip this entry if its last accessed date indicates that it
                // is younger that the
                // configured minimum age
                Date lastModifiedDate = entry.getDtLastModified();
                long ageInMillis = (now.getTime()) - (lastModifiedDate.getTime());
                long ageInSec = ageInMillis / 1000;
                logger.debug("file [" + entry.getLongFilename() + "] ageInSec [" + ageInSec + "] vs minAge [" + minAge //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "]"); //$NON-NLS-1$

                if (ageInSec < minAge) {
                    // vetoing File because it is not old enough
                    logger.debug("vetoing file because it is not old enough [" + entry + "]"); //$NON-NLS-1$//$NON-NLS-2$

                    continue;
                }

                // Filter out duplicates based on parameters passed in earlier
                if (filterDuplicates && filterOptionsSet()) {
//                    if (!persistence.isDuplicate(entry, configuration.getFilterOnFilename(), configuration.getFilterOnLastModifiedDate()))
//                    {
                        result.add(entry);
//                    }
                } else {
                    result.add(entry);
                }
            }
        } else {
            logger.debug("No files to get, nothing matched the filename pattern."); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * Applies the default filtering rules in the following order:
     * <p/>
     * <ol>
     * <li>Exclude directories</li>
     * <li>Exclude symbolic links</li>
     * <li>Exclude the files that do not match the provided pattern</li>
     * </ol>
     * <p/>
     * <p>
     * Note: Any filtering/comparisons relating to time (i.e. minimumAgeLimit)
     * should be done against database and not List entries.
     * </p>
     *
     * @param list The <code>List&lt;ClientListEntry&gt;</code> objects to
     *             filter.
     * @return The filtered list containing only the files that have passed
     * through the filtering process.
     */
    private List<ClientListEntry> filterDefaults(List<ClientListEntry> list) {
        ClientPolarisedFilter pf = null;
        List<ClientListEntry> noDirsList = null;
        List<ClientListEntry> noLinksDirsList = null;
        List<ClientListEntry> fileList = null;
        // Ignoring directories
        if (list != null && list.size() > 0) {
            pf = new ClientPolarisedFilter(new ClientDirectoryFilter(), false);
            noDirsList = pf.applyFilter(list);
        }
        // Ignoring links
        if (noDirsList != null && noDirsList.size() > 0) {
            pf = new ClientPolarisedFilter(new ClientSymLinkFilter(), false);
            noLinksDirsList = pf.applyFilter(noDirsList);
        }

        // Ignoring filenames that don't match
        if (noLinksDirsList != null && noLinksDirsList.size() > 0) {
            pf = new ClientPolarisedFilter(new ClientFilenameFilter(filenamePattern), true);
            fileList = pf.applyFilter(noLinksDirsList);
        }

        if (fileList != null && fileList.size() > 0) {
            return fileList;
        }
        return new ArrayList<ClientListEntry>(0);
    }

    /**
     * Return whether at least one filtering option is set
     *
     * @return true if at least one filtering option is set
     */
    private boolean filterOptionsSet() {
        if (filterOnFilename || filterOnLastModifiedDate) {
            return true;
        }
        logger.warn("No filtering options were set, so skipping filter after all.");
        return false;
    }

    @Override
    public void closeSession() {
        if (this.ftpClient == null) {
            logger.debug("FTPClient is null.  Closing Session aborted."); //$NON-NLS-1$
        } else {
            if (this.ftpClient.isConnected()) {
                logger.debug("Closing FTP connection!"); //$NON-NLS-1$
                this.ftpClient.disconnect();
                logger.debug("Disconnected from FTP host."); //$NON-NLS-1$
            } else {
                logger.info("Client was already disconnected.  Closing Session aborted."); //$NON-NLS-1$
            }
        }
    }


}
