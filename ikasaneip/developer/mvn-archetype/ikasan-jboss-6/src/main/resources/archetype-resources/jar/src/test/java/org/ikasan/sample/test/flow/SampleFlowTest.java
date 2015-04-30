package org.ikasan.sample.test.flow;

import org.ikasan.component.endpoint.util.consumer.EventGeneratingConsumerConfiguration;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.platform.IkasanEIPTest;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowSubject;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.*;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Sample Flow test.
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
        "/sample-flow-conf.xml",
        "/exception-conf.xml",
        "/mock-conf.xml",
        "/hsqldb-datasource-conf.xml"
})
public class SampleFlowTest extends IkasanEIPTest
{
    /** mockery instance */
    @Resource
    Mockery mockery;

    /** flow on test */
    @Resource
    VisitingInvokerFlow sampleFlow;

    /**
     * Captures the actual components invoked and events created within the flow
     */
    @Resource
    FlowSubject testHarnessFlowEventListener;

    /**
     * Allow configuration to be changed as part of the test
     */
    @Resource (name = "sampleConsumerConfiguration")
    EventGeneratingConsumerConfiguration configuration;

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
    public void test_successful_sampleFlow_invocation()
    {
        flowTest_setup();

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

        // test with just one event
        configuration.setEventLimit(1);

        // start the flow
        this.sampleFlow.start();
        Assert.assertEquals("flow should be running", "running", this.sampleFlow.getState());

        // wait for the flow to fully execute
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            Assert.fail(e.getMessage());
        }

        // stop the flow
        this.sampleFlow.stop();

        Assert.assertEquals("flow should be stopped", "stopped", this.sampleFlow.getState());

        // run flow assertions
        flowTestHarness.assertIsSatisfied();

        // mocked assertions
        mockery.assertIsSatisfied();
    }
}
