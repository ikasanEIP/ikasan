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

import javax.jms.Session;

import junit.framework.Assert;

import org.junit.Test;
import org.quartz.SchedulerException;

/**
 * This test class supports the <code>GenericJmsConsumerConfiguration</code> class.
 * 
 * @author Ikasan Development Team
 */
public class GenericJmsConsumerConfigurationTest
{
    /**
     * Test configuration mutators.
     */
    @Test
    public void test_mutators() throws SchedulerException
    {
        GenericJmsConsumerConfiguration consumerConfiguration = new GenericJmsConsumerConfiguration();
        Assert.assertNull("initial clientId should be null", consumerConfiguration.getClientId());
        consumerConfiguration.setClientId("clientId");
        Assert.assertEquals("clientId should be populated with 'clientId'", "clientId", consumerConfiguration.getClientId());

        Assert.assertNull("initial subscriberId should be null", consumerConfiguration.getSubscriberId());
        consumerConfiguration.setSubscriberId("subscriberId");
        Assert.assertEquals("subscriberId should be populated with 'subscriberId'", "subscriberId", consumerConfiguration.getSubscriberId());

        Assert.assertNull("initial username should be null", consumerConfiguration.getUsername());
        consumerConfiguration.setUsername("username");
        Assert.assertEquals("username should be populated with 'username'", "username", consumerConfiguration.getUsername());

        Assert.assertNull("initial password should be null", consumerConfiguration.getPassword());
        consumerConfiguration.setPassword("password");
        Assert.assertEquals("password should be populated with 'password'", "password", consumerConfiguration.getPassword());

        Assert.assertEquals("initial ackowledgement should be " + Session.AUTO_ACKNOWLEDGE, Session.AUTO_ACKNOWLEDGE, consumerConfiguration.getAcknowledgement());
        consumerConfiguration.setAcknowledgement(Session.CLIENT_ACKNOWLEDGE);
        Assert.assertEquals("ackowledgement should be populated with " + Session.CLIENT_ACKNOWLEDGE, Session.CLIENT_ACKNOWLEDGE, consumerConfiguration.getAcknowledgement());

        Assert.assertTrue("initial isDurable should be true", consumerConfiguration.isDurable());
        consumerConfiguration.setDurable(false);
        Assert.assertFalse("isDurable should be populated false", consumerConfiguration.isDurable());

        Assert.assertFalse("initial isTransacted should be false", consumerConfiguration.isTransacted());
        consumerConfiguration.setTransacted(true);
        Assert.assertTrue("isTransacted should be populated true", consumerConfiguration.isTransacted());
    }

}
