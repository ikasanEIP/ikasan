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
package org.ikasan.consumer.jms;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointListener;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Implementation of an endpoint listener which acts as a proxy for the real 
 * endpoint listener. This is used where the calls on the real endpoint
 * listener would be deemed as re-entrant calls by frameworks such as Spring AOP.
 * 
 * This proxy simply provides a layer of abstraction which makes all callbacks external 
 * rather than re-entrant on the consumer class.
 *
 * @author Ikasan Development Team
 * @deprecated - use javax.jms.MessageListener; javax.jms.ExceptionListener
 */
public class JmsEndpointListener implements MessageListener, ExceptionListener
{
    /** class logger */
    private static Logger logger = Logger.getLogger(JmsEndpointListener.class);

    /** Actual endpointListener being proxied */
    private EndpointListener endpointListener;
    
    /**
     * Constructor
     * @param endpointListener
     */
    public void setEndpointListener(EndpointListener endpointListener)
    {
        this.endpointListener = endpointListener;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.EndpointListener#onException(java.lang.Throwable)
     */
    public void onException(JMSException throwable)
    {
        endpointListener.onException(throwable);
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.EndpointListener#onMessage(java.lang.Object)
     */
    public void onMessage(Message message)
    {
        endpointListener.onMessage(message);
    }
}
