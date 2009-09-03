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
package org.ikasan.framework.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.payload.service.PayloadProvider;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * JUnit test class for PayloadProvider plugin
 * @author Ikasan Development Team
 */
public class PayloadProviderPluginTest extends TestCase
{
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
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    /**
     * Mock <code>PayloadProvider</code>
     */
    PayloadProvider payloadProvider = mockery
        .mock(PayloadProvider.class);
    /**
     * Mock the event
     */
    Event event = classMockery.mock(Event.class);
    /**
     * A mocked ResourceException
     */
    ResourceException resourceException = classMockery
        .mock(ResourceException.class);

    /**
     * Test method for invoke when provider has no payloads
     * 
     * @throws PluginInvocationException
     * @throws ResourceException 
     */
    public void testInvoke_doesNotSetPayloadsWhenProviderHasNone()
            throws PluginInvocationException, ResourceException
    {
        PayloadProviderPlugin payloadProviderPlugin = new PayloadProviderPlugin(
            payloadProvider);
        mockery.checking(new Expectations()
        {
            {
                one(payloadProvider).getNextRelatedPayloads();
                will(returnValue(null));
            }
        });
        payloadProviderPlugin.invoke(null);
    }

    /**
     * Test setting related payloads
     * 
     * @throws PluginInvocationException
     * @throws ResourceException 
     */
    public void testInvoke_setsRelatedPayloadOnEventAsSourcedFromProvider()
            throws PluginInvocationException, ResourceException
    {
        final List<Payload> relatedPayloads = new ArrayList<Payload>();
        mockery.checking(new Expectations()
        {
            {
                one(payloadProvider).getNextRelatedPayloads();
                will(returnValue(relatedPayloads));
            }
        });
        PayloadProviderPlugin payloadProviderPlugin = new PayloadProviderPlugin(
            payloadProvider);
        classMockery.checking(new Expectations()
        {
            {
                // not worried about the flow/componentGroupName/sourceSystem at
                // this point
                one(event).setPayloads(relatedPayloads);
            }
        });
        payloadProviderPlugin.invoke(event);
    }

    /**
     * Tests some business logic
     * 
     * @throws ResourceException 
     */
    public void testInvoke_throwsPayloadInvoationExceptionForResourceException()
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
                one(payloadProvider).getNextRelatedPayloads();
                will(throwException(resourceException));
            }
        });
        PayloadProviderPlugin payloadProviderPlugin = new PayloadProviderPlugin(
            payloadProvider);

        try{

        payloadProviderPlugin.invoke(event);
        fail("exception should have been thrown");
        }

        catch(Throwable th){
            assertTrue("Should have thrown a PluginInvocationException", th instanceof PluginInvocationException);
            assertEquals("Underlying cause should have been the ResourceException thrown by the PayloadProvider", resourceException, th.getCause());
        }
        mockery.assertIsSatisfied();
        classMockery.assertIsSatisfied();
    }
}
