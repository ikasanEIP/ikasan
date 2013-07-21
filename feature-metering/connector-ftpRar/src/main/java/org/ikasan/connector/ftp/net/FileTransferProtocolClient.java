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
package org.ikasan.connector.ftp.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.resource.ResourceException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferUtils;
import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandGetException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandMkdirException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPutException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPwdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandRenameException;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientException;
import org.ikasan.connector.basefiletransfer.net.ClientInitialisationException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.ClientPolarisedFilter;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;

/**
 * <p>
 * This class provides the basic functionality of an FTP client based on
 * Apache's commons-net library. Some of the basic functionality is wrapped to
 * provide <code>FileTransferProtocolClient</code> specific exceptions, or to
 * provide the functionality required by a framework component.
 * </p>
 * 
 * TODO This has a good deal of similar code to SFTP client, might be able to
 * get some common code out of that
 * 
 * @author Ikasan Development Team
 */
public class FileTransferProtocolClient implements FileTransferClient
{
    /** Initialising the logger */
    private static Logger logger = Logger.getLogger(FileTransferProtocolClient.class);

    /** Whether we should transmit in Active mode (passive by default) */
    private boolean active;

    /** Remote Hostname */
    private String remoteHostname;

    /** Local Hostname */
    private String localHostname = null;

    /** Maximum no of times to retry connection */
    private Integer maxRetryAttempts;

    /** Password */
    private String password;

    /** Remote Port Number */
    private Integer remotePort;

    /** The default maximum local port number in Hex */
    private static final Integer DEFAULT_MAXIMUM_LOCAL_PORT = 0xFFFF;

    /** User name */
    private String username;

    /**
     * FTP Client systemKey, if set, expected to be in the
     * FTPClientConfig.SYST_* range
     */
    private String systemKey;

    /** Timeout in milliseconds to use when opening a socket. Defaults to unlimited (0) */
    private Integer connectionTimeout = 0;

    /** Timeout in milliseconds to use when reading from the data connection. Defaults to unlimited (0*/
    private Integer dataTimeout = 0;

    /** Timeout in milliseconds of a currently open connection. Defaults to unlimited (0) */
    private Integer socketTimeout = 0;

    /** Third party library that implements much of FTP for us */
    private FTPClient ftpClient;

    /**
     * Constructor
     * 
     * @param active Whether we are active or passive mode
     * @param remoteHostname The remote hostname
     * @param localHost the local host
     * @param maxRetryAttempts The maximum amount of retries
     * @param password The password to connect with
     * @param remotePort The remote port to connect to
     * @param username The username
     * @param systemKey The system key
     * @param connectionTimeout connection timeout
     * @param soTimeout Socket timeout in ms after getting a connection
     * @param dataTimeout Data connection timeout in ms after opening a socket
     */
    public FileTransferProtocolClient(boolean active, String remoteHostname, String localHost, Integer maxRetryAttempts, String password, Integer remotePort,
            String username, String systemKey, Integer connectionTimeout, Integer soTimeout, Integer dataTimeout)
    {
        super();
        this.active = active;
        this.remoteHostname = remoteHostname;
        this.maxRetryAttempts = maxRetryAttempts;
        this.password = password;
        this.remotePort = remotePort;
        if(localHost != null && localHost.length() > 0)
        {
            this.localHostname = localHost;
        }
        if(connectionTimeout != null)
        {
            this.connectionTimeout = connectionTimeout;
        }
        if(soTimeout != null)
        {
            this.socketTimeout = soTimeout;
        }
        if (dataTimeout != null)
        {
            this.dataTimeout = dataTimeout;
        }
        this.username = username;
        this.systemKey = systemKey;
    }

    /**
     * All constructor arguments are essential, so use this method to validate
     * them before attempting connection
     * 
     * @throws ClientInitialisationException if any of the required parameters
     *             are invalid
     */
    public void validateConstructorArgs() throws ClientInitialisationException
    {
        // If all values seem OK, log the info and return
        if(this.remoteHostname != null && this.password != null && this.username != null)
        {
            return;
        }
        // Otherwise, trace through, log the erroneous parameters and throw an
        // exception
        StringBuilder sb = new StringBuilder("The following arguments are erroneous or missing:\n"); //$NON-NLS-1$
        if(this.remoteHostname == null || this.remoteHostname.length() == 0)
        {
            sb.append("Invalid hostname! [");
            sb.append(this.remoteHostname);
            sb.append("]");
        }
        if(this.password == null || this.password.length() == 0)
        {
            sb.append("Invalid password! ["); //$NON-NLS-1$
            sb.append(this.password);
            sb.append("]\n"); //$NON-NLS-1$
        }
        if(this.username == null || this.username.length() == 0)
        {
            sb.append("Invalid username! [");
            sb.append(this.username);
            sb.append("]\n");
        }
        throw new ClientInitialisationException(sb.toString());
    }

    /**
     * Connect to the FTP server
     * 
     * @throws ClientConnectionException Exception thrown when we can't connect
     */
    public void connect() throws ClientConnectionException
    {
        echoConfig(Level.DEBUG);
        // Checking the connection state,
        String msg = new String("Checking connection status... "); //$NON-NLS-1$
        logger.debug(msg + "[" + (isConnected() ? "connected" : "disconnected") + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        // and acting accordingly.
        if(isConnected())
        {
            logger.debug("Already connected!"); //$NON-NLS-1$
            return;
        }
        // try to gain a connection, up to the max no of retries - if one exists
        int maxAttemptConnection = 1;
        if(maxRetryAttempts != null)
        {
            maxAttemptConnection = maxRetryAttempts.intValue();
        }
        logger.debug("about to attempt up to [" + maxAttemptConnection //$NON-NLS-1$
                + "] times to establish a connection"); //$NON-NLS-1$
        ClientConnectionException connectionException = null;
        for (int retryCount = 0; retryCount < maxAttemptConnection; retryCount++)
        {
            try
            {
                doConnect();
                if(isConnected())
                {
                    logger.debug("Connection established on attempt [" //$NON-NLS-1$
                            + (retryCount + 1) + "]"); //$NON-NLS-1$
                    break;
                }
                // Default else
                logger.warn("Attempt [" + (retryCount + 1) //$NON-NLS-1$
                        + "] failed to connect, without exception!"); //$NON-NLS-1$
            }
            catch (ClientConnectionException e)
            {
                connectionException = e;
                logger.warn("Attempt [" + (retryCount + 1) + "] failed to connect due to : ", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        // if we are still not connected after retrying, we need to throw the
        // exception from the last failure
        if(!isConnected())
        {
            if(connectionException != null)
            {
                logger.error("Failed to connect after ]" + maxAttemptConnection //$NON-NLS-1$
                        + "] retries."); //$NON-NLS-1$
                throw connectionException;
            }
            // Default else
            logger.error("Failed to connect after [" + maxAttemptConnection //$NON-NLS-1$
                    + "] retries, but ClientConnectionException was not thrown!!"); //$NON-NLS-1$
        }
        logger.debug("Connected!"); //$NON-NLS-1$
    }

    /**
     * login to the FTP server
     * 
     * @throws ClientConnectionException Exception thrown when we can't connect
     */
    public void login() throws ClientConnectionException
    {
        echoConfig(Level.DEBUG);
        // Checking the connection state,
        String msg = new String("Checking connection status... "); //$NON-NLS-1$
        logger.debug(msg + "[" + (isConnected() ? "connected" : "disconnected") + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        if(isConnected())
        {
            try
            {
                if(ftpClient.login(username, password))
                {
                    /*
                     * Event though the FTP spec says that we should default to
                     * ASCII mode, as we do not yet support mode changes, we
                     * will default into BINARY mode, as that guarantees that
                     * the file will be transfered explicitly and not have any
                     * UNIX <-> Windows character substitutions made.
                     */
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    logger.debug("Successfully logged into ftp server."); //$NON-NLS-1$
                }
                else
                {
                    throw new ClientConnectionException("Login was refused."); //$NON-NLS-1$
                }
            }
            catch (IOException e)
            {
                throw new ClientConnectionException("IOException caught trying to login."); //$NON-NLS-1$
            }
        }
        else
        {
            throw new ClientConnectionException("Tried to login with disconnected client!"); //$NON-NLS-1$
        }
    }

    /**
     * Method that handles the creation and connection for FTP.
     * 
     * @throws ClientConnectionException if connection attempt fails
     */
    private void doConnect() throws ClientConnectionException
    {
        this.ftpClient = new FTPClient();
        String msg = new String("Attempting connection to [" + remoteHostname + "] using ["); //$NON-NLS-1$ //$NON-NLS-2$
        if(active)
        {
            msg = msg + "active";
        }
        else
        {
            msg = msg + "passive";
        }
        msg = msg + "] mode.";
        logger.debug(msg);
        try
        {
            if(!active)
            {
                ftpClient.enterLocalPassiveMode();
            }
            /*
             * Summer (Nov 26th 2008): Rather than relying on the FTP client to
             * figure out the system it is connecting to (and hence what parsers
             * it should use) we pass this configuration to the client and force
             * it to use it.
             */
            if((systemKey != null) && (!"".equals(systemKey)))
            {
                ftpClient.configure(new FTPClientConfig(systemKey));
            }
            // leave local port unspecified
            int localPort = 0;
            this.ftpClient.setDefaultTimeout(this.connectionTimeout);
            // Keep trying to connect, until successful
            for (int i = 0; i < DEFAULT_MAXIMUM_LOCAL_PORT; i++)
            {
                try
                {
                    logger.debug("Connecting to remote host [" + this.remoteHostname + ":" + this.remotePort + "] from local host [" + this.localHostname + ":"
                            + localPort + "].");
                    ftpClient.connect(InetAddress.getByName(this.remoteHostname), this.remotePort, InetAddress.getByName(this.localHostname), localPort);
                    this.ftpClient.setSoTimeout(this.socketTimeout);
                    this.ftpClient.setDataTimeout(this.dataTimeout);
                    break;
                }
                catch (BindException be)
                {
                    logger.info("Address is already in use.. will try again. Exception [" + be.getMessage() + "]");
                }
            }
        }
        catch (SocketException se)
        {
            msg = new String(msg + " [Failed]"); //$NON-NLS-1$
            logger.info(msg);
            // Clean up after ourselves just in case
            try
            {
                logger.info("something bad happened trying to disconnect....");
                if(this.ftpClient != null && this.ftpClient.isConnected())
                {
                    logger.info("DISCONNECTING");
                    this.ftpClient.disconnect();
                }
            }
            catch (IOException disconnectException)
            {
                logger.warn("Could not cleanup after a failed connect, this may leave behind open sockets.", disconnectException);
            }
            throw new ClientConnectionException(msg, se);
        }
        catch (IOException ie)
        {
            msg = new String(msg + " [Failed]"); //$NON-NLS-1$
            logger.info(msg);
            // Clean up after ourselves
            try
            {
                if(this.ftpClient != null && this.ftpClient.isConnected())
                {
                    this.ftpClient.disconnect();
                }
            }
            catch (IOException disconnectException)
            {
                logger.warn("Could not cleanup after a failed connect, this may leave behind open sockets.");
            }
            throw new ClientConnectionException(msg, ie);
        }
        logger.debug("Connected!"); //$NON-NLS-1$
    }

    /**
     * Method that tests if the underlying library is valid and connected.
     * 
     * @return <code>true</code> if fully connected, <code>false</code>
     *         otherwise
     */
    public boolean isConnected()
    {
        if(this.ftpClient != null)
        {
            return this.ftpClient.isConnected();
        }
        return false;
    }

    /**
     * Disconnect
     */
    public void disconnect()
    {
        logger.debug("Disconnecting..."); //$NON-NLS-1$
        try
        {
            this.ftpClient.disconnect();
            logger.debug("Disconnected... [OK]"); //$NON-NLS-1$
        }
        catch (IOException e)
        {
            logger.info("Disconnecting... [Failed] due to: ", e); //$NON-NLS-1$
        }
    }

    /**
     * Method used to log the configuration information used to initialise the
     * client.
     * 
     * @param logLevel The log level at which to log the information
     */
    public void echoConfig(Level logLevel)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("FTP configuration information:"); //$NON-NLS-1$
        sb.append("\nRemote Hostname  = [");
        sb.append(remoteHostname);
        sb.append("]\nLocal Hostname   = [");
        sb.append(localHostname);
        sb.append("]\nPassword         = [");
        // sb.append(password);
        // We do not want to log the password
        sb.append("********");
        sb.append("]\nRemote Port      = [");
        sb.append(remotePort);
        sb.append("]\nUsername         = [");
        sb.append(username);
        sb.append("]"); //$NON-NLS-1$
        logger.log(logLevel, sb.toString());
    }

    public void ensureConnection() throws ResourceException
    {
        if(!isConnected())
        {
            try
            {
                connect();
                login();
            }
            catch (ClientConnectionException e1)
            {
                throw new ResourceException(
                    "Failed to ensure that the underlying ftp connection is still open. Likely this was previously open, closed prematurely, and now cannot be reestablished", //$NON-NLS-1$
                    e1);
            }
        }
    }

    public void cd(String targetPath) throws ClientCommandCdException
    {
        // This code is not quite ideal as printWorkingDirectory() could fail
        // but the net result is that the developer will be informed of the
        // fault regardless
        String currentDirectory = null;
        try
        {
            currentDirectory = this.ftpClient.printWorkingDirectory();
            if(!this.ftpClient.changeWorkingDirectory(targetPath))
            {
                throw new ClientCommandCdException("Failed to call directory [" + targetPath //$NON-NLS-1$
                        + "] from [" + currentDirectory + "]"); //$NON-NLS-1$//$NON-NLS-2$
            }
            logger.debug("CD from [" + currentDirectory + "] to [" + this.ftpClient.printWorkingDirectory() + "].");
        }
        catch (IOException e)
        {
            throw new ClientCommandCdException("Failed to call directory [" + targetPath //$NON-NLS-1$
                    + "] from [" + currentDirectory + "]", e); //$NON-NLS-1$//$NON-NLS-2$
        }
        return;
    }

    public void deleteRemoteDirectory(String directoryPath, boolean recurse) throws ClientException, ClientCommandLsException
    {
        try
        {
            if(recurse)
            {
                try
                {
                    List<ClientListEntry> entryList = ls(directoryPath);
                    for (ClientListEntry entry : entryList)
                    {
                        String filePath = entry.getUri().getPath();
                        if(entry.isDirectory())
                        {
                            if(!filePath.endsWith("."))
                            {
                                deleteRemoteDirectory(filePath, recurse);
                            }
                        }
                        else
                        {
                            deleteRemoteFile(filePath);
                        }
                    }
                }
                catch (URISyntaxException e)
                {
                    throw new ClientCommandLsException(e);
                }
            }
            if(!this.ftpClient.removeDirectory(directoryPath))
            {
                throw new ClientException("Exception while deleting directory [" + directoryPath + "]");
            }
        }
        catch (IOException e)
        {
            throw new ClientException("Exception while deleting directory [" + directoryPath + "]", e);
        }
    }

    public void deleteRemoteFile(String filename) throws ClientException
    {
        try
        {
            if(!this.ftpClient.deleteFile(filename))
            {
                throw new ClientException("Exception while deleting file [" + filename + "]"); //$NON-NLS-1$//$NON-NLS-2$                
            }
        }
        catch (IOException e)
        {
            throw new ClientException("Exception while deleting file [" + filename + "]", e); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public BaseFileTransferMappedRecord get(ClientListEntry clientListEntry) throws ClientCommandGetException
    {
        // Construct file path and get the file into an
        // BaseFileTransferMappedRecord
        URI uri = clientListEntry.getUri();
        File srcFile = new File((uri).getPath());
        logger.debug("Getting file [" + srcFile.getPath() + "] into an BaseFileTransferMappedRecord"); //$NON-NLS-1$ //$NON-NLS-2$
        // Getting the file content
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = srcFile.getName();
        BaseFileTransferMappedRecord record = null;
        try
        {
            if(!this.ftpClient.retrieveFile(fileName, output))
            {
                throw new ClientCommandGetException("Failed to get file [" + fileName //$NON-NLS-1$
                        + "] from directory [" + uri.getPath()); //$NON-NLS-1$
            }
            record = BaseFileTransferUtils.createBaseFileTransferMappedRecord(uri, output);
            output.close();
        }
        catch (IOException e)
        {
            throw new ClientCommandGetException("Failed to get file [" + fileName //$NON-NLS-1$
                    + "] from directory [" + uri.getPath(), e); //$NON-NLS-1$
        }
        return record;
    }

    public BaseFileTransferMappedRecord get(String filePath) throws ClientCommandGetException
    {
        URI uri;
        BaseFileTransferMappedRecord record = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            uri = new URI(filePath);
            try
            {
                logger.debug("getting file from filepath: [" + filePath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                if(!this.ftpClient.retrieveFile(filePath, output))
                {
                    throw new ClientCommandGetException("Failed to get file from [" + filePath + "]"); //$NON-NLS-1$ //$NON-NLS-2$                    
                }
                record = BaseFileTransferUtils.createBaseFileTransferMappedRecord(uri, output);
                output.close();
            }
            catch (IOException e)
            {
                throw new ClientCommandGetException("Failed to get file from [" + filePath + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        catch (URISyntaxException e)
        {
            throw new ClientCommandGetException("could not create URI from filePath", e); //$NON-NLS-1$
        }
        return record;
    }

    /**
     * OutputStream is closed by the caller
     * 
     * @param filePath The path to the file that we're getting
     * @param outputStream The stream we're getting the file with
     * @throws ClientCommandGetException Exception if we could not get the file
     */
    public void get(String filePath, OutputStream outputStream) throws ClientCommandGetException
    {
        logger.debug("get called with filePath [" + filePath + "] and outputStream [" + outputStream + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        try
        {
            if(!ftpClient.retrieveFile(filePath, outputStream))
            {
                throw new ClientCommandGetException();
            }
        }
        catch (IOException e)
        {
            throw new ClientCommandGetException(e);
        }
    }

    public void get(String filePath, OutputStream outputStream, int resume, long offset) throws ClientCommandGetException
    {
        throw new ClientCommandGetException("Not yet implemented"); //$NON-NLS-1$
    }

    public InputStream getAsInputStream(String filePath) throws ClientCommandGetException
    {
        InputStream input = null;
        try
        {
            input = ftpClient.retrieveFileStream(filePath);
            if(input == null)
            {
                throw new ClientCommandGetException("InputStream for [" + filePath + "] was null.");
            }
            // Apache recommends using completePendingCommand() after
            // retrieveFileStream()
            if(!ftpClient.completePendingCommand())
            {
                throw new ClientCommandGetException("Error trying to complete pending command.");
            }
            return input;
        }
        catch (IOException e)
        {
            throw new ClientCommandGetException(e);
        }
    }

    public InputStream getContentAsStream(ClientListEntry entry) throws ClientCommandGetException
    {
        // Construct file path and get the file into an OutputStream
        File srcFile = new File((entry.getUri()).getPath());
        logger.debug("Trying to get data from file [" + srcFile.getPath() + "] into an InputStream"); //$NON-NLS-1$ //$NON-NLS-2$
        InputStream input = null;
        // Getting the file content
        String currentDir = null;
        try
        {
            currentDir = this.ftpClient.printWorkingDirectory();
            input = this.ftpClient.retrieveFileStream(srcFile.getName());
            if(input == null)
            {
                throw new ClientCommandGetException("Failed to get file [" + srcFile.getName() //$NON-NLS-1$
                        + "] from directory [" + currentDir + "]"); //$NON-NLS-1$//$NON-NLS-2$
            }
            // Apache recommends using completePendingCommand() after
            // retrieveFileStream()
            if(!ftpClient.completePendingCommand())
            {
                throw new ClientCommandGetException("Failed to complete the get command."); //$NON-NLS-1$
            }
        }
        catch (IOException e)
        {
            throw new ClientCommandGetException("Failed to get file [" + srcFile.getName() //$NON-NLS-1$
                    + "] from directory [" + currentDir + "]", e); //$NON-NLS-1$//$NON-NLS-2$
        }
        return input;
    }

    /**
     * Method used to get a list of all files and directories in the target path
     * including the "." and ".." directories.
     * 
     * @param path The (directory) path to list.
     * @return A <code>ClientListEntry</code> typed <code>List</code>
     * @throws URISyntaxException If a malformed <code>URI</code> is created
     * @throws ClientCommandLsException If the directory listing fails (i.e.
     *             path not a directory or path not found)
     */
    public List<ClientListEntry> ls(String path) throws ClientCommandLsException, URISyntaxException
    {
        try
        {
            return doList(path, null);
        }
        catch (ClientException e)
        {
            throw new ClientCommandLsException(e);
        }
    }

    /**
     * Method used to get the listing of a remote directory path. When used
     * without any <code>SFTPClientFilter</code>s, this method will return all
     * files and directories in the target path including the "." and ".."
     * directories.
     * 
     * Note: Currently there are no rules defining the order of the
     * <code>List</code> returned.
     * 
     * @param path The (directory) path to list.
     * @param filters The <code>SFTPClientListEntry</code>
     * @return A List of<code>SFTPClientFilter</code>s to apply
     * @throws URISyntaxException If a malformed <code>URI</code> is created
     * @throws ClientException If the directory listing fails (i.e. path not a
     *             directory or path not found)
     */
    private List<ClientListEntry> doList(String path, List<ClientPolarisedFilter> filters) throws ClientException, URISyntaxException
    {
        List<ClientListEntry> list = null;
        List<ClientListEntry> filteredList = null;
        try
        {
            String startDir = this.ftpClient.printWorkingDirectory();
            logger.debug("Start directory [" + startDir + "].");
            if(!this.ftpClient.changeWorkingDirectory(path))
            {
                throw new ClientException("Unable to change dir to: [" + path + "]");
            }
            String currentDir = this.ftpClient.printWorkingDirectory();
            logger.debug("Listing directory [" + currentDir + "]");
            // Get a list the files, if it's empty, return null
            FTPFile[] ftpFiles = this.ftpClient.listFiles(".");
            if(ftpFiles == null)
            {
                logger.debug("Directory was empty.");
                return list;
            }
            // initialise an array of ClientListEntries
            list = new ArrayList<ClientListEntry>(ftpFiles.length);
            // Create a complete list of all files and directories as bespoke
            // entries
            for (FTPFile ftpFile : ftpFiles)
            {
                // Apache net library can return null elements in list for
                // unparsed items
                if(ftpFile != null)
                {
                    URI fileUri = this.getURI(currentDir, ftpFile.getName());
                    ClientListEntry entry = convertFTPFileToClientListEntry(ftpFile, fileUri);
                    list.add(entry);
                }
                else
                {
                    logger.warn("One of the ftp file listings could not be parsed.");
                }
            }
            // Return to the calling directory
            if(!this.ftpClient.changeWorkingDirectory(startDir))
            {
                throw new ClientException("Unable to change dir back to: [" + startDir + "]");
            }
            // Filter the list
            if(filters != null && filters.size() > 0)
            {
                filteredList = BaseFileTransferUtils.filterList(list, filters);
            }
        }
        catch (IOException e)
        {
            StringBuilder sb = new StringBuilder(384);
            sb.append("Failed to get listing for directory! ["); //$NON-NLS-1$
            sb.append(path);
            sb.append(']');
            throw new ClientException(sb.toString(), e);
        }
        return (filteredList != null) ? filteredList : list;
    }

    public void put(String name, byte[] content) throws ClientCommandPutException
    {
        InputStream ins = new ByteArrayInputStream(content);
        try
        {
            if(!this.ftpClient.storeFile(name, ins))
            {
                throw new ClientCommandPutException("Failed to write input stream to file! [" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$                
            }
            // Close the InputStream once we're finished with it
            ins.close();
        }
        catch (IOException e)
        {
            throw new ClientCommandPutException("Failed to write input stream to file! [" + name + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * FTP Put using an output stream form an input stream.
     * 
     * @param fileName Name of the file we're putting
     * @param inputStream Stream we're putting the file with
     * @throws ClientCommandPutException Exception if we can't put
     * @throws ClientCommandLsException Exception if we can't lista directory
     * @throws ClientCommandMkdirException Exception if we can't make a
     *             directory
     * 
     */
    public void putWithOutputStream(String fileName, InputStream inputStream) throws ClientCommandPutException, ClientCommandLsException,
            ClientCommandMkdirException
    {
        try
        {
            ensureParentsExist(fileName);
            OutputStream outputStream = ftpClient.storeFileStream(fileName);
            if(outputStream == null)
            {
                throw new ClientCommandPutException("OutputStream for [" + fileName + "] was null");
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            int result = 0;
            long writeCounter = 0;
            while (result != -1)
            {
                result = inputStream.read();
                if(result != -1)
                {
                    bufferedOutputStream.write(result);
                    writeCounter++;
                }
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            // Apache recommends using completePendingCommand() after
            // storeFileStream()
            if(!ftpClient.completePendingCommand())
            {
                throw new ClientCommandPutException("Error on completePendingCommand within 'putWithOutputStream'.");
            }
        }
        catch (IOException e)
        {
            throw new ClientCommandPutException(e);
        }
    }

    public String pwd() throws ClientCommandPwdException
    {
        String currentDir = null;
        try
        {
            currentDir = this.ftpClient.printWorkingDirectory();
        }
        catch (IOException e)
        {
            throw new ClientCommandPwdException("Failed to get working directory!", e); //$NON-NLS-1$
        }
        return currentDir;
    }

    public void rename(String currentPath, String newPath) throws ClientCommandRenameException
    {
        logger.debug("rename called with currentPath [" + currentPath + "], newPath [" + newPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        try
        {
            String dirBefore = this.ftpClient.printWorkingDirectory();
            logger.debug("Working directory before rename = [" + dirBefore + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            logger.debug("Current Path = [" + currentPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            logger.debug("New Path = [" + newPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            if(!this.ftpClient.rename(currentPath, newPath))
            {
                throw new ClientCommandRenameException("Failed to rename [" + currentPath + "] to [" + newPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            logger.debug("Successfully renamed [" + currentPath + "] to [" //$NON-NLS-1$ //$NON-NLS-2$
                    + newPath + "]"); //$NON-NLS-1$
            String dirAfter = this.ftpClient.printWorkingDirectory();
            logger.debug("Working directory after rename = [" + dirAfter + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            if(!dirBefore.equals(dirAfter))
            {
                this.ftpClient.changeWorkingDirectory(dirBefore);
                logger.debug("Returning to previous working = [" + dirBefore + "]"); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        catch (IOException e)
        {
            throw new ClientCommandRenameException("Failed to rename [" + currentPath + "] to [" + newPath + "]", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * Constructing a <code>ClientListEntry</code> object from an
     * <code>FTPFile</code> object. This is a direct map with some formatting
     * changes.
     * 
     * @param ftpFile The <code>FTPFile</code> to map to a
     *            <code>ClientListEntry</code>
     * @param fileUri The URI of the underlying file for the particular
     *            <code>FTPFile</code>
     * @return ClientListEntry
     */
    private ClientListEntry convertFTPFileToClientListEntry(FTPFile ftpFile, URI fileUri)
    {
        ClientListEntry clientListEntry = new ClientListEntry();
        clientListEntry.setUri(fileUri);
        clientListEntry.setName(ftpFile.getName());
        clientListEntry.setClientId(null);
        // Can't distinguish between Last Accessed and Last Modified
        clientListEntry.setDtLastAccessed(ftpFile.getTimestamp().getTime());
        clientListEntry.setDtLastModified(ftpFile.getTimestamp().getTime());
        clientListEntry.setSize(ftpFile.getSize());
        clientListEntry.isDirectory(ftpFile.isDirectory());
        clientListEntry.isLink(ftpFile.isSymbolicLink());
        clientListEntry.setLongFilename(ftpFile.getRawListing());
        clientListEntry.setAtime(ftpFile.getTimestamp().getTime().getTime());
        clientListEntry.setMtime(ftpFile.getTimestamp().getTime().getTime());
        clientListEntry.setAtimeString(ftpFile.getTimestamp().toString());
        clientListEntry.setMtimeString(ftpFile.getTimestamp().toString());
        // clientListEntry.setFlags();
        clientListEntry.setGid(ftpFile.getGroup());
        clientListEntry.setUid(ftpFile.getUser());
        // TODO might be able to ask which permissions it has and build an int
        // and String from there
        // clientListEntry.setPermissions();
        // clientListEntry.setPermissionsString();
        // No extended information
        clientListEntry.setExtended(null);
        return clientListEntry;
    }

    /**
     * Method used to create a <code>URI</code> object from an absolute path to
     * a target. The absolute path needs be passed in as two parameters: One
     * indicating the absolute path of the parent directory and one indicating
     * the actual file name.
     * 
     * Note: Avoid having any encoding/decoding of the path in here
     * 
     * TODO: Could add check to see if this is root path or not!
     * 
     * @param absDir The absolute directory
     * @param filename The file name
     * @return URI URI of the file
     * @throws URISyntaxException Exception if the URI is not valid
     */
    private URI getURI(String absDir, String filename) throws URISyntaxException
    {
        StringBuilder absolutePath = new StringBuilder(absDir.length() + 1 + filename.length());
        absolutePath.append(absDir);
        absolutePath.append('/');
        absolutePath.append(filename);
        // absolutePath.trimToSize();
        StringBuilder userInfo = new StringBuilder(this.username.length());
        userInfo.append(this.username);
        // userInfo.trimToSize();
        return new URI("ftp", userInfo.toString(), this.remoteHostname, this.remotePort, absolutePath.toString(), null, null);
    }

    /**
     * Creates, if necessary all the parents in given file path
     * 
     * @param filePath The path to the file
     * @throws ClientCommandLsException Exception if we cannot list files
     * @throws ClientCommandMkdirException Exception if we cannot make a
     *             directory
     */
    private void ensureParentsExist(String filePath) throws ClientCommandLsException, ClientCommandMkdirException
    {
        logger.debug("ensureParentsExist called with [" + filePath + "]"); //$NON-NLS-1$//$NON-NLS-2$
        File file = new File(filePath);
        List<File> parents = new ArrayList<File>();
        BaseFileTransferUtils.findParents(file, parents);
        Collections.reverse(parents);
        for (File directory : parents)
        {
            if(!dirExists(directory))
            {
                logger.debug("creating new parent dir [" + directory.getPath() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                mkdir(directory.getPath());
            }
        }
    }

    /**
     * Determines if a remote directory exists
     * 
     * @param directory Directory to check
     * @return true if this file represents an existent remote directory
     * @throws ClientCommandLsException Exception if we can't list the directory
     */
    private boolean dirExists(File directory) throws ClientCommandLsException
    {
        boolean dirFound = false;
        String directoryParentPath = null;
        try
        {
            directoryParentPath = ftpClient.printWorkingDirectory();
        }
        catch (IOException e)
        {
            throw new ClientCommandLsException("Could not get working directory.", e);
        }
        if(directoryParentPath == null)
        {
            throw new ClientCommandLsException("Could not get working directory.");
        }
        if(directory.getParent() != null)
        {
            directoryParentPath = directory.getParentFile().getPath();
        }
        List<ClientListEntry> entries;
        try
        {
            entries = ls(directoryParentPath);
            // does the ls on the directory's parent contain the directory?
            Iterator<ClientListEntry> iterator = entries.iterator();
            while (iterator.hasNext())
            {
                File entryFile = new File(iterator.next().getLongFilename());
                if(entryFile.getName().equals(directory.getName()))
                {
                    dirFound = true;
                }
            }
        }
        catch (URISyntaxException e)
        {
            throw new ClientCommandLsException(e);
        }
        return dirFound;
    }

    /**
     * Utility method for <code>mkdir(String newDirPath, boolean force)</code>
     * that defaults the <code>force</code> parameter to <code>true</code>.
     * 
     * @param newPath The new path to create
     * @throws ClientCommandMkdirException Exception if we can't make a
     *             directory
     */
    private void mkdir(String newPath) throws ClientCommandMkdirException
    {
        try
        {
            createRemotePath(newPath, true);
        }
        catch (ClientException e)
        {
            throw new ClientCommandMkdirException(e);
        }
    }

    /**
     * Utility method used to create a path on the remote host. Any path element
     * that does not exist along the path will be created if the
     * <code>force</code> parameter is set to true. Currently used by the ...
     * method.
     * 
     * @param newPath The path to create on the remote host. This can be either
     *            absolute or relative to current working directory. Absolute
     *            paths must start with a '/' character.
     * @param force If true all missing elements along the <code>newPath</code>
     *            will be created.
     * 
     * @throws ClientException If the operation fails (i.e. because of
     *             permission issues). It is left up the caller method to throw
     *             the appropriate specific exception as this in only used
     *             internally.
     */
    private void createRemotePath(String newPath, boolean force) throws ClientException
    {
        StringBuilder file = new StringBuilder(256);
        // Make a note of the current working directory
        String cwd = null;
        try
        {
            cwd = this.ftpClient.printWorkingDirectory();
        }
        catch (IOException e)
        {
            throw new ClientException("Could not get working directory.", e);
        }
        if(cwd == null)
        {
            throw new ClientException("Could not get working directory.");
        }
        // If the newpath starts with a separator, assume a root directory;
        if(newPath.startsWith("/"))
        {
            file.append(newPath);
        }
        // otherwise, assume subdirectory of current working directory
        else
        {
            file.append(cwd);
            file.append('/');
            file.append(newPath);
        }
        // Hence by this point we deal with an absolute path on the remote host
        // Create an arraylist holding all the relative path elements for this
        // absolute path. Note: the root needs to be added explicitly
        StringTokenizer st = new StringTokenizer(file.toString(), "/");
        ArrayList<String> pathElements = new ArrayList<String>();
        pathElements.add("/");
        while (st.hasMoreTokens())
            pathElements.add(st.nextToken());
        // Therefore we can go to the root and start creating the directories
        // one by one without worrying over the platform's filesystem separator
        boolean createFlag = false;
        for (String pathElement : pathElements)
        {
            try
            {
                if(!this.ftpClient.changeWorkingDirectory(pathElement))
                {
                    if(force)
                    {
                        createFlag = true;
                        logger.debug("Will try to create working directory to [" + pathElement + "]");
                    }
                    else
                    {
                        throw new ClientException("Could not change working directory to [" + pathElement + "]");
                    }
                }
            }
            catch (IOException e)
            {
                if(force)
                {
                    createFlag = true;
                    logger.debug("Will try to create working directory to [" + pathElement + "]");
                }
                else
                {
                    throw new ClientException("Could not change working directory to [" + pathElement + "]", e);
                }
            }
            // By this point we have either cd'ed into the existing directory,
            // or the directory does not exists and the createFlag is set to
            // true, hence try mkdir
            if(createFlag)
            {
                try
                {
                    if(!this.ftpClient.makeDirectory(pathElement))
                    {
                        throw new ClientException("Failed to create path element [" + pathElement + "] of path [" + newPath + "]");
                    }
                    if(!this.ftpClient.changeWorkingDirectory(pathElement))
                    {
                        throw new ClientException("Failed to navigate to path element [" + pathElement + "] of path [" + newPath + "]");
                    }
                    logger.debug("Created path element [" + pathElement + "] of path [" + newPath + "]");
                }
                catch (IOException e)
                {
                    throw new ClientException("Failed to create & navigate to path element [" + pathElement + "] of path [" + newPath + "]", e);
                }
            }
            // Otherwise, reset createFlag and go for next path element
            createFlag = false;
        }
        // Reset the working directory to where we have started from
        try
        {
            if(!this.ftpClient.changeWorkingDirectory(cwd))
            {
                throw new ClientException("Failed to reset working directory to [" + cwd + "]");
            }
        }
        catch (IOException e)
        {
            throw new ClientException("Failed to reset working directory to [" + cwd + "]", e);
        }
    }
}
