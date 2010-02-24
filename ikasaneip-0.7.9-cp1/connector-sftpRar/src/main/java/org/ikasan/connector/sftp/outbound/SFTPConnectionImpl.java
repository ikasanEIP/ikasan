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
package org.ikasan.connector.sftp.outbound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnection;

import org.apache.log4j.Logger;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.ServiceLocator;
import org.ikasan.common.component.Format;
import org.ikasan.common.component.Spec;
import org.ikasan.common.util.checksum.ChecksumSupplier;
import org.ikasan.common.util.checksum.Md5ChecksumSupplier;

import org.ikasan.connector.ConnectorException;
import org.ikasan.connector.ResourceLoader;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.basefiletransfer.DataAccessUtil;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.OlderFirstClientListEntryComparator;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
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
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
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
public class SFTPConnectionImpl extends BaseFileTransferConnectionImpl implements BaseFileTransferConnection
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(SFTPConnectionImpl.class);
    
 
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

    /**
     * Constructor which takes ManagedConnection as a parameter
     * 
     * @param mc The ManagedConnection
     */
    public SFTPConnectionImpl(ManagedConnection mc)
    {
        super(mc);
        this.managedConnection = (SFTPManagedConnection) mc;
        this.clientId = managedConnection.getClientID();
    }

    /**
     * getManagedConnection is called by the SFTPManagedConnection when the
     * application server wants to reassociate a virtual connection with a new
     * manager.
     */
    public TransactionalCommandConnection getManagedConnection()
    {
        logger.debug("Called getManagedConnection()"); //$NON-NLS-1$
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
        // Remove and nullify this virtual connection
        this.managedConnection.removeConnection(this);
        // This can trigger managed connection cleanup()
        this.managedConnection.sendClosedEvent(this);
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

    // //////////////////////////////////////////////////////////
    // Client virtual connection methods
    // //////////////////////////////////////////////////////////
    /**
     * Validate - If there is validation to be done in the future then pass that
     * validation to the manager.
     */
    @Override
    public void validate() //throws ResourceException
    {
        logger.debug("Called validate()..."); //$NON-NLS-1$
        // this.managedConnection.validate();
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
            long minAge, boolean destructive, boolean filterDuplicates, boolean filterOnFilename, boolean  filterOnLastModifiedDate, boolean chronological) 
            throws ResourceException
    {
        Payload result = null;

        ExecutionContext executionContext = new ExecutionContext();
        // Pass through the client Id
        logger.debug("Got clientId [" + clientId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.debug("Source = [" + sourceDir+ "] moveOnSuccess = [" + moveOnSuccess + "] and archive dir = [" + moveOnSuccessNewPath + "].");
        executionContext.put(ExecutionContext.CLIENT_ID, clientId);

        BaseFileTransferDao baseFileTransferDao = DataAccessUtil.getBaseFileTransferDao();

        FileDiscoveryCommand fileDiscoveryCommand = new FileDiscoveryCommand(sourceDir, filenamePattern,
            baseFileTransferDao, minAge, filterDuplicates, filterOnFilename, filterOnLastModifiedDate);
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
            FileChunkDao fileChunkDao = DataAccessUtil.getFileChunkDao();

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

                deliveryCommand = new DeliverFileCommand(outputTarget, renameExtension, overwrite);
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
                deliveryCommand = new DeliverFileCommand(outputTarget, renameExtension, overwrite);

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
            BaseFileTransferDao baseFileTransferDao = DataAccessUtil.getBaseFileTransferDao();
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
        return executeCommand(command);
    }

    /**
     * Determines if we need to handle this as a chunk reference
     * 
     * @param payload
     * @return true if the payload is simply a reference to a set of chunks
     */
    private boolean isChunkReference(Payload payload)
    {
        boolean result = false;
        String format = payload.getFormat();
        if (format != null && Format.REFERENCE.toString().equals(payload.getFormat()))
        {
            result = true;
        }
        return result;
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
            FileChunkDao fileChunkDao = DataAccessUtil.getFileChunkDao();

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
        result.setSrcSystem(clientId);
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
     * Method used to map an <code>FileChunkHeade</code> object to a
     * <code>Payload</code> object.
     * 
     * @param header The record as returned from the SFTPClient
     * @return A payload constructed from the record.
     */
    public static Payload fileChunkHeaderToPayload(FileChunkHeader header)
    {
        // TODO global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload payload = serviceLocator.getPayloadFactory().newPayload(header.getFileName(), Spec.TEXT_XML,
            MetaDataInterface.UNDEFINED, header.toXml().getBytes());
        payload.setFormat(Format.REFERENCE.toString());
        String componentGroupName = ResourceLoader.getInstance().getProperty("component.group.name");
        payload.setSrcSystem(componentGroupName);
        // need to do checksumming
        payload.setChecksum(header.getInternalMd5Hash());
        payload.setChecksumAlg(Md5ChecksumSupplier.MD5);
        return payload;
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
        return (this.getManagedConnection().executeCommand(command));
    }


}
