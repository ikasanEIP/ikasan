/* 
 * $Id: PayloadPublisherPluginTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/plugins/PayloadPublisherPluginTest.java $
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
package org.ikasan.framework.plugins;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.framework.payload.service.PayloadPublisher;
import org.ikasan.framework.plugins.PayloadPublisherPlugin;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * JUnit test class for PayloadPublisherPlugin
 * @author Ikasan Development Team
 */
public class PayloadPublisherPluginTest extends TestCase
{
    /**
     * Interface Mockery
     */
    private Mockery mockery = new Mockery();
    /**
     * Mockery for classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test invoking the plugin
     * @throws PluginInvocationException
     * @throws ResourceException 
     */
    public void testInvoke() throws PluginInvocationException,
            ResourceException
    {
        final PayloadPublisher payloadPublisher = mockery
            .mock(PayloadPublisher.class);
        final Payload payload = mockery.mock(Payload.class);
        PayloadPublisherPlugin payloadPublisherPlugin = new PayloadPublisherPlugin(
            payloadPublisher);
        mockery.checking(new Expectations()
        {
            {
                one(payloadPublisher).publish(payload);
            }
        });
        payloadPublisherPlugin.invoke(payload);
    }

    /**
     * A mocked ResourceException
     */
    ResourceException resourceException = classMockery
        .mock(ResourceException.class);

    /**
     * Test invoking the plugin
     * @throws ResourceException 
     */
    public void testInvoke_throwsPluginInvocationExceptionForResourceException()
            throws ResourceException
    {
        final PayloadPublisher payloadPublisher = mockery
            .mock(PayloadPublisher.class);
        final Payload payload = mockery.mock(Payload.class);
        PayloadPublisherPlugin payloadPublisherPlugin = new PayloadPublisherPlugin(
            payloadPublisher);
        classMockery.checking(new Expectations()
        {
            {
                one(resourceException).fillInStackTrace();
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(payloadPublisher).publish(payload);
                will(throwException(resourceException));
            }
        });
        try
        {
            payloadPublisherPlugin.invoke(payload);
            fail("exception should have been thrown");
        }
        catch (Throwable th)
        {
            assertTrue("Should have thrown a PluginInvocationException",
                th instanceof PluginInvocationException);
            assertEquals(
                "Underlying cause should have been the ResourceException thrown by the PayloadPublisher",
                resourceException, th.getCause());
        }
        mockery.assertIsSatisfied();
        classMockery.assertIsSatisfied();
    }
}
