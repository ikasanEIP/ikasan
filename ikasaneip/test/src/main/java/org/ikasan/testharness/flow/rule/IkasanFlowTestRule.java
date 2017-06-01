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
package org.ikasan.testharness.flow.rule;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowSubject;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.*;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * JUnit Rule implementation allowing flow tests to be created and executed using a builder pattern.
 * <p>
 * The underlying Flow will be automatically stopped and checked when the rule is evaluated at the end of the test
 *
 * @author Ikasan Development Team
 */
public class IkasanFlowTestRule implements TestRule
{
    /**
     * the expectations created by the builder methods
     */
    private OrderedExpectation flowExpectations;

    /**
     * the FlowSubject listener
     */
    private FlowTestHarness flowTestHarness;

    /**
     * the Flow under test
     */
    private Flow flow;

    /**
     * an optional scheduledConsumerName used to indicate the flow is triggered by a ScheduledConsumer
     */
    String scheduledConsumerName;

    /**
     * flag used to open a repeating block
     */
    private boolean openBlock = false;

    /**
     * optional block of components used to repeat
     */
    private List<AbstractComponent> blockComponents = new ArrayList<>();

    /**
     * flag indicating the expected end state (stopped=false, stoppedInError=true)
     */
    private boolean errorEndState = false;

    public IkasanFlowTestRule()
    {
        this.flowExpectations = new OrderedExpectation();
    }

    /**
     * Applies the basic flow test rules:
     * 1. Stop the flow and assert it is stopped; or stoppedInError if withErrorEndState() was called when building
     * 2. Assert the flowTestHarness for component invocation completeness
     *
     * @param base        a base Statement
     * @param description a Description (not used)
     * @return a Statement, wrapping the provided instance, that can be evaluated by JUnit
     */
    @Override
    public Statement apply(final Statement base, Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                base.evaluate();
                if (flow != null && flowTestHarness != null)
                {
                    flow.stop();
                    assertEquals("flow should be stopped", errorEndState ?
                            "stoppedInError" :
                            "stopped", flow.getState());
                    flowTestHarness.assertIsSatisfied();
                }
            }
        };
    }

    private void addExpectation(AbstractComponent component)
    {
        if (openBlock)
        {
            blockComponents.add(component);
        }
        else
        {
            flowExpectations.expectation(component, component.getName());
        }
    }

    /**
     * Set the flow which this Rule will check
     *
     * @param flow the Flow
     * @return this rule
     */
    public IkasanFlowTestRule withFlow(Flow flow)
    {
        Assert.assertNotNull("Flow cannot be null", flow);
        this.flow = flow;
        return this;
    }

    /**
     * Indicate the flow end state will be 'stoppedInError', used in negative testing
     *
     * @return this rule
     */
    public IkasanFlowTestRule withErrorEndState()
    {
        errorEndState = true;
        return this;
    }

    /**
     * Expect a consumer
     *
     * @param name the consumer name
     * @return this rule
     */
    public IkasanFlowTestRule consumer(String name)
    {
        if (this.flow == null)
        {
            Assert.fail("withFlow() should be called first to set the flow on this rule");
        }
        addExpectation(new ConsumerComponent(name));
        // detect a ScheduledConsumer
        if (flow.getFlowElement(name).getFlowComponent().getClass().getCanonicalName().equals("org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer"))
        {
            this.scheduledConsumerName = name;
        }
        return this;
    }

    /**
     * Expect a consumer
     *
     * @param name the consumer name
     * @return this rule
     */
    public IkasanFlowTestRule scheduledConsumer(String name)
    {
        if (this.flow == null)
        {
            Assert.fail("withFlow() should be called first to set the flow on this rule");
        }
        addExpectation(new ConsumerComponent(name));

        this.scheduledConsumerName = name;

        return this;
    }

    /**
     * Expect a splitter
     *
     * @param name the splitter name
     * @return this rule
     */
    public IkasanFlowTestRule splitter(String name)
    {
        addExpectation(new SplitterComponent(name));
        return this;
    }

    /**
     * Expect a converter
     *
     * @param name the converter name
     * @return this rule
     */
    public IkasanFlowTestRule converter(String name)
    {
        addExpectation(new ConverterComponent(name));
        return this;
    }

    /**
     * Expect a producer
     *
     * @param name the producer name
     * @return this rule
     */
    public IkasanFlowTestRule producer(String name)
    {
        addExpectation(new ProducerComponent(name));
        return this;
    }

    /**
     * Expect a SingleRecipientRouter
     *
     * @param name the router name
     * @return this rule
     */
    public IkasanFlowTestRule router(String name)
    {
        addExpectation(new SingleRecipientRouterComponent(name));
        return this;
    }

    /**
     * Expect a MultiRecipientRouter
     *
     * @param name the MRR name
     * @return this rule
     */
    public IkasanFlowTestRule multiRecipientRouter(String name)
    {
        addExpectation(new MultiRecipientRouterComponent(name));
        return this;
    }

    /**
     * Expect a translator
     *
     * @param name the translator name
     * @return this rule
     */
    public IkasanFlowTestRule translator(String name)
    {
        addExpectation(new TranslatorComponent(name));
        return this;
    }

    /**
     * Expect a broker
     *
     * @param name the broker name
     * @return this rule
     */
    public IkasanFlowTestRule broker(String name)
    {
        addExpectation(new BrokerComponent(name));
        return this;
    }

    /**
     * Expect a filter
     *
     * @param name the filter name
     * @return this rule
     */
    public IkasanFlowTestRule filter(String name)
    {
        addExpectation(new FilterComponent(name));
        return this;
    }

    /**
     * Expect a sequencer
     *
     * @param name the sequencer name
     * @return this rule
     */
    public IkasanFlowTestRule sequencer(String name)
    {
        addExpectation(new SequencerComponent(name));
        return this;
    }

    /**
     * Start a block of repeated components
     *
     * @return this rule
     */
    public IkasanFlowTestRule blockStart()
    {
        this.openBlock = true;
        blockComponents = new ArrayList<>();
        return this;
    }

    /**
     * Close the current block of repeated components
     *
     * @return this rule
     */
    public IkasanFlowTestRule blockEnd()
    {
        this.openBlock = false;
        return this;
    }

    /**
     * Repeat the current block n times;
     *
     * @param n number of times to repeat the block
     * @return this rule
     */
    public IkasanFlowTestRule repeat(int n)
    {
        blockEnd();
        for (int i = 0; i < n; i++)
        {
            for (AbstractComponent component : blockComponents)
            {
                addExpectation(component);
            }
        }
        return this;
    }

    /**
     * Invoke the scheduled consumer via its triggerSchedulerNow method
     * <p>
     * This method executes the underlying Job asynchronously
     */
    public void fireScheduledConsumer()
    {
        FlowElement<?> flowElement = flow.getFlowElement(scheduledConsumerName);
        ScheduledConsumer consumer = (ScheduledConsumer) flowElement.getFlowComponent();
        try
        {
            consumer.triggerSchedulerNow();
        }
        catch (SchedulerException se)
        {
            throw new RuntimeException(se);
        }
    }

    /**
     * Invoke the scheduled consumer with a given JobExecutionContext (can be mocked).
     *
     * This method executes synchronously - when the flow finished executing the given task this method will then return.
     *
     * @param jobExecutionContext a JobExecutionContext
     */
    public void fireScheduledConsumerSynchronously(JobExecutionContext jobExecutionContext)
    {
        FlowElement<?> flowElement = flow.getFlowElement(scheduledConsumerName);
        ScheduledConsumer consumer = (ScheduledConsumer) flowElement.getFlowComponent();
        consumer.execute(jobExecutionContext);
    }

    /**
     * Setup the flow expectations and start the given flow.
     *
     * @param testHarnessFlowEventListener the test harness flow listener
     */
    public void startFlow(FlowSubject testHarnessFlowEventListener)
    {
        flowTestHarness = new FlowTestHarnessImpl(flowExpectations);
        testHarnessFlowEventListener.removeAllObservers();
        testHarnessFlowEventListener.addObserver((FlowObserver) flowTestHarness);
        testHarnessFlowEventListener.setIgnoreEventCapture(true);
        if (this.scheduledConsumerName != null)
        {
            FlowElement<?> flowElement = flow.getFlowElement(scheduledConsumerName);
            ScheduledConsumerConfiguration configuration = ((ScheduledConsumer) flowElement.getFlowComponent()).getConfiguration();
            configuration.setCronExpression("0/5 * * * * ? 2099"); // set to never run
            configuration.setEager(false); // do not callback on the provider once complete
        }
        flow.start();
        Assert.assertEquals("flow should be running", "running", flow.getState());
    }

    /**
     * Sleep for a bit to let a flow execution complete
     *
     * @param time the number of milliseconds to sleep for
     */
    public void sleep(long time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
            Assert.fail("Sleep interrupted: " + e.getMessage());
        }
    }
}
