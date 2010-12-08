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
package org.ikasan.endpoint.ftp.consumer;

import javax.resource.spi.InvalidPropertyException;

import org.apache.log4j.Logger;
import org.ikasan.framework.factory.DirectoryURLFactory;

/**
 * SFTP Consumer Configuration model.
 * 
 * @author Ikasan Development Team
 */
public class FtpConsumerConfiguration
{
    /** Remote directory from which to discover files */
    private String sourceDirectory;

    /** Regular expression for matching file names */
    private String filenamePattern;

    /** Classname for source directories URLs factory */
    private DirectoryURLFactory sourceDirectoryURLFactory;

    /** Whether we filterDuplicates what we are picking up - True by default */
    private Boolean filterDuplicates = Boolean.TRUE;

    /** Filter on Filename - True by default */
    private Boolean filterOnFilename = Boolean.TRUE;

    /** Filter on LastModifiedDate - True by default */
    private Boolean filterOnLastModifiedDate = Boolean.TRUE;

    /** Rename the remote file once successfully retrieved */
    private Boolean renameOnSuccess = Boolean.FALSE;

    /** Extension to use when renaming file */
    private String renameOnSuccessExtension;

    /** Move the remote file to once successfully retrieved */
    private Boolean moveOnSuccess = Boolean.FALSE;

    /** New path to use when moving the file */
    private String moveOnSuccessNewPath;

    /** Sort result set by chronological order - true by default. */
    private Boolean chronological = Boolean.TRUE;

    /** Chunk files when retrieving */
    private Boolean chunking = Boolean.FALSE;

    /** Maximum size of chunk when chunking, defaults to 1MB */
    private Integer chunkSize = Integer.valueOf(1048576);

    /** Attempt to verify integrity of retrieved file by comparing with a checksum supplied by the remote system */
    private Boolean checksum = Boolean.FALSE;

    /** Minimum age (in seconds) of file to match */
    private Long minAge = Long.valueOf(120);

    /** Whether or not we delete the file after picking it up */
    private Boolean destructive = Boolean.FALSE;

    /** Maximum rows that housekeeping can deal with, defaults to -1 (ignore) */
    private Integer maxRows = Integer.valueOf(-1);

    /** Number of days in age the files need to be to be considered for housekeeping, defaults to -1 (ignore) */
    private Integer ageOfFiles = Integer.valueOf(-1);
    
    /** Unique client identifier */
    private String clientID;

    /** Cleanup transactional journal on completion */
    private Boolean cleanupJournalOnComplete = Boolean.TRUE;

    /** Remote host */
    private String remoteHost = String.valueOf("localhost");
    
    /** Remote port */
    private Integer remotePort = Integer.valueOf(21);

    /** Max retry attempts */
    private Integer maxRetryAttempts = Integer.valueOf(3);

    /** User */
    private String username;

    /** Password/passphrase */
    private String password;

    /** Whether it is active transfer mode - default False */
    private Boolean active = Boolean.FALSE;

    /** Connection Timeout */
    private Integer connectionTimeout = Integer.valueOf(60000);

    /** Data connection timeout, default is 0 (infinite) */
    private Integer dataTimeout = Integer.valueOf(300000);
    
    /** Socket connection timeout, default is 0 (infinite) */
    private Integer socketTimeout = Integer.valueOf(300000);

    /** System key */
    private String systemKey = "";

    /** Logger instance */
    private final static Logger logger = Logger.getLogger(FtpConsumerConfiguration.class);

    /**
     * Validate configured properties.
     * 
     * @throws InvalidPropertyException if combination of configured properties is invalid
     */
    public void validate() throws InvalidPropertyException
    {
        if(this.renameOnSuccess.booleanValue())
        {
            if(this.destructive.booleanValue())
            {
                throw new InvalidPropertyException("renameOnSuccess[" + this.renameOnSuccess 
                        + "] and destructive[" + this.destructive 
                        + "] are mutually exclusive.");
            }
            if(this.moveOnSuccess.booleanValue())
            {
                throw new InvalidPropertyException("renameOnSuccess[" + this.renameOnSuccess 
                        + "] and moveOnSuccess[" + this.moveOnSuccess 
                        + "] are mutually exclusive.");
            }
            if(this.renameOnSuccessExtension == null)
            {
                throw new InvalidPropertyException("renameOnSuccess[" + this.renameOnSuccess 
                        + "] requires renameOnSuccessExtention to be specified.");
            }
        }
        
        if(this.moveOnSuccess.booleanValue())
        {
            if(this.destructive.booleanValue())
            {
                throw new InvalidPropertyException("moveOnSuccess[" + this.moveOnSuccess 
                        + "] and destructive[" + this.destructive 
                        + "] are mutually exclusive.");
            }
            if(this.moveOnSuccessNewPath == null)
            {
                throw new InvalidPropertyException("moveOnSuccess[" + this.moveOnSuccess 
                        + "] requires moveOnSuccessNewPath to be specified.");
            }
        }
        
        if (this.systemKey == null || this.systemKey.equals(" "))
        {
            logger.info("Provided systemKey value [" + this.systemKey + "] is invalid. Reverting to default empty String.");
            this.systemKey = "";
        }
    }

    /**
     * @return the sourceDirectory
     */
    public String getSourceDirectory()
    {
        return this.sourceDirectory;
    }

    /**
     * @param sourceDirectory the sourceDirectory to set
     */
    public void setSourceDirectory(String sourceDirectory)
    {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * @return the filenamePattern
     */
    public String getFilenamePattern()
    {
        return this.filenamePattern;
    }

    /**
     * @param filenamePattern the filenamePattern to set
     */
    public void setFilenamePattern(String filenamePattern)
    {
        this.filenamePattern = filenamePattern;
    }

    /**
     * @return the sourceDirectoryURLFactory
     */
    public DirectoryURLFactory getSourceDirectoryURLFactory()
    {
        return this.sourceDirectoryURLFactory;
    }

    /**
     * @param sourceDirectoryURLFactory the sourceDirectoryURLFactory to set
     */
    public void setSourceDirectoryURLFactory(
            DirectoryURLFactory sourceDirectoryURLFactory)
    {
        this.sourceDirectoryURLFactory = sourceDirectoryURLFactory;
    }

    /**
     * @return the filterDuplicates
     */
    public Boolean getFilterDuplicates()
    {
        return this.filterDuplicates;
    }

    /**
     * @param filterDuplicates the filterDuplicates to set
     */
    public void setFilterDuplicates(Boolean filterDuplicates)
    {
        this.filterDuplicates = filterDuplicates;
    }

    /**
     * @return the filterOnFilename
     */
    public Boolean getFilterOnFilename()
    {
        return this.filterOnFilename;
    }

    /**
     * @param filterOnFilename the filterOnFilename to set
     */
    public void setFilterOnFilename(Boolean filterOnFilename)
    {
        this.filterOnFilename = filterOnFilename;
    }

    /**
     * @return the filterOnLastModifiedDate
     */
    public Boolean getFilterOnLastModifiedDate()
    {
        return this.filterOnLastModifiedDate;
    }

    /**
     * @param filterOnLastModifiedDate the filterOnLastModifiedDate to set
     */
    public void setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate)
    {
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
    }

    /**
     * @return the renameOnSuccess
     */
    public Boolean getRenameOnSuccess()
    {
        return this.renameOnSuccess;
    }

    /**
     * @param renameOnSuccess the renameOnSuccess to set
     */
    public void setRenameOnSuccess(Boolean renameOnSuccess)
    {
        this.renameOnSuccess = renameOnSuccess;
    }

    /**
     * @return the renameOnSuccessExtension
     */
    public String getRenameOnSuccessExtension()
    {
        return this.renameOnSuccessExtension;
    }

    /**
     * @param renameOnSuccessExtension the renameOnSuccessExtension to set
     */
    public void setRenameOnSuccessExtension(String renameOnSuccessExtension)
    {
        this.renameOnSuccessExtension = renameOnSuccessExtension;
    }

    /**
     * @return the moveOnSuccess
     */
    public Boolean getMoveOnSuccess()
    {
        return this.moveOnSuccess;
    }

    /**
     * @param moveOnSuccess the moveOnSuccess to set
     */
    public void setMoveOnSuccess(Boolean moveOnSuccess)
    {
        this.moveOnSuccess = moveOnSuccess;
    }

    /**
     * @return the moveOnSuccessNewPath
     */
    public String getMoveOnSuccessNewPath()
    {
        return this.moveOnSuccessNewPath;
    }

    /**
     * @param moveOnSuccessNewPath the moveOnSuccessNewPath to set
     */
    public void setMoveOnSuccessNewPath(String moveOnSuccessNewPath)
    {
        this.moveOnSuccessNewPath = moveOnSuccessNewPath;
    }

    /**
     * @return the chronological
     */
    public Boolean getChronological()
    {
        return this.chronological;
    }

    /**
     * @param chronological the chronological to set
     */
    public void setChronological(Boolean chronological)
    {
        this.chronological = chronological;
    }

    /**
     * @return the chunking
     */
    public Boolean getChunking()
    {
        return this.chunking;
    }

    /**
     * @param chunking the chunking to set
     */
    public void setChunking(Boolean chunking)
    {
        this.chunking = chunking;
    }

    /**
     * @return the chunkSize
     */
    public Integer getChunkSize()
    {
        return this.chunkSize;
    }

    /**
     * @param chunkSize the chunkSize to set
     */
    public void setChunkSize(Integer chunkSize)
    {
        this.chunkSize = chunkSize;
    }

    /**
     * @return the checksum
     */
    public Boolean getChecksum()
    {
        return this.checksum;
    }

    /**
     * @param checksum the checksum to set
     */
    public void setChecksum(Boolean checksum)
    {
        this.checksum = checksum;
    }

    /**
     * @return the minAge
     */
    public Long getMinAge()
    {
        return this.minAge;
    }

    /**
     * @param minAge the minAge to set
     */
    public void setMinAge(Long minAge)
    {
        this.minAge = minAge;
    }

    /**
     * @return the destructive
     */
    public Boolean getDestructive()
    {
        return this.destructive;
    }

    /**
     * @param destructive the destructive to set
     */
    public void setDestructive(Boolean destructive)
    {
        this.destructive = destructive;
    }

    /**
     * @return the maxRows
     */
    public Integer getMaxRows()
    {
        return this.maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows;
    }

    /**
     * @return the ageOfFiles
     */
    public Integer getAgeOfFiles()
    {
        return this.ageOfFiles;
    }

    /**
     * @param ageOfFiles the ageOfFiles to set
     */
    public void setAgeOfFiles(Integer ageOfFiles)
    {
        this.ageOfFiles = ageOfFiles;
    }

    /**
     * @return the clientID
     */
    public String getClientID()
    {
        return this.clientID;
    }

    /**
     * @param clientID the clientID to set
     */
    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }

    /**
     * @return the cleanupJournalOnComplete
     */
    public Boolean getCleanupJournalOnComplete()
    {
        return this.cleanupJournalOnComplete;
    }

    /**
     * @param cleanupJournalOnComplete the cleanupJournalOnComplete to set
     */
    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    /**
     * @return the remoteHost
     */
    public String getRemoteHost()
    {
        return this.remoteHost;
    }

    /**
     * @param remoteHost the remoteHost to set
     */
    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort()
    {
        return this.remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    /**
     * @return the maxRetryAttempts
     */
    public Integer getMaxRetryAttempts()
    {
        return this.maxRetryAttempts;
    }

    /**
     * @param maxRetryAttempts the maxRetryAttempts to set
     */
    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the active
     */
    public Boolean getActive()
    {
        return this.active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active)
    {
        this.active = active;
    }

    /**
     * @return the connectionTimeout
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the dataTimeout
     */
    public Integer getDataTimeout()
    {
        return this.dataTimeout;
    }

    /**
     * @param dataTimeout the dataTimeout to set
     */
    public void setDataTimeout(Integer dataTimeout)
    {
        this.dataTimeout = dataTimeout;
    }

    /**
     * @return the socketTimeout
     */
    public Integer getSocketTimeout()
    {
        return this.socketTimeout;
    }

    /**
     * @param socketTimeout the socketTimeout to set
     */
    public void setSocketTimeout(Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }

    /**
     * @param systemKey the systemKey to set
     */
    public void setSystemKey(String systemKey)
    {
        this.systemKey = systemKey;
    }

    /**
     * @return the systemKey
     */
    public String getSystemKey()
    {
        return this.systemKey;
    }
}
