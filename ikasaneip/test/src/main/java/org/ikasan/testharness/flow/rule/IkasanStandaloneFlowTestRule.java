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

import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.*;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.ikasan.testharness.flow.listener.FlowEventListenerSubject;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.SocketUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * JUnit Rule implementation allowing flow tests to be created and executed using a builder pattern.
 * <p>
 * The rule will take care of starting up spring context bases on IkasanApplicationFactory and Application class
 * provided in constructor.
 * <p>
 * The underlying Flow will be automatically stopped and checked when the rule is evaluated at the end of the test
 *
 * @author Ikasan Development Team
 */
public class IkasanStandaloneFlowTestRule implements TestRule
{

    private IkasanApplication ikasanApplication;
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

    FlowEventListenerSubject testHarnessFlowEventListener;


    public IkasanStandaloneFlowTestRule(String flowUnderTest,Class<?> applicationClass)
    {
        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000) };
        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(applicationClass,args);

        Module module = ikasanApplication.getModules().get(0);
        this.flow = (Flow) module.getFlow(flowUnderTest);
        if(flow == null) {
            throw new RuntimeException("Flow ["+flowUnderTest+"] not found in application context.");
        }
        this.flowExpectations = new OrderedExpectation();
        testHarnessFlowEventListener = new FlowEventListenerSubject(DefaultReplicationFactory.getInstance());

    }

    public IkasanStandaloneFlowTestRule(String flowUnderTest,Class<?> applicationClass,String args[])
    {
        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(applicationClass,args);

        Module module = ikasanApplication.getModules().get(0);
        this.flow = (Flow) module.getFlow(flowUnderTest);
        if(flow == null) {
            throw new RuntimeException("Flow ["+flowUnderTest+"] not found in application context.");
        }
        this.flowExpectations = new OrderedExpectation();
        testHarnessFlowEventListener = new FlowEventListenerSubject(DefaultReplicationFactory.getInstance());

    }

    public IkasanStandaloneFlowTestRule(String flowUnderTest,IkasanApplication ikasanApplication)
    {
        this.ikasanApplication = ikasanApplication;
        Module module = this.ikasanApplication.getModules().get(0);
        this.flow = (Flow) module.getFlow(flowUnderTest);
        if(flow == null) {
            throw new RuntimeException("Flow ["+flowUnderTest+"] not found in application context.");
        }
        this.flowExpectations = new OrderedExpectation();
        testHarnessFlowEventListener = new FlowEventListenerSubject(DefaultReplicationFactory.getInstance());

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

                    // shutdown application
                    ikasanApplication.close();
                }
            }
        };
    }

    public void assertFlowComponentExecution(){
        if (flow != null && flowTestHarness != null)
        {
            flowTestHarness.assertIsSatisfied();

        }
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
    public IkasanStandaloneFlowTestRule withFlow(Flow flow)
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
    public IkasanStandaloneFlowTestRule withErrorEndState()
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
    public IkasanStandaloneFlowTestRule consumer(String name)
    {
        if (this.flow == null)
        {
            Assert.fail("withFlow() should be called first to set the flow on this rule");
        }
        addExpectation(new ConsumerComponent(name));
        // detect a ScheduledConsumer
        if (getComponent(name) instanceof ScheduledConsumer)
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
    public IkasanStandaloneFlowTestRule scheduledConsumer(String name)
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
    public IkasanStandaloneFlowTestRule splitter(String name)
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
    public IkasanStandaloneFlowTestRule converter(String name)
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
    public IkasanStandaloneFlowTestRule producer(String name)
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
    public IkasanStandaloneFlowTestRule router(String name)
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
    public IkasanStandaloneFlowTestRule multiRecipientRouter(String name)
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
    public IkasanStandaloneFlowTestRule translator(String name)
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
    public IkasanStandaloneFlowTestRule broker(String name)
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
    public IkasanStandaloneFlowTestRule filter(String name)
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
    public IkasanStandaloneFlowTestRule sequencer(String name)
    {
        addExpectation(new SequencerComponent(name));
        return this;
    }

    /**
     * Start a block of repeated components
     *
     * @return this rule
     */
    public IkasanStandaloneFlowTestRule blockStart()
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
    public IkasanStandaloneFlowTestRule blockEnd()
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
    public IkasanStandaloneFlowTestRule repeat(int n)
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
        ScheduledConsumer consumer = (ScheduledConsumer) getComponent(scheduledConsumerName);
        try
        {
            Trigger trigger = newTrigger().withIdentity("name", "group").build();
            consumer.scheduleAsEagerTrigger(trigger, 0);
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
        ScheduledConsumer consumer = (ScheduledConsumer) getComponent(scheduledConsumerName);
        consumer.execute(jobExecutionContext);
    }

    /**
     * Setup the flow expectations, make sure that testHarness listener is attached to flow and start the given flow.
     *
     */
    public void startFlow()
    {
        flowTestHarness = new FlowTestHarnessImpl(flowExpectations);
        testHarnessFlowEventListener.removeAllObservers();
        testHarnessFlowEventListener.addObserver((FlowObserver) flowTestHarness);
        testHarnessFlowEventListener.setIgnoreEventCapture(true);
        if (this.scheduledConsumerName != null)
        {
            Object component = getComponent(scheduledConsumerName);
            ScheduledConsumerConfiguration configuration = ((ScheduledConsumer) component).getConfiguration();
            configuration.setCronExpression("0/5 * * * * ? 2099"); // set to never run
            configuration.setEager(false); // do not callback on the provider once complete
        }
        flow.setFlowListener(testHarnessFlowEventListener);
        flow.start();
        Assert.assertEquals("flow should be running", "running", flow.getState());
    }

    /**
     * Gets component of given name from this flow.
     *
     * @param componentName component name to be looked up in flow
     * @return component as Object
     */
    public Object getComponent(String componentName){
        Object flowElement =  flow.getFlowElement(componentName).getFlowComponent();
        return getTargetObject(flowElement,Object.class);
    }

    /**
     * Gets object or unwraps object from JdkDynamicProxy.
     *
     * @param proxy object which might have been proxied
     * @param targetClass target class type
     * @param <T> returned Object casted to type T
     * @return
     */
    private <T> T getTargetObject(Object proxy, Class<T> targetClass) {
        try
        {
            if (AopUtils.isJdkDynamicProxy(proxy))
            {
                return (T) ((Advised) proxy).getTargetSource().getTarget();
            }
            else
            {
                return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets configuration of component with given name from this flow.
     *
     * @param componentName component name to be looked up in flow
     * @param classType class Type to configuration object
     * @return configuration object casted to T
     */
    public <T> T getComponentConfig(String componentName, Class<T> classType){
        Object  component = getComponent(componentName);
        Class<?> c = component.getClass();
        try
        {

            Method  method = c.getDeclaredMethod ("getConfiguration", null);
            return (T) method.invoke (component, null);
        }
        catch (IllegalAccessException|InvocationTargetException|NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }

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

    public IkasanApplication getIkasanApplication()
    {
        return ikasanApplication;
    }
}
