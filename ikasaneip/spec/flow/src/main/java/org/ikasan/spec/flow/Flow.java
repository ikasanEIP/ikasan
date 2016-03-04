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
package org.ikasan.spec.flow;

import org.ikasan.spec.serialiser.SerialiserFactory;

import java.util.List;

/**
 * Interface representing a business path for a <code>FlowEvent<code>
 * <p/>
 * Invocation represents the traversal of that business path.
 *
 * @author Ikasan Development Team
 */
public interface Flow
{
    /**
     * Returns the name of this flow
     *
     * @return String name of this flow
     */
    String getName();

    /**
     * Accessor for moduleName
     *
     * @return name of the module this flow exist for
     */
    String getModuleName();

    /**
     * Accessor for getting the flow elements
     * TODO - is this really needed ?
     *
     * @return list of flow elements
     */
    List<FlowElement<?>> getFlowElements();

    /**
     * Accessor for getting the flow element by its given name.
     * If the name does not exist then null is returned.
     * If more than one element exists with this name then the first one
     * encountered is returned.
     *
     * @return list of flow elements
     */
    FlowElement<?> getFlowElement(String name);

    /**
     * Method to get the configuration for the given flow.
     *
     * @return the flow configuration.
     */
    FlowConfiguration getFlowConfiguration();

    /**
     * Setter for a listener for flow events
     *
     * @param flowEventListener
     */
    void setFlowListener(FlowEventListener flowEventListener);

    /**
     * Setter for a List of listeners for the FlowInvocationContext
     *
     * @param flowInvocationContextListeners the listeners
     */
    void setFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners);

    /**
     * Getter for the List of listeners for the FlowInvocationContext
     * @return the list
     */
    List<FlowInvocationContextListener> getFlowInvocationContextListeners();


    /**
     * Invoke all start operations for the flow that are required prior to an event invocation including starting the consumer.
     * For instance, this could include setting any flow component configurations,
     * or starting any flow managed resources.
     */
    void start();

    /**
     * Invoke all start operations for the flow that are required prior to an event invocation, but immediately pause the consumer.
     * For instance, this could include setting any flow component configurations,
     * or starting any flow managed resources.
     */
    void startPause();

    /**
     * Invoke all stop operations for the flow that are required on shutdown of the invoking client.
     * For instance, this could include stopping any flow managed resources.
     */
    void stop();

    /**
     * Invoke stop on the consumer component only. All other components will remain
     * initialised and running.
     */
    void pause();

    /**
     * Invoke start on the consumer component only. All other components will remain
     * initialised and running.
     */
    void resume();

    /**
     * Returns the current runtmie state of this flow.
     * String - runtime state
     */
    String getState();

    /**
     * Get the serialiser factory associated with this flow.
     *
     * @return
     */
    SerialiserFactory getSerialiserFactory();

    /**
     * Is this flow in a running state
     *
     * @return
     */
    boolean isRunning();

    /**
     * Is this flow in a paused state
     *
     * @return
     */
    boolean isPaused();

    /**
     * Start the context listeners
     */
    void startContextListeners();

    /**
     * Stop the context listeners
     */
    void stopContextListeners();

    /**
     * determine the context listener state
     * @return true if the listeners are active/running, false if they are stopped/disabled
     */
    boolean areContextListenersRunning();
}
