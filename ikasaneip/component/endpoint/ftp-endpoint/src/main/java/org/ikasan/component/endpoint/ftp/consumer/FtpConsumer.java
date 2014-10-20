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
package org.ikasan.component.endpoint.ftp.consumer;

import org.apache.log4j.Logger;

import org.ikasan.component.endpoint.ftp.common.ClientConnectionException;
import org.ikasan.component.endpoint.ftp.common.ClientInitialisationException;
import org.ikasan.component.endpoint.ftp.common.FileTransferProtocolClient;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedEventIdentifierException;
import org.ikasan.spec.flow.FlowEvent;
import java.util.Date;


/**
 * Implementation of a generic client FTP consumer.
 *
 * @author Ikasan Development Team
 */
public class FtpConsumer
        implements Consumer<EventListener<?>, EventFactory>,
        EndpointListener<Object>,
        ConfiguredResource<FtpConsumerConfiguration> {
    /**
     * class logger
     */
    private static Logger logger = Logger.getLogger(FtpConsumer.class);

    /**
     * consumer event factory
     */
    protected EventFactory<FlowEvent<?, ?>> flowEventFactory;

    /**
     * consumer event listener
     */
    protected EventListener eventListener;

    /**
     * configured resource id
     */
    protected String configuredResourceId;

    /**
     * Ftp consumer configuration - default to vanilla instance
     */
    protected FtpConsumerConfiguration configuration = new FtpConsumerConfiguration();

    /**
     * Common library used by both inbound and outbound connectors
     */
    private FileTransferProtocolClient ftpClient;

    /**
     * Default constructor
     */
    public FtpConsumer() {
        // nothing to do with the default constructor
    }

    /**
     * Constructor
     *
     * @param configuration
     */
    public FtpConsumer(FtpConsumerConfiguration configuration) {

        this.configuration = configuration;
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be 'null'");
        }
    }

    public void setEventFactory(EventFactory flowEventFactory) {
        this.flowEventFactory = flowEventFactory;
    }

    /**
     * Start the underlying JMS
     */
    public void start() {
        try {
            createFTPClient();
        } catch (ClientInitialisationException e) {
            e.printStackTrace();
        } catch (ClientConnectionException e) {
            e.printStackTrace();
        }


    }

    /**
     * Stop the underlying JMS
     */
    public void stop() {

        closeSession();

    }

    /**
     * TODO - find a better way to ascertain if underlying JMS is running?
     * Is the underlying JMS actively running
     *
     * @return boolean
     */
    public boolean isRunning() {
        return true;
    }

    /**
     * Set the consumer event listener
     *
     * @param eventListener
     */
    public void setListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }


    /**
     * Callback method from the underlying JMS tech.
     * On invocation this method creates a flowEvent from the tech specific
     * message and invokes the event listener.
     */
    public void onMessage(Object message) {
        if (this.eventListener == null) {
            throw new RuntimeException("No active eventListeners registered!");
        }

        try {
            FlowEvent<?, ?> flowEvent = flowEventFactory.newEvent(new Date().getTime(), message);
            this.eventListener.invoke(flowEvent);
        } catch (ManagedEventIdentifierException e) {
            this.eventListener.invoke(e);
        }
    }

    /**
     * Callback method from the JMS connector for exception reporting.
     *
     * @param jmsException
     */
    public void onException(Throwable jmsException) {
        this.eventListener.invoke(jmsException);
    }

    public FtpConsumerConfiguration getConfiguration() {
        return this.configuration;
    }

    public String getConfiguredResourceId() {
        return this.configuredResourceId;
    }

    public void setConfiguration(FtpConsumerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.Consumer#getEventFactory()
     */
    public EventFactory getEventFactory() {
        return this.flowEventFactory;
    }

    /**
     * Creates the FileTransferProtocolClient based off the properties from the
     * ConnectionRequestInfo, and opens the connection
     *
     * @throws ClientInitialisationException Exception thrown by connector
     */
    private void createFTPClient() throws ClientInitialisationException, ClientConnectionException {
        logger.debug("Called createFTPClient \n"
                + "active   [" + this.configuration.getActive() + "]\n"
                + "host     [" + this.configuration.getRemoteHost() + "]\n"
                + "maxretry [" + this.configuration.getMaxRetryAttempts() + "]\n"
                + "password [" + this.configuration.getPassword() + "]\n"
                + "port     [" + this.configuration.getRemotePort() + "]\n"
                + "user     [" + this.configuration.getUsername() + "]");
        // Active
        boolean active = this.configuration.getActive();
        // Hostname
        String remoteHostname = null;
        if (this.configuration.getRemoteHost() != null) {
            remoteHostname = this.configuration.getRemoteHost();
        } else {
            throw new ClientInitialisationException("Remote hostname is null."); //$NON-NLS-1$
        }
        // Max retry attempts (Integer unboxes to int)
        int maxRetryAttempts;
        if (this.configuration.getMaxRetryAttempts() != null) {
            maxRetryAttempts = this.configuration.getMaxRetryAttempts();
        } else {
            throw new ClientInitialisationException("max retry attempts is null"); //$NON-NLS-1$
        }
        // Password
        String password;
        if (this.configuration.getPassword() != null) {
            password = this.configuration.getPassword();
        } else {
            throw new ClientInitialisationException("password is null"); //$NON-NLS-1$
        }
        // Port (Integer unboxes to int)
        int remotePort;
        if (this.configuration.getRemotePort() != null) {
            remotePort = this.configuration.getRemotePort();
        } else {
            throw new ClientInitialisationException("Remote port is null"); //$NON-NLS-1$
        }
        // Username
        String username = null;
        if (this.configuration.getUsername() != null) {
            username = this.configuration.getUsername();
        } else {
            throw new ClientInitialisationException("username is null"); //$NON-NLS-1$
        }
        String localHostname = null;

        String systemKey = this.configuration.getSystemKey();
        Integer connectionTimeout = this.configuration.getConnectionTimeout();
        Integer dataTimeout = this.configuration.getDataTimeout();
        Integer soTimeout = this.configuration.getSocketTimeout();

        // Create a FileTransferProtocolClient
        this.ftpClient = new FileTransferProtocolClient(active, remoteHostname, localHostname, maxRetryAttempts, password, remotePort, username, systemKey,
                connectionTimeout, dataTimeout, soTimeout);

        this.ftpClient.validateConstructorArgs();

        // attempts to open the connection
        ftpClient.connect();
        // attempts to login
        ftpClient.login();

    }


    protected void closeSession() {
        if (this.ftpClient == null) {
            logger.debug("FTPClient is null.  Closing Session aborted."); //$NON-NLS-1$
        } else {
            if (this.ftpClient.isConnected()) {
                logger.debug("Closing FTP connection!"); //$NON-NLS-1$
                this.ftpClient.disconnect();
                logger.debug("Disconnected from FTP host."); //$NON-NLS-1$
            } else {
                logger.info("Client was already disconnected.  Closing Session aborted."); //$NON-NLS-1$
            }
        }
    }
}
