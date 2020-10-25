package org.ikasan.testharness.flow.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Allows for capturing of test messages on JMS endpoints
 */
public class MessageListenerVerifier<KEY, VALUE>
{
    private Map<String, Object> props = new HashMap<>();

    private ExecutorService executor = null;

    private ConsumerFactory<KEY, VALUE> consumerFactory;

    private org.apache.kafka.clients.consumer.Consumer<KEY, VALUE> consumer;

    protected List<Object> captureResults = Collections.synchronizedList(new ArrayList<>());

    private boolean isRunning = false;

    private String topicName;


    public MessageListenerVerifier(final String brokerUrl, final String topicName, final Class keyDeserializer,
                                   final Class valueDeserializer, final String clientId)
    {
        this.topicName = topicName;

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
    }

    public List<Object> getCaptureResults() {
        return captureResults;
    }

    public void start()
    {
        this.consumerFactory = new DefaultKafkaConsumerFactory<>(this.props);
        this.consumer = consumerFactory.createConsumer();

        TopicPartition topicPartition = new TopicPartition(topicName, 0);
        this.consumer.assign(List.of(topicPartition));
        this.consumer.seek(topicPartition, 0);


        this.executor = Executors.newSingleThreadExecutor();
        this.isRunning = true;
        executor.submit(() -> {
            while(this.isRunning) {
                ConsumerRecords<KEY, VALUE> messages = consumer.poll(Duration.ofMillis(100));

                if (!messages.isEmpty()) {
                    messages.forEach(consumerRecord -> {
                        this.captureResults.add(consumerRecord);
                    });
                }
            }
        });
    }

    public void stop()
    {
        this.isRunning = false;
    }

}
