package org.ikasan.sample.test.flow;

import org.apache.commons.io.FileUtils;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.producer.FileProducerConfiguration;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.*;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.platform.IkasanEIPTest;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowSubject;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.*;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sample Scheduled Flow test.
 *
 * @author Ikasan Developmnet Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/monitor-service-conf.xml",
        "/monitor-conf.xml",
        "/shared-conf.xml",
        "/target-filesystem-flow-conf.xml",
        "/filesystem-conf.xml",
        "/jms-conf.xml",
        "/scheduled-conf.xml",
        "/exception-conf.xml",
        "/ikasan-transaction-conf.xml",
        "/mock-conf.xml",
        "/test-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
})
public class TargetFilesystemFlowTest extends IkasanEIPTest
{
    /** mockery instance */
    @Resource
    Mockery mockery;

    /** flow on test */
    @Resource
    VisitingInvokerFlow targetFlow;

    @Resource
    Producer testDataProducer;

    /**
     * Captures the actual components invoked and events created within the flow
     */
    @Resource
    FlowSubject testHarnessFlowEventListener;

    // test file details
    String testFilename = "test.txt";
    File testFile = new File(testFilename);

    /**
     * Setup will clear down any previously defined observers and ignore all exception transformations.
     *
     */
    private void flowTest_setup()
    {
        testHarnessFlowEventListener.removeAllObservers();
    }

    /**
     * Tests flow operation for Sample Flow.
     */
    @SuppressWarnings("unchecked")
    @Test
    @DirtiesContext
    public void test_successful_sampleFlow_invocation() throws IOException
    {
        flowTest_setup();

        // create test file
        FileUtils.write(testFile, "test data");

        //
        // setup expectations
        FlowTestHarness flowTestHarness = new FlowTestHarnessImpl(new OrderedExpectation()
        {
            {
                // main request flow
                expectation(new ConsumerComponent("Consumer Name"), "Consumer Name");
                expectation(new ProducerComponent("Producer Name"), "Producer Name");
            }
        });

        testHarnessFlowEventListener.addObserver((FlowObserver) flowTestHarness);
        testHarnessFlowEventListener.setIgnoreEventCapture(true);

        // configure AMQ to provide in-memory destinations for the test
        Map<String,String> jndiProperties = new HashMap<String,String>();
        jndiProperties.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jndiProperties.put("java.naming.provider.url", "vm://localhost?broker.persistent=false");

        // configure the JMS consumer for the test
        FlowElement<?> consumerFlowElement = this.targetFlow.getFlowElement("Consumer Name");
        ConfiguredResource<SpringMessageConsumerConfiguration> configuredProducer = (ConfiguredResource)consumerFlowElement.getFlowComponent();
        SpringMessageConsumerConfiguration consumerConfiguration = configuredProducer.getConfiguration();
        consumerConfiguration.setConnectionFactoryName("ConnectionFactory");
        consumerConfiguration.setConnectionFactoryJndiProperties(jndiProperties);
        consumerConfiguration.setDestinationJndiName("dynamicTopics/queue");
        consumerConfiguration.setDurable(false);
        consumerConfiguration.setDestinationJndiProperties(jndiProperties);

        // test data producer
        SpringMessageProducerConfiguration testProducerConfiguration = ((ConfiguredResource<SpringMessageProducerConfiguration>) testDataProducer).getConfiguration();
        testProducerConfiguration.setConnectionFactoryJndiProperties(jndiProperties);
        testProducerConfiguration.setConnectionFactoryName("ConnectionFactory");
        testProducerConfiguration.setDestinationJndiName("dynamicTopics/queue");
        testProducerConfiguration.setDestinationJndiProperties(jndiProperties);
        ((ManagedResource)testDataProducer).startManagedResource();

        // target flow file system producer
        FlowElement producerFlowElement = this.targetFlow.getFlowElement("Producer Name");
        FileProducerConfiguration configuration = ((ConfiguredResource<FileProducerConfiguration>)producerFlowElement.getFlowComponent()).getConfiguration();
        configuration.setFilename(testFilename);

        // start the flow
        this.targetFlow.start();
        Assert.assertEquals("flow should be running", "running", this.targetFlow.getState());

        // publish test message
        testDataProducer.invoke("test message");

        // wait for the flow to fully execute
        try
        {
            Thread.sleep(4000);
        }
        catch(InterruptedException e)
        {
            Assert.fail(e.getMessage());
        }

        // stop the flow
        this.targetFlow.stop();

        Assert.assertEquals("flow should be stopped", "stopped", this.targetFlow.getState());

        // run flow assertions
        flowTestHarness.assertIsSatisfied();

        // mocked assertions
        mockery.assertIsSatisfied();
    }

    @After
    public void teardown()
    {
        if(testFile.exists())
        {
            FileUtils.deleteQuietly(testFile);
        }
    }
}