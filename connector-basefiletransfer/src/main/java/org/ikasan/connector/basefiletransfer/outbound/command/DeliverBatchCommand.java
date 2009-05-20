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

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.outbound.command.util.BatchedFileProvider;
import org.ikasan.connector.basefiletransfer.outbound.command.util.FileHandle;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UniqueIdGenerator;

/**
 * Delivers a batch of files via file transfer
 * 
 * @author Ikasan Development Team
 */
public class DeliverBatchCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(DeliverBatchCommand.class);

    /** Directory on remote FS to deliver into */
    private String outputDirectory;

    /** Temp directory to deliver content to within the output directory */
    private String tempDirectory;

    /**
     * TODO To be implemented
     * 
     * Allow the delivery to overwrite any existing files of the same name
     */
    private boolean overwriteExisting;

    /** Folder name for delivering these files into */
    private String batchFolder;

    /**
     * Flag specifying whether or not an file transfer has actually been
     * attempted
     */
    private boolean putAttempted = false;

    /** Id generator for generating temp dir names */
    private UniqueIdGenerator idGenerator = new DefaultIdGenerator();

    /** No Args Constructor Required by Hibernate */
    @SuppressWarnings("unused")
    private DeliverBatchCommand()
    {
        // Default constructor
    }

    /**
     * Constructor
     * 
     * @param outputDirectory The directory we're delivering to
     * @param overwriteExisting Whether we overwrite existing files or not
     */
    public DeliverBatchCommand(String outputDirectory, boolean overwriteExisting)
    {
        super();
        this.outputDirectory = outputDirectory;
        this.overwriteExisting = overwriteExisting;
        this.tempDirectory = "temp_" + generateId();
    }

    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        logger.info("execute called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        // Save off the original location to return to
        String originalDirectory = printWorkingDirectoryName();
        
        // Change directory to where we need to go
        changeDirectory(outputDirectory);

        // Get the batched file provider
        BatchedFileProvider batchedFileProvider = (BatchedFileProvider) executionContext
            .getRequired(ExecutionContext.BATCHED_FILE_PROVIDER);

        // Get the batched file
        String batchedFileName = (String) executionContext.getRequired(ExecutionContext.BATCHED_FILE_NAME);
        batchFolder = batchedFileName;
        int suffix = 1;
        while (findFile(batchFolder) != null)
        {
            batchFolder = batchedFileName + "_" + suffix;
            suffix++;
        }

        while (batchedFileProvider.hasNext())
        {
            FileHandle fileHandle = batchedFileProvider.next();
            File fileInsideTempDir = new File(tempDirectory, fileHandle.getPath());
            if (fileHandle.isDirectory())
            {
                logger.info("Ignoring as its just a directory [" + fileHandle.getPath() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                deliverInputStream(fileInsideTempDir.getPath(), fileHandle.getContentAsInputStream());
            }
        }

        // Go back to the original directory
        changeDirectory(originalDirectory);

        return new ExecutionOutput(tempDirectory);
    }

    /**
     * generates a unique id
     * 
     * @return unique id as a String
     */
    private String generateId()
    {
        return idGenerator.getUniqueId();
    }

    /**
     * Deliver the input stream to the filePath
     * 
     * @param filePath The path that we're writing to
     * @param inputStream The stream we're using to write with
     * @throws ResourceException Exception thrown by the Conn4ctor
     * 
     * TODO - this method is slightly different from the same method in 
     * DeliverFileCommand. I would have thought it would be more similar, but
     * have no time to check/change/test at the moment. To Be Reviewed.
     */
    private void deliverInputStream(String filePath, InputStream inputStream) throws ResourceException
    {
        logger.debug("deliverInputStream called with filePath [" + filePath + "]"); //$NON-NLS-1$//$NON-NLS-2$

        putAttempted = true;
        putWithOutputStream(filePath, inputStream);
    }

    /**
     * (non-Javadoc) Needs to move everything out of the temp directory to which
     * it was delivered, to its final destination
     * 
     * @see org.ikasan.connector.base.command.AbstractTransactionalResourceCommand#doCommit()
     */
    @Override
    protected void doCommit() throws ResourceException
    {
        logger.info("commit called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$);
        getClient().ensureConnection();
        String originalDirectory = printWorkingDirectoryName();
        changeDirectory(outputDirectory);
        renameFile(tempDirectory, batchFolder);
        changeDirectory(originalDirectory);
    }

    @Override
    protected void doRollback() throws ResourceException
    {
        logger.info("rollback called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        // only need to even try and do anything if an put was actually
        // attempted

        logger.info("put attempted: [" + putAttempted + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        if (putAttempted)
        {
            File tempFile = new File(outputDirectory, tempDirectory);
            String tempDirPath = tempFile.getPath();

            logger.info("inside rollback, about to delete temp dir [" + tempDirPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            deleteDir(tempDirPath, true);
        }

    }

    /**
     * @return tempDirectory
     */
    public String getTempDirectory()
    {
        return tempDirectory;
    }

    /**
     * Setter called by Hibernate
     * 
     * @param tempDirectory The temp directory
     */
    @SuppressWarnings("unused")
    private void setTempDirectory(String tempDirectory)
    {
        this.tempDirectory = tempDirectory;
    }

    /**
     * Accessor method for outputDirectory
     * 
     * @return outputDirectory
     */
    @SuppressWarnings("unused")
    private String getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Setter method for outputDirectory, used by Hibernate
     * 
     * @param outputDirectory The directory we're outputting to
     */
    @SuppressWarnings("unused")
    private void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return batchFolder
     */
    public String getBatchFolder()
    {
        return batchFolder;
    }

    /**
     * Setter called by Hibernate
     * 
     * @param batchFolder The batch folder
     */
    @SuppressWarnings("unused")
    private void setBatchFolder(String batchFolder)
    {
        this.batchFolder = batchFolder;
    }

    /**
     * Simple Id default Id generator
     * 
     * @author duncro
     * 
     */
    class DefaultIdGenerator implements UniqueIdGenerator
    {

        public String getUniqueId()
        {
            return UUID.randomUUID().toString();
        }

    }

    /**
     * allows the default id generator to be overridden
     * 
     * @param idGenerator The id generator
     */
    public void setIdGenerator(UniqueIdGenerator idGenerator)
    {
        this.idGenerator = idGenerator;
    }

    /**
     * Accessor for putAttempted
     * 
     * @return putAttempted
     */
    public boolean isPutAttempted()
    {
        return putAttempted;
    }

    /**
     * Setter called by Hibernate
     * 
     * @param putAttempted The put attempted flag
     */
    @SuppressWarnings("unused")
    private void setPutAttempted(boolean putAttempted)
    {
        this.putAttempted = putAttempted;
    }

}
