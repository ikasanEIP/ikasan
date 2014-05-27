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
package org.ikasan.consumer.jms;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Implementation of an authenticated ConnectionFactory.
 * Use Cases for this are primarily around WebLogic's JMS authentication.
 *  
 * @author Ikasan Development Team
 * @deprecated - replaced with {@link org.ikasan.component.endpoint.jms.AuthenticatedConnectionFactory} in the ikasan-jms-client library
 */
public class AuthenticatedConnectionFactory implements ConnectionFactory
{
    /** class logger */
    private static Logger logger = Logger.getLogger(AuthenticatedConnectionFactory.class);

    /** JMS Connection Factory */
    protected String connectionFactoryName;
    
    /** allow for default properties */
    private Properties properties;
    
    /**
     * Constructor
     * @param connectionFactoryName
     */
    public AuthenticatedConnectionFactory(String connectionFactoryName)
    {
        this.connectionFactoryName = connectionFactoryName;
        if(connectionFactoryName == null)
        {
            throw new IllegalArgumentException("connectionFactoryName cannot be 'null'");
        }
    }

    /**
     * Constructor
     * @param connectionFactoryName
     * @param properties
     */
    public AuthenticatedConnectionFactory(String connectionFactoryName, Properties properties)
    {
        this.connectionFactoryName = connectionFactoryName;
        if(connectionFactoryName == null)
        {
            throw new IllegalArgumentException("connectionFactoryName cannot be 'null'");
        }
        
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see javax.jms.ConnectionFactory#createConnection()
     */
    public Connection createConnection() throws JMSException
    {
        ConnectionFactory connectionFactory = this.getConnectionFactory(this.properties);
        return connectionFactory.createConnection();
    }

    /* (non-Javadoc)
     * @see javax.jms.ConnectionFactory#createConnection(java.lang.String, java.lang.String)
     */
    public Connection createConnection(String username, String password) throws JMSException
    {
        Properties props = getProperties();
        if(this.properties != null)
        {
            props.putAll(this.properties);
        }
        props.put("java.naming.security.principal", username);
        props.put("java.naming.security.credentials", password);

        ConnectionFactory connectionFactory = this.getConnectionFactory(props);
        return connectionFactory.createConnection();
    }

    protected ConnectionFactory getConnectionFactory(Properties properties)
    {
        try
        {
            InitialContext initialContext = (properties == null ? getInitialContext() : getInitialContext(properties));
            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(connectionFactoryName);
            if (connectionFactory == null)
            {
                throw new NamingException("Cannot find connectionFactory " + connectionFactoryName);
            }
            
            return connectionFactory;
        }
        catch (NamingException e)
        {
            throw new EndpointException(e);
        }
    }
    
    /**
     * Factory method (for convenience of testing) for getting the initial context
     * @return
     * @throws NamingException
     */
    protected InitialContext getInitialContext() throws NamingException
    {
        return new InitialContext();
    }

    /**
     * Factory method (for convenience of testing) for getting the initial context
     * @param properties
     * @return
     * @throws NamingException
     */
    protected InitialContext getInitialContext(Properties properties) throws NamingException
    {
        return new InitialContext(properties);
    }

    /**
     * Factory method (for convenience of testing) for getting the properties
     * @return
     */
    protected Properties getProperties()
    {
        return new Properties();
    }
}
