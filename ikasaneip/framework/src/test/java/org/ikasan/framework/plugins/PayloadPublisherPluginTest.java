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
