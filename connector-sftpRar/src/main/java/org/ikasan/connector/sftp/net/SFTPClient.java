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
package org.ikasan.connector.sftp.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.resource.ResourceException;
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
import org.ikasan.connector.basefiletransfer.net.ClientFilenameFormatter;
import org.ikasan.connector.basefiletransfer.net.ClientFixedFilenameFormatter;
import org.ikasan.connector.basefiletransfer.net.ClientInitialisationException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.ClientPolarisedFilter;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * <p>
 * This class provides the basic functionality of an SFTP client based on the
 * jCraft jsch library. Some of the basic functionality is wrapped to provide
 * <code>SFTPClient</code> specific exceptions, or to provide the
 * functionality required by a framework component.
 * </p>
 * 
 * @author Ikasan Development Team
 */
public class SFTPClient implements FileTransferClient
{

    /** This directory */
    private final static String CURRENT_DIRECTORY = "."; //$NON-NLS-1$

    /** Parent directory */
    private final static String PARENT_DIRECTORY = ".."; //$NON-NLS-1$

    /** Initialising the logger */
    private static Logger logger = Logger.getLogger(SFTPClient.class);

    /** User name */
    private String username;

    /** Remote host name */
    private String remoteHostname;

    /** Local hostname */
    private String localHostname;

    /** Private key file name */
    private File prvKey;

    /** Known hosts file name */
    private File knownHosts;

    /** Port */
    private int remotePort;

    /** Socket connection timeout in milliseconds. Defaults to unlimited (0)*/
    private Integer connectionTimeout = 0;

    /** The default maximum local port number in Hex*/
    private static final Integer DEFAULT_MAXIMUM_LOCAL_PORT = 0xFFFF;

    /** Default authentication order */
    private static final String DEFAULT_PREFERRED_AUTHENTICATIONS = "publickey,password,gssapi-with-mic";

    /** Destroy files after successful get */
    private boolean getDestructive;

    /** Destroy files after successful put */
    private boolean putDestructive;

    /** File Separator */
    private String lfs;

    /** Directory separator */
    private String tfs;

    /** Maximum no of times to retry connection */
    private Integer maxRetryAttempts;

    // Third-party library objects
    /** SSH/SSL/SFTP client */
    private JSch jsch;

    /** Session */
    private Session session;

    /** Channel */
    private Channel channel;

    /** SFTP Channel */
    private ChannelSftp channelSftp;

    /** Preferred authentication order */
    private String preferredAuthentications;

    /**
     * SFTPClient constructor where all parameters are provided by the user
     * 
     * <p>
     * Note: Providing the knownHosts file location is essential in order to
     * avoid ssh prompts for trusting hosts.
     * </p>
     * Note: The key used needs to be in openssh format (i.e. no header info)
     * 
     * @param prvKey The private key file (rsa or dsa key)
     * @param knownHosts The .ssh "known_hosts" file
     * @param username The username for the connection
     * @param remoteHostname The target host 
     * @param remotePort The port to connect to 
     * @param localHostname - the local hostname to bind to
     * @param maxRetryAttempts - number of connection retries before failure
     * @param connectionTimeout - socket connection timeout
     * @param preferredAuthentications 
     */
    public SFTPClient(File prvKey, File knownHosts, String username, String remoteHostname, 
            int remotePort, String localHostname,
            Integer maxRetryAttempts, String preferredAuthentications, Integer connectionTimeout)
    {
        super();
        this.prvKey = prvKey;
        this.knownHosts = knownHosts;
        this.username = username;
        this.remoteHostname = remoteHostname;
        this.remotePort = remotePort;
        this.getDestructive = false;
        this.putDestructive = false;
        this.lfs = System.getProperty("file.separator");
        this.tfs = new String("/");
        this.maxRetryAttempts = maxRetryAttempts;
        if (localHostname != null && localHostname.length() > 0)
        {
            this.localHostname = localHostname;
        }
        if (preferredAuthentications != null && preferredAuthentications.length() > 0)
        {
            this.preferredAuthentications = preferredAuthentications;
        }
        else
        {
            this.preferredAuthentications = DEFAULT_PREFERRED_AUTHENTICATIONS;
        }
        if (connectionTimeout != null)
        {
            this.connectionTimeout = connectionTimeout;
        }
    }

    /**
     * Convenience constructor which only gets the username and hostname as
     * parameters and searches for the ssh files ("id_dsa" and "known_hosts") at
     * the default ssh directory (i.e. ~/.ssh).
     * 
     * As of 03/07/2007 Only used by SFTPClientTest
     * 
     * <p>
     * Note: Providing the knownHosts file location is essential in order to
     * avoid ssh prompts for trusting hosts.
     * </p>
     * Note: The key used needs to be in openssh format (i.e. no header info)
     * 
     * @param username The username for the connection
     * @param hostname The target host
     */
    public SFTPClient(String username, String hostname)
    {
        this.username = username;
        this.remoteHostname = hostname;
        // Create File objects reflecting the locations of the required ssh
        // files and populate the relative class variables.
        this.lfs = System.getProperty("file.separator");
        this.tfs = new String("/");
        String homeDir = System.getProperty("user.home");
        String sshDir = new String(".ssh");
        String prvKeyFile = new String("id_rsa"); // id_dsa is an alternative
        String knownHostsFile = new String("known_hosts");
        this.prvKey = new File(homeDir + lfs + sshDir + lfs + prvKeyFile);
        this.knownHosts = new File(homeDir + lfs + sshDir + lfs + knownHostsFile);
        this.remotePort = 22;
    }

    /**
     * All constructor arguments are essential, so use this method to validate
     * them before attempting connection
     * 
     * @throws ClientInitialisationException if any of the required parameters 
     * are invalid
     */
    public void validateConstructorArgs() throws ClientInitialisationException
    {
        // If all values seem OK, log the info and return
        if (this.prvKey.exists() && this.knownHosts.exists() && this.username != null && this.remoteHostname != null)
        {
            echoConfig(Level.DEBUG);
            return;
        }

        // Otherwise, trace through, log the erroneous parameters and throw an exception
        StringBuilder sb = new StringBuilder("The following arguments are erroneous or missing:\n");
        if (this.remoteHostname == null || this.remoteHostname.length() == 0)
        {
            sb.append("Invalid hostname! ["); //$NON-NLS-1$
            sb.append(this.remoteHostname);
            sb.append("]"); //$NON-NLS-1$
        }
        if (!this.knownHosts.exists())
        {
            sb.append("Known hosts file not found! ["); //$NON-NLS-1$
            sb.append(this.knownHosts.getAbsolutePath());
            sb.append("]\n"); //$NON-NLS-1$
        }
        if (!this.prvKey.exists())
        {
            sb.append("Private key file not found! ["); //$NON-NLS-1$
            sb.append(this.prvKey.getAbsolutePath());
            sb.append("]\n"); //$NON-NLS-1$
        }
        if (this.username == null || this.username.length() == 0)
        {
            sb.append("Invalid username! ["); //$NON-NLS-1$
            sb.append(this.username);
            sb.append("]\n"); //$NON-NLS-1$
        }
        throw new ClientInitialisationException(sb.toString());
    }

    /**
     * Wrapper for the doConnect() method. Checks the state of the connection
     * and only initialises the connection process if not already connected. The
     * check is done using the <code>isConnected()</code> method, while the
     * actual connection is established using the private
     * 
     * If a maxRetryAttempts exists, then a connection will be attempted up to a
     * maximum of this value. Otherwise just a single attempt will be made
     * 
     * <code>doConnect()</code> method.
     * 
     * @throws ClientConnectionException if connection attempt fails
     */
    public void connect() throws ClientConnectionException
    {

        // Checking the connection state,
        String msg = new String("Checking connection status... "); //$NON-NLS-1$
        logger.info(msg + "[" + (isConnected() ? "connected" : "disconnected") + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        // and acting accordingly.
        if (isConnected())
        {
            logger.debug("Session and channel already connected!"); //$NON-NLS-1$
            return;
        }

        // try to gain a connection, up to the max no of retries - if one exists
        int maxAttemptConnection = 1;
        if (maxRetryAttempts != null)
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
                if (isConnected())
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
                logger.warn("Attempt [" + (retryCount + 1) + "] failed to connect due to: " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }

        // if we are still not connected after retrying, we need to throw the
        // exception from the last failure
        if (!isConnected())
        {
            if (connectionException != null)
            {
                logger.error("Failed to connect after [" + maxAttemptConnection //$NON-NLS-1$
                        + "] retries."); //$NON-NLS-1$
                throw connectionException;
            }
            // Default else
            logger.error("Failed to connect after ]" + maxAttemptConnection //$NON-NLS-1$
                    + "] retries, but ClientConnectionException was not thrown!!"); //$NON-NLS-1$
        }

        logger.info("Session and channel connected!"); //$NON-NLS-1$
    }

    /**
     * Method that handles the <code>session</code> and <code>channel</code>
     * creation and connection.
     * 
     * Note: The current implementation of Jsch does not allow us to identify
     * between authentication and communication errors during session or channel
     * connect, hence any exception will be wrapped and thrown as
     * <code>ClientConnectionException</code>.
     * 
     * @throws ClientConnectionException if connection attempt fails
     */
    private void doConnect() throws ClientConnectionException
    {
        this.jsch = new JSch();
        String msg = new String("Attempting connection to [" + remoteHostname + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        logger.info(msg);
        try
        {
            JSch.setConfig("PreferredAuthentications", this.preferredAuthentications);
            msg = new String("Adding private key to identity..."); //$NON-NLS-1$
            logger.debug(msg);
            this.jsch.addIdentity(this.prvKey.getAbsolutePath());
            msg = new String("Setting the known hosts..."); //$NON-NLS-1$
            logger.debug(msg);
            this.jsch.setKnownHosts(this.knownHosts.getAbsolutePath());
            msg = new String("Getting the session and connecting..."); //$NON-NLS-1$
            logger.debug(msg);
            this.session = jsch.getSession(this.username, this.remoteHostname, this.remotePort);
            this.session.connect(this.connectionTimeout);
            if (this.localHostname != null)
            {
                echoConfig(Level.INFO);
                for (int i = 0; i < DEFAULT_MAXIMUM_LOCAL_PORT; i++)
                {
                    try
                    {
                        //this.localPort = this.generateRandomPortNumber();
                        logger.debug("Connecting to remote host [" + this.remoteHostname + ":" + this.remotePort + "] from local host [" + this.localHostname + ":" + this.localHostname + "].");
                        this.session.setPortForwardingL(this.localHostname, 0, this.remoteHostname, this.remotePort);
                        break;
                    }
                    catch (JSchException e)
                    {
                        //Very crude way of handing port clashes thanks to JSCH library
                        //and their generic JSchException!!
                        if (e.getCause() instanceof BindException)
                        {
                            logger.info("Address is already in use.. will try again. Exception [" + e.getMessage() + "].");
                        }
                    }
                }
            }
            
            msg = new String("Getting the sftp channel and connecting..."); //$NON-NLS-1$
            logger.debug(msg);
            this.channel = session.openChannel("sftp");
            this.channel.connect();
            this.channelSftp = (ChannelSftp) channel;
        }
        catch (JSchException e)
        {
            msg = new String(msg + " [Failed]"); //$NON-NLS-1$
            logger.info(msg);
            // Make sure we clean up anything that was left lying around
            disconnect();
            throw new ClientConnectionException(msg, e);
        }
        logger.debug("Connected!"); //$NON-NLS-1$
    }

    /**
     * Method that tests if the underlying library's session and channels are
     * valid and connected.
     * 
     * @return <code>true</code> if fully connected, <code>false</code>
     *         otherwise
     */
    public boolean isConnected()
    {
        // Getting the status of each connection related object
        boolean a = (this.session == null) ? false : this.session.isConnected();
        boolean b = (this.channel == null) ? false : this.channel.isConnected();
        boolean c = (this.channelSftp == null) ? false : this.channelSftp.isConnected();
        // If all the above are true, then we are connected
        // Else, two possibilities, either we are all happily disconnected, or
        // one of the above is null (when it shouldn't - hence assume
        // disconnected
        return (a && b && c);
    }

    /**
     * Method that handles the <code>channel</code> and <code>session</code>
     * disconnection.
     */
    public void disconnect()
    {
        logger.debug("Disconnecting..."); //$NON-NLS-1$
        if (this.channelSftp != null && this.channelSftp.isConnected())
        {
            logger.debug("Disconnecting SftpChannel..."); //$NON-NLS-1$
            this.channelSftp.disconnect();
        }
        if (this.channel != null && this.channel.isConnected())
        {
            logger.debug("Disconnecting Channel..."); //$NON-NLS-1$
            this.channel.disconnect();
        }
        if (this.session != null && this.session.isConnected())
        {
            logger.debug("Disconnecting Session..."); //$NON-NLS-1$
            this.session.disconnect();
        }
        logger.info("Disconnecting... [OK]"); //$NON-NLS-1$
    }

    /**
     * Method which allows the client to rename a file on the SFTP server. The
     * paths provided should be <b>absolute</b> (i.e. starting with a '/') - in
     * which case the jsch library will read from the root of the target host -
     * or <b>relative</b> - in which case the will be treated in reference to
     * the current working directory on the host.
     * 
     * @param currentPath The file or directory to rename
     * @param newPath The file or directory to rename to
     * 
     * Note: Using <code>File</code>s for this operation will result in
     * errors if the host's OS is not the same as the one running the client
     * because the <code>File.getAbsolutePath()</code> method will always
     * return a string path valid for the current OS.
     * 
     * @throws ClientCommandRenameException
     */
    public void rename(String currentPath, String newPath) throws ClientCommandRenameException
    {
        logger.debug("rename called with currentPath [" + currentPath + "], newPath [" + newPath + "]");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        
        try
        {
            String dirBefore = this.channelSftp.pwd();

            StringBuilder sb = new StringBuilder(384);
            sb.append("Working directory before rename = ["); //$NON-NLS-1$
            sb.append(dirBefore);
            sb.append(']');
            sb.trimToSize();
            logger.info(sb.toString());

            sb = new StringBuilder(384);
            sb.append("Current Path = ["); //$NON-NLS-1$
            sb.append(currentPath);
            sb.append(']');
            sb.trimToSize();
            logger.info(sb.toString());

            sb = new StringBuilder(384);
            sb.append("New Path = ["); //$NON-NLS-1$
            sb.append(newPath);
            sb.append(']');
            sb.trimToSize();
            logger.info(sb.toString());

            this.channelSftp.rename(currentPath, newPath);
            sb = new StringBuilder(640);
            sb.append("Successfully renamed ["); //$NON-NLS-1$
            sb.append(currentPath);
            sb.append("] to ["); //$NON-NLS-1$
            sb.append(newPath);
            sb.append(']');
            sb.trimToSize();
            logger.info(sb.toString());
            String dirAfter = this.channelSftp.pwd();
            sb = new StringBuilder(384);
            sb.append("Working directory after rename = ["); //$NON-NLS-1$
            sb.append(dirAfter);
            sb.append(']');
            sb.trimToSize();
            logger.info(sb.toString());

            if (!dirBefore.equals(dirAfter))
            {
                this.channelSftp.cd(dirBefore);
                sb = new StringBuilder(384);
                sb.append("Returning to previous working = ["); //$NON-NLS-1$
                sb.append(dirBefore);
                sb.append(']');
                sb.trimToSize();
                logger.debug(sb.toString());
            }
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(640);
            sb.append("Failed to rename ["); //$NON-NLS-1$
            sb.append(currentPath);
            sb.append("] to ["); //$NON-NLS-1$
            sb.append(newPath);
            sb.append(']');
            sb.trimToSize();
            throw new ClientCommandRenameException(sb.toString(), e);
        }
    }

    /**
     * Method used to implement the put() functionality of the sftp client. The
     * process is dealt with as follows:
     * <ul>
     * <li>Get the list of relevant absolute <b>source</b> paths (encoded to
     * UTF8)</li>
     * <li>Get the list of relative <b>source</b> paths</li>
     * <li>Get the list of relevant absolute <b>destination</b> paths</li>
     * <li>If the <code>force</code> parameter is true, then create all
     * required paths on the remote host</li>
     * <li>For each <b>file</b> in the relevant absolute <b>destination</b>
     * paths list, put the file</li>
     * </ul>
     * 
     * This method relies on a number of other methods, namely:
     * <ul>
     * <li>listAbsoluteSourcePaths() (This can be recursive to any depth, or
     * just current dir)</li>
     * </ul>
     * 
     * As of 03/07/2007 - only used by SFTPClientTest
     * 
     * @param src
     * @param rDir
     * @param rFile
     * @param mode
     * @param filter
     * @param recurse
     * @param force
     * @throws ClientCommandPutException
     */
    public void put(File src, String rDir, String rFile, int mode, FilenameFilter filter, boolean recurse, boolean force)
            throws ClientCommandPutException
    {
        String remoteDir = rDir;

        // Get the list of absolute source paths
        // Note: Directory absSrcPaths are expected to end with a '/' !!
        List<String> absSrcPaths = listAbsoluteSourcePaths(src, filter, recurse);
        if (logger.getLevel() == Level.DEBUG)
        {
            for (String s : absSrcPaths)
            {
                logger.debug("absSrcPath [" + s + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // Get the list of relative source paths. The formatter is used along
        // with the check for isDirectory to allow us to handle the
        // file-to-specificFile put situation
        ClientFilenameFormatter formatter = null;

        if (!src.isDirectory() && rFile != null && rFile.length() > 0)
        {
            formatter = new ClientFixedFilenameFormatter(rFile);
        }

        List<String> relSrcPaths = listRelativeSourcePaths(src, absSrcPaths, formatter);

        if (logger.getLevel() == Level.DEBUG)
        {
            for (String s : relSrcPaths)
            {
                logger.debug("relSrcPath [" + s + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // Sanity check
        if (absSrcPaths.size() != relSrcPaths.size())
        {
            logger.error("relSrcPaths and absSrcPaths are not the same size!"); //$NON-NLS-1$
        }

        // Before getting the list of absolute destination paths, ensure that
        // the provided destination directory is a root or not. If it starts
        // with a "/" assume root, otherwise assume relative to the users login
        // directory. If no rDir value is provided, assume the name of the the
        // directory along the source path.
        String absRemoteDir = new String("");
        String cwd;
        try
        {
            cwd = this.channelSftp.pwd();
        }
        catch (SftpException e)
        {
            throw new ClientCommandPutException(e);
        }
        if (rDir == null || rDir.length() == 0)
        {
            if (src.isDirectory())
                remoteDir = new String(cwd + src.getName());
            else
                remoteDir = new String(cwd + src.getParentFile().getName());
        }
        if (remoteDir.startsWith("/"))
        {
            absRemoteDir = new String(remoteDir);
        }
        else
        {
            absRemoteDir = new String(cwd + "/" + remoteDir);
        }

        // Get the list of absolute destination paths
        List<String> absDstPaths = new ArrayList<String>();
        for (String rsp : relSrcPaths)
        {
            StringBuilder sb = new StringBuilder(512);
            sb.append(absRemoteDir);
            sb.append('/');
            sb.append(rsp);
            sb.trimToSize();
            String adp = sb.toString().replaceAll("//", "/");
            absDstPaths.add(adp);
        }

        if (logger.getLevel() == Level.DEBUG) for (String s : absDstPaths)
        {
            logger.debug("absDstPath [" + s + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // If force directory creation is on, find the directories that need be
        // created. This is done as follows:
        // 1. We ensure that the list is sorted alphabetically
        // 2. Create a list of entries that can be removed by comparing the
        // the entries against each other
        if (force)
        {
            // Set the first entry to be the root
            ArrayList<String> entriesToCreate = new ArrayList<String>();
            for (int i = 0; i < absDstPaths.size() - 1; i++)
            {
                if (absDstPaths.get(i).endsWith("/")) entriesToCreate.add(absDstPaths.get(i));
            }
            for (String etc : entriesToCreate)
            {
                try
                {
                    if (!remotePathExists(etc)) this.createRemotePath(etc, recurse);
                }
                catch (ClientException e)
                {
                    throw new ClientCommandPutException(e);
                }
            }
        }
        // By this point all required directories have been created, so we can
        // go on and copy all the file entries
        // Sanity check before copying as we will need references from both
        // lists
        if (absSrcPaths.size() != absDstPaths.size())
        {
            logger.error("absDstPaths and absSrcPaths are not the same size!"); //$NON-NLS-1$
        }
        // Before going crazy with putting files, check the possibility of just
        // one file being put to a specific file
        for (int i = 0; i < absSrcPaths.size(); i++)
        {
            if (!absSrcPaths.get(i).endsWith("/"))
            {
                File f = new File(absSrcPaths.get(i));
                String absSrcFilename = f.getPath();
                String absDstFilename = absDstPaths.get(i);
                StringBuilder sb = new StringBuilder(640);
                sb.append("Putting ["); //$NON-NLS-1$
                sb.append(absSrcFilename);
                sb.append("] to ["); //$NON-NLS-1$
                sb.append(absDstFilename);
                sb.append("]... "); //$NON-NLS-1$
                try
                {
                    this.channelSftp.put(absSrcFilename, absDstFilename, null, mode);
                    sb.append("[OK]"); //$NON-NLS-1$
                    logger.info(sb.toString());
                }
                catch (Exception e)
                {
                    sb.append("[FAILED]"); //$NON-NLS-1$
                    throw new ClientCommandPutException(sb.toString(), e);
                }
            }
        }
        // If put is destructive, delete the local file/directory
        if (this.putDestructive)
        {
            String srcType = src.isDirectory() ? "Directory" : "File";
            StringBuilder sb = new StringBuilder(384);
            sb.append("Destructive put. "); //$NON-NLS-1$
            sb.append(srcType);
            sb.append(" will be deleted ["); //$NON-NLS-1$
            sb.append(src.getAbsolutePath());
            sb.append(']');
            sb.trimToSize();
            logger.info(sb.toString());
            if (!src.isDirectory())
                src.delete();
            else
                deleteLocalDir(src);
        }
    }

    /**
     * Puts a <code>byte[]</code> as a file to the destination. The name to
     * use is provided by the name parameter.
     * 
     * Also possibly allow for more than just the overwrite mode.
     * 
     * @param name
     * @param content The payload which should contain a the file content as a
     * <code>byte[]</code> and the absolute or relative filename as the 
     * <code>Payload</code>'s <code>name</code> field.
     * 
     * @throws ClientCommandPutException If the underlying library put
     *             operation fails.
     */
    public void put(String name, byte[] content) throws ClientCommandPutException
    {
        InputStream ins = new ByteArrayInputStream(content);
        
        try
        {
            this.channelSftp.put(ins, name, ChannelSftp.OVERWRITE);
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(256);
            sb.append("Failed to write input stream to file! ["); //$NON-NLS-1$
            sb.append(name);
            sb.append(']');
            sb.trimToSize();
            throw new ClientCommandPutException(sb.toString(), e);
        }
    }

    /**
     * Method used to get a specified file (or files matching a pattern) from
     * remote host and put it in the the specified local file/directory.
     * <p>
     * If the local file/directory is not specified, default values are used for
     * both the directory (running dir) and file.
     * </p>
     * <p>
     * The source file will be deleted if the <code>getDestructive</code> class
     * variable is set to <code>true</code>
     * 
     * <p>
     * The write modes for writing the file to the local system are as follows:
     * <ul>
     * <li>"get" for <code>ChannelSftp.OVERWRITE</code></li>
     * <li>"get-resume" for <code>ChannelSftp.RESUME</code></li>
     * <li>"get-append" for <code>ChannelSftp.APPEND</code></li>
     * </ul>
     * 
     * Suppressed Warning:  We assume that this.channelSftp.ls(path) returns Vector<LsEntry>
     * 
     * @param rDir The absolute path to the remote target directory as 
     * <code>String</code>.
     * @param rFile The remote target file (or files based on pattern) as
     * <code>String</code>.
     * @param lDir The absolute path to the local target directory as
     * <code>String</code>.
     * @param lFile The local filename as <code>String</code>.
     * @param mode The write mode to be used when writing the file locally.
     * @throws ClientCommandGetException with appropriate message depending on
     * problem.
     */
    @SuppressWarnings("unchecked")
    public void get(String rDir, String rFile, String lDir, String lFile, int mode) throws ClientCommandGetException
    {
        if (mode != ChannelSftp.OVERWRITE && mode != ChannelSftp.RESUME && mode != ChannelSftp.APPEND)
        {
            StringBuilder sb = new StringBuilder(256);
            sb.append("Unsupported write mode for get ["); //$NON-NLS-1$
            sb.append(mode);
            sb.append("]. Available modes are OVERWRITE(1), RESUME(2) and APPEND(3)."); //$NON-NLS-1$
            throw new ClientCommandGetException(sb.toString());
        }
        // Go to the target remote directory. If it doesn't exist, user made a
        // mistake.
        try
        {
            this.channelSftp.cd(rDir);
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(384);
            sb.append("Failed to call remote directory ["); //$NON-NLS-1$
            sb.append(rDir);
            sb.append(']');
            // logger.severe(sb.toString());
            throw new ClientCommandGetException(sb.toString(), e);
        }
        // Go to the local directory. If not found, try to create it.
        try
        {
            // The local path has to be absolute, therefore we can check that it
            // exists and it is a directory.
            File localDir = new File(lDir);
            if (localDir.exists() && localDir.isDirectory())
                this.channelSftp.lcd(localDir.getAbsolutePath());
            else
            {
                localDir.mkdirs();
                this.channelSftp.lcd(localDir.getAbsolutePath());
            }
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(384);
            sb.append("Failed to call local directory ["); //$NON-NLS-1$
            sb.append(lDir);
            sb.append(']');
            // logger.severe(sb.toString());
            throw new ClientCommandGetException(sb.toString(), e);
        }
        logger.debug("Current local dir [" + this.channelSftp.lpwd() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        
        try
        {
            logger.debug("Current remote dir [" + this.channelSftp.pwd() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (SftpException e)
        {
            throw new ClientCommandGetException(e);
        }
        try
        {
            SftpProgressMonitor monitor = null;
            // Local filename explicitly specified
            if (rFile != null && lFile != null)
            {
                this.channelSftp.get(rFile, lFile, monitor, mode);
                StringBuilder sb = new StringBuilder(640);
                sb.append("Getting ["); //$NON-NLS-1$
                sb.append(rFile);
                sb.append("] into ["); //$NON-NLS-1$
                sb.append(lDir);
                sb.append("] as ["); //$NON-NLS-1$
                sb.append(lFile);
                sb.append("] successful!"); //$NON-NLS-1$
                logger.info(sb.toString());
                if (this.isGetDestructive())
                {
                    try
                    {
                        deleteRemoteFile(rFile);
                    }
                    catch (ClientException e)
                    {
                        throw new ClientCommandGetException(e);
                    }
                }
            }
            // Local filename will be the same as remote filename
            else if (rFile != null && lFile == null)
            {
                this.channelSftp.get(rFile, rFile, monitor, mode);
                StringBuilder sb = new StringBuilder(640);
                sb.append("Getting ["); //$NON-NLS-1$
                sb.append(rFile);
                sb.append("] into ["); //$NON-NLS-1$
                sb.append(lDir);
                sb.append("] successful!"); //$NON-NLS-1$
                logger.info(sb.toString());
                if (this.isGetDestructive())
                {
                    try
                    {
                        deleteRemoteFile(rFile);
                    }
                    catch (ClientException e)
                    {
                        throw new ClientCommandGetException(e);
                    }
                }
            }
            // In this case, only a source directory was specified, so get
            // everything under the directory
            else
            {
                // Calling an ls on the current directory will return a vector
                // of LsEntry
                String path = this.channelSftp.pwd();
                Vector<LsEntry> v = this.channelSftp.ls(path);
                // Create arraylists of all valid file names and subdirectories
                ArrayList<LsEntry> validFiles = new ArrayList<LsEntry>();
                ArrayList<LsEntry> validSubDirs = new ArrayList<LsEntry>();
                for (LsEntry i : v)
                {
                    if (i.getAttrs().isDir())
                    {
                        String file = new String(i.getFilename());
                        if (file.equals(CURRENT_DIRECTORY) || file.equals(PARENT_DIRECTORY))
                        {
                            logger.debug("Ignoring directory [" + file + "]"); //$NON-NLS-1$//$NON-NLS-2$
                        }
                        else
                        {
                            validSubDirs.add(i);
                        }
                    }
                    else
                    {
                        validFiles.add(i);
                    }
                }
                // Copy the all the files in the current dir first
                for (LsEntry i : validFiles)
                {
                    String file = new String(i.getFilename());
                    this.channelSftp.get(file, file, monitor, mode);
                    logger.info("File to get [" + file + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                    if (this.isGetDestructive())
                    {
                        try
                        {
                            deleteRemoteFile(file);
                        }
                        catch (ClientException e)
                        {
                            throw new ClientCommandGetException(e);
                        }
                    }
                }
                // Recursively call the method for all subdirectories
                for (LsEntry i : validSubDirs)
                {
                    String newRemoteDir = rDir + tfs + i.getFilename();
                    String newLocalDir = lDir + lfs + i.getFilename();
                    get(newRemoteDir, newLocalDir, mode);
                    // Note: Should not enable the following code as default action
                    //if (this.isGetDestructive())
                    //  deleteRemoteFile(newRemoteDir);
                }
            }
        }
        catch (SftpException e)
        {
            String msg = new String("Exception while getting file(s)!"); //$NON-NLS-1$
            throw new ClientCommandGetException(msg, e);
        }
    }

    /**
     * Overloading method for get.
     * 
     * @param rDir The absolute path to the remote (source) directory as
     *            <code>String</code>
     * @param lDir The absolute path to the local (destination) directory as
     *            <code>String</code>
     * @param mode The write mode to be used when writing the file locally.
     * @throws ClientCommandGetException
     */
    public void get(String rDir, String lDir, int mode) throws ClientCommandGetException
    {
        get(rDir, null, lDir, null, mode);
    }

    /**
     * Get the raw Stream
     * 
     * @param entry
     * @return InputStream
     * @throws ClientCommandGetException
     */
    public InputStream getContentAsStream(ClientListEntry entry) throws ClientCommandGetException
    {
        // Construct file path and get the file into an OutputStream
        File srcFile = new File((entry.getUri()).getPath());
        StringBuilder sb = new StringBuilder(384);
        sb.append("Trying to get data from file ["); //$NON-NLS-1$
        sb.append(srcFile.getPath());
        sb.append("] into an InputStream"); //$NON-NLS-1$
        sb.trimToSize();
        logger.info(sb.toString());
        InputStream input = null;
        // Getting the file content
        try
        {
            SftpProgressMonitor monitor = null;
            input = this.channelSftp.get(srcFile.getName(), monitor);
        }
        catch (SftpException e)
        {
            sb = new StringBuilder(384);
            sb.append("Failed to get file ["); //$NON-NLS-1$
            sb.append(srcFile.getName());
            sb.append("] from directory ["); //$NON-NLS-1$
            try
            {
                sb.append(this.channelSftp.pwd());
            }
            catch (SftpException e1)
            {
                throw new ClientCommandGetException(e1);
            }
            sb.append(']');
            throw new ClientCommandGetException(sb.toString(), e);
        }
        return input;
    }

    /**
     * Retrieves a remote file as an BaseFileTransferMappedRecord
     * 
     * @param filePath
     * @return BaseFileTransferMappedRecord
     * @throws ClientCommandGetException
     */
    public BaseFileTransferMappedRecord get(String filePath) throws ClientCommandGetException
    {
        URI uri;
        try
        {
            uri = new URI(filePath);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try
            {
                SftpProgressMonitor monitor = null;
                logger.info("getting file from filepath: [" + filePath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.channelSftp.get(filePath, output, monitor);
            }
            catch (SftpException e)
            {
                throw new ClientCommandGetException("Failed to get file from sftp [" + filePath + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return BaseFileTransferUtils.createBaseFileTransferMappedRecord(uri, output);
        }
        catch (URISyntaxException e)
        {
            throw new ClientCommandGetException("could not create URI from filePath", e); //$NON-NLS-1$
        }
    }

    /**
     * Get a BaseFileTransferMappedRecord
     * 
     * @param entry
     * @return BaseFileTransferMappedRecord
     * @throws ClientCommandGetException
     */
    public BaseFileTransferMappedRecord get(ClientListEntry entry) throws ClientCommandGetException
    {
        // Construct file path and get the file into an BaseFileTransferMappedRecord
        URI uri = entry.getUri();

        File srcFile = new File((uri).getPath());
        StringBuilder sb = new StringBuilder(384);
        sb.append("Getting file ["); //$NON-NLS-1$
        sb.append(srcFile.getPath());
        sb.append("] into an BaseFileTransferMappedRecord"); //$NON-NLS-1$
        sb.trimToSize();
        logger.info(sb.toString());
        // Getting the file content
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = srcFile.getName();
        try
        {
            SftpProgressMonitor monitor = null;
            this.channelSftp.get(fileName, output, monitor);
        }
        catch (SftpException e)
        {
            sb = new StringBuilder(384);
            sb.append("Failed to get file ["); //$NON-NLS-1$
            sb.append(fileName);
            sb.append("] from directory ["); //$NON-NLS-1$
            try
            {
                sb.append(this.channelSftp.pwd());
            }
            catch (SftpException e1)
            {
                throw new ClientCommandGetException(e1);
            }
            sb.append(']');
            throw new ClientCommandGetException(sb.toString(), e);
        }
        BaseFileTransferMappedRecord record = BaseFileTransferUtils.createBaseFileTransferMappedRecord(uri, output);
        return record;
    }

    /**
     * Method to be used in order to return the current directory on the remote
     * host.
     * 
     * @return The <b>absolute
     *         </p>
     *         path up to and including the current working directory.
     * 
     * @throws ClientCommandPwdException If the underlying operation fails
     */
    public String pwd() throws ClientCommandPwdException
    {
        String currentDir = null;
        try
        {
            currentDir = new String(this.channelSftp.pwd());
        }
        catch (Exception e)
        {
            throw new ClientCommandPwdException("Failed to get working directory!", e); //$NON-NLS-1$
        }
        return currentDir;
    }

    /**
     * Method used to call a directory on the remote host.
     * 
     * @param targetPath The directory to call
     * @throws ClientCommandCdException If the underlying operation fails
     *             (i.e. target directory not found)
     */
    public void cd(String targetPath) throws ClientCommandCdException
    {
        try
        {
            this.channelSftp.cd(targetPath);
        }
        catch (Exception e)
        {
            StringBuilder sb = new StringBuilder(384);
            sb.append("Failed to call directory ["); //$NON-NLS-1$
            sb.append(targetPath);
            sb.append("] from ["); //$NON-NLS-1$
            try
            {
                sb.append(this.channelSftp.pwd());
            }
            catch (SftpException e1)
            {
                throw new ClientCommandCdException(e1);
            }
            sb.append(']');
            // logger.error(sb.toString());
            throw new ClientCommandCdException(sb.toString(), e);
        }
        return;
    }

    /**
     * Utility method for <code>mkdir(String newDirPath, boolean force)</code>
     * that defaults the <code>force</code> parameter to <code>true</code>.
     * 
     * @param newPath The new path to create
     * @throws ClientCommandMkdirException
     */
    public void mkdir(String newPath) throws ClientCommandMkdirException
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
     * Method to recursively delete all files and directories under a <b>local</b>
     * directory.
     * 
     * @param dir The <i>local</i> directory to delete
     * @return <code>true</code> if all deletions successful,
     *         <code>false</code> otherwise
     */
    private boolean deleteLocalDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (String child : children)
            {
                boolean success = deleteLocalDir(new File(dir, child));
                if (!success)
                {
                    return false;
                }
            }
        }
        // The parent directory is now empty and we can delete it.
        return dir.delete();
    }

    /**
     * Utility method that checks whether a path exists on the server host.
     * 
     * Note: for non-absolute paths, the path will examined with the user's home
     * directory as root.
     * 
     * @param remotePath path to check on the server host.
     * @return <code>true</code> if path exists, <code>false</code>
     *         otherwise.
     * @throws ClientException when the checking fails
     */
    private boolean remotePathExists(String remotePath) throws ClientException
    {
        // Sanity check on remote path
        boolean exists = false;
        try
        {
            // lstat will fail with exception if no stats can be found for path
            SftpATTRS s = this.channelSftp.stat(remotePath);
            exists = true;
            StringBuilder sb = new StringBuilder(384);
            sb.append("Remote path already exists! ["); //$NON-NLS-1$
            sb.append(s.toString());
            sb.append(']');
            sb.trimToSize();
            logger.debug(sb.toString());
        }
        catch (SftpException e)
        {
            if (e.id == 2 || e.id == 4) // 2 = SSH_FX_NO_SUCH_FILE
            {
                exists = false;
                StringBuilder sb = new StringBuilder(384);
                sb.append("Remote path does not exist! ["); //$NON-NLS-1$
                sb.append(remotePath);
                sb.append(']');
                logger.debug(sb.toString());
            }
            else
            {
                StringBuilder sb = new StringBuilder(384);
                sb.append("Failed to get attributes for remote path ["); //$NON-NLS-1$
                sb.append(remotePath);
                sb.append(']');
                // logger.severe(sb.toString());
                throw new ClientException(sb.toString(), e);
            }
        }
        return exists;
    }

    /**
     * Method used to create a <code>URI</code> object from an absolute path
     * to a target. The absolute path needs be passed in as two parameters: One
     * indicating the absolute path of the parent directory and one indicating
     * the actual file name.
     * 
     * Note:Avoid having any encoding/decoding of the path in here 
     * 
     * TODO: Could add check to see if this is root path or not!
     * 
     * @param absDir
     * @param filename
     * @return URI
     * @throws URISyntaxException
     */
    private URI getURI(String absDir, String filename) throws URISyntaxException
    {
        StringBuilder absolutePath = new StringBuilder(512);
        absolutePath.append(absDir);
        absolutePath.append('/');
        absolutePath.append(filename);
        absolutePath.trimToSize();
        // 12 is length of ";fingerprint=", 128 is a guess for Fingerprint length
        StringBuilder userInfo = new StringBuilder(this.username.length() + 12 + 128);
        userInfo.append(this.username);
        userInfo.append(";fingerprint=");
        userInfo.append(this.session.getHostKey().getFingerPrint(this.jsch));
        userInfo.trimToSize();
        return new URI("sftp", userInfo.toString(), this.remoteHostname, this.remotePort, absolutePath.toString(), null, null);
    }

    /**
     * Utility method used to create a path on the remote host. Any path element
     * that does not exist along the path will be created if the
     * <code>force</code> parameter is set to true. Currently used by the ...
     * method.
     * 
     * @param newPath The path to create on the remote host. This can be either
     * absolute or relative to current working directory. Absolute paths must 
     * start with a '/' character.
     * @param force If true all missing elements along the <code>newPath</code>
     * will be created.
     * 
     * @throws ClientException If the operation fails (i.e. because of
     * permission issues). It is left up the caller method to throw the 
     * appropriate specific exception as this in only used internally. 
     */
    private void createRemotePath(String newPath, boolean force) throws ClientException
    {
        StringBuilder file = new StringBuilder(256);
        // Make a note of the current working directory
        String cwd = null;
        try
        {
            cwd = this.channelSftp.pwd();
        }
        catch (SftpException e)
        {
            throw new ClientException(e);
        }
        // If the newpath starts with a separator, assume a root directory;
        if (newPath.startsWith("/"))
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
                this.channelSftp.cd(pathElement);
            }
            catch (SftpException e)
            {
                if (e.id == 2) // 2 = SSH_FX_NO_SUCH_FILE
                {
                    StringBuilder sb = new StringBuilder(640);
                    sb.append("Path element ["); //$NON-NLS-1$
                    sb.append(pathElement);
                    sb.append("] of ["); //$NON-NLS-1$
                    sb.append(file.toString());
                    sb.append("] does not exist!"); //$NON-NLS-1$
                    if (force)
                    {
                        createFlag = true;
                        logger.info(sb.toString());
                    }
                    else
                    {
                        throw new ClientException(sb.toString(), e);
                    }
                }
                else
                {
                    StringBuilder sb = new StringBuilder(640);
                    sb.append("Failed to navigate to path element ["); //$NON-NLS-1$
                    sb.append(pathElement);
                    sb.append("] of path element ["); //$NON-NLS-1$
                    sb.append(file.toString());
                    sb.append(']');
                    throw new ClientException(sb.toString(), e);
                }
            }
            // By this point we have either cd'ed into the existing directory,
            // or the directory does not exists and the createFlag is set to
            // true, hence try mkdir
            if (createFlag)
            {
                try
                {
                    this.channelSftp.mkdir(pathElement);
                    this.channelSftp.cd(pathElement);
                    StringBuilder sb = new StringBuilder(640);
                    sb.append("Created path element ["); //$NON-NLS-1$
                    sb.append(pathElement);
                    sb.append("] of path ["); //$NON-NLS-1$
                    sb.append(newPath);
                    sb.append(']');
                    logger.info(sb.toString());
                }
                catch (SftpException e)
                {
                    StringBuilder sb = new StringBuilder(640);
                    sb.append("Failed to create & navigate to path element ["); //$NON-NLS-1$
                    sb.append(pathElement);
                    sb.append("] of path ["); //$NON-NLS-1$
                    sb.append(newPath);
                    sb.append(']');
                    // logger.warn(sb.toString());
                    throw new ClientException(sb.toString(), e);
                }
            }
            // Otherwise, reset createFlag and go for next path element
            createFlag = false;
        }
        // Reset the working directory to where we have started from
        try
        {
            this.channelSftp.cd(cwd);
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(640);
            sb.append("Failed to reset working directory to ["); //$NON-NLS-1$
            sb.append(cwd);
            sb.append(']');
            logger.warn(sb.toString(), e);
            throw new ClientException(sb.toString(), e);
        }
    }

    /**
     * Attempts to delete a given file from the remote host. Used by get()
     * (getDestructive) as well as by outside clients
     * 
     * @param filename The absolute path to a file/directory on the remote host.
     * @throws ClientException
     */
    public void deleteRemoteFile(String filename) throws ClientException
    {
        try
        {
            this.channelSftp.rm(filename);
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(384);
            sb.append("Exception while deleting source file or directory ["); //$NON-NLS-1$
            sb.append(filename);
            sb.append(']');
            logger.debug("Exception thrown whilst deleting file or directory [" + e + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            throw new ClientException(sb.toString(), e);
        }
    }

    /**
     * Method used to get a list of all files and directories in the target path
     * including the "." and ".." directories.
     * 
     * @param path The (directory) path to list.
     * @return A <code>ClientListEntry</code> typed <code>List</code>
     * @throws URISyntaxException If a malformed <code>URI</code> is created
     * @throws ClientCommandLsException If the directory listing fails (i.e.
     * path not a directory or path not found)
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
     * List entries on the server given a path and a polarised filter
     * 
     * As of 28/06/2007 - Only used by Test Clients
     * 
     * @param path
     * @param filter
     * @return List of SFTPClientListEntries
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     */
    public List<ClientListEntry> ls(String path, ClientPolarisedFilter filter)
            throws ClientCommandLsException, URISyntaxException
    {
        List<ClientPolarisedFilter> filterList = new ArrayList<ClientPolarisedFilter>(1);
        filterList.add(filter);
        try
        {
            return doList(path, filterList);
        }
        catch (ClientException e)
        {
            throw new ClientCommandLsException(e);
        }
    }

    /**
     * List entries on the server given a path and a filter for the list
     * 
     * As of 28/06/2007 - Only used by Test Clients
     * 
     * @param path
     * @param filterList
     * @return List of ClientListEntries
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     */
    public List<ClientListEntry> ls(String path, List<ClientPolarisedFilter> filterList)
            throws ClientCommandLsException, URISyntaxException
    {
        try
        {
            return doList(path, filterList);
        }
        catch (ClientException e)
        {
            throw new ClientCommandLsException(e);
        }
    }

    /**
     * Method used to get the listing of a remote directory path. When used
     * without any <code>SFTPClientFilter</code>s, this method will return
     * all files and directories in the target path including the "." and ".."
     * directories.
     * 
     * Note: Currently there are no rules defining the order of the
     * <code>List</code> returned.
     * 
     * Suppressed Warning:  We assume that this.channelSftp.ls(path) returns Vector<LsEntry>
     * 
     * @param path The (directory) path to list.
     * @param filters The <code>SFTPClientListEntry</code>
     * @return A List of<code>SFTPClientFilter</code>s to apply
     * @throws URISyntaxException If a malformed <code>URI</code> is created
     * @throws ClientException If the directory listing fails (i.e. path not
     * a directory or path not found)
     */
    @SuppressWarnings("unchecked")
    private List<ClientListEntry> doList(String path, List<ClientPolarisedFilter> filters)
            throws ClientException, URISyntaxException
    {
        List<ClientListEntry> list = null;
        List<ClientListEntry> filteredList = null;
        try
        {
            // Check whether we are dealing with a file or a dir
            SftpATTRS ps = this.channelSftp.stat(path);
            if (ps.isDir())
            {
                // Call the target remote directory.
                String startDir = this.channelSftp.pwd();
                this.channelSftp.cd(path);
                String currentDir = this.channelSftp.pwd();
                logger.debug("Listing directory [" + currentDir + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                Vector<LsEntry> v = this.channelSftp.ls(".");
                list = new ArrayList<ClientListEntry>(v.size());
                // Create a complete list of all files and dirs as bespoke entries
                for (LsEntry lsEntry : v)
                {
                    URI fileUri = this.getURI(currentDir, lsEntry.getFilename());
                    ClientListEntry entry = convertLsEntryToClientListEntry(lsEntry, fileUri);
                    list.add(entry);
                }
                // Return to the calling directory
                this.channelSftp.cd(startDir);
            }
            else
            {
                list = new ArrayList<ClientListEntry>(1);
                int fs = path.lastIndexOf('/');
                String dir = null;// path.substring(0, fs);
                if (path.startsWith(System.getProperty("file.separator"))) //$NON-NLS-1$
                {
                    dir = path.substring(0, fs);
                }
                else
                {
                    // assume relative to whatever path we currently are
                    dir = this.channelSftp.pwd() + path.substring(0, fs);
                }
                String file = path.substring(fs);
                URI fileUri = this.getURI(dir, file);
                ClientListEntry entry = convertSftpATTRSToClientListEntry(ps, null, fileUri, file);
                list.add(entry);
            }
            if (filters != null && filters.size() > 0)
            {
                filteredList = BaseFileTransferUtils.filterList(list, filters);
            }
        }
        catch (SftpException e)
        {
            StringBuilder sb = new StringBuilder(384);
            sb.append("Failed to get listing for directory! ["); //$NON-NLS-1$
            sb.append(path);
            sb.append(']');
            throw new ClientException(sb.toString(), e);
        }
        
        if (filteredList != null)
        {
            return filteredList;
        }
        // Default else
        return list;
    }

    /**
     * Method auxiliary to put() - used to return a list of absolute source file
     * paths for a given <code>File</code>. In the case of directories, the
     * method can be set to descend down sub directories (by setting the
     * <code>recurse</code> parameter to <code>true</code>) while the
     * returned results can also be filtered by a <code>FilenameFilter</code>
     * if need be.
     * 
     * <p>
     * Note: If the underlying file encoding is not UTF-8, the paths are also
     * encoded to UTF-8.
     * 
     * @param srcPath The <code>File</code> to list
     * @param filter If not <code>null</code>, the returned list will be
     * filtered based on this.
     * @param recurse Indicates whether to traverse sub directories of the
     * srcPath
     * @return A list of absolute paths based on the parameters
     */
    private List<String> listAbsoluteSourcePaths(File srcPath, FilenameFilter filter, boolean recurse)
    {
        // Get a reference to the systems file encoding:
        String fileEncoding = System.getProperty("file.encoding");
        // List of files / directories
        List<String> files = new ArrayList<String>();
        // Get files / directories in the directory
        File[] entries = (srcPath.isDirectory()) ? srcPath.listFiles() : new File[] { srcPath };
        // Go over entries
        for (File entry : entries)
        {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(srcPath, entry.getName()))
            {
                URI uri = entry.toURI();
                String path = new String(uri.getPath());
                if (!fileEncoding.equals("UTF-8"))
                {
                    files.add(BaseFileTransferUtils.stringEncoder(path));
                }
            }
            // If the file is a directory and the recurse flag is set, 
            // recurse into the directory
            if (recurse && entry.isDirectory())
            {
                files.addAll(listAbsoluteSourcePaths(entry, filter, recurse));
            }
        }
        // Return the sorted collection of files
        Collections.sort(files);
        return files;
    }

    /**
     * Method auxiliary to <code>put()</code>
     * 
     * @param src
     * @param absSrcPaths
     * @param formatter
     * @return List of relative source paths
     */
    private List<String> listRelativeSourcePaths(File src, List<String> absSrcPaths,
            ClientFilenameFormatter formatter)
    {
        List<String> relSrcPaths = new ArrayList<String>(absSrcPaths.size());
        URI uri = src.toURI();
        for (String asp : absSrcPaths)
        {
            String relSrcPath = new String(asp.substring(uri.getPath().length()));
            String formattedRelSrcPath = null;
            if (formatter != null) formattedRelSrcPath = formatter.translateFilename(relSrcPath);
            relSrcPaths.add((formattedRelSrcPath != null) ? formattedRelSrcPath : relSrcPath);
        }
        return relSrcPaths;
    }

    /**
     * Setter method for the boolean <code>putDestructive</code> class
     * variable. This will indicate whether put functionality will be
     * destructive.
     * 
     * @param putDestructive Boolean value for the <code>putDestructive</code>
     */
    public void setPutDestructive(boolean putDestructive)
    {
        this.putDestructive = putDestructive;
    }

    /**
     * Getter method for the boolean <code>getDestructive</code> class
     * variable.
     * 
     * @return True if the get operation is destructive, false otherwise.
     */
    public boolean isGetDestructive()
    {
        return getDestructive;
    }

    /**
     * Setter method for the boolean <code>getDestructive</code> class
     * variable. This will indicate whether get functionality will be
     * destructive. Get will delete files as long as they are copied.
     * Directories are NOT deleted (for now).
     * 
     * @param getDestructive Boolean value for the <code>getDestructive</code>
     */
    public void setGetDestructive(boolean getDestructive)
    {
        this.getDestructive = getDestructive;
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
        sb.append("SFTP configuration information:"); //$NON-NLS-1$
        sb.append("\nHostname         = ["); //$NON-NLS-1$
        sb.append(remoteHostname);
        sb.append("]\nKnown hosts file = ["); //$NON-NLS-1$
        sb.append(knownHosts.getAbsolutePath());
        sb.append("]\nLocal Host       = ["); //$NON-NLS-1$
        sb.append(localHostname);
        sb.append("]\nRemote Port      = ["); //$NON-NLS-1$
        sb.append(remotePort);
        sb.append("]\nPrivate key file = ["); //$NON-NLS-1$
        sb.append(prvKey.getAbsolutePath());
        sb.append("]\nUsername         = ["); //$NON-NLS-1$
        sb.append(username);
        sb.append("]"); //$NON-NLS-1$
        logger.log(logLevel, sb.toString());
    }

    /**
     * Method used to log current connection status information for the client.
     * 
     * As of 28/06/2007 - Used by Test Clients only
     * 
     * @param logLevel The log level at which to log the information
     */
    public void echoStatus(Level logLevel)
    {
        String s1 = (session == null) ? "null" : "" + session.isConnected(); //$NON-NLS-1$ //$NON-NLS-2$
        String s2 = (channel == null) ? "null" : "" + channel.isConnected(); //$NON-NLS-1$ //$NON-NLS-2$
        String s3 = (channelSftp == null) ? "null" : "" //$NON-NLS-1$//$NON-NLS-2$
                + channelSftp.isConnected();
        StringBuilder sb = new StringBuilder(256);
        sb.append("SFTP status information:"); //$NON-NLS-1$
        sb.append(" \nSession connected?     = ["); //$NON-NLS-1$
        sb.append(s1);
        sb.append("]\nChannel connected?     = ["); //$NON-NLS-1$
        sb.append(s2);
        sb.append("]\nSftpChannel connected? = ["); //$NON-NLS-1$
        sb.append(s3);
        sb.append("]\nIsConnected()          = ["); //$NON-NLS-1$
        sb.append(isConnected());
        sb.append("]"); //$NON-NLS-1$
        logger.log(logLevel, sb.toString());
    }

    public void ensureConnection() throws ResourceException
    {
        if (!isConnected())
        {
            try
            {
                connect();
            }
            catch (ClientConnectionException e1)
            {
                throw new ResourceException(
                    "Failed to ensure that the underlying connection is still open. Likely this was previously open, closed prematurely, and now cannot be reestablished", //$NON-NLS-1$
                    e1);
            }
        }
    }

    /**
     * Utilises the underlying API to return an InputStream as the result of the
     * GET operation
     * 
     * @param filePath
     * @return InputStream
     * 
     * @throws ClientCommandGetException
     */
    public InputStream getAsInputStream(String filePath) throws ClientCommandGetException
    {
        try
        {
            return channelSftp.get(filePath);
        }
        catch (SftpException e)
        {
            throw new ClientCommandGetException(e);
        }
    }

    /**
     * Utilises the underlying API to provide a GET implementation that delivers
     * its result into a supplied OutputStream, resuming at the specified offset
     * of the target file
     * 
     * @param filePath
     * @param outputStream
     * @param resume
     * @param offset
     * @throws ClientCommandGetException
     */
    public void get(String filePath, OutputStream outputStream, int resume, long offset)
            throws ClientCommandGetException
    {
        SftpProgressMonitor monitor = null;
        try
        {
            channelSftp.get(filePath, outputStream, monitor, resume, offset);
        }
        catch (SftpException e)
        {
            throw new ClientCommandGetException(e);
        }

    }

    /**
     * Utilises the underlying API to provide a GET implementation that delivers
     * its result into a supplied OutputStream
     * 
     * @param filePath
     * @param outputStream
     * 
     * @throws ClientCommandGetException
     */
    public void get(String filePath, OutputStream outputStream) throws ClientCommandGetException
    {
        logger.debug("get called with filePath [" + filePath + "] and outputStream [" + outputStream + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        try
        {
            channelSftp.get(filePath, outputStream);
        }
        catch (SftpException e)
        {
            throw new ClientCommandGetException(e);
        }

    }

    /**
     * Performs an SFTP put, resuming at the designated offset
     * 
     * @param fileName
     * 
     * @throws ClientCommandPutException
     * @throws ClientCommandMkdirException
     * @throws ClientCommandLsException
     */
    public void putWithOutputStream(String fileName, InputStream inputStream) throws ClientCommandPutException,
            ClientCommandLsException, ClientCommandMkdirException
    {
        try
        {
            ensureParentsExist(fileName);
            channelSftp.put(inputStream, fileName);
        }
        catch (SftpException e)
        {
            throw new ClientCommandPutException(e);
        }
    }

    /**
     * Deletes a remote directory
     * 
     * @param directoryPath
     * @param recurse 
     * @throws ClientException
     * @throws ClientCommandLsException
     */
    public void deleteRemoteDirectory(String directoryPath, boolean recurse) throws ClientException,
            ClientCommandLsException
    {

        try
        {
            if (recurse)
            {
                try
                {
                    List<ClientListEntry> entryList = ls(directoryPath);
                    for (ClientListEntry entry : entryList)
                    {
                        String filePath = entry.getUri().getPath();
                        if (entry.isDirectory())
                        {
                            if (!filePath.endsWith("."))
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
            this.channelSftp.rmdir(directoryPath);
        }
        catch (SftpException e)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Exception while deleting  directory ["); //$NON-NLS-1$
            sb.append(directoryPath);
            sb.append(']');
            logger.debug("Exception thrown whilst deleting directory [" + e + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            throw new ClientException(sb.toString(), e);
        }
    }

    /**
     * Creates, if necessary all the parents in given file path
     * 
     * @param filePath
     * @throws ClientCommandLsException
     * @throws ClientCommandMkdirException
     */
    private void ensureParentsExist(String filePath) throws ClientCommandLsException,
            ClientCommandMkdirException
    {
        logger.debug("ensureParentsExist called with [" + filePath + "]");  //$NON-NLS-1$//$NON-NLS-2$

        File file = new File(filePath);

        List<File> parents = new ArrayList<File>();
        BaseFileTransferUtils.findParents(file, parents);
        Collections.reverse(parents);

        for (File directory : parents)
        {
            if (!dirExists(directory))
            {
                logger.debug("creating new parent dir [" + directory.getPath() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                mkdir(directory.getPath());
            }
        }
    }

    /**
     * Determines if a remote directory exists
     * 
     * @param directory
     * @return true if this file represents an existent remote directory
     * @throws ClientCommandLsException
     */
    private boolean dirExists(File directory) throws ClientCommandLsException
    {
        boolean dirFound = false;
        String directoryParentPath = null;
        try
        {
            directoryParentPath = channelSftp.pwd();
        }
        catch (SftpException e)
        {
            throw new ClientCommandLsException(e);
        }
        if (directory.getParent() != null)
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

                if (entryFile.getName().equals(directory.getName()))
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
     * Constructing a <code>ClientListEntry</code> object from an
     * <code>LsEntry</code> object. This is a direct map with some formatting
     * changes.
     *
     * @param lsEntry The <code>LsEntry</code> to map to a <code>ClientListEntry</code>
     * @param fileUri The URI of the underlying file for the particular <code>LsEntry</code>
     * @return ClientListEntry
     */
    public ClientListEntry convertLsEntryToClientListEntry(LsEntry lsEntry, URI fileUri)
    {
        SftpATTRS attrs = lsEntry.getAttrs();
        String longName = lsEntry.getLongname();
        String fileName = lsEntry.getFilename();
        return convertSftpATTRSToClientListEntry(attrs, longName, fileUri, fileName);
    }    
    
    /**
     * Constructing a <code>ClientListEntry</code> object from an
     * <code>SftpATTRS</code> object. This is a direct map with some formatting
     * changes.
     *
     * @param attrs The <code>SftpATTRS</code> to map to a <code>ClientEntry</code>
     * @param longName 
     * @param fileUri The URI of the underlying file for the particular <code>LsEntry</code>
     * @param fileName 
     * @return ClientListEntry
     */
    public ClientListEntry convertSftpATTRSToClientListEntry(SftpATTRS attrs, String longName, URI fileUri, String fileName)
    {
        ClientListEntry clientListEntry = new ClientListEntry();
        
        clientListEntry.setUri(fileUri);
        clientListEntry.setName(fileName);
        clientListEntry.setClientId(null);
        clientListEntry.setDtLastAccessed(new Date(((long) attrs.getATime()) * 1000));
        clientListEntry.setDtLastModified(new Date(((long) attrs.getMTime()) * 1000));
        clientListEntry.setSize(attrs.getSize());
        clientListEntry.isDirectory(attrs.isDir());
        clientListEntry.isLink(attrs.isLink());

        if (longName == null)
        {
            clientListEntry.setLongFilename(attrs.toString());
        }
        else
        {
            clientListEntry.setLongFilename(longName);
        }
        
        clientListEntry.setAtime(attrs.getATime());
        clientListEntry.setMtime(attrs.getMTime());
        clientListEntry.setAtimeString(attrs.getAtimeString());
        clientListEntry.setMtimeString(attrs.getMtimeString());
        clientListEntry.setFlags(attrs.getFlags());
        clientListEntry.setGid("" + attrs.getGId());
        clientListEntry.setUid("" + attrs.getUId());
        clientListEntry.setPermissions(attrs.getPermissions());
        clientListEntry.setPermissionsString(attrs.getPermissionsString());

        ArrayList<String> extended;
        String[] a = attrs.getExtended();
        if (a != null && a.length > 0)
        {
            extended = new ArrayList<String>(a.length);
            for (int i = 0; i < a.length; i++)
            {
                extended.add("" + a[i]);
            }
            clientListEntry.setExtended(extended);
        }
        
        return clientListEntry;
    }
}
