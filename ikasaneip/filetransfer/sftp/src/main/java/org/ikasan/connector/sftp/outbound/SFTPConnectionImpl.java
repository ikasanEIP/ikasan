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
package org.ikasan.connector.sftp.outbound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.connector.base.command.*;
import org.ikasan.filetransfer.Payload;
import org.ikasan.filetransfer.util.checksum.ChecksumSupplier;
import org.ikasan.filetransfer.util.checksum.Md5ChecksumSupplier;
import org.ikasan.connector.ConnectorException;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.OlderFirstClientListEntryComparator;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnectionImpl;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferMappedRecordTransformer;
import org.ikasan.connector.basefiletransfer.outbound.command.ChecksumDeliveredCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.ChecksumValidatorCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.ChunkingRetrieveFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.CleanupChunksCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverBatchCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.FileDiscoveryCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.RetrieveFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.util.FilenameRegexpMatchedTargetDirectorySelector;
import org.ikasan.connector.basefiletransfer.outbound.command.util.TargetDirectorySelector;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzipNotSupportedException;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzippingFileProvider;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.listener.TransactionCommitFailureListener;
import org.ikasan.connector.util.chunking.io.ChunkInputStream;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.ChunkHeaderLoadException;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

/**
 * This class implements the virtual connection to the SFTP server. An instance
 * of this class is returned to the clients via the getConnection() method
 * called on SFTPConnectionFactory by the Application Server.
 * 
 * All functionality for the SFTP virtual connection is defined in the interface
 * and implemented within this class.
 * 
 * In reality much of the work is passed onto the SFTPManagedConnection as that
 * does the 'physical work' of talking to the SFTP Server
 * 
 * @author Ikasan Development Team
 */
public class SFTPConnectionImpl extends BaseFileTransferConnectionImpl
{
    /** The logger instance. */
    private static Logger logger = LoggerFactory.getLogger(SFTPConnectionImpl.class);

    /**
     * Id for Client of this connector
     */
    private String clientId;
    
    /**
     * Md5 is the only checksum algorithm currently supported
     */
    private ChecksumSupplier checksumSupplier = new Md5ChecksumSupplier();

    /**
     * The managed connection, overrides the definition in the EISConnectionImpl
     * so that we don't have to cast back all of the time
     */
    protected SFTPManagedConnection managedConnection;
    
    /**
     * TODO THis needs to be configurable Threshold at which we begin chunking
     * files, instead of getting them complete
     */
    private Long chunkableThreshold = 1024 * 1024l; // ie 1MB

    private FileChunkDao fileChunkDao;
    private BaseFileTransferDao baseFileTransferDao;

    /**
     * Constructor which takes ManagedConnection as a parameter
     * 
     * @param mc The ManagedConnection
     */
    public SFTPConnectionImpl(SFTPManagedConnection mc, FileChunkDao fileChunkDao, BaseFileTransferDao baseFileTransferDao)
    {
        this.managedConnection = mc;
        this.clientId = managedConnection.getClientID();
        this.fileChunkDao = fileChunkDao;
        this.baseFileTransferDao = baseFileTransferDao;

    }

    /**
     * getManagedConnection is called by the SFTPManagedConnection when the
     * application server wants to reassociate a virtual connection with a new
     * manager.
     */
    public TransactionalCommandConnection getManagedConnection()
    {
        logger.debug("Called getManagedConnection()"); //$NON-NLS-1$
        for(TransactionCommitFailureListener listener: this.listeners)
        {
            this.managedConnection.addListener(listener);
        }
        return this.managedConnection;
    }

    /**
     * setManagedConnection is called by the SFTPManagedConnection when the
     * application server wants to reassociate a virtual connection with a new
     * manager.
     * 
     * @param managedConnection -
     */
    public void setManagedConnection(final TransactionalCommandConnection managedConnection)
    {
        logger.debug("Called setManagedConnection()"); //$NON-NLS-1$
        this.managedConnection = (SFTPManagedConnection)managedConnection;
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
        if (this.managedConnection == null)
        {
            logger.debug("ManagedConnection is null, exiting close early."); //$NON-NLS-1$
            return;
        }
        // Close the physical session
        logger.debug("Calling closeSession."); //$NON-NLS-1$
        this.managedConnection.closeSession();

        // Set the managed connection to null if not already done
        if (this.managedConnection != null)
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
    public Payload getDiscoveredFile(String sourceDir, String filenamePattern, boolean renameOnSuccess,
            String renameOnSuccessExtension, boolean moveOnSuccess, String moveOnSuccessNewPath, boolean chunking, int chunkSize, boolean checksum,
            long minAge, boolean destructive, boolean filterDuplicates, boolean filterOnFilename, boolean  filterOnLastModifiedDate,
            boolean chronological, boolean isRecursive)
            throws ResourceException
    {
        Payload result = null;

        ExecutionContext executionContext = new ExecutionContext();
        // Pass through the client Id
        logger.debug("Got clientId [" + clientId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.debug("Source = [" + sourceDir+ "] moveOnSuccess = [" + moveOnSuccess + "] and archive dir = [" + moveOnSuccessNewPath + "].");
        executionContext.put(ExecutionContext.CLIENT_ID, clientId);

        FileDiscoveryCommand fileDiscoveryCommand = new FileDiscoveryCommand(sourceDir, filenamePattern,
            baseFileTransferDao, minAge, filterDuplicates, filterOnFilename, filterOnLastModifiedDate, isRecursive);

        // Discover any new files
        List<?> entries = executeCommand(fileDiscoveryCommand, executionContext).getResultList();
        if (chronological)
        {
            List<ClientListEntry> list = (List<ClientListEntry>)entries;
            logger.info("Sorting entries list by chronological order.");
            Collections.sort(list, new OlderFirstClientListEntryComparator());
        }
        logger.debug("got entries from FileDiscoveryCommand: [" + entries + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        // If there are any new files, only source the first one
        // TODO this should be configurable
        if (!entries.isEmpty())
        {
            logger.debug("Got [" + entries.size() + "] entries.");
            ClientListEntry entry = (ClientListEntry) entries.get(0);
            String fullMovePath = moveOnSuccessNewPath;
            //if moveOnSuccess is true, then the path is appended to include the file name.
            if (moveOnSuccess)
            {
                int lastIndexOf = entry.getUri().getPath().lastIndexOf("/");
                fullMovePath = fullMovePath + entry.getUri().getPath().substring(lastIndexOf);
            }
            result = sourceFile(entry, clientId, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, fullMovePath, chunking, chunkSize, checksum,
                destructive, baseFileTransferDao);
        }
        return result;
    }

    /**
     * Deliver the Payload
     * 
     * @param payload to deliver
     * @param unzip flag
     * @throws ResourceException -
     * @deprecated - use deliverInputStream
     */
    public void deliverPayload(Payload payload, String outputDir, Map<String, String> outputTargets, boolean overwrite,
            String renameExtension, boolean checksumDelivered, boolean unzip, boolean cleanup) throws ResourceException
    {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.PAYLOAD, payload);
        String tempFilePath;
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(outputTargets);

        // determine any the target directory for this payload within the output
        // dir if a payload specific target directory has been specified
        String outputTarget = selector.getTargetDirectory(payload, outputDir);

        if (isChunkReference(payload))
        {
            FileChunkHeader reconstitutedFileChunkHeader = FileChunkHeader.fromXml(new String(payload.getContent()));
            if (reconstitutedFileChunkHeader == null)
            {
                throw new ConnectorException("Could not deserialize payload content"); //$NON-NLS-1$
            }
            Long fileHeaderPrimaryKey = reconstitutedFileChunkHeader.getId();
            if (fileHeaderPrimaryKey == null)
            {
                throw new ConnectorException("Could not get pk from deserialized payload content"); //$NON-NLS-1$
            }

            FileChunkHeader fileChunkHeader;
            try
            {
                fileChunkHeader = fileChunkDao.load(fileHeaderPrimaryKey);
            }
            catch (ChunkHeaderLoadException e)
            {
                throw new ConnectorException(
                    "FileChunkHeader with pk [" + fileHeaderPrimaryKey + "] could not be reloaded from the database", e); //$NON-NLS-1$ //$NON-NLS-2$
            }

            List<FileConstituentHandle> handles = getConstituentHandles(fileChunkDao, fileChunkHeader.getFileName(),
                fileChunkHeader.getChunkTimeStamp());
            ChunkInputStream chunkInputStream;
            try
            {
                chunkInputStream = new ChunkInputStream(handles, fileChunkDao);
            }
            catch (IOException e1)
            {
                throw new ResourceException("Exception creating ChunkInputStream", e1); //$NON-NLS-1$
            }

            TransactionalResourceCommand deliveryCommand = null;
            if (!unzip)
            {
                executionContext.put(ExecutionContext.FILE_INPUT_STREAM, chunkInputStream);
                executionContext.put(ExecutionContext.RELATIVE_FILE_PATH_PARAM, fileChunkHeader.getFileName());

                // do not support createParentDirectory for PayloadDelivery as this should be deprecated
                deliveryCommand = new DeliverFileCommand(outputTarget, renameExtension, overwrite, false, null);
            }
            else
            // unzip
            {
                try
                {
                    executionContext.put(ExecutionContext.BATCHED_FILE_PROVIDER, new UnzippingFileProvider(
                        chunkInputStream));
                }
                catch (UnzipNotSupportedException e)
                {
                    throw new ResourceException("Exception trying to unzip stream", e); //$NON-NLS-1$

                }
                executionContext.put(ExecutionContext.BATCHED_FILE_NAME, fileChunkHeader.getFileName());

                deliveryCommand = new DeliverBatchCommand(outputTarget, overwrite);

            }
            tempFilePath = (String) executeCommand(deliveryCommand, executionContext).getResult();

            if (cleanup)
            {
                logger.debug("about to cleanup file chunks"); //$NON-NLS-1$
                executionContext.put(ExecutionContext.FILE_CHUNK_HEADER, reconstitutedFileChunkHeader);
                
                Map<String, Object> beanFactory = new HashMap<String, Object>();
                beanFactory.put("fileChunkDao", fileChunkDao);
                
                CleanupChunksCommand cleanupChunksCommand = new CleanupChunksCommand();
                cleanupChunksCommand.setBeanFactory(beanFactory);

                executeCommand(cleanupChunksCommand, executionContext);
                // executeForResult(executionContext, "cleanupChunksCommand");
                logger.debug("back from file chunk cleanup"); //$NON-NLS-1$
            }

        }
        else
        {
            // not a chunked file
            TransactionalResourceCommand deliveryCommand = null;
            BaseFileTransferMappedRecord mappedRecord = BaseFileTransferMappedRecordTransformer
                .payloadToMappedRecord(payload);

            if (!unzip)
            {

                executionContext.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, mappedRecord);
                // do not support createParentDirectory for PayloadDelivery as this should be deprecated
                deliveryCommand = new DeliverFileCommand(outputTarget, renameExtension, overwrite, false, null);

            }
            else
            {
                // unzip mapped record file
                ByteArrayInputStream bais = new ByteArrayInputStream(mappedRecord.getContent());
                try
                {
                    executionContext.put(ExecutionContext.BATCHED_FILE_PROVIDER, new UnzippingFileProvider(bais));
                }
                catch (UnzipNotSupportedException e)
                {
                    throw new ResourceException("Exception trying to unzip byte array", e); //$NON-NLS-1$

                }
                executionContext.put(ExecutionContext.BATCHED_FILE_NAME, mappedRecord.getName());

                deliveryCommand = new DeliverBatchCommand(outputTarget, overwrite);

            }
            tempFilePath = (String) executeCommand(deliveryCommand, executionContext).getResult();
        }

        // reload and checksum the delivered file
        if (checksumDelivered)
        {
            executionContext.put(ExecutionContext.DELIVERED_FILE_PATH_PARAM, tempFilePath);
            ChecksumDeliveredCommand checksumDeliveredCommand = new ChecksumDeliveredCommand();
            executeCommand(checksumDeliveredCommand, executionContext);
        }
    }

    /**
     * TODO Non trivial change but this too can go up one level to
     * BaseFileTransferConnectionImpl
     */
    public void housekeep(int maxRows, int ageOfFiles) throws ResourceException
    {
        try
        {
            //BaseFileTransferDao baseFileTransferDao = DataAccessUtil.getBaseFileTransferDao();
            baseFileTransferDao.housekeep(clientId, ageOfFiles, maxRows);
        }
        catch (Exception e)
        {
            throw new ResourceException(e);
        }
    }
    
    /**
     * Executes a command with a given ExecutionContext
     * 
     * @param command to execute
     * @param executionContext -
     * @return ExecutionOutput
     * @throws ResourceException -
     */
    @Override
    protected ExecutionOutput executeCommand(TransactionalResourceCommand command, ExecutionContext executionContext)
            throws ResourceException
    {
        command.setExecutionContext(executionContext);
        super.addListenersToCommand(command);
        return executeCommand(command);
    }



    /**
     * Retrieves lightweight handles to the File Chunks that will be needed for
     * reconstitution
     * 
     * @param fileChunkDao
     * 
     * @param fileName
     * @param fileChunkTimeStamp
     * @return List of <code>FileConstituentHandle</code>
     * @throws ResourceException
     */
    private List<FileConstituentHandle> getConstituentHandles(FileChunkDao fileChunkDao, String fileName,
            Long fileChunkTimeStamp) throws ResourceException
    {
        // find references to all the chunks that we need to reconstitute the
        // file
        List<FileConstituentHandle> constituentHandles = fileChunkDao.findChunks(fileName, fileChunkTimeStamp, null,
            null);
        if (constituentHandles.isEmpty())
        {
            throw new ResourceException("No chunks found for file: [" + fileName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return constituentHandles;
    }

    /**
     * Retrieves a single from the remote dir
     * 
     * @param entry
     * @param clientID
     * @param checksum
     * @param chunkSize
     * @param renameOnSuccessExtension
     * @param renameOnSuccess
     * @param moveOnSuccess 
     * @param moveOnSuccessNewPath 
     * @param chunking 
     * @param destructive 
     * @param baseFileTransferDao
     * @return Payload
     * @throws ResourceException
     */
    private Payload sourceFile(ClientListEntry entry, String clientID, boolean renameOnSuccess,
            String renameOnSuccessExtension, boolean moveOnSuccess, String moveOnSuccessNewPath, 
            boolean chunking, int chunkSize, boolean checksum, boolean destructive, 
            BaseFileTransferDao baseFileTransferDao)
            throws ResourceException
    {
        logger.info("sourceFile called with entry: [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.info("move on success = [" +  moveOnSuccess + "] and path = [" + moveOnSuccessNewPath + "].");
        Payload result = null;
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.CLIENT_ID, clientID);
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);
        if (chunking && shouldChunk(entry))
        {
            //FileChunkDao fileChunkDao = DataAccessUtil.getFileChunkDao();

            // chunking specific
            logger.debug("About to call ChunkingRetrieveFileCommand"); //$NON-NLS-1$
            ChunkingRetrieveFileCommand chunkingRetrieveFileCommand = new ChunkingRetrieveFileCommand(
                baseFileTransferDao, clientID, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, fileChunkDao, chunkSize, destructive);

            Object executionResult = executeCommand(chunkingRetrieveFileCommand, executionContext).getResult();
            FileChunkHeader fileChunkHeader = (FileChunkHeader) executionResult;

            result = fileChunkHeaderToPayload(fileChunkHeader);
            // create some special kind of Payload for Chunked Files
            // end chunking specific
        }
        else
        {
            // non chunking specific
            logger.info("About to call RetrieveFileCommand"); //$NON-NLS-1$
            RetrieveFileCommand retrieveFileCommand = new RetrieveFileCommand(baseFileTransferDao, renameOnSuccess,
                renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, destructive);

            ExecutionOutput executionResult = executeCommand(retrieveFileCommand, executionContext);
            BaseFileTransferMappedRecord sftpMappedRecord = (BaseFileTransferMappedRecord) executionResult.getResult();
            if (sftpMappedRecord == null)
            {
                logger.warn("No file was picked up."); //$NON-NLS-1$
                return result;
            }
            result = BaseFileTransferMappedRecordTransformer.mappedRecordToPayload(sftpMappedRecord);
            // end non chunking specific
        }
        String sourcePath = entry.getUri().getPath();
        executionContext.put(ExecutionContext.PAYLOAD, result);
        logger.debug("About to call ChecksumValidatorCommand"); //$NON-NLS-1$

        if (checksum)
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
     * Determines if this file should be chunked or not
     * 
     * @param entry
     * @return true if the file should be chunked
     */
    private boolean shouldChunk(ClientListEntry entry)
    {
        boolean result = false;
        if (chunkableThreshold != null)
        {
            result = entry.getSize() > chunkableThreshold;
        }
        return result;
    }



    /**
     * Executes the supplied command
     * 
     * @param command
     * @return ExecutionOutput
     * @throws ResourceException
     */
    protected ExecutionOutput executeCommand(TransactionalResourceCommand command) throws ResourceException
    {
        super.addListenersToCommand(command);
        return (this.getManagedConnection().executeCommand(command));
    }
}
