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
package org.ikasan.history.listener;

import org.apache.log4j.Logger;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.history.MessageHistoryService;

/**
 * Context listener that delegates to a MessageHistoryService to save the FlowInvocationContext
 *
 * @author Ikasan Development Team
 */
public class MessageHistoryContextListener<T> implements FlowInvocationContextListener
{
    private static final Logger logger = Logger.getLogger(MessageHistoryContextListener.class);

    /** the delegate service used to save the flowInvocationContext */
    protected MessageHistoryService<FlowInvocationContext, T> messageHistoryService;

    /** the module and flow name */
    protected String moduleName, flowName;

    /** boolean to determine whether to rethrow any caught exceptions thrown by the underlying service, defaults to false */
    protected boolean rethrowServiceExceptions = false;

    public MessageHistoryContextListener(MessageHistoryService<FlowInvocationContext, T> messageHistoryService, String moduleName, String flowName)
    {
        if (messageHistoryService == null)
        {
            throw new IllegalArgumentException("messageHistoryService cannot be null");
        }
        this.messageHistoryService = messageHistoryService;
        if (moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be null");
        }
        this.moduleName = moduleName;
        if (flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be null");
        }
        this.flowName = flowName;
    }

    @Override
    public void endFlow(FlowInvocationContext flowInvocationContext)
    {
        try
        {
            messageHistoryService.save(flowInvocationContext, moduleName, flowName);
        }
        catch (RuntimeException re)
        {
            if (rethrowServiceExceptions)
            {
                throw re;
            }
            logger.warn("Failed to save flowInvocationContext", re);
        }
    }

    public void setRethrowServiceExceptions(boolean rethrowServiceExceptions)
    {
        this.rethrowServiceExceptions = rethrowServiceExceptions;
    }
}
