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

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;

/**
 * Implementation of an authenticated ConnectionFactory pinned to the 
 * executing thread. The connectionFactory has to be accessed on the fly
 * as the executing thread is not known until we receive a message.
 * Use Cases for this are primarily around WebLogic's JMS authentication.
 *  
 * @author Ikasan Development Team
 */
public class ThreadAuthenticatedConnectionFactory implements ConnectionFactory
{
    /** class logger */
    private static Logger logger = Logger.getLogger(ThreadAuthenticatedConnectionFactory.class);

    /** JMS Connection Factory */
    protected String connectionFactoryName;
    
    /** stash the connectionFactory for the executing thread */
    private ThreadLocal<ConnectionFactory> threadLocal = new ThreadLocal<ConnectionFactory>();

    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public ThreadAuthenticatedConnectionFactory(String connectionFactoryName)
    {
        this.connectionFactoryName = connectionFactoryName;
        if(connectionFactoryName == null)
        {
            throw new IllegalArgumentException("connectionFactoryName cannot be 'null'");
        }
    }

    /* (non-Javadoc)
     * @see javax.jms.ConnectionFactory#createConnection()
     */
    public Connection createConnection() throws JMSException
    {
        ConnectionFactory pinnedConnectionFactory = this.threadLocal.get();
        if(pinnedConnectionFactory == null)
        {
            throw new JMSException("No Authenticated connectionFactory available on this thread! Create one using createConnection(username,password)");
        }
        
        return pinnedConnectionFactory.createConnection();
    }

    /* (non-Javadoc)
     * @see javax.jms.ConnectionFactory#createConnection(java.lang.String, java.lang.String)
     */
    public Connection createConnection(String username, String password) throws JMSException
    {
        ConnectionFactory pinnedConnectionFactory = this.threadLocal.get();
        if(pinnedConnectionFactory == null)
        {
            Properties props = new Properties();
            props.put("java.naming.security.principal", username);
            props.put("java.naming.security.credentials", password);

            try
            {
                InitialContext initialContext = new InitialContext(props);
                pinnedConnectionFactory = (ConnectionFactory) initialContext.lookup(connectionFactoryName);
                if (pinnedConnectionFactory == null)
                {
                    throw new NamingException("Cannot find connectionFactory " + connectionFactoryName);
                }
            }
            catch (NamingException e)
            {
                throw new EndpointException(e);
            }

            this.threadLocal.set(pinnedConnectionFactory);
        }

        return pinnedConnectionFactory.createConnection();
    }
}
