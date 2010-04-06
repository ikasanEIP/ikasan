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
package org.ikasan.framework.monitor;

import java.util.Calendar;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.common.component.ComponentState;
import org.ikasan.common.component.Status;

/**
 * Monitor listener implementation which notifies the JNDI of state changes.
 * 
 * @author Ikasan Development Team
 */
public class MonitorJNDINotifier extends AbstractMonitorListener
{
    /** Logger */
    private static Logger logger = Logger.getLogger(MonitorJNDINotifier.class);

    /** JNDI context */
    protected Context context;

    /** JNDI URL for state object */
    protected String url;

    /** keep a reference to the previous state so we only report changes */
    protected Status status;

    /**
     * Constructor for monitor
     * 
     * @param name The name of the Monitor
     * @param context The context for the JNDI monitor
     * @param url The URI to store the status
     */
    public MonitorJNDINotifier(String name, Context context, String url)
    {
        super(name);
        this.context = context;
        this.url = url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.monitor.AbstractMonitorListener#notify(java.lang.String)
     */
    @Override
    public void notify(String text)
    {
        ComponentState componentState = getComponentState(text);
        // update status instance
        this.status = new Status(componentState, new Long(Calendar.getInstance().getTimeInMillis()));
        try
        {
            this.bindStatus(status);
        }
        catch (NamingException e)
        {
            logger.warn("Monitor listener [" + this.getName() + "] failed to set initiator state.", e);
        }
    }

    /**
     * Translate the initiator state on to that required for reporting through the JNDI.
     * 
     * @param state The state to lookup with
     * @return componentState
     */
    protected static ComponentState getComponentState(String state)
    {
        if (state != null)
        {
            if (state.equals("stoppedInError"))
            {
                return ComponentState.ERROR;
            }
            if (state.equals("stopped"))
            {
                return ComponentState.STOPPED;
            }
            if (state.equals("runningInRecovery"))
            {
                return ComponentState.RECOVERING;
            }
            if (state.equals("running"))
            {
                return ComponentState.RUNNING;
            }
        }
        // default to error
        logger.warn("State [" + state + "] not supported. Defaulting to 'Unknown' state.");
        return ComponentState.UNKNOWN;
    }

    /**
     * Bind the status object to the JNDI.
     * 
     * @param statusToBind The status to bind to JNDI
     * @throws NamingException Exception if we cannot bind that status
     */
    protected void bindStatus(Status statusToBind) throws NamingException
    {
        try
        {
            this.context.bind(this.url, statusToBind);
        }
        catch (NameNotFoundException e)
        {
            this.createContext();
            this.context.rebind(this.url, statusToBind);
        }
        catch (NameAlreadyBoundException e)
        {
            this.context.rebind(this.url, statusToBind);
        }
    }

    /**
     * Create the complete sub context for any new URL binding.
     * 
     * @throws NamingException Exception if we cannot create the context
     */
    private void createContext() throws NamingException
    {
        Name parsedUrl = this.context.getNameParser("").parse(this.url);
        Context subctx = this.context;
        for (int pos = 0; pos < parsedUrl.size(); pos++)
        {
            String ctxName = parsedUrl.get(pos);
            try
            {
                subctx = (Context) subctx.lookup(ctxName);
            }
            catch (NameNotFoundException e)
            {
                subctx = subctx.createSubcontext(ctxName);
            }
        }
    }
}
