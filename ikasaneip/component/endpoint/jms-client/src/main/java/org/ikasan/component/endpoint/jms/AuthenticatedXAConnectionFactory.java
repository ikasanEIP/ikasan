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

import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Implementation of an authenticated XAConnectionFactory.
 * Use Cases for this are primarily around WebLogic's JMS authentication or Spring wrapped connectionFactories.
 *  
 * @author Ikasan Development Team
 */
public class AuthenticatedXAConnectionFactory implements XAConnectionFactory, ConnectionFactory
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(AuthenticatedXAConnectionFactory.class);

    /** JMS XA Connection Factory instance */
    protected XAConnectionFactory xaConnectionFactory;

    /** JMS Connection Factory name - used if connectionFactory instance not passed */
    protected String connectionFactoryName;

    /** allow for default properties */
    private Properties properties;

    /** principal */
    private String username;

    /** credential */
    private String password;

    public void setXAConnectionFactory(XAConnectionFactory xaConnectionFactory)
    {
        this.xaConnectionFactory = xaConnectionFactory;
    }

    public void setXAConnectionFactoryName(String connectionFactoryName)
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

    public XAConnection createXAConnection() throws JMSException
    {
        if(this.xaConnectionFactory != null)
        {
            return xaConnectionFactory.createXAConnection(this.username, this.password);
        }

        XAConnectionFactory xaConnectionFactory = this.getXAConnectionFactory(this.properties);
        return xaConnectionFactory.createXAConnection();
    }

    public XAConnection createXAConnection(String username, String password) throws JMSException
    {
        if(this.xaConnectionFactory != null)
        {
            return xaConnectionFactory.createXAConnection(username, password);
        }

        Properties props = getProperties();
        if(this.properties != null)
        {
            props.putAll(this.properties);
        }
        props.put("java.naming.security.principal", username);
        props.put("java.naming.security.credentials", password);

        XAConnectionFactory connectionFactory = this.getXAConnectionFactory(props);
        return connectionFactory.createXAConnection();
    }

    protected XAConnectionFactory getXAConnectionFactory(Properties properties)
    {
        try
        {
            InitialContext initialContext = (properties == null ? getInitialContext() : getInitialContext(properties));
            XAConnectionFactory connectionFactory = (XAConnectionFactory) initialContext.lookup(connectionFactoryName);
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

    @Override
    public Connection createConnection() throws JMSException
    {
        return createXAConnection();
    }

    @Override
    public Connection createConnection(String username, String password) throws JMSException
    {
        return createXAConnection(username, password);
    }
}
