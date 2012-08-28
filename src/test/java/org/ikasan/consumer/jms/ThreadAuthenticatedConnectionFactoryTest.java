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

import org.ikasan.spec.component.endpoint.EndpointException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * This test class supports the <code>ThreadAuthenticatedConnectionFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ThreadAuthenticatedConnectionFactoryTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** Mock properties */
    final Properties properties = mockery.mock(Properties.class, "mockProperties");

    /** Mock initialContext */
    final InitialContext initialContext = mockery.mock(InitialContext.class, "mockInitialContext");

    /** Mock connectionFactory */
    final ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");

    /** Mock connection */
    final Connection connection = mockery.mock(Connection.class, "mockConnection");

    /**
     * Test failed constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_due_to_null_connectionFactoryName()
    {
        new ThreadAuthenticatedConnectionFactory(null);
    }

    /**
     * Test creation of a connection with default properties.
     * @throws JMSException 
     * @throws NamingException 
     */
    @Test
    public void test_createConnection_default_properties() throws JMSException, NamingException
    {
        mockery.checking(new Expectations()
        {
            {
                // calculate and set the midPrice
                exactly(1).of(initialContext).lookup("connectionFactoryName");
                will(returnValue(connectionFactory));
                exactly(1).of(connectionFactory).createConnection();
                will(returnValue(connection));
            }
        });

        ConnectionFactory connectionFactory = new StubbedThreadAuthenticatedConnectionFactory("connectionFactoryName", properties);
        connectionFactory.createConnection();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Test creation of a connection with username and password.
     * @throws JMSException 
     * @throws NamingException 
     */
    @Test
    public void test_createConnection_with_username_password() throws JMSException, NamingException
    {
        mockery.checking(new Expectations()
        {
            {
                // calculate and set the midPrice
                exactly(1).of(initialContext).lookup("connectionFactoryName");
                will(returnValue(connectionFactory));
                exactly(1).of(connectionFactory).createConnection();
                will(returnValue(connection));
                exactly(1).of(properties).put("java.naming.security.principal", "username");
                exactly(1).of(properties).put("java.naming.security.credentials", "password");
            }
        });

        ConnectionFactory connectionFactory = new StubbedThreadAuthenticatedConnectionFactory("connectionFactoryName");
        connectionFactory.createConnection("username", "password");
        
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed creation of a connection with default properties.
     * @throws JMSException 
     * @throws NamingException 
     */
    @Test(expected = EndpointException.class)
    public void test_failed_createConnection_default_properties() throws JMSException, NamingException
    {
        mockery.checking(new Expectations()
        {
            {
                // calculate and set the midPrice
                exactly(1).of(initialContext).lookup("connectionFactoryName");
                will(returnValue(null));
            }
        });

        ConnectionFactory connectionFactory = new StubbedThreadAuthenticatedConnectionFactory("connectionFactoryName", properties);
        connectionFactory.createConnection();
        
        mockery.assertIsSatisfied();
    }

    private class StubbedThreadAuthenticatedConnectionFactory extends ThreadAuthenticatedConnectionFactory
    {
        /**
         * @param connectionFactoryName
         */
        public StubbedThreadAuthenticatedConnectionFactory(String connectionFactoryName)
        {
            super(connectionFactoryName);
        }
        
        /**
         * @param connectionFactoryName
         */
        public StubbedThreadAuthenticatedConnectionFactory(String connectionFactoryName, Properties properties)
        {
            super(connectionFactoryName, properties);
        }
        
        protected InitialContext getInitialContext(Properties properties) throws NamingException
        {
            return initialContext;
        }
        
        protected Properties getProperties()
        {
            return properties;
        }
        
    }
}
