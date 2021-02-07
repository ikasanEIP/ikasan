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

public class ActiveMqHelper
{
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