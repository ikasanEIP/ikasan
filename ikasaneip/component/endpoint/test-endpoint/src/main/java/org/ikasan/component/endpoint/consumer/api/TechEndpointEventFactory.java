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
package org.ikasan.component.endpoint.consumer.api;

import org.ikasan.component.endpoint.consumer.api.event.APIIntervalEvent;
import org.ikasan.component.endpoint.consumer.api.event.APIRepeatEvent;

/**
 * Contract for the Tech Endpoint event factory contract.
 *
 * This factory allows the creation of tech endpoint events based on any parameter inputs as specified by the implementor.
 *
 * @author Ikasan Development Team
 */
public interface TechEndpointEventFactory<API_EVENT>
{
    /**
     * Create an instance of the tech endpoint message event based on the incoming message parameter.
     * @param message
     * @param <M>
     * @return
     */
    <M> API_EVENT getMessageEvent(M message);

    /**
     * Create an instance of the tech endpoint message event based on the incoming lifeIdentifier and message parameters.
     * @param lifeIdentifier
     * @param message
     * @param <I>
     * @param <M>
     * @return
     */
    <I,M> API_EVENT getMessageEvent(I lifeIdentifier, M message);

    /**
     * Create an instance of the tech endpoint exception event based on the incoming exception parameter.
     * @param exception
     * @param <T>
     * @return
     */
    <T> API_EVENT getExceptionEvent(T exception);

    /**
     * Create an instance of an interval event (a wait on the endpoint) based on the interval value parameter.
     * @param interval
     * @return
     */
    APIIntervalEvent getIntervalEvent(long interval);

    /**
     * Create an instance of a repeat event based on the parameters.
     * @param apiMessageEvent
     * @param repeat
     * @return
     */
    APIRepeatEvent getRepeatEvent(API_EVENT apiMessageEvent, int repeat);

}



