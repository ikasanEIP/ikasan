package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.bigqueue.BigQueueImpl;
import org.ikasan.bigqueue.IBigQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ComponentConfiguration {

    @Value("${queue.dir}")
    private String queueDir;

    @Value("${inbound.queue.name}")
    private String inboundQueueName;

    @Value("${outbound.queue.name}")
    private String outboundQueueName;


    @Bean
    public IBigQueue outboundQueue() throws IOException {
        return new BigQueueImpl(queueDir, this.outboundQueueName);
    }

    @Bean
    public IBigQueue inboundQueue() throws IOException {
        return new BigQueueImpl(queueDir, this.inboundQueueName);
    }
}
