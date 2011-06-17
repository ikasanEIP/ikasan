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
package org.ikasan.framework.initiator.messagedriven.jca;

import org.apache.log4j.Logger;
import org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener;
import org.ikasan.framework.initiator.messagedriven.MessageListenerContainer;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;

/**
 * Extension of Spring's DefaultMessageListenerContainer to expose listener setup failures to a registered Listener
 *
 * @author Ikasan Development Team
 *
 */
public class SpringMessageListenerContainer extends JmsMessageEndpointManager implements MessageListenerContainer
{
 private Logger logger = Logger.getLogger(SpringMessageListenerContainer.class);

    /**
     * Flag indicating last attempt to connect was a failure
     */
    private boolean listenerSetupFailure = false;

    /**
     * Registered failure listener
     */
    private ListenerSetupFailureListener listenerSetupExceptionListener;


    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.messagedriven.MessageListenerContainer#setListenerSetupExceptionListener(org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener)
     */
    public void setListenerSetupExceptionListener(ListenerSetupFailureListener listenerSetupExceptionListener)
    {
        this.listenerSetupExceptionListener = listenerSetupExceptionListener;
    }


    /**
     * Accessor for listenerSetupFailure flag
     *
     * @return listenerSetupFailure
     */
    public boolean isListenerSetupFailure()
    {
        return listenerSetupFailure;
    }
}
