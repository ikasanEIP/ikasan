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
