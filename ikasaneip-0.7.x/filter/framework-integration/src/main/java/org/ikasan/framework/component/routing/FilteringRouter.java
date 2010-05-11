/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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

package org.ikasan.framework.component.routing;

import org.ikasan.common.Payload;
import org.ikasan.filter.FilterRule;
import org.ikasan.filter.MessageFilter;
import org.ikasan.filter.DefaultMessageFilter;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.endpoint.Endpoint;
import org.ikasan.framework.component.routing.SingleResultRouter;
import org.ikasan.framework.flow.FlowElement;

/**
 * A {@link Router} implementation that delegates to a {@link MessageFilter}. If the
 * filter returns null, the event <b>must</b> be removed from flow: routed to an {@link Endpoint} component.
 * Otherwise, the event <b>must</b> be passed through the flow: routed next {@link FlowElement} in the flow.
 * 
 * @author Summer
 *
 */
public class FilteringRouter extends SingleResultRouter
{
    /** Routing result for events to be removed from flow.*/
    //Made protected to be accessed from test class*/
    protected final static String DISCARD_MESSAGE = "discard_message";

    /** Routing result for events to continue to next flow element.*/
    //Made protected to be accessed from test class*/
    protected static final String PASS_MESSAGE_THRU = "pass_message_through";

    /** The MessageFilter */
    private final MessageFilter  filter;

    /**
     * Constructor
     * @param filterRule {@link FilterRule} for filter
     */
    public FilteringRouter(final FilterRule filterRule)
    {
        this.filter = new DefaultMessageFilter(filterRule);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.framework.component.routing.SingleResultRouter#evaluate(org.ikasan.framework.component.Event)
     */
    @Override
    protected String evaluate(Event event)
    {
        Payload payload = event.getPayloads().get(0);
        String message = new String(payload.getContent());
        String filteredMessage = this.filter.filter(message);
        if (filteredMessage != null)
        {
            return PASS_MESSAGE_THRU;
        }
        else
        {
            return DISCARD_MESSAGE;
        }
    }

}
