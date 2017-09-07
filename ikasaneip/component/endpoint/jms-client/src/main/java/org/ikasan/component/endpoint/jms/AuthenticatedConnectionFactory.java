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
package org.ikasan.component.endpoint.jms;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.component.endpoint.EndpointException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Implementation of an authenticated ConnectionFactory.
 * Use Cases for this are primarily around WebLogic's JMS authentication or Spring wrapped connectionFactories.
 *  
 * @author Ikasan Development Team
 */
public class AuthenticatedConnectionFactory implements ConnectionFactory
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(AuthenticatedConnectionFactory.class);

    /** JMS Connection Factory instance */
    protected ConnectionFactory connectionFactory;

    /** JMS Connection Factory name - used if connectionFactory instance not passed */
    protected String connectionFactoryName;
    
    /** allow for default properties */
    private Properties properties;

    /** principal */
    private String username;

    /** credential */
    private String password;

    public void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    public void setConnectionFactoryName(String connectionFactoryName)
    {
        this.connectionFactoryName = connectionFactoryName;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    /* (non-Javadoc)
         * @see javax.jms.ConnectionFactory#createConnection()
         */
    public Connection createConnection() throws JMSException
    {
        if(this.connectionFactory != null)
        {
            return connectionFactory.createConnection(this.username, this.password);
        }

        ConnectionFactory connectionFactory = this.getConnectionFactory(this.properties);
        return connectionFactory.createConnection();
    }

    /* (non-Javadoc)
     * @see javax.jms.ConnectionFactory#createConnection(java.lang.String, java.lang.String)
     */
    public Connection createConnection(String username, String password) throws JMSException
    {
        if(this.connectionFactory != null)
        {
            return connectionFactory.createConnection(username, password);
        }

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
     * @throws javax.naming.NamingException
     */
    protected InitialContext getInitialContext() throws NamingException
    {
        return new InitialContext();
    }

    /**
     * Factory method (for convenience of testing) for getting the initial context
     * @param properties
     * @return
     * @throws javax.naming.NamingException
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
