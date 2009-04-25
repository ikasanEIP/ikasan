/*
 * $Id: FileTransferPayloadProvider.java 16744 2009-04-22 10:05:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/client-filetransfer/src/main/java/org/ikasan/framework/payload/service/FileTransferPayloadProvider.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.payload.service;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;

import org.apache.log4j.Logger;
import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.framework.factory.DirectoryURLFactory;

/**
 * The FileTransfer Payload Provider
 * 
 * @author Ikasan Development Team
 */
public class FileTransferPayloadProvider implements PayloadProvider
{
    /** Message for moveOnSucccessNewPath not being configured */
    public static final String ILLEGAL_ARGS_MOVE_NULL_SUCCESS_PATH = "moveOnSucccessNewPath has not been configured.";

    /** Message for Moving the file and Get Destructive are mutually exclusive */
    public static final String ILLEGAL_ARGS_MOVING_DESTRUCTIVE_READ = "Moving the file and Get Destructive are mutually exclusive.";

    /** Message for renameExtension not being configured */
    public static final String ILLEGAL_ARGS_RENAME_NULL_EXTENSION = "renameExtension has not been configured.";

    /** Message for Moving the file and renaming it are mutually exclusive */
    public static final String ILLEGAL_ARGS_MOVE_RENAME = "Moving the file and renaming it are mutually exclusive.";

    /** Message for RenameOnSuccess and Get Destructive are mutually exclusive */
    public static final String ILLEGAL_ARGS_DESTRUCTIVE_READ_RENAME = "RenameOnSuccess and Get Destructive are mutually exclusive.";

    /** Remote directory from which to discover files */
    protected String srcDirectory;

    /** Regular expression for matching file names */
    protected String filenamePattern;

    /** Classname for source directories URLs factory */
    protected DirectoryURLFactory sourceDirectoryURLFactory;

    /** Whether we filterDuplicates what we are picking up - True by default */
    protected boolean filterDuplicates = true;

    /** Filter on Filename - True by default */
    protected boolean filterOnFilename = true;

    /** Filter on LastModifiedDate - True by default */
    protected boolean filterOnLastModifiedDate = true;

    /** Rename the remote file once successfully retrieved */
    protected boolean renameOnSuccess = false;

    /** Extension to use when renaming file */
    protected String renameOnSuccessExtension;

    /** Move the remote file to once successfully retrieved */
    protected boolean moveOnSuccess = false;

    /** New path to use when moving the file */
    protected String moveOnSuccessNewPath;

    /** Sort result set by chronological order - false by default. */
    protected boolean chronological = false;

    /** Chunk files when retrieving */
    protected boolean chunking = false;

    /** Maximum size of chunk when chunking, defaults to 1MB */
    protected int chunkSize = 1048576;

    /**
     * Attempt to verify integrity of retrieved file by comparing with a checksum supplied by the remote system
     */
    protected boolean checksum = false;

    /** Minimum age (in seconds) of file to match */
    protected long minAge = 120;

    /** Whether or not we delete the file after picking it up */
    protected boolean destructive = false;

    /** The file transfer connection template */
    protected FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /** Maximum rows that housekeeping can deal with, defaults to -1 (ignore) */
    protected int maxRows = -1;

    /**
     * Number of days in age the files need to be to be considered for housekeeping, defaults to -1 (ignore)
     */
    protected int ageOfFiles = -1;

    /** Logger */
    private static Logger logger = Logger.getLogger(FileTransferPayloadProvider.class);

    /**
     * Constructor
     * 
     * @param srcDirectory - The directory to get the file from
     * @param filenamePattern - The pattern to search on
     * @param connectionFactory - The connection factory
     * @param connectionSpec - The connection specification
     */
    public FileTransferPayloadProvider(String srcDirectory, String filenamePattern,
            EISConnectionFactory connectionFactory, ConnectionSpec connectionSpec)
    {
        this.srcDirectory = srcDirectory;
        this.filenamePattern = filenamePattern;
        this.fileTransferConnectionTemplate = new FileTransferConnectionTemplate(connectionFactory, connectionSpec);
    }

    /**
     * Validates that we don't have an illegal combination of configuration options
     * 
     * @throws ResourceException - Exception if the JCA connector fails
     */
    protected void validateConfiguration() throws ResourceException
    {
        if (renameOnSuccess)
        {
            if (destructive)
            {
                throw new ResourceException(ILLEGAL_ARGS_DESTRUCTIVE_READ_RENAME);
            }
            if (moveOnSuccess)
            {
                throw new ResourceException(ILLEGAL_ARGS_MOVE_RENAME);
            }
            if (renameOnSuccessExtension == null)
            {
                throw new ResourceException(ILLEGAL_ARGS_RENAME_NULL_EXTENSION); //$NON-NLS-1$
            }
        }
        if (moveOnSuccess)
        {
            if (destructive)
            {
                throw new ResourceException(ILLEGAL_ARGS_MOVING_DESTRUCTIVE_READ); //$NON-NLS-1$
            }
            if (moveOnSuccessNewPath == null)
            {
                throw new ResourceException(ILLEGAL_ARGS_MOVE_NULL_SUCCESS_PATH); //$NON-NLS-1$
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.payload.service.PayloadProvider#getNextRelatedPayloads()
     */
    public List<Payload> getNextRelatedPayloads() throws ResourceException
    {
        validateConfiguration();
        List<Payload> result = null;
        List<String> dirs = this.getSrcDirs();
        for (String source : dirs)
        {
            Payload discoveredFile = fileTransferConnectionTemplate.getDiscoveredFile(source, filenamePattern,
                renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, chunking, chunkSize,
                checksum, minAge, destructive, filterDuplicates, filterOnFilename, filterOnLastModifiedDate,
                chronological);
            if (discoveredFile != null)
            {
                result = new ArrayList<Payload>();
                result.add(discoveredFile);
                // return result;
                break;
            }
        }
        this.housekeep();
        return result;
    }

    /**
     * Apply any configured housekeeping on this connection template.
     * 
     * @throws ResourceException - Exception if the JCA connector fails
     */
    protected void housekeep() throws ResourceException
    {
        // If the values have been set then housekeep, else don't
        if (this.maxRows > -1 && this.ageOfFiles > -1)
        {
            fileTransferConnectionTemplate.housekeep(this.maxRows, this.ageOfFiles);
        }
        else
        {
            logger.debug("FileFilter Housekeeping is not configured");
        }
    }

    /**
     * Return a list of src directories to be polled.
     * 
     * @return List of src directories
     */
    protected List<String> getSrcDirs()
    {
        List<String> dirs = new ArrayList<String>();
        // If we've been passed a factory it means there are multiple directories to
        // poll, starting from this.srcDirectory
        if (this.sourceDirectoryURLFactory != null)
        {
            dirs = this.sourceDirectoryURLFactory.getDirectoriesURLs(this.srcDirectory);
        }
        else
        {
            dirs.add(this.srcDirectory);
        }
        return dirs;
    }

    /**
     * Returns true if we are filtering duplicates
     * 
     * @return true if we are filtering duplicates else false
     */
    public boolean isFilterDuplicates()
    {
        return filterDuplicates;
    }

    /**
     * Set the filter duplicates flag
     * 
     * @param filterDuplicates - filter duplicates flag to set
     */
    public void setFilterDuplicates(boolean filterDuplicates)
    {
        this.filterDuplicates = filterDuplicates;
    }

    /**
     * Returns true if we are filtering on filename
     * 
     * @return true if we are filtering on filename else false
     */
    public boolean isFilterOnFilename()
    {
        return filterOnFilename;
    }

    /**
     * Set the filterOnFilename flag
     * 
     * @param filterOnFilename - filter on file name flag to set
     */
    public void setFilterOnFilename(boolean filterOnFilename)
    {
        this.filterOnFilename = filterOnFilename;
    }

    /**
     * Returns true if we are filtering on LastModifiedDate
     * 
     * @return true if we are filtering on LastModifiedDate else false
     */
    public boolean isFilterOnLastModifiedDate()
    {
        return filterOnLastModifiedDate;
    }

    /**
     * Set the filterOnLastModifiedDate flag
     * 
     * @param filterOnLastModifiedDate - filter on last modified date flag to set
     */
    public void setFilterOnLastModifiedDate(boolean filterOnLastModifiedDate)
    {
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
    }

    /**
     * Return true if we are renaming the file after successful delivery
     * 
     * @return true if we are renaming the file after successful delivery else false
     */
    public boolean isRenameOnSuccess()
    {
        return renameOnSuccess;
    }

    /**
     * Set the renameOnSuccess flag
     * 
     * @param renameOnSuccess - rename on success flag to set
     */
    public void setRenameOnSuccess(boolean renameOnSuccess)
    {
        this.renameOnSuccess = renameOnSuccess;
    }

    /**
     * Get the rename on success file extension
     * 
     * @return the rename on success file extension
     */
    public String getRenameOnSuccessExtension()
    {
        return renameOnSuccessExtension;
    }

    /**
     * Set the rename on success file extension
     * 
     * @param renameOnSuccessExtension - extension to rename delivered file to
     */
    public void setRenameOnSuccessExtension(String renameOnSuccessExtension)
    {
        this.renameOnSuccessExtension = renameOnSuccessExtension;
    }

    /**
     * Return true if we move the file after successful delivery
     * 
     * @return true if we move the file after successful delivery else false
     */
    public boolean isMoveOnSuccess()
    {
        return moveOnSuccess;
    }

    /**
     * Set the setMoveOnSuccess flag
     * 
     * @param moveOnSuccess - move on good delivery flag
     */
    public void setMoveOnSuccess(boolean moveOnSuccess)
    {
        this.moveOnSuccess = moveOnSuccess;
    }

    /**
     * Get the new path that we move the file to
     * 
     * @return the new path that we move the file to
     */
    public String getMoveOnSuccessNewPath()
    {
        return moveOnSuccessNewPath;
    }

    /**
     * Set the path that we move the file to after successful delivery
     * 
     * @param moveOnSuccessNewPath - New path to move successfully delivered file to
     */
    public void setMoveOnSuccessNewPath(String moveOnSuccessNewPath)
    {
        this.moveOnSuccessNewPath = moveOnSuccessNewPath;
    }

    /**
     * Return true if we are chunking the large file
     * 
     * @return true if we are chunking the large file else false
     */
    public boolean isChunking()
    {
        return chunking;
    }

    /**
     * Set the chunking flag
     * 
     * @param chunking - chunking flag to set
     */
    public void setChunking(boolean chunking)
    {
        this.chunking = chunking;
    }

    /**
     * Get the size of the chunks
     * 
     * @return size of the chunks
     */
    public int getChunkSize()
    {
        return chunkSize;
    }

    /**
     * Set the size of the chunks
     * 
     * @param chunkSize - chunk size to set
     */
    public void setChunkSize(int chunkSize)
    {
        this.chunkSize = chunkSize;
    }

    /**
     * Return true if we are to checksum the file
     * 
     * @return true if we are to checksum the file else false
     */
    public boolean isChecksum()
    {
        return checksum;
    }

    /**
     * Set the checksum flag
     * 
     * @param checksum - checksum flag to set
     */
    public void setChecksum(boolean checksum)
    {
        this.checksum = checksum;
    }

    /**
     * Get the minimum age of the file to pick up
     * 
     * @return the minimum age of the file to pick up
     */
    public long getMinAge()
    {
        return minAge;
    }

    /**
     * Set the minimum age of the file to pick up
     * 
     * @param minAge - minimum age to set
     */
    public void setMinAge(long minAge)
    {
        this.minAge = minAge;
    }

    /**
     * Return true if we are destructively picking up files
     * 
     * @return true if we are destructively picking up files else false
     */
    public boolean isDestructive()
    {
        return destructive;
    }

    /**
     * Set the destructive flag
     * 
     * @param destructive - destructive flag to set
     */
    public void setDestructive(boolean destructive)
    {
        this.destructive = destructive;
    }

    /**
     * Get the file connection transfer template
     * 
     * @return the file connection transfer template
     */
    public FileTransferConnectionTemplate getFileTransferConnectionTemplate()
    {
        return fileTransferConnectionTemplate;
    }

    /**
     * Get the source directory
     * 
     * @return source directory
     */
    public String getSrcDirectory()
    {
        return srcDirectory;
    }

    /**
     * Get the file name pattern
     * 
     * @return the file name pattern
     */
    public String getFilenamePattern()
    {
        return filenamePattern;
    }

    /**
     * Set the SourceDirectoryURLFactory
     * 
     * @param factory - source durectory URL factory set
     */
    public void setSourceDirectoryURLFactory(DirectoryURLFactory factory)
    {
        this.sourceDirectoryURLFactory = factory;
    }

    /**
     * Get the SourceDirectoryURLFactory
     * 
     * @return Source DirectoryURLFactory
     */
    public DirectoryURLFactory getSourceDirectoryURLFactory()
    {
        return this.sourceDirectoryURLFactory;
    }

    /**
     * Whether to sort the result file set by older first.
     * 
     * @return boolean
     */
    public boolean isChronological()
    {
        return this.chronological;
    }

    /**
     * Set the chronological flag.
     * 
     * @param chronological - chronological order flag to set
     */
    public void setChronological(boolean chronological)
    {
        this.chronological = chronological;
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows()
    {
        return maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(int maxRows)
    {
        this.maxRows = maxRows;
    }

    /**
     * @return the ageOfFiles
     */
    public int getAgeOfFiles()
    {
        return ageOfFiles;
    }

    /**
     * @param ageOfFiles the ageOfFiles to set
     */
    public void setAgeOfFiles(int ageOfFiles)
    {
        this.ageOfFiles = ageOfFiles;
    }
}
