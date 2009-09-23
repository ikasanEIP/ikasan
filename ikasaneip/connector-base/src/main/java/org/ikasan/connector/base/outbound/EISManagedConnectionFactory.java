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
package org.ikasan.connector.base.outbound;

import javax.resource.*;
import javax.resource.spi.*;
import javax.security.auth.*;

import org.apache.log4j.Logger;

import org.ikasan.common.CommonXMLParser;
import org.ikasan.connector.ConnectorContext;
import org.ikasan.connector.ConnectorEnvironment;
import org.ikasan.connector.ResourceLoader;
import org.ikasan.connector.ConnectorXMLParser;
import org.ikasan.connector.ConnectorXMLTransformer;
import org.ikasan.connector.ConnectorXSLTransformer;

import java.util.*;
import java.io.*;

/**
 * This is the abstract factory class which extends the ManagedConnectionFactory
 * for obtaining real physical connections to the associated EIS.
 * 
 * This class is central to the creation of ConnectionFactories and
 * ManagedConnections.
 * 
 * This (and extensions thereof) is the first class that the application server
 * will instantiate.
 * 
 * All the other classes in the resource adapter are instantiated, directly or
 * indirectly, by this one.
 * 
 * @author Ikasan Development Team
 */
public abstract class EISManagedConnectionFactory implements ManagedConnectionFactory, Serializable
{
    /** Logger */
    private static Logger logger = Logger.getLogger(EISManagedConnectionFactory.class);

    /** default serial version uid */
    private static final long serialVersionUID = 1L;

    /** Print writer */
    private PrintWriter writer = null;

    /** All sessions require a clientID */
    protected String clientID;

    /** non transactional session factory */
    protected String sessionFactoryName;

    /** local transaction session factory */
    protected String localSessionFactoryName;

    /** XA transaction session factory */
    protected String xaSessionFactoryName;

    /** Connector Context */
    protected ConnectorContext connectorContext = ResourceLoader.getInstance().newContext();

    /** Connector XML Parser */
    protected ConnectorXMLParser xmlParser = ResourceLoader.getInstance().newXMLParser();

    /** Connector XML Transformer */
    protected ConnectorXMLTransformer xmltransformer = ResourceLoader.getInstance().newXMLTransformer();

    /** Connector XSL Transformer */
    protected ConnectorXSLTransformer xsltransformer = ResourceLoader.getInstance().newXSLTransformer();

    /** Connector Environment */
    protected ConnectorEnvironment connectorEnv = ResourceLoader.getInstance().newEnvironment();

    /**
     * The application server calls this method to create a new EIS-specific
     * ConnectionFactory.
     * 
     * The extending class must implement this for the specific EIS.
     * 
     * @param connectionManager - The connection manager to use for this connection factory
     * @return ConnectionFactory - The connection factory
     * @throws ResourceException - Exception if creation fails
     */
    public abstract Object createConnectionFactory(final ConnectionManager connectionManager) throws ResourceException;

    /**
     * The application server or stand-alone client calls this method to create
     * a new EIS-specific ConnectionFactory.
     * 
     * The extending class must implement this for the specific EIS.
     */
    public abstract Object createConnectionFactory() throws ResourceException;

    /**
     * The application server calls this method to create a new physical (real)
     * connection to the EIS.
     * 
     * The extending class must implement this for the specific EIS.
     */
    public abstract ManagedConnection createManagedConnection(final Subject subject, final ConnectionRequestInfo cri) throws ResourceException;

    /**
     * hashCode() uses specific instance properties to generate a hashcode which
     * can be used to uniquely identify this class instance.
     * 
     * The extending class must implement this for the specific EIS.
     */
    @Override
    public abstract int hashCode();

    /**
     * equals() is used to determine whether two instances of this class are set
     * with the same configuration properties.
     * 
     * The extending class must implement this for the specific EIS.
     */
    @Override
    public abstract boolean equals(final Object object);

    /**
     * This method is called by the application server when the client asks for
     * a new connection. The application server passes in a Set of all the
     * active virtual connections, and this object must pick one that is
     * currently handling a physical connection that can be shared to support
     * the new client request. Typically this sharing will be allowed if the
     * security attributes and properties of the new request match an existing
     * physical connection.
     * 
     * If nothing is available, the method must return null, so that the
     * application server knows it has to create a new physical connection.
     */
    public abstract ManagedConnection matchManagedConnections(final Set connections, final Subject subject, final ConnectionRequestInfo cri);

    /**
     * Standard getter for the logWriter
     * 
     * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()
     */
    public PrintWriter getLogWriter()
    {
        logger.debug("Getting logWriter..."); //$NON-NLS-1$
        return this.writer;
    }

    /**
     * Standard setter for the logWriter
     * 
     * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter writer)
    {
        this.writer = writer;
        logger.debug("Setting logWriter..."); //$NON-NLS-1$
    }

    /**
     * Standard getter for the connectorContext
     * 
     * @return ConnectorContext
     */
    public ConnectorContext getConnectorContext()
    {
        logger.debug("Getting connectorContext..."); //$NON-NLS-1$
        return this.connectorContext;
    }

    /**
     * Standard getter for the connectorEnvironment
     * 
     * @return ConnectorEnvironment
     */
    public ConnectorEnvironment getConnectorEnv()
    {
        logger.debug("Getting connectorEnv [" + this.connectorEnv + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.connectorEnv;
    }

    /**
     * Standard getter for the connectorXMLParser
     * 
     * @return ConnectorXMLParser
     */
    public CommonXMLParser getXmlParser()
    {
        logger.debug("Getting xmlparser [" + this.xmlParser + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.xmlParser;
    }

    /**
     * Standard getter for the connectorXMLTransformer
     * 
     * @return ConnectorXMLTransformer
     */
    public ConnectorXMLTransformer getXmlTransformer()
    {
        logger.debug("Getting xmltransformer [" + this.xmltransformer + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.xmltransformer;
    }

    /**
     * Standard getter for the connectorXSLTransformer
     * 
     * @return ConnectorXSLTransformer
     */
    public ConnectorXSLTransformer getXslTransformer()
    {
        logger.debug("Getting xsltransformer [" + this.xsltransformer + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.xsltransformer;
    }

    /**
     * Getter for clientID
     * 
     * @return String - clientID
     */
    public String getClientID()
    {
        logger.debug("Getting clientID [" + this.clientID + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.clientID;
    }

    /**
     * Setter for clientID.
     * 
     * @param clientID - The client id to set
     */
    public void setClientID(String clientID)
    {
        this.clientID = clientID;
        logger.debug("Setting clientID [" + this.clientID + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for the sessionFactoryName
     * 
     * @return the sessionFactoryName
     */
    public String getSessionFactoryName()
    {
        logger.debug("Getting dataSource [" + this.sessionFactoryName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.sessionFactoryName;
    }

    /**
     * Setter for the sessionFactoryName
     * 
     * @param sessionFactoryName the sessionFactoryName to set
     */
    public void setSessionFactoryName(String sessionFactoryName)
    {
        this.sessionFactoryName = sessionFactoryName;
        logger.debug("Setting sessionFactoryName [" + this.sessionFactoryName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for the localSessionFactoryName
     * 
     * @return the localSessionFactoryName
     */
    public String getLocalSessionFactoryName()
    {
        logger.debug("Getting localSessionFactoryName [" + this.localSessionFactoryName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.localSessionFactoryName;
    }

    /**
     * Setter for the localSessionFactoryName
     * 
     * @param localSessionFactoryName the localSessionFactoryName to set
     */
    public void setLocalSessionFactoryName(String localSessionFactoryName)
    {
        this.localSessionFactoryName = localSessionFactoryName;
        logger.debug("Setting localSessionFactoryName [" + this.localSessionFactoryName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for the xaSessionFactoryName
     * 
     * @return the xaSessionFactoryName
     */
    public String getXASessionFactoryName()
    {
        logger.debug("Getting xaSessionFactoryName [" + this.xaSessionFactoryName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.xaSessionFactoryName;
    }

    /**
     * Setter for the xaSessionFactoryName
     * 
     * @param xaSessionFactoryName the xaSessionFactoryName to set
     */
    public void setXASessionFactoryName(String xaSessionFactoryName)
    {
        this.xaSessionFactoryName = xaSessionFactoryName;
        logger.debug("Setting xaSessionFactoryName [" + this.xaSessionFactoryName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Validate standard properties required for all connections.
     * 
     * @throws InvalidPropertyException - Exception if hte proprty is invalid
     */
    protected void validateConnectionProperties() throws InvalidPropertyException
    {
        if (this.clientID == null)
        {
            throw new InvalidPropertyException("ClientID property cannot be null."); //$NON-NLS-1$
        }
        if (this.sessionFactoryName == null)
        {
            logger.warn("Connector property 'sessionFactoryName' is [" //$NON-NLS-1$
                    + this.sessionFactoryName + "]. " //$NON-NLS-1$
                    + "You will not be able to use a non-transactional data source!"); //$NON-NLS-1$
        }
        if (this.localSessionFactoryName == null)
        {
            logger.warn("Connector property 'localSessionFactoryName' is [" //$NON-NLS-1$
                    + this.localSessionFactoryName + "]. " //$NON-NLS-1$
                    + "You will not be able to use a local transactional data source!"); //$NON-NLS-1$
        }
        if (this.xaSessionFactoryName == null)
        {
            logger.warn("Connector property 'xaSessionFactoryName' is [" //$NON-NLS-1$
                    + this.xaSessionFactoryName + "]. " //$NON-NLS-1$
                    + "You will not be able to use an XA transactional data source!"); //$NON-NLS-1$
        }
    }
}
