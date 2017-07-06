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
package org.ikasan.spec.recovery;

/**
 * Recovery Manager contract.
 * 
 * @author Ikasan Development Teams
 */
public interface RecoveryManager<RESOLVER, CONTEXT, ID>
{
    /**
     * Set a resolver which translates the incoming exception and component name
     * in to an action to be taken by the recovery manager.
     * @param resolver
     */
    void setResolver(RESOLVER resolver);
    
    /**
     * Set a resolver which translates the incoming exception and component name
     * in to an action to be taken by the recovery manager.
     * @param managedResources
     * @param <MANAGED_RESOURCES>
     */
    <MANAGED_RESOURCES> void setManagedResources(MANAGED_RESOURCES managedResources);
    
    /**
     * Get the resolver for this recovery manager.
     * @return  resolver
     */
    RESOLVER getResolver();

    /**
     * Start or continue a recovery based on the passed CONTEXT
     * @param context
     * @param throwable
     * @param event
     * @param identifier
     * @param <EVENT>
     * @param <ID>
     */
    <EVENT> void recover(CONTEXT context, Throwable throwable, EVENT event, ID identifier);

    /**
     * Start or continue a recovery based on the passed CRITERIA.
     * @param component
     * @param throwable
     */
    void recover(String component, Throwable throwable);
    
    /**
     * Is the recovery manager currently running a recovery.
     * @return
     */
    boolean isRecovering();
    
    /**
     * Is the recovery manager in an unrecoverable state.
     * @return
     */
    boolean isUnrecoverable();
    
    /**
     * Cancel any recovery currently running in the recovery manager.
     */
    void cancelAll();

    /**
     * Cancel a recovery given the current identifier for a flow (flow event id)
     * @param identifier the identifier
     */
    void cancel(ID identifier);

    /**
     * Initialize the state of the recovery manager clearing down any previously
     * held states resulting from previous executions.
     */
    void initialise();
}
