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
package org.ikasan.flow.visitorPattern.invoker;

import com.google.common.util.concurrent.*;
import org.apache.log4j.Logger;
import org.ikasan.flow.visitorPattern.DefaultFlowInvocationContext;
import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.management.ManagedService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;

/**
 * A default implementation of the FlowElementInvoker for a splitter
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class ConcurrentSplitterFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<Splitter>, ManagedService
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(ConcurrentSplitterFlowElementInvoker.class);

    /** executor service for thread dispatching - default fixed pool of 10 */
    private ListeningExecutorService executorService;

    /** does this component require the full flowEvent or just the payload */
    Boolean requiresFullEventForInvocation;

    /** count for number of submitted future tasks */
    int count;

    /** handle to any exceptions called back from the future task */
    Throwable callbackException;

    /** handle to the failed tasks flow invocation context */
    FlowInvocationContext failedTaskFlowInvocationContext;

    /**
     * Constructor
     * @param executorService
     */
    public ConcurrentSplitterFlowElementInvoker(ExecutorService executorService)
    {
        this.executorService = MoreExecutors.listeningDecorator(executorService);
        if(executorService == null)
        {
            throw new IllegalArgumentException("executorService cannot be 'null'");
        }
    }

    @Override
    public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, final FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<Splitter> flowElement)
    {
        flowInvocationContext.addInvokedComponentName(flowElement.getComponentName());
        notifyListenersBeforeElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);

        Splitter splitter = flowElement.getFlowComponent();
        List payloads;
        if(requiresFullEventForInvocation == null)
        {
            try
            {
                // try with flowEvent and if successful mark this component
                payloads = splitter.split(flowEvent);
                requiresFullEventForInvocation = Boolean.TRUE;
            }
            catch(ClassCastException e)
            {
                payloads = splitter.split(flowEvent.getPayload());
                requiresFullEventForInvocation = Boolean.FALSE;
            }
        }
        else
        {
            if(requiresFullEventForInvocation.booleanValue())
            {
                payloads = splitter.split(flowEvent);
            }
            else
            {
                payloads = splitter.split(flowEvent.getPayload());
            }
        }

        FlowElement nextFlowElement = getDefaultTransition(flowElement);
        if (nextFlowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName() + "] contains a Splitter, but it has no default transition! "
                    + "Splitters should never be the last component in a flow");
        }

        if (payloads == null || payloads.size() == 0)
        {
            throw new SplitterException("FlowElement [" + flowElement.getComponentName() + "] contains a Splitter. "
                    + "Splitters must return at least one payload.");
        }

        // initialise futures task stats
        count = 0;
        callbackException = null;

        List<ListenableFuture<FlowInvocationContext>> futures = new ArrayList<ListenableFuture<FlowInvocationContext>>(payloads.size());
        for (Object payload : payloads)
        {
            if(payload instanceof FlowEvent)
            {
                flowEvent = (FlowEvent)payload;
            }
            else
            {
                flowEvent.setPayload(payload);
            }
            notifyListenersAfterElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);

            FlowElement nextFlowElementInRoute = nextFlowElement;

            // TODO - replace new DefaultFlowInvocationContext with a factory method
            Callable<FlowInvocationContext> asyncTask = newAsyncTask(nextFlowElementInRoute, flowEventListener, moduleName, flowName, new DefaultFlowInvocationContext(), flowEvent);
            final ListenableFuture<FlowInvocationContext> listenableFuture = executorService.submit(asyncTask);
            futures.add(listenableFuture);
            Futures.addCallback(listenableFuture, new FutureCallback<FlowInvocationContext>() {
                public void onSuccess(FlowInvocationContext taskFlowInvocationContext) {
                    flowInvocationContext.combine(taskFlowInvocationContext);
                    count++;
                }

                public void onFailure(Throwable thrown) {
                    if (thrown instanceof SplitFlowElementException) {
                        SplitFlowElementException splitFlowElementException = (SplitFlowElementException) thrown;
                        if (callbackException == null) {
                            callbackException = splitFlowElementException.getThrown();
                            failedTaskFlowInvocationContext = splitFlowElementException.getFlowInvocationContext();
                        }
                    } else {
                        callbackException = thrown;
                    }
                }
            });

            if(callbackException != null)
            {
                break;
            }
        }

        while( pendingCallback(payloads) )
        {
            try
            {
                Thread.sleep(1);
            }
            catch(InterruptedException e)
            {
                logger.warn("Sleep interrupted", e);
            }
        }

        if(callbackException != null)
        {
            for(ListenableFuture future:futures)
            {
                try
                {
                    if(!future.isDone())
                    {
                        future.cancel(true);
                    }
                }
                catch(CancellationException e)
                {
                    logger.warn("Failed to cancel task", e);
                }
            }

            flowInvocationContext.combine(failedTaskFlowInvocationContext);
            if(callbackException instanceof RuntimeException)
            {
                throw (RuntimeException)callbackException;
            }

            throw new SplitterException(callbackException);
        }

        return null;
    }

    /**
     * Allows for easier testing
     * @param payloads
     * @return
     */
    protected boolean pendingCallback(List payloads)
    {
        return count < payloads.size() && callbackException == null;
    }

    /**
     * Factory method to aid testing.
     *
     * @param nextFlowElementInRoute
     * @param flowEventListener
     * @param moduleName
     * @param flowName
     * @param flowInvocationContext
     * @param flowEvent
     * @return
     */
    protected Callable newAsyncTask(FlowElement nextFlowElementInRoute, FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent)
    {
        return new SplitFlowElement(nextFlowElementInRoute, flowEventListener, moduleName, flowName, new DefaultFlowInvocationContext(), flowEvent);
    }

    /**
     * Concurrent executions
     *
     */
    class SplitFlowElement implements Callable<FlowInvocationContext>
    {
        FlowElement _nextFlowElementInRoute;
        FlowEventListener _flowEventListener;
        String _moduleName;
        String _flowName;
        FlowInvocationContext _flowInvocationContext;
        FlowEvent _flowEvent;

        public SplitFlowElement(FlowElement _nextFlowElementInRoute, FlowEventListener _flowEventListener, String _moduleName, String _flowName, FlowInvocationContext _flowInvocationContext, FlowEvent _flowEvent)
        {
            this._nextFlowElementInRoute = _nextFlowElementInRoute;
            if(_nextFlowElementInRoute == null)
            {
                throw new IllegalArgumentException("_nextFlowElementInRoute cannot be 'null'");
            }

            this._flowEventListener = _flowEventListener;
            if(_flowEventListener == null)
            {
                throw new IllegalArgumentException("_flowEventListener cannot be 'null'");
            }

            this._moduleName = _moduleName;
            if(_moduleName == null)
            {
                throw new IllegalArgumentException("_moduleName cannot be 'null'");
            }

            this._flowName = _flowName;
            if(_flowName == null)
            {
                throw new IllegalArgumentException("_flowName cannot be 'null'");
            }

            this._flowInvocationContext = _flowInvocationContext;
            if(_flowInvocationContext == null)
            {
                throw new IllegalArgumentException("_flowInvocationContext cannot be 'null'");
            }

            this._flowEvent = _flowEvent;
            if(_flowEvent == null)
            {
                throw new IllegalArgumentException("_flowEvent cannot be 'null'");
            }
        }

        @Override
        public FlowInvocationContext call()
        {
            try
            {
                while (_nextFlowElementInRoute != null)
                {
                    _nextFlowElementInRoute = _nextFlowElementInRoute.getFlowElementInvoker().invoke(_flowEventListener, _moduleName, _flowName, _flowInvocationContext, _flowEvent, _nextFlowElementInRoute);
                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }
                }

                return null;
            }
            catch(Throwable t)
            {
                throw new SplitFlowElementException(t, _flowInvocationContext);
            }
        }
    }

    /**
     * Exception
     *
     */
    class SplitFlowElementException extends RuntimeException
    {
        FlowInvocationContext flowInvocationContext;
        Throwable thrown;

        public SplitFlowElementException(Throwable thrown, FlowInvocationContext flowInvocationContext)
        {
            this.thrown = thrown;
            this.flowInvocationContext = flowInvocationContext;
        }

        public FlowInvocationContext getFlowInvocationContext() {
            return flowInvocationContext;
        }

        public Throwable getThrown() {
            return thrown;
        }
    }

    @Override
    public void destroy()
    {
        if (executorService != null)
        {
            logger.info("ConcurrentSplitterFlowElement shutting down executorService");
            executorService.shutdown();
        }
    }

}