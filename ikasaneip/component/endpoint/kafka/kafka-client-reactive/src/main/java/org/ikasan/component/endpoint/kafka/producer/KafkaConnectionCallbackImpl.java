package org.ikasan.component.endpoint.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaConnectionCallbackImpl<IDENTIFIER, PAYLOAD> implements KafkaConnectionCallback {
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(KafkaConnectionCallbackImpl.class);

    private FlowEvent<IDENTIFIER, PAYLOAD> payload;
    private org.apache.kafka.clients.producer.Producer<Object, PAYLOAD> producer;
    private Serialiser<PAYLOAD, byte[]> serialiser;


    public KafkaConnectionCallbackImpl(FlowEvent<IDENTIFIER, PAYLOAD> payload
        , org.apache.kafka.clients.producer.Producer<Object, PAYLOAD> producer
        , Serialiser<PAYLOAD, byte[]> serialiser) {
        this.payload = payload;
        this.producer = producer;
        this.serialiser = serialiser;
    }

    @Override
    public void execute() throws Throwable {
        AtomicReference<Throwable> throwable = new AtomicReference<>();

//        producer.send(new ProducerRecord<>(this.configuration.getTopicName(), this.keyProvider.getKey(), payload.getPayload()), (metadata, exception) -> {
//            if (exception != null) {
//                logger.warn("Failed to send a record to Kafka: {}", "", exception);
//                throwable.set(exception);;
//            }
//        });

        if(throwable.get() != null) throw throwable.get();
    }
}
