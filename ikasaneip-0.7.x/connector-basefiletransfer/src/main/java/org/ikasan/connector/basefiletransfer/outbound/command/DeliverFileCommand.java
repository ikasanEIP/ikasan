/*
 * $Id$
 * $URL$
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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.InputStream;

import javax.resource.ResourceException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;

/**
 * Delivers a specified payload to a remote directory
 * 
 * @author Ikasan Development Team
 */
public class DeliverFileCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(DeliverFileCommand.class);

    /** we are dealing with pathnames so make sure we stay platform independent */
    final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    /**
     * Temporary file extension to use when delivering
     */
    private String renameExtension;

    /**
     * Directory on remote FS to deliver into
     */
    private String outputDirectory;

    /**
     * Name of the file we are delivering
     */
    private String fileName;

    /**
     * Temporary name of the file until we commit the transaction
     */
    private String tempFileName;

    /**
     * Allow the delivery to overwrite any existing files of the same name
     */
    private boolean overwriteExisting;
    
    /**
     * Flag specifying whether or not an put has actually been attempted
     */
    private boolean putAttempted = false;

    /** Default Constructor for Hibernate */
    public DeliverFileCommand()
    {
        // Do Nothing
    }
    
    /**
     * Constructor
     * 
     * @param outputDirectory
     * @param renameExtension
     * @param overwriteExisting
     */
    public DeliverFileCommand(String outputDirectory, String renameExtension, boolean overwriteExisting)
    {
        super();

        this.renameExtension = renameExtension;
        this.outputDirectory = outputDirectory;
        this.overwriteExisting = overwriteExisting;
    }

    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        boolean changeDirectory = false;
        logger.info("execute called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        String originalDirectory = printWorkingDirectoryName();
        if(!outputDirectory.equals(".") && !outputDirectory.equals(originalDirectory))
        {
            changeDirectory(outputDirectory);
            changeDirectory = true;
        }

        BaseFileTransferMappedRecord mappedRecord = (BaseFileTransferMappedRecord) executionContext
            .get(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD);

        if (mappedRecord != null)
        {
            deliverMappedRecord(mappedRecord);
        }
        else
        {
            InputStream inputStream = (InputStream) executionContext.getRequired(ExecutionContext.FILE_INPUT_STREAM);
            String filePath = (String) executionContext.getRequired(ExecutionContext.RELATIVE_FILE_PATH_PARAM);

            deliverInputStream(filePath, inputStream);
        }

        if(changeDirectory)
        {
            changeDirectory(originalDirectory);
        }

        String destinationPath = outputDirectory + FILE_SEPARATOR + tempFileName;
        
        logger.info("delivered file as hidden: [" + destinationPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return new ExecutionOutput(destinationPath);
    }

    /**
     * Deliver the input stream to the filePath
     * 
     * @param filePath
     * @param inputStream
     * @throws ResourceException
     */
    private void deliverInputStream(String filePath, InputStream inputStream) throws ResourceException
    {
        fileName = filePath;
        tempFileName = filePath+renameExtension;
        String tempFilePath = tempFileName;

        if(!overwriteExisting)
        {
            if(fileExists(tempFileName))
            {
                throw new ResourceException("Cannot deliver tempfile [" 
                    + tempFileName + "] as a file of that name already exists");
            }

            if(fileExists(fileName))
            {
                throw new ResourceException("Cannot deliver file [" 
                    + fileName + "] as a file of that name already exists");
            }
        }
        
        putAttempted = true;
        putWithOutputStream(tempFilePath, inputStream);
    }

    /**
     * Deliver the mapped record
     * 
     * @param mappedRecord
     * @throws ResourceException
     */
    private void deliverMappedRecord(BaseFileTransferMappedRecord mappedRecord) throws ResourceException
    {
        fileName = mappedRecord.getName();
        tempFileName = fileName + renameExtension;
        mappedRecord.setName(tempFileName);

        if(!overwriteExisting)
        {
            if(fileExists(tempFileName))
            {
                throw new ResourceException("Cannot deliver tempfile [" 
                    + tempFileName + "] as a file of that name already exists");
            }

            if(fileExists(fileName))
            {
                throw new ResourceException("Cannot deliver file [" 
                    + fileName + "] as a file of that name already exists");
            }
        }
        
        putAttempted = true;
        putFile(mappedRecord);
    }

    /**
     * Checks if the file we want to write already exists in target directory.
     * If file exists, but <code>overwriteExisitng</code> flag is false, then the file
     * will be overwritten at the commit stage. Otherwise, throws an exception.
     * @param filename - file name to check
     * @return true if the file name exists; false if it doesn't
     * @throws ResourceException Thrown if <code>overwriteExisitng</code> flag is true and file 
     * already exists. This will cause the transaction to rollback. 
     */
    private boolean fileExists(final String filename) throws ResourceException
    {
        ClientListEntry existingFile = findFile(filename);
        if (existingFile != null)
        {
            return true;
        }
        return false;
    }

    /**
     * Commit if file delivery is successful: rename file from its temporary name to actual name.
     * This method will not check for duplicate file names - it simply overwrites anything of the same
     * name regardless of the overwrite flag. This is a commit routine and these types
     * of delivery checks should have already been completed.
     * 
     * IMPORTANT NOTE: Operations in this commit method should be kept to a 
     * minimum to reduce potential fails on the commit.
     */
    @Override
    protected void doCommit() throws ResourceException
    {
        boolean changeDirectory = false;
        
        logger.info("commit called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$);
        FileTransferClient client = getClient();
        client.ensureConnection();
        String originalDirectory = printWorkingDirectoryName();
        if(!outputDirectory.equals(".") && !outputDirectory.equals(originalDirectory))
        {
            changeDirectory(outputDirectory);
            changeDirectory = true;
        }

        renameFile(tempFileName, fileName);

        if(changeDirectory)
        {
            changeDirectory(originalDirectory);
        }

        client.disconnect();
        logger.info("Disconnected.");
    }

    @Override
    protected void doRollback() throws ResourceException
    {
        logger.info("rollback called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        //only need to even try and do anything if an put was actually attempted
        
        logger.info("put attempted: [" +putAttempted + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        if (putAttempted){
            String originalDirectory = printWorkingDirectoryName();
            changeDirectory(outputDirectory);

            ClientListEntry deliveredEntry = findFile(tempFileName);
            if (deliveredEntry!=null){
                deleteFile(tempFileName);
    
                // Log the output directory and change back to the working dir
                logFileList(listDirectory(CURRENT_DIRECTORY), outputDirectory);
    
                changeDirectory(originalDirectory);
            }
        }
        
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
            .append("id", this.getId()) //$NON-NLS-1$
            .append("renameExtension", this.renameExtension) //$NON-NLS-1$
            .append("fileName", this.fileName) //$NON-NLS-1$
            .append("outputDirectory", this.outputDirectory) //$NON-NLS-1$
            .append("client", getClient()) //$NON-NLS-1$
            .append("tempFileName", this.tempFileName) //$NON-NLS-1$
            .append("fileSeparator", this.fileSeparator).toString(); //$NON-NLS-1$
    }

    /**
     * Accessor for FileName
     * 
     * @return fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Setter called by Hibernate
     * 
     * @param fileName
     */
    @SuppressWarnings("unused")
    private void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Accessor for tempFileName
     * 
     * @return tempFileName
     */
    public String getTempFileName()
    {
        return tempFileName;
    }

    /**
     * Setter called by Hibernate
     * 
     * @param tempFileName
     */
    @SuppressWarnings("unused")
    private void setTempFileName(String tempFileName)
    {
        this.tempFileName = tempFileName;
    }

    /**
     * Accessor for putAttempted
     * @return putAttempted
     */
    public boolean isPutAttempted()
    {
        return putAttempted;
    }

    /**
     * Setter called by Hibernate
     * 
     * @param putAttempted
     */
    @SuppressWarnings("unused")
    private void setPutAttempted(boolean putAttempted)
    {
        this.putAttempted = putAttempted;
    }

    /**
     * Get the output directory
     * @return The directory where we output the file to
     */
    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Private setter for Hibernate to use
     * @param outputDirectory
     */
    @SuppressWarnings("unused")
    private void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Check if we overwrite the existing file
     * @return true if we overwrite else false
     */
    public boolean isOverwriteExisting()
    {
        return overwriteExisting;
    }

    /**
     * Privately set the overwrite existing flag, used by Hibernate
     * @param overwriteExisting
     */
    @SuppressWarnings("unused")
    private void setOverwriteExisting(boolean overwriteExisting)
    {
        this.overwriteExisting = overwriteExisting;
    }

}
