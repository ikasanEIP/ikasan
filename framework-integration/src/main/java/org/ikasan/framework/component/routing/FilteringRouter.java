/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */

package org.ikasan.framework.component.routing;

import org.ikasan.filter.DefaultMessageFilter;
import org.ikasan.filter.FilterException;
import org.ikasan.filter.FilterRule;
import org.ikasan.filter.MessageFilter;
import org.ikasan.framework.component.Event;


/**
 * A {@link Router} implementation that delegates to a {@link MessageFilter}. If the
 * filter returns null, the event <b>must</b> be removed from flow: routed to an {@link Endpoint} component.
 * Otherwise, the event <b>must</b> be passed through the flow: routed next {@link FlowElement} in the flow.
 * 
 * @author Ikasan Development Team
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
    private final MessageFilter<Event>  filter;

    /**
     * Constructor
     * @param filterRule {@link FilterRule} for filter
     */
    public FilteringRouter(final FilterRule<Event> filterRule)
    {
        this.filter = new DefaultMessageFilter<Event>(filterRule);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.framework.component.routing.SingleResultRouter#evaluate(org.ikasan.framework.component.Event)
     */
    @Override
    protected String evaluate(Event event) throws RouterException
    {
        try
        {
            Event filteredMessage = this.filter.filter(event);
            if (filteredMessage != null)
            {
                return PASS_MESSAGE_THRU;
            }
            else
            {
                return DISCARD_MESSAGE;
            }
        }
        catch(FilterException e)
        {
            throw new RouterException(e);
        }
    }

}
