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
package org.ikasan.framework.initiator;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.monitor.MonitorListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class AbstractInitiatorTest
{

    
    /**
     * JMock Mockery
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    
    /**
     * mock MonitorListener
     */
    private MonitorListener firstMonitorListener = mockery.mock(MonitorListener.class, "firstMonitorListener");
 
    /**
     * mock MonitorListener
     */
    private MonitorListener secondMonitorListener = mockery.mock(MonitorListener.class, "secondMonitorListener");

    /**
     * name of the module
     */
    private String moduleName = "moduleName";
    
    /**
     * name of the initiator
     */
    private String initiatorName = "initiatorName";
 
    /**
     * mocked Flow
     */
    private Flow flow = mockery.mock(Flow.class);
    
    /**
     * System under test
     */
    private AbstractInitiator abstractInitiator = new MockInitiator(moduleName,initiatorName, flow);
    

    
    @Test
    public void testConstructor(){
        //just testing the constructor our system under test already used
        
        Assert.assertEquals("name should be that passed in on constructor", initiatorName, abstractInitiator.getName());
        Assert.assertEquals("moduleName should be that passed in on constructor", moduleName, abstractInitiator.getModuleName());
        Assert.assertEquals("flow should be that passed in on constructor", flow, abstractInitiator.getFlow());
    }
    
    @Test 
    public void testNotifyMonitorListeners_willNotifyAllRegisteredListeners(){
        final String runningState = InitiatorState.RUNNING.getName();
        //add two monitorListeners and check that they are notified with the state name
        mockery.checking(new Expectations()
        {
            {
                one(firstMonitorListener).notify(runningState);
                one(secondMonitorListener).notify(runningState);
            }
        });
        
        abstractInitiator.addListener(firstMonitorListener);
        abstractInitiator.addListener(secondMonitorListener);
        abstractInitiator.notifyMonitorListeners();
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void testAddRemoveNotifyListener()
    {
        
        
        //by default there should be no monitorListeners registered with this initiator
        Assert.assertTrue("by default there should be no monitorListeners registered with this initiator", abstractInitiator.getMonitorListeners().isEmpty());
        
        abstractInitiator.addListener(firstMonitorListener);
        abstractInitiator.addListener(secondMonitorListener);
        
        Assert.assertEquals("there should be exactly 2 monitor listeners, following the registrtion of two distinct listeners", 2, abstractInitiator.getMonitorListeners().size());        
        Assert.assertEquals("first MonitorListener should be the first one added", firstMonitorListener, abstractInitiator.getMonitorListeners().get(0));
        Assert.assertEquals("second MonitorListener should be the second one added", secondMonitorListener, abstractInitiator.getMonitorListeners().get(1));
        
        //remove one of the listeners and check that only the other one now is registered
        abstractInitiator.removeListener(firstMonitorListener);       

        Assert.assertEquals("there should be exactly 1 monitor listeners, after we started with two, and we deregistered 1", 1, abstractInitiator.getMonitorListeners().size());        
        Assert.assertFalse("list of registered monitor listeners should not contain the one we deregistered", abstractInitiator.getMonitorListeners().contains(firstMonitorListener));
    }
    
    @Test
    public void testGetState(){
        Assert.assertEquals("getState should return InitiatorState.RUNNING if initiator implementation isRunning(), but not isRecovering()", InitiatorState.RUNNING,abstractInitiator.getState()); 
        
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertEquals("getState should return InitiatorState.RECOVERING if initiator implementation isRunning(), AND isRecovering()", InitiatorState.RECOVERING,abstractInitiator.getState()); 
    
        ((MockInitiator)abstractInitiator).setRunning(false);
        Assert.assertEquals("getState should return InitiatorState.STOPPED if initiator implementation !isRunning(), AND !isError()", InitiatorState.STOPPED,abstractInitiator.getState()); 
    
        ((MockInitiator)abstractInitiator).setError(true);
        Assert.assertEquals("getState should return InitiatorState.ERROR if initiator implementation !isRunning(), AND isError()", InitiatorState.ERROR,abstractInitiator.getState()); 
    }
    
    @Test 
    public void testIsError(){
        Assert.assertFalse("isError() should return value of the error flag", abstractInitiator.isError()); 
        ((MockInitiator)abstractInitiator).setError(true);
        Assert.assertTrue("isError() should return value of the error flag", abstractInitiator.isError()); 

    }
    
    @Test
    public void testStart_willClearFlagsBeforeInvokingStartInitiator(){
        
        //set up the mock implementation such that the error flag and the stopping flag are set beforehand, - we just want to make sure these get cleared on start
        ((MockInitiator)abstractInitiator).setError(true);
        ((MockInitiator)abstractInitiator).setStopping(true);
        Assert.assertFalse("just checking that our mock implementation has not had startInitiator called on it before", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        abstractInitiator.start();
        Assert.assertTrue("startInitiator should have been called as a part of the start method", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
    }

    @Test
    public void testStop_onRecoveringInitiator_willSetStoppingFlagAndCancelRetryBeforeInvokingStopInitiator(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    }
   
    @Test
    public void testStop_onNonRecoveringInitiator_willSetStoppingBeforeInvokingStopInitiator(){
        ((MockInitiator)abstractInitiator).setRecovering(false);
        
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    }
    
    /**
     * When handleAction is passed a null action, and the initiator is recovering, it should invoke the completeRetry routine
     */
    @Test
    public void testHandleAction_withNullAction_willCompleteRetryIfRecovering(){
        ((MockInitiator)abstractInitiator).setRecovering(true);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
        
        //invoke the method that will result in handleAction
        ((MockInitiator)abstractInitiator).invokeHandleAction(null);
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
    }
    
    
    
    class MockInitiator extends AbstractInitiator implements Initiator{
        
        private boolean running = true;
        private boolean recovering = false;
        private boolean startInitiatorCalled = false;
        private boolean completeRetryCycleCalled = false;

        
        public boolean isCompleteRetryCycleCalled()
        {
            return completeRetryCycleCalled;
        }


        public void invokeHandleAction(IkasanExceptionAction ikasanExceptionAction){
            handleAction(ikasanExceptionAction);
        }
        
        
        public boolean isStartInitiatorCalled()
        {
            return startInitiatorCalled;
        }



        public boolean isStopInitiatorCalled()
        {
            return stopInitiatorCalled;
        }

        private boolean stopInitiatorCalled = false;

        public MockInitiator(String moduleName, String name, Flow flow)
        {
            super(moduleName, name, flow);
        }

        public void setRunning(boolean running){
            this.running = running;
        }
        
        public void setRecovering(boolean recovering){
            this.recovering=recovering;
        }

        public String getType()
        {
            // TODO Auto-generated method stub
            return null;
        }


        
        public void setError(boolean error){
            this.error = error;
        }
        
        public void setStopping(boolean stopping){
            this.stopping = stopping;
        }

        public boolean isRecovering()
        {
            return recovering;
        }

        public boolean isRunning()
        {
            return running;
        }


        @Override
        protected void completeRetryCycle()
        {
            recovering=false;
            completeRetryCycleCalled = true;
        }

        @Override
        protected Logger getLogger()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void cancelRetryCycle()
        {
            recovering=false;
            
        }

        @Override
        protected void startInitiator() throws InitiatorOperationException
        {
            running = true;
            startInitiatorCalled=true;
            Assert.assertFalse("stopping flag should never be set by the time startInitiator is called", stopping);
            Assert.assertFalse("error flag should never be set by the time startInitiator is called", error);
            
        }

        @Override
        protected void stopInitiator() throws InitiatorOperationException
        {
            running = false;
            stopInitiatorCalled = true;
            Assert.assertTrue("stopping flag should always be set prior to stopInitiator being called", stopping);
            Assert.assertFalse("isRecovering() should never be true once stopInitiator is called", isRecovering());
        }

        @Override
        protected void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException
        {
            // TODO Auto-generated method stub
            
        }
        
    }
}
