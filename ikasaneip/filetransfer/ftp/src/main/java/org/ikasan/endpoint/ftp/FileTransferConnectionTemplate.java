/*
 * $Id$
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.endpoint.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.client.ConnectionCallback;
import org.ikasan.connector.BaseFileTransferConnection;
import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalImpl;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.ftp.outbound.FTPConnectionRequestInfo;
import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.connector.listener.TransactionCommitFailureListener;
import org.ikasan.connector.listener.TransactionCommitFailureObserverable;
import org.ikasan.connector.ftp.outbound.FTPManagedConnection;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.filetransfer.Payload;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionSpec;
import javax.resource.spi.ConnectionRequestInfo;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test the FileTRansferConnectionTempalte class
 *
 * @author Ikasan Development Team
 */
public class FileTransferConnectionTemplate implements TransactionCommitFailureObserverable
{

    private static Logger logger = LoggerFactory.getLogger(FileTransferConnectionTemplate.class);

    protected List<TransactionCommitFailureListener> listeners = new ArrayList<TransactionCommitFailureListener>();

    /** Journal for logging activity of this connector */
    private TransactionJournal transactionJournal = null;

    private FTPManagedConnection ftpManagedConnection;

    private TransactionalResourceCommandDAO transactionalResourceCommandDAO;
    private FileChunkDao fileChunkDao;
    private BaseFileTransferDao baseFileTransferDao;

    private JtaTransactionManager transactionManager;
    /**
     * Constructor
     *
     * @param connectionSpec - THe connection spec
     */
    public FileTransferConnectionTemplate(ConnectionSpec connectionSpec,TransactionalResourceCommandDAO transactionalResourceCommandDAO,
                                          FileChunkDao fileChunkDao, BaseFileTransferDao baseFileTransferDao,JtaTransactionManager transactionManager)  throws ResourceException
    {
        this.fileChunkDao = fileChunkDao;
        this.transactionalResourceCommandDAO = transactionalResourceCommandDAO;
        this.baseFileTransferDao = baseFileTransferDao;
        this.transactionManager = transactionManager;

        ftpManagedConnection = new FTPManagedConnection(connectionSpecToCRI(connectionSpec));
        ftpManagedConnection.setTransactionJournal(getTransactionJournal(transactionalResourceCommandDAO,fileChunkDao));
        // Open a session on the managed connection
        ftpManagedConnection.openSession();
        // Return the managed connection (with an open session)

    }

    /**
     * Test Delivering a payload
     *
     * @param payload - The payload to deliver
     * @param outputDir - The directory to place the file in
     * @param outputTargets - The Map of targets to deliver the file to
     * @param overwrite - Overwrite existing files flag
     * @param renameExtension - The extension for the temp file rename
     * @param checksumDelivered - Flag for whether we perform checksumming
     * @param unzip - Flag for whether we unzip the delivered file
     * @param cleanup - Cleanup txn journal flag
     * @throws ResourceException - Exception if JCA connector fails
     * @deprecated - use deliverInputStream
     */
    public void deliverPayload(final Payload payload, final String outputDir, final Map<String, String> outputTargets, final boolean overwrite,
                               final String renameExtension, final boolean checksumDelivered, final boolean unzip, final boolean cleanup) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                addListenersToConnection((BaseFileTransferConnection) connection);

                ((BaseFileTransferConnection) connection).deliverPayload(payload, outputDir, outputTargets, overwrite, renameExtension, checksumDelivered,
                        unzip, cleanup);
                return null;
            }
        });
    }

    /**
     * Delivering an InputStream
     *
     * @param inputStream - The 'file'
     * @param fileName - The name of the file
     * @param outputDir - The directory to place the file in
     * @param overwrite - Overwrite existing files flag
     * @param renameExtension - The extension for the temp file rename
     * @param checksumDelivered - Flag for whether we perform checksumming
     * @param unzip - Flag for whether we unzip the delivered file
     * @param createParentDirectory -  
     * @param tempFileName -
     * @throws ResourceException - Exception if JCA connector fails
     */
    public void deliverInputStream(final InputStream inputStream, final String fileName, final String outputDir, final boolean overwrite,
                                   final String renameExtension, final boolean checksumDelivered, final boolean unzip, final boolean createParentDirectory,
                                   final String tempFileName) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                addListenersToConnection((BaseFileTransferConnection) connection);
                ((BaseFileTransferConnection) connection).deliverInputStream(inputStream, fileName, outputDir,  overwrite, renameExtension, checksumDelivered,unzip, createParentDirectory, tempFileName);
                return null;
            }
        });
    }

    /**
     * Test the getDiscoveredFile
     *
     * @param sourceDir - The directory to get the file from
     * @param filenamePattern - The pattern to search on
     * @param renameOnSuccess - Whether we rename a file on successful delivery
     * @param renameOnSuccessExtension - The extension to rename to
     * @param moveOnSuccess - Whether we move the file on successful delivery
     * @param moveOnSuccessNewPath - Where we move the file to
     * @param chunking - Whether we are chunking enabled
     * @param chunkSize - The size of the chunks
     * @param checksum - Whether we checksum the pickup 
     * @param minAge - The minimum age the file has to be in order to be picked up 
     * @param destructive - Whether we pick up destructively
     * @param filterDuplicates - Whether we filter duplicates
     * @param filterOnFilename - Whether we filter duplicates based on file name
     * @param filterOnLastModifedDate - Whether we filter duplicates based on file name
     * @param chronological - Whether we pickup files in age order
     * @param isRecursive - Whether we pickup files and all subdirectories
     *
     * @return The discovered file as a Payload
     * @throws ResourceException - Exception if the JCA connector fails
     */
    public Payload getDiscoveredFile(final String sourceDir, final String filenamePattern, final boolean renameOnSuccess,
                                     final String renameOnSuccessExtension, final boolean moveOnSuccess, final String moveOnSuccessNewPath, final boolean chunking, final int chunkSize,
                                     final boolean checksum, final long minAge, final boolean destructive,
                                     final boolean filterDuplicates, final boolean filterOnFilename, final boolean filterOnLastModifedDate, final boolean chronological, final boolean isRecursive) throws ResourceException
    {
        return (Payload) execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                addListenersToConnection((BaseFileTransferConnection) connection);
                Payload discoveredFile = ((BaseFileTransferConnection) connection).getDiscoveredFile(sourceDir,
                        filenamePattern, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath,
                        chunking, chunkSize, checksum, minAge, destructive, filterDuplicates, filterOnFilename,
                        filterOnLastModifedDate, chronological,isRecursive);
                return discoveredFile;
            }
        });
    }

    /**
     * Housekeep the FileFilter table
     *
     * @param maxRows Max rows the housekeeper will deal with
     * @param ageOfFiles How old the files have to be in days to 
     * be considered for housekeeping
     * @throws ResourceException - Exception if JCA connector fails
     */
    public void housekeep(final int maxRows, final int ageOfFiles) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                BaseFileTransferConnection baseFileTransferConnection = (BaseFileTransferConnection)connection;
                baseFileTransferConnection.housekeep(maxRows, ageOfFiles);
                return null;
            }
        });
    }

    /**
     * Helper method to add the listeners to the connection.
     *
     * @param connection
     */
    protected void addListenersToConnection(BaseFileTransferConnection connection)
    {
        for(TransactionCommitFailureListener listener: this.listeners)
        {
            connection.addListener(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.client.listener.TransactionCommitFailureObserverable#addListener(org.ikasan.client.listener.TransactionCommitFailureListener)
     */
    @Override
    public void addListener(TransactionCommitFailureListener listener)
    {
        this.listeners.add(listener);
    }


    /**
     * Execute the action specified by the given action object with a
     * Connection.
     *
     * @param action callback object that exposes the Connection
     * @return the result object from working with the Connection
     * @throws ResourceException if there is any problem
     */
    public Object execute(ConnectionCallback action) throws ResourceException
    {
        Connection connection = null;
        try
        {
            connection = (Connection) ftpManagedConnection.getConnection(this.fileChunkDao,baseFileTransferDao);
            try
            {
                if (connection instanceof BaseFileTransferConnection)
                {
                    BaseFileTransferConnection sc = (BaseFileTransferConnection) connection;
                    TransactionalCommandConnection smc = sc.getManagedConnection();
                    transactionManager.getTransactionManager().getTransaction().enlistResource(smc);
                }

                Object toreturn =  action.doInConnection(connection);

                return toreturn;
            }
            catch (SystemException e)
            {
                e.printStackTrace();
                return null;
            }
            catch (RollbackException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Closes the connection, suppressing any exceptions
     *
     * @param connection - possibly null, not necessarily open
     */
    public static void closeConnection(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                logger.debug("Attempting to close EIS Connection");
                connection.close();
            }
            catch (ResourceException ex)
            {
                logger.warn("Could not close EIS Connection", ex);
            }
            catch (Throwable ex)
            {
                // We don't trust the EIS provider: It might throw
                // RuntimeException or Error.
                logger.debug("Unexpected exception on closing EIS Connection",
                        ex);
            }
        }
    }

    /**
     * Lazily instantiates the TransactionJournal
     *
     * @return TransactionJournal
     */
    protected TransactionJournal getTransactionJournal(TransactionalResourceCommandDAO transactionalResourceCommandDAO,
                                                       FileChunkDao fileChunkDao)
    {
        if (transactionJournal == null)
        {

            Map<String, Object> beanFactory = new HashMap<String, Object>();
            beanFactory.put("fileChunkDao", fileChunkDao);
            transactionJournal = new TransactionJournalImpl(transactionalResourceCommandDAO, "SFTPCLient",
                    beanFactory);
        }
        return transactionJournal;
    }

    /**
     * Converts the connection spec into a connection request info
     *
     * @param spec The client connection details
     * @return The connection request info
     */
    private FTPConnectionRequestInfo connectionSpecToCRI(ConnectionSpec spec)
    {
        logger.debug("Converting Connection Spec to CRI"); //$NON-NLS-1$
        FTPConnectionSpec ftpConnectionSpec = (FTPConnectionSpec)spec;
        FTPConnectionRequestInfo fcri = new FTPConnectionRequestInfo();
        if (ftpConnectionSpec != null)
        {
            fcri.setActive(ftpConnectionSpec.getActive());
            fcri.setCleanupJournalOnComplete(ftpConnectionSpec.getCleanupJournalOnComplete());
            fcri.setClientID(ftpConnectionSpec.getClientID());
            fcri.setRemoteHostname(ftpConnectionSpec.getRemoteHostname());
            fcri.setMaxRetryAttempts(ftpConnectionSpec.getMaxRetryAttempts());
            fcri.setPassword(ftpConnectionSpec.getPassword());
            fcri.setRemotePort(ftpConnectionSpec.getRemotePort());
            fcri.setUsername(ftpConnectionSpec.getUsername());
            fcri.setPollTime(ftpConnectionSpec.getPollTime());
            fcri.setSystemKey(ftpConnectionSpec.getSystemKey());
            fcri.setConnectionTimeout(ftpConnectionSpec.getConnectionTimeout());
            fcri.setDataTimeout(ftpConnectionSpec.getDataTimeout());
            fcri.setSocketTimeout(ftpConnectionSpec.getSocketTimeout());

            fcri.setIsFTPS(ftpConnectionSpec.getIsFTPS());
            fcri.setFtpsPort(ftpConnectionSpec.getFtpsPort());
            fcri.setFtpsProtocol(ftpConnectionSpec.getFtpsProtocol());
            fcri.setFtpsIsImplicit(ftpConnectionSpec.getFtpsIsImplicit());
            fcri.setFtpsKeyStoreFilePath(ftpConnectionSpec.getFtpsKeyStoreFilePath());
            fcri.setFtpsKeyStoreFilePassword(ftpConnectionSpec.getFtpsKeyStoreFilePassword());
        }
        return fcri;
    }
}
