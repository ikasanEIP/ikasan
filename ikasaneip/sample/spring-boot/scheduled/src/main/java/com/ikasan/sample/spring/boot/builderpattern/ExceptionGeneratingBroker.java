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

import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by majean on 09/10/2017.
 */
public class ExceptionGeneratingBroker implements Broker, ManagedResource
{
    private static Logger logger = LoggerFactory.getLogger(ExceptionGeneratingBroker.class);

    private boolean shouldThrowExclusionException = false;

    private boolean shouldThrowRecoveryException = false;

    private boolean shouldThrowScheduledRecoveryException = false;

    private boolean shouldThrowStoppedInErrorException = false;

    private boolean shouldThrowRecoveryExceptionEveryNInvocations;

    private int numberOfInvocationsBeforeRetry;

    private int counter = 0;
    private int recoveryCount = 0;

    @Override public Object invoke(Object o) throws EndpointException
    {
        counter+=1;
        logger.info("Invoking ExceptionGeneratingBroker! " + counter);
        if(shouldThrowExclusionException){
            throw  new SampleGeneratedException("This exception is thrown to test exclusion.");
        }

        if(shouldThrowScheduledRecoveryException){
            // Set this to false to allow for the flow to successfully recover after the
            // recovery manager has kicked in. Next execution will not throw the exception.
            shouldThrowScheduledRecoveryException = false;
            throw  new SampleScheduledRecoveryGeneratedException("This exception is thrown to test recovery.");
        }

        if(shouldThrowRecoveryException){
            // Set this to false to allow for the flow to successfully recover after the
            // recovery manager has kicked in. Next execution will not throw the exception.
            shouldThrowRecoveryException = false;
            throw  new EndpointException("This exception is thrown to test recovery.");
        }

        if(shouldThrowRecoveryExceptionEveryNInvocations && counter >= numberOfInvocationsBeforeRetry
            && recoveryCount < 5 ){
            recoveryCount++;
            // Set this to false to allow for the flow to successfully recover after the
            // recovery manager has kicked in. Next execution will not throw the exception.
            throw  new EndpointException("This exception is thrown to test recovery every n invocation.");
        }

        if(shouldThrowStoppedInErrorException){
            throw  new RuntimeException("This exception is thrown to test stoppedInError.");
        }
        return o;
    }

    public void setShouldThrowExclusionException(boolean shouldThrowExclusionException)
    {
        this.shouldThrowExclusionException = shouldThrowExclusionException;
    }

    public void setShouldThrowRecoveryException(boolean shouldThrowRecoveryException)
    {
        this.shouldThrowRecoveryException = shouldThrowRecoveryException;
    }

    public void setShouldThrowScheduledRecoveryException(boolean shouldThrowScheduledRecoveryException)
    {
        this.shouldThrowScheduledRecoveryException = shouldThrowScheduledRecoveryException;
    }
    public void setShouldThrowStoppedInErrorException(boolean shouldThrowStoppedInErrorException)
    {
        this.shouldThrowStoppedInErrorException = shouldThrowStoppedInErrorException;
    }

    public void setShouldThrowRecoveryExceptionEveryNInvocations(boolean shouldThrowRecoveryExceptionEveryNInvocations) {
        this.shouldThrowRecoveryExceptionEveryNInvocations = shouldThrowRecoveryExceptionEveryNInvocations;
    }

    public void setNumberOfInvocationsBeforeRetry(int numberOfInvocations) {
        this.numberOfInvocationsBeforeRetry = numberOfInvocations;
    }

    public void reset(){
        shouldThrowExclusionException = false;
        shouldThrowRecoveryException = false;
        shouldThrowScheduledRecoveryException = false;
        shouldThrowStoppedInErrorException = false;
    }

    @Override
    public void startManagedResource() {

    }

    @Override
    public void stopManagedResource() {

    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    @Override
    public boolean isCriticalOnStartup() {
        return false;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {

    }
}
