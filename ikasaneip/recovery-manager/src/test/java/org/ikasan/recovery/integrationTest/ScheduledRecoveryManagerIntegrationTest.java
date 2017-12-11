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
package org.ikasan.recovery.integrationTest;

import org.ikasan.recovery.ScheduledRecoveryManagerFactory;
import org.ikasan.spec.component.IsConsumerAware;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.FlowElement;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.exceptionResolver.ExceptionGroup;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.MatchingExceptionResolver;
import org.ikasan.exceptionResolver.action.ExceptionAction;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.exceptionResolver.action.StopAction;
import org.ikasan.exceptionResolver.matcher.MatcherBasedExceptionGroup;
import org.ikasan.spec.recovery.RecoveryManagerFactory;
import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FinalAction;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.recovery.RecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Integration testing for Recovery Manager implementation.
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class ScheduledRecoveryManagerIntegrationTest
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(ScheduledRecoveryManagerIntegrationTest.class);

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    /** scheduler for recovery manager */
    private Scheduler scheduler;

    /** scheduled job factory */
    private ScheduledJobFactory scheduledJobFactory;
    
    /** stubbed consumer */
    private Consumer consumer;

    /** recovery manager factory */
    private RecoveryManagerFactory recoveryManagerFactory;

    /** component name */
    private String componentName = "componentName";

    /** flow name */
    private String flowName = "flowName";

    /** module name */
    private String moduleName = "moduleName";

    /** exclusion service */
    private ExclusionService exclusionService;

    /** error reporting service */
    private ErrorReportingService errorReportingService;

    private FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

    @Before
    public void setUp()
    {
        this.scheduler = SchedulerFactory.getInstance().getScheduler();
        this.scheduledJobFactory = CachingScheduledJobFactory.getInstance();
        this.consumer = new StubbedConsumer();
        this.recoveryManagerFactory = new ScheduledRecoveryManagerFactory(scheduler, scheduledJobFactory);
        this.exclusionService = new StubbedExclusionService();
        this.errorReportingService = new StubbedErrorReportingService();

    }

    /**
     * Test initial state of recovery manager after instantiation.
     */
    @Test
    public void test_recoveryManager_state_after_creation()
    {
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);

        Assert.assertFalse("recovery manager should not be recovering", recoveryManager.isRecovering());
        Assert.assertFalse("recovery manager should not be unrecoverable", recoveryManager.isUnrecoverable());
    }

    /**
     * Test recovery manager default behaviour when no resolver has been
     * specified.
     */
    @Test
    @Ignore
    public void test_recoveryManager_default_stop_when_no_resolver()
    {
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);

        // start the consumer and pass exception to recoveryManager
        consumer.start();
        Assert.assertTrue("consumer should be running", consumer.isRunning());
        
        try
        {
            recoveryManager.recover(componentName, new Exception());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Stop", e.getMessage());
        }
        
        //
        // expected results are consumer has been stopped and RM reports
        // unrecoverable
        Assert.assertFalse(consumer.isRunning());
        Assert.assertFalse(recoveryManager.isRecovering());
        Assert.assertTrue(recoveryManager.isUnrecoverable());
    }

    /**
     * Test recovery manager with resolver for stop action.
     */
    @Test
    @Ignore
    public void test_recoveryManager_resolver_to_stopAction()
    {
        //
        // create an exception resolver
        ExceptionAction stopAction = StopAction.instance();
        IsInstanceOf instanceOfException = new org.hamcrest.core.IsInstanceOf(Exception.class);
        MatcherBasedExceptionGroup matcher = new MatcherBasedExceptionGroup(instanceOfException, stopAction);
        List<ExceptionGroup> matchers = new ArrayList<>();
        matchers.add(matcher);
        ExceptionResolver resolver = new MatchingExceptionResolver(matchers);

        //
        // create the RM and set the resolver
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);
        recoveryManager.setResolver(resolver);

        //
        // start the consumer and pass exception to recoveryManager
        consumer.start();
        Assert.assertTrue("consumer should be running", consumer.isRunning());
        
        try
        {
            recoveryManager.recover(componentName, new Exception());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Stop", e.getMessage());
        }
        
        //
        // expected results are consumer has been stopped and RM reports
        // unrecoverable
        Assert.assertFalse("consumer should not be running", consumer.isRunning());
        Assert.assertFalse("recovery manager should not be recovering", recoveryManager.isRecovering());
        Assert.assertTrue("recovery manager should be report unrecoverable", recoveryManager.isUnrecoverable());
    }

    /**
     * Test recovery manager with resolver for exclude action.
     */
    @Test
    public void test_recoveryManager_resolver_to_excludeAction()
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowInvocationContext).getLastComponentName();
                will(returnValue("componentName"));

                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.EXCLUDE);

                exactly(1).of(flowInvocationContext).setErrorUri(null);
            }});
        //
        // create an exception resolver
        ExceptionAction excludeEventAction = ExcludeEventAction.instance();
        IsInstanceOf instanceOfException = new org.hamcrest.core.IsInstanceOf(Exception.class);
        MatcherBasedExceptionGroup matcher = new MatcherBasedExceptionGroup(instanceOfException, excludeEventAction);
        List<ExceptionGroup> matchers = new ArrayList<>();
        matchers.add(matcher);
        ExceptionResolver resolver = new MatchingExceptionResolver(matchers);

        //
        // create the RM and set the resolver
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);
        recoveryManager.setResolver(resolver);

        //
        // start the consumer and pass exception to recoveryManager
        consumer.start();
        Assert.assertTrue("consumer should be running", consumer.isRunning());

        try
        {
            recoveryManager.recover(flowInvocationContext, new Exception(), "", "");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("ExcludeEvent", e.getMessage());
        }

        //
        // expected results are consumer is still running
        Assert.assertTrue("consumer should be running", consumer.isRunning());
        Assert.assertFalse("recovery manager should not be recovering", recoveryManager.isRecovering());
        Assert.assertFalse("recovery manager should not report unrecoverable", recoveryManager.isUnrecoverable());
    }

    /**
     * Test recovery manager with resolver for retry action.
     * @throws SchedulerException 
     */
    @Test
    public void test_recoveryManager_resolver_to_retryAction() throws SchedulerException
    {
        JobKey jobKey = new JobKey("recoveryJob_"+flowName+Thread.currentThread().getId(), moduleName);

        //
        // create an exception resolver
        ExceptionAction retryAction = new RetryAction((long)2000, 2);
        IsInstanceOf instanceOfException = new org.hamcrest.core.IsInstanceOf(Exception.class);
        MatcherBasedExceptionGroup matcher = new MatcherBasedExceptionGroup(instanceOfException, retryAction);
        List<ExceptionGroup> matchers = new ArrayList<>();
        matchers.add(matcher);
        ExceptionResolver resolver = new MatchingExceptionResolver(matchers);

        //
        // create the RM and set the resolver
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);
        recoveryManager.setResolver(resolver);

        //
        // start the consumer and pass exception to recoveryManager
        consumer.start();
        Assert.assertTrue("consumer should be running", consumer.isRunning());

        //
        // first retry action
        try
        {
            recoveryManager.recover(componentName, new Exception());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Retry (delay=2000, maxRetries=2)", e.getMessage());
            Assert.assertFalse("consumer should not be running", consumer.isRunning());
            Assert.assertTrue("recovery manager should be recovering", recoveryManager.isRecovering());
            Assert.assertFalse("recovery manager should not be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNotNull("job should still be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

        // wait for scheduler callback to restart the consumer
        while(!consumer.isRunning())
        {
            logger.info("Waiting for consumer to be started...");
            pause(100);
        }
        
        //
        // second retry action
        try
        {
            Assert.assertTrue("consumer should be running", consumer.isRunning());
            recoveryManager.recover(componentName, new Exception());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Retry (delay=2000, maxRetries=2)", e.getMessage());
            Assert.assertFalse("consumer should not be running", consumer.isRunning());
            Assert.assertTrue("recovery manager should be recovering", recoveryManager.isRecovering());
            Assert.assertFalse("recovery manager should not be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNotNull("job should still be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

        // wait for scheduler callback to restart the consumer
        while(!consumer.isRunning()){pause(100);};
        
        //
        // third retry action
        try
        {
            Assert.assertTrue(consumer.isRunning());
            recoveryManager.recover(componentName, new Exception());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Exhausted maximum retries.", e.getMessage());
            Assert.assertFalse("consumer should not be running", consumer.isRunning());
            Assert.assertFalse("recovery manager should not be recovering", recoveryManager.isRecovering());
            Assert.assertTrue("recovery manager should be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNull("job should not be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

    }

    /**
     * Test recovery manager with resolver for one type of retry action
     * followed by a different type of retry action.
     * @throws SchedulerException 
     */
    @Test
    public void test_recoveryManager_resolver_to_retryActionA_followed_by_retryActionB() throws SchedulerException
    {
        JobKey jobKey = new JobKey("recoveryJob_"+flowName+ Thread.currentThread().getId(), moduleName);

        //
        // create an exception resolver
        ExceptionAction retryActionA = new RetryAction((long)2000, 2);
        IsInstanceOf instanceOfIllegalArgumentException = new org.hamcrest.core.IsInstanceOf(IllegalArgumentException.class);
        MatcherBasedExceptionGroup matcherA = new MatcherBasedExceptionGroup(instanceOfIllegalArgumentException, retryActionA);

        ExceptionAction retryActionB = new RetryAction((long)1000, 2);
        IsInstanceOf instanceOfNullPointerException = new org.hamcrest.core.IsInstanceOf(NullPointerException.class);
        MatcherBasedExceptionGroup matcherB = new MatcherBasedExceptionGroup(instanceOfNullPointerException, retryActionB);
        
        List<ExceptionGroup> matchers = new ArrayList<>();
        matchers.add(matcherA);
        matchers.add(matcherB);
        ExceptionResolver resolver = new MatchingExceptionResolver(matchers);

        //
        // create the RM and set the resolver
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);
        recoveryManager.setResolver(resolver);

        //
        // start the consumer and pass exception to recoveryManager
        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        //
        // first retry action
        try
        {
            recoveryManager.recover(componentName, new IllegalArgumentException());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Retry (delay=2000, maxRetries=2)", e.getMessage());
            Assert.assertFalse("consumer should not be running", consumer.isRunning());
            Assert.assertTrue("recovery manager should be recovering", recoveryManager.isRecovering());
            Assert.assertFalse("recovery manager should not be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNotNull("job should still be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

        // wait for scheduler callback to restart the consumer
        while(!consumer.isRunning()){pause(100);};
        
        //
        // second retry action
        try
        {
            Assert.assertTrue(consumer.isRunning());
            recoveryManager.recover(componentName, new NullPointerException());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Retry (delay=1000, maxRetries=2)", e.getMessage());
            Assert.assertFalse("Consumer should not be running", consumer.isRunning());
            Assert.assertTrue("recovery manager should be recovering", recoveryManager.isRecovering());
            Assert.assertFalse("recovery manager should be not be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNotNull("job should still be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

        // wait for scheduler callback to restart the consumer
        while(!consumer.isRunning()){pause(100);}
        
        //
        // third retry action
        try
        {
            Assert.assertTrue(consumer.isRunning());
            recoveryManager.recover(componentName, new NullPointerException());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Retry (delay=1000, maxRetries=2)", e.getMessage());
            Assert.assertFalse("Consumer should not be running", consumer.isRunning());
            Assert.assertTrue("recovery manager should be recovering", recoveryManager.isRecovering());
            Assert.assertFalse("recovery manager should be not be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNotNull("job should still be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

        // wait for scheduler callback to restart the consumer
        while(!consumer.isRunning()){pause(100);}
        
        //
        // forth retry action
        try
        {
            recoveryManager.recover(componentName, new NullPointerException());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Exhausted maximum retries.", e.getMessage());
            Assert.assertFalse("Consumer should not be running", consumer.isRunning());
            Assert.assertFalse("recovery manager should not be recovering", recoveryManager.isRecovering());
            Assert.assertTrue("recovery manager should be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNull("job should not be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

    }

    /**
     * Test recovery manager with resolver for a retry action
     * followed by a stop action.
     * @throws SchedulerException 
     */
    @Test
    public void test_recoveryManager_resolver_to_retryAction_followed_by_stopAction() throws SchedulerException
    {
        JobKey jobKey = new JobKey("recoveryJob_"+flowName+Thread.currentThread().getId(), moduleName);

        //
        // create an exception resolver
        ExceptionAction retryAction = new RetryAction((long)2000, 2);
        IsInstanceOf instanceOfIllegalArgumentException = new org.hamcrest.core.IsInstanceOf(IllegalArgumentException.class);
        MatcherBasedExceptionGroup matcherA = new MatcherBasedExceptionGroup(instanceOfIllegalArgumentException, retryAction);

        ExceptionAction stopAction = StopAction.instance();
        IsInstanceOf instanceOfNullPointerException = new org.hamcrest.core.IsInstanceOf(NullPointerException.class);
        MatcherBasedExceptionGroup matcherB = new MatcherBasedExceptionGroup(instanceOfNullPointerException, stopAction);
        
        List<ExceptionGroup> matchers = new ArrayList<>();
        matchers.add(matcherA);
        matchers.add(matcherB);
        ExceptionResolver resolver = new MatchingExceptionResolver(matchers);

        //
        // create the RM and set the resolver
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        setIsAware(recoveryManager);

        recoveryManager.setResolver(resolver);

        //
        // start the consumer and pass exception to recoveryManager
        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        //
        // first retry action
        try
        {
            recoveryManager.recover(componentName, new IllegalArgumentException());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Retry (delay=2000, maxRetries=2)", e.getMessage());
            Assert.assertFalse("consumer should not be running", consumer.isRunning());
            Assert.assertTrue("recovery manager should be recovering", recoveryManager.isRecovering());
            Assert.assertFalse("recovery manager should not be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNotNull("job should still be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }

        // wait for scheduler callback to restart the consumer
        while(!consumer.isRunning()){pause(100);}
        
        //
        // second retry action
        try
        {
            Assert.assertTrue(consumer.isRunning());
            recoveryManager.recover(componentName, new NullPointerException());
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Stop", e.getMessage());
            Assert.assertFalse("Consumer should not be running", consumer.isRunning());
            Assert.assertFalse("recovery manager should not be recovering", recoveryManager.isRecovering());
            Assert.assertTrue("recovery manager should be unrecoverable", recoveryManager.isUnrecoverable());
            Assert.assertNull("job should not be registered with scheduler",
                this.scheduler.getJobDetail(jobKey));
        }
    }

    /**
     * Stubbed ExclusionService implementation for testing
     * @author Ikasan Development Team
     */
    private class StubbedExclusionService<T> implements ExclusionService
    {
        @Override
        public boolean isBlackListed(Object o) {
            return false;
        }

        @Override
        public void addBlacklisted(Object o, String uri, FlowInvocationContext flowInvocationContext)
        {

        }


        @Override
        public String getErrorUri(Object o) {
            return null;
        }

        @Override
        public FlowInvocationContext getFlowInvocationContext(Object o)
        {
            return null;
        }

        @Override
        public void park(Object event, Object id) {

        }

        @Override
        public void removeBlacklisted(Object o) {

        }

        @Override
        public void setTimeToLive(Long timeToLive) {

        }

        @Override
        public void housekeep() {

        }

        @Override
        public boolean isBlackListEmpty() {
            return true;
        }
    }

    /**
     * Stubbed ErrorReportingService implementation for testing
     * @author Ikasan Development Team
     */
    private class StubbedErrorReportingService<T> implements ErrorReportingService
    {
        @Override
        public Object find(String uri) {
            return null;
        }

        @Override
        public Map find(List uris) {
            return null;
        }

        @Override
        public String notify(String flowElementName, Object o, Throwable throwable) {
            return null;
        }

        @Override
        public String notify(String flowElementName, Object o, Throwable throwable, String action) {
            return null;
        }

        @Override
        public String notify(String flowElementName, Throwable throwable) {
            return null;
        }

        @Override
        public String notify(String flowElementName, Throwable throwable, String action) {
            return null;
        }

        @Override
        public void setTimeToLive(Long timeToLive) {

        }

        @Override
        public void housekeep() {

        }

		/* (non-Javadoc)
		 * @see org.ikasan.spec.error.reporting.ErrorReportingService#find(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
		 */
		@Override
		public List find(List moduleName, List flowName, List flowElementname,
				Date startDate, Date endDate, int size)
		{
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.ikasan.spec.error.reporting.ErrorReportingService#rowCount(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
		 */
		@Override
		public Long rowCount(List moduleName, List flowName,
				List flowElementname, Date startDate, Date endDate)
		{
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.ikasan.spec.error.reporting.ErrorReportingService#find(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date, java.lang.String, java.lang.String, int)
		 */
		@Override
		public List find(List moduleName, List flowName, List flowElementname,
				Date startDate, Date endDate, String action,
				String exceptionClass, int size)
		{
			// TODO Auto-generated method stub
			return null;
		}
    }

    /**
     * Call isAware setters on the RM to populate as required.
     *
     * @param recoveryManager
     */
    private void setIsAware(RecoveryManager recoveryManager)
    {
        if(recoveryManager instanceof IsConsumerAware)
        {
            ((IsConsumerAware)recoveryManager).setConsumer(consumer);
        }

        if(recoveryManager instanceof IsExclusionServiceAware)
        {
            ((IsExclusionServiceAware)recoveryManager).setExclusionService(exclusionService);
        }

        if(recoveryManager instanceof IsErrorReportingServiceAware)
        {
            ((IsErrorReportingServiceAware)recoveryManager).setErrorReportingService(errorReportingService);
        }
    }

    /**
     * Stubbed consumer for testing
     * 
     * @author Ikasan Development Team
     * 
     */
    private class StubbedConsumer implements Consumer
    {
        /** state of this consumer */
        private boolean isRunning = false;

        public boolean isRunning()
        {
            return this.isRunning;
        }

        public void setListener(Object arg0)
        {
            // do not care about this method for the purpose of these tests
        }

        public void start()
        {
            this.isRunning = true;
        }

        public void stop()
        {
            this.isRunning = false;
        }

		public void setEventFactory(Object arg0) 
		{
            // do not care about this method for the purpose of these tests
		}

        public Object getEventFactory()
        {
            // do not care about this method for the purpose of these tests
            return null;
        }
    }

    /**
     * Allow pausing of the execution thread.
     * @param period
     */
    private void pause(int period)
    {
        try
        {
            Thread.sleep(period);
        }
        catch(InterruptedException e)
        {
            // dont care
        }
    }
}
