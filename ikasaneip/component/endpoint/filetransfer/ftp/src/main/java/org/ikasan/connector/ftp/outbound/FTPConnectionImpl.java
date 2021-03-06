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
package org.ikasan.connector.ftp.outbound;

import com.google.common.cache.Cache;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.OlderFirstClientListEntryComparator;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnectionImpl;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferMappedRecordTransformer;
import org.ikasan.connector.basefiletransfer.outbound.command.ChecksumValidatorCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.ChunkingRetrieveFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.FileDiscoveryCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.RetrieveFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.listener.TransactionCommitFailureListener;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.filetransfer.Payload;
import org.ikasan.filetransfer.util.checksum.ChecksumSupplier;
import org.ikasan.filetransfer.util.checksum.Md5ChecksumSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.resource.ResourceException;
import java.util.Collections;
import java.util.List;

import static org.ikasan.filetransfer.FilePayloadAttributeNames.RELATIVE_PATH;

/**
 * This class implements the virtual connection to the FTP server.<br>
 * An instance of this class is returned to the clients via the getConnection()
 * method called on<br>
 * FTPConnectionFactory by the Application Server.
 * 
 * All functionality for the FTP virtual connection is defined in the interface
 * and implemented within this class.
 * 
 * In reality much of the work is passed onto the FTPManagedConnection as that
 * does the 'physical work' of talking to the FTP Server
 * 
 * @author Ikasan Development Team
 */
public class FTPConnectionImpl extends BaseFileTransferConnectionImpl
{
    /** The logger instance. */
    private static Logger logger = LoggerFactory.getLogger(FTPConnectionImpl.class);

    /** Id for Client of this connector */
    private String clientId;

    /** Md5 is the only checksum algorithm currently supported */
    private ChecksumSupplier checksumSupplier = new Md5ChecksumSupplier();

    /**
     * The managed connection, overrides the definition in the EISConnectionImpl
     * so that we don't have to cast back all of the time
     */
    protected FTPManagedConnection managedConnection;

    /**
     * TODO THis needs to be configurable Threshold at which we begin chunking
     * files, instead of getting them complete
     */
    private Long chunkableThreshold = 1024 * 1024l; // ie 1MB

    private FileChunkDao fileChunkDao;

    private BaseFileTransferDao baseFileTransferDao;

    private Cache<String, Boolean> duplicateFilesCache;

    /**
     * Constructor which takes ManagedConnection as a parameter
     *
     * @param mc
     * @param fileChunkDao
     * @param baseFileTransferDao
     */
    public FTPConnectionImpl(FTPManagedConnection mc, FileChunkDao fileChunkDao, BaseFileTransferDao baseFileTransferDao) {
        this(mc, fileChunkDao, baseFileTransferDao, null);
    }

    /**
     * Constructor which takes ManagedConnection as a parameter
     *
     * @param mc
     * @param fileChunkDao
     * @param baseFileTransferDao
     * @param duplicateFilesCache
     */
    public FTPConnectionImpl(FTPManagedConnection mc, FileChunkDao fileChunkDao, BaseFileTransferDao baseFileTransferDao,
                             Cache<String, Boolean> duplicateFilesCache)
    {
        this.managedConnection = (FTPManagedConnection) mc;
        this.clientId = managedConnection.getClientID();
        this.fileChunkDao = fileChunkDao;
        this.baseFileTransferDao = baseFileTransferDao;
        this.duplicateFilesCache = duplicateFilesCache;

    }
    /**
     * getManagedConnection is called by the FTPManagedConnection when the
     * application server wants to re-associate a virtual connection with a new
     * manager.
     */
    public TransactionalCommandConnection getManagedConnection()
    {
        logger.info("Called getManagedConnection(). I have " + this.listeners.size() + " listeners!"); //$NON-NLS-1$
        for(TransactionCommitFailureListener listener: this.listeners)
        {
            this.managedConnection.addListener(listener);
        }
        return this.managedConnection;
    }

    /**
     * setManagedConnection is called by the FTPManagedConnection when the
     * application server wants to re-associate a virtual connection with a new
     * manager.
     * 
     * @param managedConnection - The managed connection
     */
    public void setManagedConnection(final TransactionalCommandConnection managedConnection)
    {
        logger.debug("Called setManagedConnection()"); //$NON-NLS-1$
        this.managedConnection = (FTPManagedConnection) managedConnection;
    }

    /**
     * Clients call this to indicate that they have finished with a connection.
     * Of course, we aren't really going to close anything, we will simply
     * indicate to the manager of this virtual connection that it is now
     * defunct.
     */
    @Override
    public void close()
    {
        logger.debug("Called close()"); //$NON-NLS-1$
        // Ignore if already closed
        if(this.managedConnection == null)
        {
            logger.debug("ManagedConnection is null, exiting close early."); //$NON-NLS-1$
            return;
        }
        // Close the physical session
        logger.debug("Calling closeSession."); //$NON-NLS-1$
        this.managedConnection.closeSession();
        /// Set the managed connection to null if not already done
        if(this.managedConnection != null)
        {
            this.managedConnection = null;
        }
        // The managed connection has already been set to null
        // by its own cleanup method
        else
        {
            logger.debug("Managed Connection was already set to null."); //$NON-NLS-1$
        }
    }

    // /////////////////////////////////////////////////////
    // Business Methods
    // /////////////////////////////////////////////////////

    /* 
     * We suppress the warning for not checking that each item that
     * comes back is a ClientListEntry, since we're dealing with a
     * 1.4 library that doesn't _really_ know, but we trust it anyhow.
     */
    @SuppressWarnings("unchecked")
    public Payload getDiscoveredFile(String sourceDir, String filenamePattern, boolean renameOnSuccess, String renameOnSuccessExtension, boolean moveOnSuccess,
            String moveOnSuccessNewPath, boolean chunking, int chunkSize, boolean checksum, long minAge, boolean destructive, boolean filterDuplicates,
            boolean filterOnFilename, boolean filterOnLastModifiedDate, boolean chronological, boolean isRecursive,
                                     boolean provideRelativePath) throws ResourceException
    {
        Payload result = null;
        ExecutionContext executionContext = new ExecutionContext();
        // Pass through the client Id
        logger.debug("Got clientId [" + this.clientId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.debug("Source = [" + sourceDir+ "] moveOnSuccess = [" + moveOnSuccess + "] and archive dir = [" + moveOnSuccessNewPath + "].");
        executionContext.put(ExecutionContext.CLIENT_ID, this.clientId);
        FileDiscoveryCommand fileDiscoveryCommand = new FileDiscoveryCommand(sourceDir, filenamePattern, baseFileTransferDao, minAge, filterDuplicates,
            filterOnFilename, filterOnLastModifiedDate,isRecursive, chronological, false, duplicateFilesCache);

        // Discover any new files
        List<?> entries = executeCommand(fileDiscoveryCommand, executionContext).getResultList();

        if(logger.isDebugEnabled()) {
            logger.debug("got entries from FileDiscoveryCommand: [" + entries + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // If there are any new files, only source the first one
        // TODO this should be configurable
        if(!entries.isEmpty())
        {
            logger.debug("Got [" + entries.size() + "] entries.");
            ClientListEntry entry = (ClientListEntry) entries.get(0);
            String fullMovePath = moveOnSuccessNewPath;
            // if moveOnSuccess is true, then the path is appended to include
            // the file name.
            if(moveOnSuccess)
            {
                int lastIndexOf = entry.getUri().getPath().lastIndexOf("/");
                fullMovePath = fullMovePath + entry.getUri().getPath().substring(lastIndexOf);
            }
            result = sourceFile(entry, this.clientId, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, fullMovePath, chunking, chunkSize, checksum,
                destructive, baseFileTransferDao);
            result.setAttribute(RELATIVE_PATH, entry.evalRelativePath(sourceDir));

        }
        return result;
    }

    /**
     * Executes a command with a given ExecutionContext
     * 
     * @param command - The command to execute
     * @param executionContext - The context of execution
     * @return ExecutionOutput - The output from executing the command
     * @throws ResourceException - Exception from the FTP connector
     */
    @Override
    protected ExecutionOutput executeCommand(TransactionalResourceCommand command, ExecutionContext executionContext) throws ResourceException
    {
        command.setExecutionContext(executionContext);
        super.addListenersToCommand(command);
        return executeCommand(command);
    }



    /**
     * Retrieves lightweight handles to the File Chunks that will be needed for
     * reconstitution
     * 
     * @param fileChunkDao - The DAO class for file chunks
     * @param fileName - The name of the file
     * @param fileChunkTimeStamp - The timestamp of the file chunk
     * @return List of <code>FileConstituentHandle</code>
     * @throws ResourceException - The FTP Connector Exception if any.
     */
    private List<FileConstituentHandle> getConstituentHandles(FileChunkDao fileChunkDao, String fileName, Long fileChunkTimeStamp) throws ResourceException
    {
        // Find references to all the chunks that we need to reconstitute the
        // file
        List<FileConstituentHandle> constituentHandles = fileChunkDao.findChunks(fileName, fileChunkTimeStamp, null, null);
        if(constituentHandles.isEmpty())
        {
            throw new ResourceException("No chunks found for file: [" + fileName + "]");
        }
        return constituentHandles;
    }

    /**
     * Retrieves a single file from the remote directory
     * 
     * @param entry The file to get
     * @param clientID The client id (client that is requesting this)
     * @param checksum Flag on whether or not there is a checksum involved
     * @param chunkSize The size of the chunk (if we're file chunking)
     * @param renameOnSuccessExtension Filename extension for renamed file
     * @param renameOnSuccess Flag to indicate whether we rename the file on
     *            successful retrieval
     * @param moveOnSuccess Flag to indicate whether we move the file on
     *            successful retrieval
     * @param moveOnSuccessNewPath Path to move the successfully retrieved file
     *            to
     * @param chunking Boolean flag whether we are chunking or not
     * @param destructive Whether to destructively read or not
     * @param baseFileTransferDao The DAO class for chunking
     * @return FTPMappedRecord
     * @throws ResourceException Exception thrown by the FTP conenctor
     */
    private Payload sourceFile(ClientListEntry entry, String clientID, boolean renameOnSuccess, String renameOnSuccessExtension, boolean moveOnSuccess,
            String moveOnSuccessNewPath, boolean chunking, int chunkSize, boolean checksum, boolean destructive, BaseFileTransferDao baseFileTransferDao)
            throws ResourceException
    {
        logger.debug("sourceFile called with entry: [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        // FTPMappedRecord result = null;
        Payload result = null;
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.CLIENT_ID, clientID);
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);
        if(chunking && shouldChunk(entry))
        {
            // chunking specific
            logger.debug("About to call ChunkingRetrieveFileCommand"); //$NON-NLS-1$
            ChunkingRetrieveFileCommand chunkingRetrieveFileCommand = new ChunkingRetrieveFileCommand(baseFileTransferDao, clientID, renameOnSuccess,
                renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, fileChunkDao, chunkSize, destructive);
            Object executionResult = executeCommand(chunkingRetrieveFileCommand, executionContext).getResult();
            FileChunkHeader fileChunkHeader = (FileChunkHeader) executionResult;
            result = fileChunkHeaderToPayload(fileChunkHeader);
            // create some special kind of Payload for Chunked Files
            // end chunking specific
        }
        else
        {
            // non chunking specific
            logger.debug("About to call RetrieveFileCommand"); //$NON-NLS-1$
            RetrieveFileCommand retrieveFileCommand = new RetrieveFileCommand(baseFileTransferDao, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess,
                moveOnSuccessNewPath, destructive);
            ExecutionOutput executionResult = executeCommand(retrieveFileCommand, executionContext);
            BaseFileTransferMappedRecord ftpMappedRecord = (BaseFileTransferMappedRecord) executionResult.getResult();
            if(ftpMappedRecord == null)
            {
                logger.warn("No file was picked up."); //$NON-NLS-1$
                return result;
            }
            result = BaseFileTransferMappedRecordTransformer.mappedRecordToPayload(ftpMappedRecord);
            // end non chunking specific
        }
        String sourcePath = entry.getUri().getPath();
        executionContext.put(ExecutionContext.PAYLOAD, result);
        logger.debug("About to call ChecksumValidatorCommand"); //$NON-NLS-1$
        if(checksum)
        {
            logger.info("comparing checksum of sourced file with that in external checksum file"); //$NON-NLS-1$
            ChecksumValidatorCommand checksumValidatorCommand = new ChecksumValidatorCommand(checksumSupplier, destructive, sourcePath);
            executeCommand(checksumValidatorCommand, executionContext);
        }
        else
        {
            logger.info("checksumming disabled"); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * TODO Non trivial change but this too can go up one level to
     * BaseFileTransferConnectionImpl
     * 
     * @param maxRows The maximum amount of rows to housekeep
     * @param ageOfFiles The age of the files to housekeep
     * @throws ResourceException Exception thrown by the FTP Connector
     */
    public void housekeep(int maxRows, int ageOfFiles) throws ResourceException
    {
        try
        {
            baseFileTransferDao.housekeep(clientId, ageOfFiles, maxRows);
        }
        catch (Exception e)
        {
            throw new ResourceException(e);
        }
    }

    /**
     * Determines if this file should be chunked or not
     * 
     * @param entry File to check
     * @return true if the file should be chunked
     */
    private boolean shouldChunk(ClientListEntry entry)
    {
        boolean result = false;
        if(chunkableThreshold != null)
        {
            result = entry.getSize() > chunkableThreshold;
        }
        return result;
    }



    /**
     * Executes the supplied command
     * 
     * @param command Command to execute
     * @return ExecutionOutput
     * @throws ResourceException Exception from FTP Connector
     */
    private ExecutionOutput executeCommand(TransactionalResourceCommand command) throws ResourceException
    {
        super.addListenersToCommand(command);
        return (this.getManagedConnection().executeCommand(command));
    }

}
