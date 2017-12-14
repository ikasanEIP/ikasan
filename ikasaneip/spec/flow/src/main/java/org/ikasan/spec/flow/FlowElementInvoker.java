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


import java.util.List;

/**
 * Interface for classes capable of invoking a specified <code>FlowElement</code> with the specified <code>FlowEvent</code>
 * 
 * @author Ikasan Development Team
 */
public interface FlowElementInvoker<COMPONENT>
{
    /** types of flow element invoker */
    public static String CONSUMER = "Consumer";
    public static String PRODUCER = "Producer";
    public static String TRANSLATOR = "Translator";
    public static String CONVERTER = "Converter";
    public static String FILTER = "Filter";
    public static String SEQUENCER = "Sequencer";
    public static String SINGLE_RECIPIENT_ROUTER = "SingleRecipientRouter";
    public static String MULTI_RECIPIENT_ROUTER = "MultiRecipientRouter";
    public static String SPLITTER = "Splitter";
    public static String BROKER = "Broker";

    /**
     * Invokes the specified <code>FlowElement</code>with the specified <code>FlowEvent</code>
     *
     * @param flowEventListener
     * @param moduleName
     * @param flowName
     * @param flowInvocationContext
     * @param flowEvent argument for the <code>FlowElement</code>'s component
     * @param flowElement for invocation
     * @return FlowElement for subsequent execution
     */
    FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext,
            FlowEvent flowEvent, FlowElement<COMPONENT> flowElement);

    /**
     * Override the calls to the context invocation
     */
    void setIgnoreContextInvocation(boolean ignoreContextInvocation);

    /**
     * Set the flow invocation context listeners
     *
     * @param flowInvocationContextListeners
     */
    void setFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners);

    /**
     * Set invoke context listeners flag.
     *
     * @param invokeContextListeners
     */
    void setInvokeContextListeners(boolean invokeContextListeners);

    /**
     * Get the invoker type being implemented.
     * @return
     */
    public String getInvokerType();

}
