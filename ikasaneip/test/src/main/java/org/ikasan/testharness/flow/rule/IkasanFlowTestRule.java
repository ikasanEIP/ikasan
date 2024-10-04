/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */
package org.ikasan.testharness.flow.rule;

import org.apache.commons.lang3.ArrayUtils;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowSubject;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.*;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.ikasan.testharness.flow.listener.FlowEventListenerSubject;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.quartz.TriggerBuilder.newTrigger;

import static org.awaitility.Awaitility.with;

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

    private boolean allowScheduledConsumerToRunOnSchedule;

    FlowEventListenerSubject testHarnessFlowEventListener;

    public IkasanFlowTestRule()
    {
        this.flowExpectations = new OrderedExpectation();
        testHarnessFlowEventListener = new FlowEventListenerSubject(DefaultReplicationFactory.getInstance());

    }

    public IkasanFlowTestRule(boolean allowScheduledConsumerToRunOnSchedule)
    {
        this();
        this.allowScheduledConsumerToRunOnSchedule = allowScheduledConsumerToRunOnSchedule;
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
                assertEquals("in rule apply - flow should be stopped", errorEndState ?
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
            flowExpectations.expectation(component);
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
     * Invoke the scheduled consumer via its triggerSchedulerNow method as a regular business callback.
     * <p>
     * This method executes the underlying Job asynchronously
     */
    public void fireScheduledConsumer()
    {
        ScheduledConsumer consumer = (ScheduledConsumer) getComponent(scheduledConsumerName);
        try
        {
            JobDetail jobDetail = ((ScheduledComponent<JobDetail>)consumer).getJobDetail();
            String jobNameIteration = jobDetail.getKey().getName() + "_" + consumer.getConfiguration().getConsolidatedCronExpressions().get(0).hashCode();

            Trigger trigger = newTrigger().withIdentity(jobNameIteration, jobDetail.getKey().getGroup()).forJob(jobDetail).build();
            trigger.getJobDataMap().put(ScheduledConsumer.CRON_EXPRESSION, consumer.getConfiguration().getConsolidatedCronExpressions().get(0));
            consumer.scheduleAsEagerTrigger(trigger, 0);
        }
        catch (SchedulerException se)
        {
            throw new RuntimeException(se);
        }
    }

    /**
     * Invoke the scheduled consumer via its triggerSchedulerNow method as if the callback was driven from a
     * persistent recovery marker.
     * <p>
     * This method executes the underlying Job asynchronously
     */
    public void fireScheduledConsumerPersistentRecovery()
    {
        ScheduledConsumer consumer = (ScheduledConsumer) getComponent(scheduledConsumerName);
        try
        {
            JobDetail jobDetail = ((ScheduledComponent<JobDetail>)consumer).getJobDetail();
            Trigger trigger = newTrigger().withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup()).forJob(jobDetail).build();
            trigger.getJobDataMap().put(ScheduledConsumer.CRON_EXPRESSION, consumer.getConfiguration().getConsolidatedCronExpressions().get(0));
            trigger.getJobDataMap().put(ScheduledConsumer.PERSISTENT_RECOVERY, ScheduledConsumer.PERSISTENT_RECOVERY);
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
        if (this.scheduledConsumerName != null && !allowScheduledConsumerToRunOnSchedule)
        {
            FlowElement<?> flowElement = flow.getFlowElement(scheduledConsumerName);
            ScheduledConsumerConfiguration configuration = ((ScheduledConsumer) flowElement.getFlowComponent()).getConfiguration();
            configuration.setCronExpression("0/5 * * * * ? 2099"); // set to never run
            configuration.setEager(false); // do not callback on the provider once complete
        }
        flow.start();
        Assert.assertEquals("In startFlow with flowSubject - flow should be running", Flow.RUNNING, flow.getState());
    }

    /**
     * Setup the flow expectations and start the given flow.
     *
     */
    public void startFlow()
    {
        flowTestHarness = new FlowTestHarnessImpl(flowExpectations);
        testHarnessFlowEventListener.removeAllObservers();
        testHarnessFlowEventListener.addObserver((FlowObserver) flowTestHarness);
        testHarnessFlowEventListener.setIgnoreEventCapture(true);
        if (this.scheduledConsumerName != null && !allowScheduledConsumerToRunOnSchedule)
        {
            Object component = getComponent(scheduledConsumerName);
            ScheduledConsumerConfiguration configuration = ((ScheduledConsumer) component).getConfiguration();
            configuration.setCronExpression("0/5 * * * * ? 2099"); // set to never run
            configuration.setEager(false); // do not callback on the provider once complete
        }
        flow.addFlowListener(testHarnessFlowEventListener);
        flow.start();
        Assert.assertEquals("In startFlow() - flow should be running", Flow.RUNNING, flow.getState());
    }

    /**
     * Setup the flow expectations and start and pause the given flow.
     *
     */
    public void startPauseFlow()
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
        flow.addFlowListener(testHarnessFlowEventListener);
        flow.startPause();
        Assert.assertEquals("flow should be paused", Flow.PAUSED, flow.getState());
    }

    /**
     * Resume a paused flow.
     *
     */
    public void resumeFlow()
    {
        if(flow.getState().equals(Flow.PAUSED))
        {
            flow.resume();
        }
        else
        {
            throw new RuntimeException("Flow not is a paused state to be resumed");
        }

        Assert.assertEquals("flow should be running", Flow.RUNNING, flow.getState());
    }

    /**
     * Pause a running flow.
     *
     */
    public void pauseFlow()
    {
        if(flow.getState().equals(Flow.RUNNING))
        {
            flow.pause();
        }
        else
        {
            throw new RuntimeException("Flow not is a running state to be paused");
        }

        Assert.assertEquals("flow should be paused", Flow.PAUSED, flow.getState());
    }

    public void stopFlow()
    {
        flow.stop();
        flow.removeFlowListener(testHarnessFlowEventListener);
        assertEquals("in stopFlow() - flow should be stopped", errorEndState ?
            "stoppedInError" :
            "stopped", flow.getState());
    }


    /**
     * Stopping flow is asynchronous so this will be more reliable when stopping the flow
     *
     * @param testName
     * @param stopStates
     */
    public void stopFlowWithAwait(String testName, String[] stopStates){
        flow.stop();
        flow.removeFlowListener(testHarnessFlowEventListener);
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                String stopState = flow.getState();
                boolean isStopped = ArrayUtils.contains(stopStates, stopState);
                if (!isStopped){
                    System.out.println("In test " + testName + " attempted to stop flow but is now not in one of" +
                        " accepted stop states " +
                        ArrayUtils.toString(stopStates) + " its state is " + stopState);
                    flow.stop();
                } else {
                    System.out.println("In test " + testName + " stopped flow successfully is in allowed stop states" +
                        " " +
                        ArrayUtils.toString(stopStates) + " its state is " + stopState);
                }
                assertEquals("Flow is stopped as expected", true, isStopped);


            });

    }


    public void assertIsSatisfied()
    {
        flowTestHarness.assertIsSatisfied();
    }

    public String getFlowState()
    {
        return flow.getState();
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

            Method method = c.getMethod ("getConfiguration", null);
            return (T) method.invoke (component, null);
        }
        catch (IllegalAccessException|InvocationTargetException |NoSuchMethodException e)
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

    public String getScheduledConsumerName() {
        return scheduledConsumerName;
    }
}
