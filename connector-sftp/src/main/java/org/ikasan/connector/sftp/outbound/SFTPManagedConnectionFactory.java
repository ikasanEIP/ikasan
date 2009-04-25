/*
 * $Id: SFTPManagedConnectionFactory.java 16794 2009-04-24 13:27:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-sftp/src/main/java/org/ikasan/connector/sftp/outbound/SFTPManagedConnectionFactory.java $
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

import java.util.Iterator;
import java.util.Set;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;

import org.ikasan.common.CommonEnvironment;
import org.ikasan.connector.base.command.HibernateTransactionalResourceCommandDAO;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalImpl;
import org.ikasan.connector.base.outbound.EISManagedConnectionFactory;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class is the factory class for obtaining physical connections to the
 * SFTP EIS. The attributes are set by the default attributes in the resource
 * adapter's deployment descriptor (ra.xml) and can be overridden by client
 * supplied properties (CRI).
 * 
 * Each connection produced from this factory is a handle to the actual physical
 * connection (ManagedConnection) to the underlying EIS.
 * 
 * On start up, the Application server first of all calls all of the setter
 * methods (populating them with the values from the ra.xml file) and then calls
 * createConnectionFactory(ConnectionManager)
 * 
 * NOTE: Each <config-property-name> element in ra.xml must have a matching
 * private variable name in this class
 * 
 * NOTE: Defaults of null are dealt with by calling classes
 * 
 * TODO Max Retry attempts is here because our 3rd party library needs to have
 * the value set for when openSession is called on the SFTPManagedConnection. We
 * need to extract that at some stage
 * 
 * TODO When everything is happy, reduced some logging back to debug level.
 * 
 * @author Ikasan Development Team 
 */
public class SFTPManagedConnectionFactory extends EISManagedConnectionFactory
{

    /** Whether we clean up the journal after a complete or not */
    private boolean cleanupJournalOnComplete = false;
    
    /** The remote SFTP host name */
    private String remoteHostname = null;

    /** The local host name */
    private String localHostname = null;

    /** The known hosts file name (fully qualified path) */
    private String knownHostsFilename = null;
    
    /** Maximum retry attempts, can only be set by the CRI, not the ra.xml */
    private Integer maxRetryAttempts = null;
    
    /** The remote SFTP port */
    private Integer remotePort = null;

    /** The private key file name (fully qualified path) */
    private String privateKeyFilename = null;

    /** Connection timeout in milliseconds */
    private Integer connectionTimeout;

    /** The user name */
    private String username = null;

    /** authentication order */
    private String preferredAuthentications = null;

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;
    
    /** The logger instance */
    private static Logger logger = Logger.getLogger(SFTPManagedConnectionFactory.class);

    /** Journal for logging activity of this connector */
    private TransactionJournal transactionJournal = null;

    /** Spring context for resolving beans */
    private ApplicationContext context = new ClassPathXmlApplicationContext("base-config.xml");

    /** Key for the Hibernate session factory supplied by Spring */
    private static final String NO_TX_BASE_FILE_TRANSFER_HIBERNATE_SESSION_FACTORY = "noTx-BaseFileTransferHibernateSessionFactory";

    /**
     * Create the connection factory with no connection manager, e.g. This is
     * the version called when not invoked by the Application Server
     */
    @Override
    public Object createConnectionFactory()
    {
        logger.debug("Called createConnectionFactory()"); //$NON-NLS-1$
        return new SFTPConnectionFactory(this, null);
    }

    /**
     * This version of createConnectionFactory is invoked by the Application
     * Server by passing its own implemented version of connection manager.
     */
    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager)
    {
        logger.debug("Called createConnectionFactory(connectionManager)");
        return new SFTPConnectionFactory(this, connectionManager);
    }

    /**
     * This can be called in two ways, but is initiated by a client need for a
     * Connection.
     * 
     * In our case SFTPConnectionManager's allocateConnection calls this method.
     * (although the Application Server's ConnectionManager can also in theory
     * call this)
     */
    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cri)
            throws ResourceException
    {
        logger.info("Called createManagedConnection"); //$NON-NLS-1$

        // Set the connection properties if they exist
        if (cri != null)
        {
            this.setConnectionProperties(cri);
        }

        // Create the new Managed Connection
        SFTPManagedConnection sftpManagedConnection = new SFTPManagedConnection(this);

        sftpManagedConnection.setTransactionJournal(getTransactionJournal());

        // Open a session on the managed connection
        sftpManagedConnection.openSession();
        // Return the managed connection (with an open session)
        return sftpManagedConnection;
    }

    /**
     * This method is called by the application server when the client asks for
     * a new connection. The application server passes in a Set of all the
     * active managed connections, and this object must pick one that is
     * currently handling a physical connection that can be shared to support
     * the new client request. Typically this sharing will be allowed if the
     * security attributes and properties of the new request match an existing
     * physical connection.
     * 
     * If nothing is available, the method must return null, so that the
     * application server knows it has to create a new physical connection.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ManagedConnection matchManagedConnections(Set connections, Subject subject, ConnectionRequestInfo info)
    {
        logger.info("Called matchManagedConnection()");
        int size = connections.size();
        logger.info("Number of connections considered = [" + size + "].");
        Iterator<?> it = connections.iterator();
        SFTPConnectionRequestInfo scri = (SFTPConnectionRequestInfo) info;
        while (it.hasNext())
        {
            Object obj = it.next();
            logger.debug("Considering object " + obj.getClass().getName());
            if (obj instanceof SFTPManagedConnection)
            {
                logger.debug("Object is a SFTPManagedConnection instance.");
                SFTPManagedConnection smc = (SFTPManagedConnection) obj;
                SFTPManagedConnectionFactory smcf = smc.getManagedConnectionFactory();
                // TODO We may also want to check that the smcf is equal to 'this'.
                if (smcf.compareSCRI(scri))
                {
                    logger.debug("Found matched Connection.");
                    
                    // This should never occur if the track-connection-by-tx property is set in your connection factory ds file
                    if (smc.transactionInProgress())
                    {
                        logger.error("Managed Connection already involved in transaction.");
                    }
                    return smc;
                }
            }
        }
        logger.info("No matched Connection for object.");
        return null;
    }

    /**
     * Generate a hash code, this is used by the application server as part of
     * its management of the connection pool
     */
    @Override
    public int hashCode()
    {
        logger.debug("Called hashCode()"); //$NON-NLS-1$
        int hashCode = this.remoteHostname.hashCode() +
            this.remotePort.hashCode() +
            this.username.hashCode() +
            this.knownHostsFilename.hashCode() +
            this.privateKeyFilename.hashCode() +
            this.clientID.hashCode();
        logger.info("HashCode = [" + hashCode + "].");
        return  hashCode;
    }

    /**
     * SFTPManagedConnetionFactory specific equality implementation. Together
     * with hashCode method, it is used by the Application Server to structure
     * the connection pool (lifted from JCA Spec section 6.5.3.2).
     * 
     * Must always return true, otherwise JBoss throws ResourceException.
     */
    @Override
    public boolean equals(Object object)
    {
        logger.debug("Called equals"); //$NON-NLS-1$
        // Valid object check
        if (object == null)
        {
            logger.debug("Object is null. Returning [false]."); //$NON-NLS-1$
            return false;
        }

        if (object instanceof SFTPManagedConnectionFactory)
        {
            logger.info("Object is a SFTPManagedConnectionFactory"); //$NON-NLS-1$
            SFTPManagedConnectionFactory smcf = (SFTPManagedConnectionFactory) object;
            return compareSMCF(smcf);
        }
        // default else
        logger.debug("Object is not valid, returning [false]."); //$NON-NLS-1$
        return false;
    }

    /**
     * Return true if the SCRI matches this connection
     * 
     * @param scri Incoming <code>ConnectionRequestInfo</code> instance from application server.
     * @return true if the <code>scri</code> matches this connection
     */
    private boolean compareSCRI(SFTPConnectionRequestInfo scri)
    {
        if (this.remoteHostname.equalsIgnoreCase(scri.getRemoteHostname()) && 
            this.remotePort.equals(scri.getRemotePort()) && 
            this.username.equals(scri.getUsername()) &&
            this.knownHostsFilename.equals(scri.getKnownHostsFilename()) &&
            this.privateKeyFilename.equals(scri.getPrivateKeyFilename()) &&
            this.clientID.equals(scri.getClientID()))
        {
            logger.info("Current connection properties matches CRI. Returning [true]."); //$NON-NLS-1$
            return true;
        }
        // Default else
        logger.info("Current connection properties do not match CRI. Returning [false]."); //$NON-NLS-1$
        return false;
    }

    /**
     * Return true if the SMCF matches this connection
     * 
     * @param smcf Incoming <code>ManagedConnectionFactory</code> instance from application server.
     * @return true if the <code>smcf</code> matches this connection
     */
    private boolean compareSMCF(SFTPManagedConnectionFactory smcf)
    {
        if (this.remoteHostname == null || 
            this.remotePort == null || 
            this.username == null ||
            this.knownHostsFilename == null ||
            this.privateKeyFilename == null ||
            this.clientID == null)
        {
            logger.warn("One of the mandatory managed connection factory variables is null."); //$NON-NLS-1$
            logger.warn("Hostname = [" + this.remoteHostname + "]"); 
            logger.warn("Port = [" + this.remotePort + "]");
            logger.warn("Username = [" + this.username +"]"); 
            logger.warn("KnonwHostFileName = [" + this.knownHostsFilename + "]");
            logger.warn("PrivateKeyFileName = [" + this.privateKeyFilename + "]");
            logger.warn("ClientID = [" + this.clientID + "].");
            return false;
        }

        // Connection specific properties check
        if (this.remoteHostname.equalsIgnoreCase(smcf.remoteHostname) && 
            this.remotePort == smcf.remotePort && 
            this.username.equals(smcf.username) &&
            this.knownHostsFilename.equals(smcf.knownHostsFilename) &&
            this.privateKeyFilename.equals(smcf.privateKeyFilename) &&
            this.clientID.equals(smcf.clientID))
        {
            logger.info("Object is equal. Returning [true]."); //$NON-NLS-1$
            return true;
        }
        // Default else
        logger.info("Object is not equal. Returning [false]."); //$NON-NLS-1$
        return false;
    }

    /**
     * Determine the connection request info provided to the connection creation
     * (for overriding the ra.xml).
     * 
     * We deliberately do not use setters here as the setters are called
     * directly by the Application Server and we want to be able to trace that
     * independently
     * 
     * @param cri <code>ConnectionRequestInfo</code> used to override the ra.xml.
     * @throws ResourceException Thrown if the received <code>cri</code> is not an instance
     * of <code>SFTPConnectionRequestInfo</code>.
     */
    private void setConnectionProperties(ConnectionRequestInfo cri) throws ResourceException
    {
        try
        {
            logger.debug("CRI was not null, so we override ra.xml values."); //$NON-NLS-1$
            SFTPConnectionRequestInfo scri = (SFTPConnectionRequestInfo) cri;
            logger.info("CRI \n" + scri);
            // Client ID sits on the EISManagedConnectionFactory
            if (scri.getClientID() != null)
            {
                logger.debug("CRI setting clientID to: [" + scri.getClientID() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.clientID = scri.getClientID();
            }
            if (scri.getRemoteHostname() != null)
            {
                logger.debug("CRI setting hostname to: [" + scri.getRemoteHostname() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.remoteHostname = scri.getRemoteHostname();
            }
            if (scri.getKnownHostsFilename() != null)
            {
                logger.debug("CRI setting knownHostsFilename to: [" + scri.getKnownHostsFilename() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.knownHostsFilename = scri.getKnownHostsFilename();
            }
            if (scri.getRemotePort() != null)
            {
                logger.debug("CRI setting port to: [" + scri.getRemotePort() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.remotePort = scri.getRemotePort();
            }
            if (scri.getPrivateKeyFilename() != null)
            {
                logger.debug("CRI setting privateKeyFilename to: [" + scri.getPrivateKeyFilename() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.privateKeyFilename = scri.getPrivateKeyFilename();
            }
            if (scri.getUsername() != null)
            {
                logger.debug("CRI setting username to: [" + scri.getUsername() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.username = scri.getUsername();
            }
            if (scri.getMaxRetryAttempts() != null)
            {
                logger.debug("CRI setting maxRetryAttempts to: [" + scri.getMaxRetryAttempts() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.maxRetryAttempts = scri.getMaxRetryAttempts();
            }
            if (scri.cleanupJournalOnComplete() != null)
            {
                logger.debug(" setting cleanup journal on complete to : [" + scri.cleanupJournalOnComplete() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                this.cleanupJournalOnComplete = scri.cleanupJournalOnComplete().booleanValue();
            }
            if (scri.getPreferredAuthentications() != null)
            {
                logger.debug("Setting authentication order to: [" + scri.getPreferredAuthentications() +"].");
                this.preferredAuthentications = scri.getPreferredAuthentications();
            }
            if (scri.getConnectionTimeout() != null)
            {
                logger.debug("Setting connection timeout to: [" + scri.getConnectionTimeout() +"].");
                this.connectionTimeout = scri.getConnectionTimeout();
            }
        }
        catch (ClassCastException e)
        {
            throw new ResourceException("ConnectionRequestInfo is not a valid "
                    + "instance for SFTP connectivity.", e);
        }
    }

    /**
     * Lazily instantiates the TransactionJournal
     * 
     * @return TransactionJournal
     */
    protected TransactionJournal getTransactionJournal()
    {

        if (transactionJournal == null)
        {
            SessionFactory sessionFactroy = (SessionFactory) context
                .getBean(NO_TX_BASE_FILE_TRANSFER_HIBERNATE_SESSION_FACTORY);

            TransactionalResourceCommandDAO dao = new HibernateTransactionalResourceCommandDAO(sessionFactroy);
            transactionJournal = new TransactionJournalImpl(dao, clientID, context);

        }
        return transactionJournal;
    }

    // ////////////////////////////////////////////////////////////////
    // Getters/Setters, Setters are initially called by the App Server
    // ////////////////////////////////////////////////////////////////

    /**
     * Get the remote host name
     * 
     * @return the remote host name
     */
    public String getRemoteHostname()
    {
        return remoteHostname;
    }

    /**
     * Set the host name
     * 
     * @param remoteHostname The host name to connect to.
     */
    public void setRemoteHostname(String remoteHostname)
    {
        logger.debug("ra.xml setting hostname to: [" + remoteHostname + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.remoteHostname = remoteHostname;
    }

    /**
     * Get the local host name
     * @return local host name
     */
    public String getLocalHostname()
    {
        return localHostname;
    }

    /**
     * Set the local host name
     * @param rtLocalHost
     */
    public void setLocalHostname(String rtLocalHost)
    {
        CommonEnvironment env = (CommonEnvironment)this.context.getBean("env");
        String localHost = env.expandEnvVar(rtLocalHost);
        logger.info("Setting localhost to [" + localHost + "].");
        if (localHost != null && localHost.length() > 0)
        {
            this.localHostname = localHost;
        }
    }

    /**
     * Get the known hosts file name
     * 
     * @return the known hosts file name
     */
    public String getKnownHostsFilename()
    {
        return knownHostsFilename;
    }

    /**
     * Set the known hosts file name
     * 
     * @param knownHostsFilename The known hosts file path.
     */
    public void setKnownHostsFilename(String knownHostsFilename)
    {
        logger.debug("ra.xml setting knownHostsFilename to: [" + knownHostsFilename + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.knownHostsFilename = knownHostsFilename;
    }

    /**
     * Get the remote port
     * 
     * @return remote port
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * Set the remote port
     * 
     * @param remotePort The port number to connect to.
     */
    public void setRemotePort(Integer remotePort)
    {
        logger.debug("ra.xml setting port to: [" + remotePort + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.remotePort = remotePort;
    }

    /**
     * Get the private key file name
     * 
     * @return the private key file name
     */
    public String getPrivateKeyFilename()
    {
        return privateKeyFilename;
    }

    /**
     * Set the private key file name
     * 
     * @param privateKeyFilename The private key.
     */
    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        logger.debug("ra.xml setting privateKeyFilename to: [" + privateKeyFilename + "]");
        this.privateKeyFilename = privateKeyFilename;
    }

    /**
     * Get the user name
     * 
     * @return the user name
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Set the user name
     * 
     * @param username The user name used to log on to EIS.
     */
    public void setUsername(String username)
    {
        logger.debug("ra.xml setting username to: [" + username + "]");
        this.username = username;
    }

    /**
     * Get the max retry attempts, as open session requires it. There is no
     * corresponding setter as we only want this to be a client property, not a
     * ra.xml property
     * 
     * @return maxRetryAttempts
     */
    public Integer getMaxRetryAttempts()
    {
        return maxRetryAttempts;
    }

    /**
     * Returns whether or not we clean up the journal on complete. There is no
     * corresponding setter as we only want this to be a client property, not a
     * ra.xml property
     * 
     * @return true if we clean up the journal on complete
     */
    public boolean isCleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    /**
     * Sets the authentication order
     * 
     * @param preferredAuthentications 
     */
    public void setPreferredAuthentications(String preferredAuthentications)
    {
        this.preferredAuthentications = preferredAuthentications;
    }

    /**
     * Get the authentication order
     * @return String
     */
    public String getPreferredAuthentications()
    {
        return this.preferredAuthentications;
    }

    /**
     * Set the connection timeout
     * @param connectionTimeout
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get socket connection time out.
     * @return Integer
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }
}
