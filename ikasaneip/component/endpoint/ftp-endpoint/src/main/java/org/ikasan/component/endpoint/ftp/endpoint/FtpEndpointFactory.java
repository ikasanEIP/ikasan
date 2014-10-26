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
package org.ikasan.component.endpoint.ftp.endpoint;

import org.apache.log4j.Logger;
import org.ikasan.component.endpoint.ftp.common.*;
import org.ikasan.component.endpoint.ftp.consumer.FtpConsumerConfiguration;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Ftp Endpoint Contract which if going to be pulled by FtpConsumer on scheduled basis.
 *
 * @author Ikasan Development Team.
 */
public class FtpEndpointFactory {

    /**
     * class logger
     */
    private static Logger logger = Logger.getLogger(FtpEndpointFactory.class);

    /**
     * InitialisesFtpEndpointImpl with FtpConsumerConfiguration properties and
     * creates the FileTransferProtocolClient  and opens the connection.
     *
     * @throws org.ikasan.component.endpoint.ftp.common.ClientInitialisationException Exception thrown by connector
     */
    public FtpEndpoint createFtpEndpoint(FtpConsumerConfiguration configuration) throws ClientInitialisationException, ClientConnectionException {

        logger.debug("Contractor FTPClient \n"
                + "active   [" + configuration.getActive() + "]\n"
                + "host     [" + configuration.getRemoteHost() + "]\n"
                + "maxretry [" + configuration.getMaxRetryAttempts() + "]\n"
                + "password [" + configuration.getPassword() + "]\n"
                + "port     [" + configuration.getRemotePort() + "]\n"
                + "user     [" + configuration.getUsername() + "]");
        // Active
        boolean active = configuration.getActive();
        // Hostname
        String remoteHostname = null;
        if (configuration.getRemoteHost() != null) {
            remoteHostname = configuration.getRemoteHost();
        } else {
            throw new ClientInitialisationException("Remote hostname is null."); //$NON-NLS-1$
        }
        // Max retry attempts (Integer unboxes to int)
        int maxRetryAttempts;
        if (configuration.getMaxRetryAttempts() != null) {
            maxRetryAttempts = configuration.getMaxRetryAttempts();
        } else {
            throw new ClientInitialisationException("max retry attempts is null"); //$NON-NLS-1$
        }
        // Password
        String password;
        if (configuration.getPassword() != null) {
            password = configuration.getPassword();
        } else {
            throw new ClientInitialisationException("password is null"); //$NON-NLS-1$
        }
        // Port (Integer unboxes to int)
        int remotePort;
        if (configuration.getRemotePort() != null) {
            remotePort = configuration.getRemotePort();
        } else {
            throw new ClientInitialisationException("Remote port is null"); //$NON-NLS-1$
        }
        // Username
        String username = null;
        if (configuration.getUsername() != null) {
            username = configuration.getUsername();
        } else {
            throw new ClientInitialisationException("username is null"); //$NON-NLS-1$
        }
        String localHostname = null;

        String systemKey = configuration.getSystemKey();
        Integer connectionTimeout = configuration.getConnectionTimeout();
        Integer dataTimeout = configuration.getDataTimeout();
        Integer soTimeout = configuration.getSocketTimeout();

        // Create a FileTransferProtocolClient
        FileTransferProtocolClient ftpClient = new FileTransferProtocolClient(active, remoteHostname, localHostname, maxRetryAttempts, password, remotePort, username, systemKey,
                connectionTimeout, dataTimeout, soTimeout);

        ftpClient.validateConstructorArgs();

        // attempts to open the connection
        ftpClient.connect();
        // attempts to login
        ftpClient.login();

        FtpEndpoint ftpEndpoint = new FtpEndpointImpl(ftpClient,
                configuration.getClientID(),
                configuration.getSourceDirectory(),
                configuration.getFilenamePattern(),
                configuration.getMinAge(),
                configuration.getFilterDuplicates(),
                configuration.getFilterOnFilename(),
                configuration.getFilterOnLastModifiedDate());
        return ftpEndpoint;
    }

}
