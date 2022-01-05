/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */

package org.ikasan.testharness.flow.jms;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.MessageStore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Used to fully clear contents of destinations (queues AND topics) down between tests. Useful if using the same
 * spring context between tests
 */
public class ActiveMqHelper
{
    public void shutdownBroker(){
        System.out.println("Shutdown Broker called -  will shutdown any brokers still running");
        Map<String, BrokerService> brokers = BrokerRegistry.getInstance().getBrokers();
        try
        {
            for (BrokerService brokerService : brokers.values())
            {
                System.out.println("Waiting for broker " + brokerService.getBrokerName() + " to be stopped");
                brokerService.stop();
                brokerService.waitUntilStopped();
                System.out.println("Broker " + brokerService.getBrokerName() + " is stopped, check = "
                    + brokerService.isStopped());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void removeAllMessages()
    {
        Map<String, BrokerService> brokers = BrokerRegistry.getInstance().getBrokers();
        try
        {
            for (BrokerService brokerService : brokers.values())
            {
                Broker broker = brokerService.getBroker();
                new ActiveMQBrokerExtension(broker).clearAllMessages();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private class ActiveMQBrokerExtension
    {
        private final Broker broker;

        public ActiveMQBrokerExtension(Broker broker)
        {
            this.broker = broker;
        }

        public void clearAllMessages() throws Exception
        {
            Map<ActiveMQDestination, Destination> destinationMap = broker.getDestinationMap();
            for (Destination destination : destinationMap.values())
            {
                ActiveMQDestination activeMQDestination = destination.getActiveMQDestination();
                if (activeMQDestination.isTopic())
                {
                    clearAllMessages((Topic) destination);
                }
                else if (activeMQDestination.isQueue())
                {
                    clearAllMessages((Queue) destination);
                }
            }
        }

        private void clearAllMessages(Topic topic) throws IOException
        {
            List<Subscription> consumers = topic.getConsumers();
            for (Subscription consumer : consumers)
            {
                ConnectionContext consumerContext = consumer.getContext();
                MessageStore messageStore = topic.getMessageStore();
                messageStore.removeAllMessages(consumerContext);
            }
        }

        private void clearAllMessages(Queue queue) throws Exception
        {
            queue.purge();
        }
    }
}