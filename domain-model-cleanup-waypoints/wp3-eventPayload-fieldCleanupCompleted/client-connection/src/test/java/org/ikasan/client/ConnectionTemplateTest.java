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
package org.ikasan.client;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * @author Ikasan Development Team
 * 
 */
public class ConnectionTemplateTest extends TestCase
{
    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /**
     * Mocked <code>Connection</code>
     */
    Connection connection = mockery.mock(Connection.class);
    /**
     * Mocked <code>ConnectionSpec</code>
     */
    ConnectionSpec connectionSpec = mockery.mock(ConnectionSpec.class);
    /**
     * Mocked <code>Connection</code>
     */
    ConnectionFactory connectionFactory = mockery
        .mock(ConnectionFactory.class);
    /**
     * Mocked <code>ResourceException</code>
     */
    ResourceException resourceException = classMockery
        .mock(ResourceException.class);
    /**
     * Mocked <code>Throwable</code>
     */
    Throwable throwable = classMockery.mock(Throwable.class);

    /**
     * Mocked <code>ConnectionCallback</code> 
     */
    ConnectionCallback connectionCallback = mockery.mock(ConnectionCallback.class);

    
    
    
    
    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#ConnectionTemplate(javax.resource.cci.ConnectionFactory, javax.resource.cci.ConnectionSpec)}.
     */
    public void testConnectionTemplate()
    {
        new ConnectionTemplate(connectionFactory, connectionSpec);
    }
    

    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#ConnectionTemplate(javax.resource.cci.ConnectionFactory, javax.resource.cci.ConnectionSpec)}.
     */
    public void testConnectionTemplate_withNullConnectionFactoryThrowsIllegalArgumentException()
    {
        try
        {
            new ConnectionTemplate(null, connectionSpec);
            fail("IllegalArgumentException should have been thrown for null ConnectionFactory");
        }
        catch (Throwable th)
        {
            assertTrue("throwable should have been IllegalArgumentException",
                th instanceof IllegalArgumentException);
        }
    }

    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#execute(org.ikasan.client.ConnectionCallback)}.
     * @throws ResourceException Exception thrown by Connector
     */
    public void testExecute_withNullConnectionSpecClosesConnectionFinally() throws ResourceException
    {
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection();
                will(returnValue(connection));
                one(connectionCallback).doInConnection(connection);
                one(connection).close();
            }
        });
        ConnectionTemplate connectionTemplate = new ConnectionTemplate(connectionFactory, null);
        connectionTemplate.execute(connectionCallback);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#execute(org.ikasan.client.ConnectionCallback)}.
     * @throws ResourceException Exception thrown by Connector
     */
    public void testExecute_withConnectionSpecClosesConnectionFinally() throws ResourceException
    {
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(connectionSpec);
                will(returnValue(connection));
                one(connectionCallback).doInConnection(connection);
                one(connection).close();
            }
        });
        ConnectionTemplate connectionTemplate = new ConnectionTemplate(connectionFactory, connectionSpec);
        connectionTemplate.execute(connectionCallback);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#execute(org.ikasan.client.ConnectionCallback)}.
     * @throws ResourceException Exception thrown by Connector
     */
    public void testExecute_whereConnctionThrowsResourceExceptionClosesConnectionFinally() throws ResourceException
    {

        final ResourceException thisResourceException = new ResourceException("testException");
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(connectionSpec);
                will(returnValue(connection));
                one(connectionCallback).doInConnection(connection);
                will(throwException(thisResourceException));
                one(connection).close();
            }
        });
        ConnectionTemplate connectionTemplate = new ConnectionTemplate(connectionFactory, connectionSpec);
        
        ResourceException caughtResourceException =null;
        try{
            connectionTemplate.execute(connectionCallback);
            fail("ResourceException should have been thrown");
        } catch(ResourceException resException){
            caughtResourceException = resException;
        }
        assertEquals("exception should be that thrown by the Connection", thisResourceException, caughtResourceException);

        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#execute(org.ikasan.client.ConnectionCallback)}.
     * @throws ResourceException Exception thrown by Connector
     */
    public void testExecute_whereConnctionThrowsRuntimeExceptionClosesConnectionFinally() throws ResourceException
    {

        final RuntimeException runtimeException = new RuntimeException("testException");
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(connectionSpec);
                will(returnValue(connection));
                one(connectionCallback).doInConnection(connection);
                will(throwException(runtimeException));
                one(connection).close();
            }
        });
        ConnectionTemplate connectionTemplate = new ConnectionTemplate(connectionFactory, connectionSpec);
        
        Throwable caughtThrowable =null;
        try{
            connectionTemplate.execute(connectionCallback);
            fail("RuntimeException should have been thrown");
        } catch(Throwable th){
            caughtThrowable = th;
        }
        assertEquals("exception should be that thrown by the Connection", runtimeException, caughtThrowable);

        mockery.assertIsSatisfied();
    }

    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#closeConnection(javax.resource.cci.Connection)}.
     * 
     * @throws ResourceException Exception thrown by Connector
     */
    public void testCloseConnection_closesCleanly() throws ResourceException
    {
        mockery.checking(new Expectations()
        {
            {
                one(connection).close();
            }
        });
        ConnectionTemplate.closeConnection(connection);
        mockery.assertIsSatisfied();
    }

    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#closeConnection(javax.resource.cci.Connection)}.
     * 
     * @throws ResourceException Exception thrown by Connector
     */
    public void testCloseConnection_suppressesResourceExceptions()
            throws ResourceException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(resourceException).fillInStackTrace();
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(connection).close();
                will(throwException((resourceException)));
            }
        });
        ConnectionTemplate.closeConnection(connection);
        mockery.assertIsSatisfied();
    }

    /**
     * Test method for
     * {@link org.ikasan.client.ConnectionTemplate#closeConnection(javax.resource.cci.Connection)}.
     * 
     * @throws ResourceException Exception thrown by Connector
     */
    public void testCloseConnection_suppressesOtherThrowables()
            throws ResourceException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(throwable).fillInStackTrace();
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(connection).close();
                will(throwException((throwable)));
            }
        });
        ConnectionTemplate.closeConnection(connection);
        mockery.assertIsSatisfied();
    }
}
