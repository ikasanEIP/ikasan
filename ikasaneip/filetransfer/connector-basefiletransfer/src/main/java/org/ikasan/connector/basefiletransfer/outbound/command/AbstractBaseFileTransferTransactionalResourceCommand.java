/*
 * $Id:$
 * $URL:$
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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.connector.base.command.AbstractTransactionalResourceCommand;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.TransactionalResource;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandGetException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandMkdirException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPutException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPwdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandRenameException;
import org.ikasan.connector.basefiletransfer.net.ClientException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.listener.TransactionCommitEvent;
import org.ikasan.connector.listener.TransactionCommitException;
import org.ikasan.connector.listener.TransactionCommitFailureListener;
import org.ikasan.connector.listener.TransactionCommitFailureObserverable;

/**
 * This is the base class for all FileTransfer oriented
 * TranasactionalResourceCommands
 * 
 * This exists simply to provide access to all common low level File Transfer
 * operations
 * 
 * @author Ikasan Development Team 
 */
public abstract class AbstractBaseFileTransferTransactionalResourceCommand extends AbstractTransactionalResourceCommand implements TransactionCommitFailureObserverable
{
    /** The logger instance. */
    private static Logger logger = LoggerFactory.getLogger(AbstractBaseFileTransferTransactionalResourceCommand.class);

    /** EOL character */
    protected final static String EOL = System.getProperty("line.separator");
    
    /** current directory */
    protected final static String CURRENT_DIRECTORY = "."; //$NON-NLS-1$

    /** The file separator */
    protected String fileSeparator = System.getProperty("file.separator"); //$NON-NLS-1$

    protected List<TransactionCommitFailureListener> listeners = new ArrayList<TransactionCommitFailureListener>();

    /** Constructor */
    public AbstractBaseFileTransferTransactionalResourceCommand()
    {
        super();
    }

    /**
     *  (non-Javadoc)
     * @see org.ikasan.connector.base.command.AbstractTransactionalResourceCommand#doExecute(org.ikasan.connector.base.command.TransactionalResource)
     */
    @Override
    public ExecutionOutput doExecute(TransactionalResource resource) throws ResourceException
    {
        ExecutionOutput result = null;
        if(logger.isDebugEnabled())
    	{
        	logger.debug("execute called on " + this.getClass().getName() //$NON-NLS-1$
                    + " with resource:" + resource.getClass().getName()); //$NON-NLS-1$
    	}

        this.transactionalResource = resource;

        result = performExecute();
        return result;
    }

    /**
     * execute method for subclasses to implement
     * 
     * @return ExecutionOutput
     * 
     * @throws ResourceException -
     */
    protected abstract ExecutionOutput performExecute() throws ResourceException;

    /**
     * Returns the working directory name
     * 
     * @return working directory name
     * @throws ResourceException -
     */
    protected String printWorkingDirectoryName() throws ResourceException
    {
        try
        {
            return getClient().pwd();
        }
        catch (ClientCommandPwdException e)
        {
            logger.warn("Underlying File Transfer operation failed [PWD]!"); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Execute a change directory on the server to directory passed in
     * 
     * @param path Path to change directory to
     * @throws ResourceException -
     */
    protected void changeDirectory(String path) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("cd to dir [" + path + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        try
        {
            getClient().cd(path);
        }
        catch (ClientCommandCdException e)
        {
            logger.warn("Underlying File Transfer operation failed [CD]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Return a list of ClientListEntry objects
     * 
     * @param directory The directory we are listing on
     * @return A list of ClientListEntry
     * @throws ResourceException -
     */
    protected List<ClientListEntry> listDirectory(String directory) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Listing directory [" + directory + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        try
        {
            return getClient().ls(directory);
        }
        catch (ClientCommandLsException e)
        {
            logger.warn("Underlying File Transfer operation failed [LS]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        catch (URISyntaxException e)
        {
            logger.warn("An URI Syntax exception occurred!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Method used to log a list of <code>ClientListEntry</code> entries.
     * The level of detail logged depends on the runtime logging level.
     * 
     * @param list - files in directory
     * @param listDescription -
     */
    protected void logFileList(List<ClientListEntry> list, String listDescription)
    {
        StringBuilder sb = new StringBuilder(256);
        if (listDescription != null && listDescription.length() > 0)
        {
            sb.append("File list ["); //$NON-NLS-1$
            sb.append(listDescription);
            sb.append("]:" + EOL); //$NON-NLS-1$
        }
        else
        {
            sb.append("File list:" + EOL); //$NON-NLS-1$
        }

        if (list != null)
        {
            // Get single line output for info level
            if (logger.isInfoEnabled())
            {
                for (ClientListEntry l : list)
                {
                    if (l.getPermissionsString() != null)
                    {
                        sb.append(l.getPermissionsString());
                    }
                    sb.append(l.getLongFilename());
                    sb.append(EOL);
                }
            }
            // Otherwise set full entry details when the log level is debug
            else if (logger.isDebugEnabled())
            {
                for (ClientListEntry l : list)
                {
                    sb.append(l.toString());
                    sb.append(EOL);
                }
            }
        }
        else
        {
            sb.append("[was empty]"); //$NON-NLS-1$
        }
        
        logger.debug(sb.toString());
    }

    /**
     * Get the Mapped record that is a representation of the file
     * 
     * @param entry A wrapper that holds the information we need to get the file
     * @return The BaseFileTransferMappedRecord representing the file
     * @throws ResourceException failure to perform   get
     */
    protected BaseFileTransferMappedRecord getFile(ClientListEntry entry) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
//            logger.debug("getFile called with: [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            logger.debug("Getting file [" + entry.getUri().getPath() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        try
        {
            return getClient().get(entry);
        }
        catch (ClientCommandGetException e)
        {
            logger.warn("Underlying File Transfer operation failed [GET]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Get the SFYPMapped record that is a representation of the file
     * 
     * @param filePath The path to the file
     * @return The BaseFileTransferMappedRecord representing the file
     * @throws ResourceException - failure to perform   get
     */
    protected BaseFileTransferMappedRecord getFile(String filePath) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("getFile called with: [" + filePath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        try
        {
            return getClient().get(filePath);
        }
        catch (ClientCommandGetException e)
        {
            logger.warn("Underlying File Transfer operation failed [GET]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Rename the file from oldPath to newPath
     * 
     * @param oldPath Original Path
     * @param newPath New Path
     * @throws ResourceException when renaming a file fails
     */
    protected void renameFile(String oldPath, String newPath) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Renaming file from [" + oldPath + "] to [" //$NON-NLS-1$ //$NON-NLS-2$ 
                + newPath + "]"); //$NON-NLS-1$
        }
        
        try
        {
            getClient().ensureConnection();
            this.getClient().rename(oldPath, newPath);
        }
        catch (ClientCommandRenameException e)
        {
            logger.warn("Failed to rename file [" + oldPath //$NON-NLS-1$
                    + "] to [" + newPath //$NON-NLS-1$ 
                    + "]. Exception occured [" + e.getMessage() + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
            throw new ResourceException(e);
        }
    }

    /**
     * Put the file given
     * 
     * @param file - the file
     * @throws ResourceException when sending a file
     */
    protected void putFile(BaseFileTransferMappedRecord file) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Putting file [" + file.getName() + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        try
        {
            this.getClient().put(file.getName(), file.getContent());
            logger.debug("Put successful."); //$NON-NLS-1$
        }
        catch (ClientCommandPutException e)
        {
            logger.warn("Underlying File Transfer operation failed [PUT]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Deletes a remote file
     * 
     * @param filename - for file to be deleted
     * @throws ResourceException when removing a file fails
     */
    protected void deleteFile(String filename) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Deleting file [" + filename + "]"); //$NON-NLS-1$//$NON-NLS-2$
        }
        
        try
        {
            getClient().ensureConnection();
            getClient().deleteRemoteFile(filename);
        }
        catch (ClientException e)
        {
            logger.warn("Underlying File Transfer operation failed [RM]!", e); //$NON-NLS-1$
            throw new ResourceException("Exception thrown when trying to delete file [" + filename + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Deletes a remote directory
     * 
     * @param directoryPath - directory to be removed
     * @param recurse - flag to delete directory recursively or not.
     * @throws ResourceException when deleting a file fails
     */
    protected void deleteDir(String directoryPath, boolean recurse) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Deleting directory [" + directoryPath + "]"); //$NON-NLS-1$//$NON-NLS-2$
        }

        try
        {
            getClient().deleteRemoteDirectory(directoryPath, recurse);
        }
        catch (ClientException e)
        {
            logger.warn("Underlying File Transfer operation failed [RMDIR]!", e); //$NON-NLS-1$
            throw new ResourceException("Exception thrown when trying to delete directory [" + directoryPath + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (ClientCommandLsException e)
        {
            throw new ResourceException(e);
        }
    }

    /**
     * Get the content from the file as an InputStream
     * 
     * @param entry - the file
     * @return content as an InputStream
     * @throws ResourceException when downloding a file gails
     */
    protected InputStream getContentAsStream(ClientListEntry entry) throws ResourceException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Getting content from [" + entry.getUri().getPath() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        try
        {
            return getClient().getContentAsStream(entry);
        }
        catch (ClientCommandGetException e)
        {
            logger.warn("Underlying File Transfer operation failed [GET]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Perform a PUT file operation suing an OutputStream
     * 
     * @param fileName - the file name
     * @param inputStream - file content as an input stream
     * @throws ResourceException when sending a file fails
     */
    protected void putWithOutputStream(String fileName, InputStream inputStream) 
        throws ResourceException
    {
        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("putting fileName [" + fileName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            getClient().putWithOutputStream(fileName, inputStream);
        }
        catch (ClientCommandPutException e)
        {
            logger.warn("Underlying File Transfer operation failed [PUT]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        catch (ClientCommandLsException e)
        {
            logger.warn("Underlying File Transfer operation failed [PUT]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        catch (ClientCommandMkdirException e)
        {
            logger.warn("Underlying File Transfer operation failed [PUT]!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
    }

    /**
     * Find a file in the current directory if it exists
     * 
     * @param filePath - where to look for file
     * @return file represented as a ClientListEntry
     * @throws ResourceException when finding the file fails
     */
    protected ClientListEntry findFile(String filePath) throws ResourceException
    {
        File file = new File(filePath);
        String fileRelativeDir = "."; 
        
        if (file.getParent() != null)
        {
            fileRelativeDir = file.getParent();
        }

        List<ClientListEntry> listDirectory = listDirectory(fileRelativeDir);
        ClientListEntry deliveredEntry = null;

        if (listDirectory != null)
        {
            for (ClientListEntry entry : listDirectory)
            {
    
                //if (entry.getUri().getPath().endsWith(file.getName()))
            	if (entry.getName().equals(file.getName()))
                {
                    deliveredEntry = entry;
                    break;
                }
            }
        }
        return deliveredEntry;
    }

    /**
     * Return the FileTransferClient
     * 
     * @return FileTransferClient
     */
    protected FileTransferClient getClient()
    {
        return ((FileTransferClient) transactionalResource);
    }

    /* (non-Javadoc)
     * @see org.ikasan.client.listener.TransactionCommitFailureObserverable#addListener(org.ikasan.client.listener.TransactionCommitFailureListener)
     */
    @Override
    public void addListener(TransactionCommitFailureListener listener)
    {
        if(!listeners.contains(listener))
        {
            logger.info("Adding listener: " + listener + " this == " + this);
            listeners.add(listener);
        }
    }

    protected void notifyListeners(TransactionCommitException exception)
    {
        logger.info("Attempting to notify listeners! Number of listeners: " + listeners.size() + " this == " + this);
        TransactionCommitEvent event = new TransactionCommitEvent(exception);
        for(TransactionCommitFailureListener listener: listeners)
        {
            logger.info("Notifying listener that commit failure occured: " + event.getException().getMessage());
            listener.commitFailureOccurred(event);
        }
    }

}
