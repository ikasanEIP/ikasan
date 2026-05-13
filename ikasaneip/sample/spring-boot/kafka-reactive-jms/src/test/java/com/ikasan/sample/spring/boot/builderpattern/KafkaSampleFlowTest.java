/* 
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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
package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.jms.TextMessage;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ikasan.component.endpoint.kafka.client.reactive.consumer.KafkaConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>JmsSampleFlow</code> class.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(partitions = 3,
    topics = {
        "test-topic" }  )
@EnableJms
public class KafkaSampleFlowTest
{

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(KafkaSampleFlowTest.class);

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private Module<Flow> moduleUnderTest;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Autowired
    private JmsListenerEndpointRegistry registry;

    @Autowired
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    private ErrorReportingService errorReportingService;

    @Autowired
    private ExclusionManagementService exclusionManagementService;

    @Autowired
    private HospitalService hospitalService;

    private IkasanFlowTestRule flowTestRule;

    private MessageListenerVerifier messageListenerVerifier;

    @Before
    public void setup(){

        flowTestRule = new IkasanFlowTestRule();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Kafka Sample Flow"));

        errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        messageListenerVerifier.start();

    }

    @After
    public void teardown(){
        // consume messages from source queue if any were left
        MessageListenerVerifier mlv = new MessageListenerVerifier(brokerUrl, "source", registry);
        mlv.start();
        flowTestRule.sleep(1000L);
        mlv.stop();

        messageListenerVerifier.stop();
        flowTestRule.stopFlow();

    }

    @DirtiesContext
    @Test
    public void test_kafka_sample_flow() throws Exception
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        producer.send(new ProducerRecord<>("test-topic", 1, SAMPLE_MESSAGE));
        producer.flush();

        //Setup component expectations

        flowTestRule.consumer("Kafka Consumer")
            .broker("Exception Generating Broker")
            .producer("JMS Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(1, messageListenerVerifier.getCaptureResults().size()));
        assertEquals(((TextMessage)messageListenerVerifier.getCaptureResults().get(0)).getText(),
            SAMPLE_MESSAGE);
    }

    @DirtiesContext
    @Test
    public void test_kafka_sample_flow_many_messages() throws Exception
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        IntStream.range(0, 1000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        producer.flush();

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(1000, messageListenerVerifier.getCaptureResults().size()));

        flowTestRule.stopFlow();

        IntStream.range(1000, 2000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        producer.flush();

        // update broker config to force exception throwing
        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(true);

        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null
                    , null, null, null, 2000);
                assertEquals(1000, exclusions.size());
            });

        flowTestRule.stopFlow();

        ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(false);
        ExceptionGeneratingBroker.setShouldThrowRecoveryException(true);

        IntStream.range(2000, 3000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        producer.flush();

        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null
                    , null, null, 2000);
                assertEquals(1010, errors.size());
            });

        flowTestRule.stopFlow();

        ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(false);
        ExceptionGeneratingBroker.setShouldThrowRecoveryException(false);

        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(2000, messageListenerVerifier.getCaptureResults().size()));
    }

    @DirtiesContext
    @Test
    public void test_kafka_sample_flow_many_messages_only_consume_from_one_partition() throws Exception
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));
        kafkaConsumerConfiguration.setPartitions(new CopyOnWriteArrayList<>(List.of("0")));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        IntStream.range(0, 1000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", i%3, "my-test-value" +i)));
        producer.flush();

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(333, messageListenerVerifier.getCaptureResults().size()));

        flowTestRule.stopFlow();

        IntStream.range(1000, 2000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", i%3, "my-test-value" +i)));
        producer.flush();

        // update broker config to force exception throwing
        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(true);

        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null
                    , null, null, null, 2000);
                assertEquals(334, exclusions.size());
            });

        flowTestRule.stopFlow();

        ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(false);
        ExceptionGeneratingBroker.setShouldThrowRecoveryException(true);

        IntStream.range(2000, 3000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", i%3, "my-test-value" +i)));
        producer.flush();

        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null
                    , null, null, 2000);
                assertEquals(363, errors.size());
            });

        flowTestRule.stopFlow();

        ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(false);
        ExceptionGeneratingBroker.setShouldThrowRecoveryException(false);

        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(666, messageListenerVerifier.getCaptureResults().size()));
    }

    @DirtiesContext
    @Test
    public void test_exclusion()
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        producer.send(new ProducerRecord<>("test-topic", 1, SAMPLE_MESSAGE));
        producer.flush();

        // update broker config to force exception throwing
        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionException(true);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);

        // stop the flow, set to no longer throw exception and restart it
        flowTestRule.stopFlow();
        ExceptionGeneratingBroker.setShouldThrowExclusionException(false);

        flowTestRule.consumer("Kafka Consumer")
            .broker("Exception Generating Broker")
            .producer("JMS Producer")
            .consumer("Kafka Consumer")
            .broker("Exception Generating Broker")
            .producer("JMS Producer");
        flowTestRule.startFlow();

        producer.send(new ProducerRecord<>("test-topic", 3, SAMPLE_MESSAGE));
        producer.send(new ProducerRecord<>("test-topic", 2, SAMPLE_MESSAGE));
        producer.flush();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(2, messageListenerVerifier.getCaptureResults().size()));

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null
            , null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(SampleGeneratedException.class.getName(), error.getExceptionClass());
        assertEquals("ExcludeEvent", error.getAction());

        // Verify the exclusion was stored to DB was stored in DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null
            , null, null, 100);
        assertEquals(1, exclusions.size());
        ExclusionEvent exclusionEvent = (ExclusionEvent) exclusions.get(0);
        assertEquals(error.getUri(), exclusionEvent.getErrorUri());
    }

    @DirtiesContext
    @Test
    public void test_exclusion_followed_by_resubmission()
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        producer.send(new ProducerRecord<>("test-topic", 1, SAMPLE_MESSAGE));
        producer.flush();

        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null
                    , null, null, 100);
                assertEquals(1, errors.size());
            });

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null
            , null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(SampleGeneratedException.class.getName(), error.getExceptionClass());
        assertEquals("ExcludeEvent", error.getAction());

        // Verify the exclusion was stored to DB was stored in DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null
            , null, null, 100);
        assertEquals(1, exclusions.size());
        ExclusionEvent exclusionEvent = (ExclusionEvent) exclusions.get(0);
        assertEquals(error.getUri(), exclusionEvent.getErrorUri());
        logger.info("Exclusion event - " + new String(exclusionEvent.getEvent()));

        exceptionGeneratingBroker.setShouldThrowExclusionException(false);
        this.flowTestRule.stopFlow();
        this.flowTestRule.startFlow();

        hospitalService.resubmit(this.moduleUnderTest.getName(),
            "Kafka Sample Flow", exclusionEvent.getErrorUri(), "username");

        exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(1, messageListenerVerifier.getCaptureResults().size()));
    }

    @DirtiesContext
    @Test
    public void test_flow_in_recovery()
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        producer.send(new ProducerRecord<>("test-topic", 1, SAMPLE_MESSAGE));
        producer.flush();

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowRecoveryException(true);

        //Setup component expectations

        flowTestRule.consumer("Kafka Consumer")
            .broker("Exception Generating Broker")
            .consumer("Kafka Consumer")
            .broker("Exception Generating Broker")
            .consumer("Kafka Consumer")
            .broker("Exception Generating Broker")
            .consumer("Kafka Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));

        await().atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        //verify no messages were published
        assertEquals(0, messageListenerVerifier.getCaptureResults().size());

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null
            , null, null, 100);
        assertEquals(4, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=1000, maxRetries=-1)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null
            , null, null, 100);
        assertEquals(0, exclusions.size());
    }

    @DirtiesContext
    @Test
    public void test_flow_stopped_in_error()
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        producer.send(new ProducerRecord<>("test-topic", 1, SAMPLE_MESSAGE));
        producer.flush();

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowStoppedInErrorException(true);

        //Setup component expectations

        flowTestRule
            .withErrorEndState()
            .consumer("Kafka Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals("stoppedInError",flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, messageListenerVerifier.getCaptureResults().size());
        
        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null
            , null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(RuntimeException.class.getName(), error.getExceptionClass());
        assertEquals("Stop", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null
            , null, null, 100);
        assertEquals(0, exclusions.size());
    }

    @DirtiesContext
    @Test
    public void test_kafka_sample_flow_many_messages_with_intermittent_recovery() throws Exception
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowRecoveryExceptionEvery100thMessage(true);

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        IntStream.range(0, 1000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        producer.flush();

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(1000, messageListenerVerifier.getCaptureResults().size()));

        flowTestRule.stopFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals("stopped", flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null
            , null, null, 100);
        assertEquals(10, errors.size());

        for(ErrorOccurrence error: errors) {
            assertEquals(EndpointException.class.getName(), error.getExceptionClass());
            assertEquals("Retry (delay=1000, maxRetries=-1)", error.getAction());
        }
    }

    @DirtiesContext
    @Test
    public void test_kafka_sample_flow_many_messages_with_intermittent_exclusion() throws Exception
    {
        KafkaConsumerConfiguration kafkaConsumerConfiguration = this.flowTestRule
            .getComponentConfig("Kafka Consumer", KafkaConsumerConfiguration.class);

        kafkaConsumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));

        ExceptionGeneratingBroker ExceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        ExceptionGeneratingBroker.setShouldThrowExclusionExceptionEvery100thMessage(true);

        // Prepare test data
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer()
            , new StringSerializer()).createProducer();
        IntStream.range(0, 1000)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        producer.flush();

        // start the flow and assert it runs
        flowTestRule.startFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(990, messageListenerVerifier.getCaptureResults().size()));

        flowTestRule.stopFlow();

        await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals("stopped", flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null
            , null, null, 100);
        assertEquals(10, errors.size());

        for(ErrorOccurrence error: errors) {
            assertEquals(SampleGeneratedException.class.getName(), error.getExceptionClass());
            assertEquals("ExcludeEvent", error.getAction());
        }

        List<Object> exclusions = exclusionManagementService.find(null, null, null
            , null, null, 100);
        assertEquals(10, exclusions.size());
    }
}
