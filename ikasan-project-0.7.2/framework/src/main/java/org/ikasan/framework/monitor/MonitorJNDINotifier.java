/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
