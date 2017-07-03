package org.ikasan.sample.test.flow;

import org.apache.commons.io.FileUtils;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.platform.IkasanEIPTest;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.jmock.Mockery;
import org.junit.After;
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
        "/source-filesystem-flow-conf.xml",
        "/filesystem-conf.xml",
        "/jms-conf.xml",
        "/scheduled-conf.xml",
        "/exception-conf.xml",
        "/mock-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
})
public class SourceScheduledFlowTest extends IkasanEIPTest
{
    /** mockery instance */
    @Resource
    Mockery mockery;

    /** flow on test */
    @Resource
    VisitingInvokerFlow sourceFlow;

    // test file details
    String testFilename = "test.txt";
    File testFile = new File(testFilename);

    /**
     * Tests flow operation for Sample Flow.
     */
    @SuppressWarnings("unchecked")
    @Test
    @DirtiesContext
    public void test_successful_sampleFlow_invocation() throws IOException
    {
        // create test file
        FileUtils.write(testFile, "test data");

        // setup the expected component invocations
        ikasanFlowTestRule.withFlow(sourceFlow)
                .consumer("Consumer Name")
                .producer("Producer Name");

        FlowElement consumerFlowElement = this.sourceFlow.getFlowElement("Consumer Name");
        FileConsumerConfiguration configuration = ((ConfiguredResource<FileConsumerConfiguration>)consumerFlowElement.getFlowComponent()).getConfiguration();
        List<String> filenames = new ArrayList<>();
        filenames.add(testFilename);
        configuration.setFilenames(filenames);

        // configure AMQ to provide in-memory destinations for the test
        Map<String,String> jndiProperties = new HashMap<>();
        jndiProperties.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jndiProperties.put("java.naming.provider.url", "vm://localhost?broker.persistent=false");

        // configure the JMS producer for the test
        FlowElement<?> producerFlowElement = this.sourceFlow.getFlowElement("Producer Name");
        ConfiguredResource<SpringMessageProducerConfiguration> configuredProducer = (ConfiguredResource)producerFlowElement.getFlowComponent();
        SpringMessageProducerConfiguration producerConfiguration = configuredProducer.getConfiguration();
        producerConfiguration.setConnectionFactoryName("ConnectionFactory");
        producerConfiguration.setConnectionFactoryJndiProperties(jndiProperties);
        producerConfiguration.setDestinationJndiName("dynamicQueues/queue");
        producerConfiguration.setDestinationJndiProperties(jndiProperties);

        // start the flow and assert it runs
        ikasanFlowTestRule.startFlow(testHarnessFlowEventListener);

        // invoke the scheduled consumer async
        ikasanFlowTestRule.fireScheduledConsumer();

        // wait for a brief while to let the flow complete
        ikasanFlowTestRule.sleep(4000L);

        // mocked assertions
        mockery.assertIsSatisfied();

        // no need to stop flow or check harness assertions - that is done by the IkasanFlowTestRule
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